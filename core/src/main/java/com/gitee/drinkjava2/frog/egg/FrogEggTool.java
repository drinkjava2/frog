/*
 * Copyright 2018 the original author or authors. 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.gitee.drinkjava2.frog.egg;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Application;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.organ.Active;
import com.gitee.drinkjava2.frog.organ.frog.FrogBigEye;
import com.gitee.drinkjava2.frog.organ.frog.FrogDrop;
import com.gitee.drinkjava2.frog.organ.frog.FrogEar;
import com.gitee.drinkjava2.frog.organ.frog.FrogEyes;
import com.gitee.drinkjava2.frog.organ.frog.FrogGuaGua;
import com.gitee.drinkjava2.frog.organ.frog.FrogGuaGuaStop;
import com.gitee.drinkjava2.frog.organ.frog.FrogHearNothing;
import com.gitee.drinkjava2.frog.organ.frog.FrogJump;
import com.gitee.drinkjava2.frog.organ.frog.FrogMouth;
import com.gitee.drinkjava2.frog.organ.frog.FrogMoves;
import com.gitee.drinkjava2.frog.util.LocalFileUtils;

/**
 * FrogEggTool save/load frog eggs to file
 * 
 * @author Yong Zhu
 * @since 1.0
 */
@SuppressWarnings("all")
public class FrogEggTool {

	/**
	 * Frogs which have higher energy lay eggs
	 * 
	 * 利用Java串行机制存盘。 能量多(也就是吃的更多)的Frog下蛋并存盘, 以进行下一轮测试，能量少的Frog被淘汰，没有下蛋的资格。
	 * 用能量的多少来简化生存竟争模拟，每次下蛋数量固定为EGG_QTY个
	 */
	public static void layEggs() {

		sortFrogsOrderByEnergyDesc();
		Frog first = Env.frogs.get(0);
		Frog last = Env.frogs.get(Env.frogs.size() - 1);
		try {
			Env.frog_eggs.clear();
			for (int i = 0; i < Env.FROG_EGG_QTY; i++)
				Env.frog_eggs.add(new Egg(Env.frogs.get(i)));
			FileOutputStream fo = new FileOutputStream(Application.CLASSPATH + "frog_eggs.ser");
			ObjectOutputStream so = new ObjectOutputStream(fo);
			so.writeObject(Env.frog_eggs);
			so.close();
			System.out.print("Fist frog has " + first.organs.size() + " organs,  energy=" + first.energy);
			System.out.println(", Last frog has " + last.organs.size() + " organs,  energy=" + last.energy);
			System.out.println("Saved " + Env.frog_eggs.size() + " eggs to file '" + Application.CLASSPATH + "frog_eggs.ser'");
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private static void sortFrogsOrderByEnergyDesc() {// 按能量多少给青蛙排序
		Collections.sort(Env.frogs, new Comparator<Animal>() {
			public int compare(Animal a, Animal b) {
				if (a.energy > b.energy)
					return -1;
				else if (a.energy == b.energy)
					return 0;
				else
					return 1;
			}
		});
	}

	public static void deleteEggs() {
		System.out.println("Delete exist egg file: '" + Application.CLASSPATH + "frog_eggs.ser'");
		LocalFileUtils.deleteFile(Application.CLASSPATH + "frog_eggs.ser");
	}

	/**
	 * 从磁盘读入一批frog Egg
	 */
	@SuppressWarnings("unchecked")
	public static void loadFrogEggs() {
		boolean errorfound = false;
		try {
			FileInputStream eggsFile = new FileInputStream(Application.CLASSPATH + "frog_eggs.ser");
			ObjectInputStream eggsInputStream = new ObjectInputStream(eggsFile);
			Env.frog_eggs = (List<Egg>) eggsInputStream.readObject();
			System.out.println("Loaded " + Env.frog_eggs.size() + " eggs from file '" + Application.CLASSPATH + "frog_eggs.ser" + "'.\n");
			eggsInputStream.close();
		} catch (Exception e) {
			errorfound = true;
		}
		if (errorfound) {
			Env.frog_eggs.clear();
			for (int j = 0; j < Env.FROG_EGG_QTY; j++) {
				Egg egg = new Egg();
				float r = 40;
				float h = 3;
				egg.organs.add(new FrogMouth().setXYZRHN(0, 0, 0, 0, h, "Eat")); // Mouth不是感觉或输出器官，没有位置和大小
				egg.organs.add(new FrogBigEye().setXYZRHN(190, 90, 500, r * 2, h, "FrogBigEye"));// 大眼睛，永远加在第一位
				egg.organs.add(new Active().setXYZRHN(500, 600, 500, r, h, "Active")); // 永远激活
				egg.organs.add(new FrogMoves.MoveUp().setXYZRHN(800, 300, 500, r, h, "Up"));
				egg.organs.add(new FrogMoves.MoveDown().setXYZRHN(800, 600, 500, r, h, "Down"));
				egg.organs.add(new FrogMoves.MoveLeft().setXYZRHN(700, 450, 500, r, h, "Left"));
				egg.organs.add(new FrogMoves.MoveRight().setXYZRHN(900, 450, 500, r, h, "Right"));
				egg.organs.add(new FrogEyes.SeeUp().setXYZRHN(200, 500 + 90, 500, r, h, "SeeUp"));
				egg.organs.add(new FrogEyes.SeeDown().setXYZRHN(200, 500 - 90, 500, r, h, "SeeDown"));
				egg.organs.add(new FrogEyes.SeeLeft().setXYZRHN(200 - 90, 500, 500, r, h, "SeeLeft"));
				egg.organs.add(new FrogEyes.SeeRight().setXYZRHN(200 + 90, 500, 500, r, h, "SeeRight"));
				egg.organs.add(new FrogGuaGua().setXYZRHN(650, 240, 500, r / 2, h, "GuaGua")); // 呱呱
				egg.organs.add(new FrogGuaGuaStop().setXYZRHN(650, 180, 500, r / 2, h, "GuaGuaStop")); // 呱呱停叫
				egg.organs.add(new FrogEar().setXYZRHN(650, 120, 500, r / 2, h, "Ear")); // 耳朵
				egg.organs.add(new FrogHearNothing().setXYZRHN(650, 60, 500, r / 2, h, "HearNothing")); // 什么也没听到时激活

				egg.organs.add(new FrogJump().setXYZRHN(750, 180, 500, r / 2, h, "Jump")); // 跳
				egg.organs.add(new FrogDrop().setXYZRHN(750, 120, 500, r / 2, h, "Drop")); // 回地上

				Env.frog_eggs.add(egg);
			}
			System.out.println("Fail to load frog egg file '" + Application.CLASSPATH + "frog_eggs.ser" + "', created " + Env.frog_eggs.size() + " eggs to do test.");
		}

	}

}
