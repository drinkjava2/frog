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
package com.github.drinkjava2.frog.egg;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.github.drinkjava2.frog.Application;
import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.organ.Active;
import com.github.drinkjava2.frog.brain.organ.Mouth;
import com.github.drinkjava2.frog.brain.organ.MoveDown;
import com.github.drinkjava2.frog.brain.organ.MoveLeft;
import com.github.drinkjava2.frog.brain.organ.MoveRight;
import com.github.drinkjava2.frog.brain.organ.MoveUp;
import com.github.drinkjava2.frog.brain.organ.Eyes.SeeDown;
import com.github.drinkjava2.frog.brain.organ.Eyes.SeeLeft;
import com.github.drinkjava2.frog.brain.organ.Eyes.SeeRight;
import com.github.drinkjava2.frog.brain.organ.Eyes.SeeUp;
import com.github.drinkjava2.frog.util.LocalFileUtils;

/**
 * EggTool save eggs to disk
 * 
 * @author Yong Zhu
 * @since 1.0
 */
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
			System.out.println(
					"Saved " + Env.frog_eggs.size() + " eggs to file '" + Application.CLASSPATH + "frog_eggs.ser'");
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private static void sortFrogsOrderByEnergyDesc() {// 按能量多少给青蛙排序
		Collections.sort(Env.frogs, new Comparator<Frog>() {
			public int compare(Frog a, Frog b) {
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
			System.out.println("Loaded " + Env.frog_eggs.size() + " eggs from file '" + Application.CLASSPATH
					+ "frog_eggs.ser" + "'.\n");
			eggsInputStream.close();
		} catch (Exception e) {
			errorfound = true;
		}
		if (errorfound) {
			Env.frog_eggs.clear();
			for (int j = 0; j < Env.FROG_EGG_QTY; j++) {
				Egg egg = new Egg();
				float r = 40;
				egg.organs.add(new Mouth().setXYZRN(0, 0, 0, 0, "Eat")); // Mouth不是感觉或输出器官，没有位置和大小
				egg.organs.add(new Active().setXYZRN(500, 600, 500, r, "Active")); // 永远激活 
				egg.organs.add(new MoveUp().setXYZRN(800, 100, 500, r, "Up"));
				egg.organs.add(new MoveDown().setXYZRN(800, 400, 500, r, "Down"));
				egg.organs.add(new MoveLeft().setXYZRN(700, 250, 500, r, "Left"));
				egg.organs.add(new MoveRight().setXYZRN(900, 250, 500, r, "Right"));
				egg.organs.add(new SeeUp().setXYZRN(200, 300 + 90, 500, r, "SeeUp"));
				egg.organs.add(new SeeDown().setXYZRN(200, 300 - 90, 500, r, "SeeDown"));
				egg.organs.add(new SeeLeft().setXYZRN(200 - 90, 300, 500, r, "SeeLeft"));
				egg.organs.add(new SeeRight().setXYZRN(200 + 90, 300, 500, r, "SeeRight"));
				Env.frog_eggs.add(egg);
			}
			System.out.println("Fail to load frog egg file '" + Application.CLASSPATH + "frog_eggs.ser" + "', created "
					+ Env.frog_eggs.size() + " eggs to do test.");
		}

	}

}
