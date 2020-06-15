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

import com.gitee.drinkjava2.frog.Application;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Snake;
import com.gitee.drinkjava2.frog.organ.Active;
import com.gitee.drinkjava2.frog.organ.frog.FrogBigEye;
import com.gitee.drinkjava2.frog.organ.snake.SnakeEyes;
import com.gitee.drinkjava2.frog.organ.snake.SnakeMouth;
import com.gitee.drinkjava2.frog.organ.snake.SnakeMoves;
import com.gitee.drinkjava2.frog.util.LocalFileUtils;

/**
 * SnakeEggTool save/load snake eggs to file
 * 
 * @author Yong Zhu
 * @since 1.0
 */
@SuppressWarnings("all")
public class SnakeEggTool {

	/**
	 * Snakes which have higher energy lay eggs
	 * 
	 * 利用Java串行机制存盘。 能量多(也就是吃的更多)的Snake下蛋并存盘, 以进行下一轮测试，能量少的Snake被淘汰，没有下蛋的资格。
	 * 用能量的多少来简化生存竟争模拟，每次下蛋数量固定为EGG_QTY个
	 */
	public static void layEggs() {
		if (!Env.SNAKE_MODE)
			return;
		sortSnakesOrderByEnergyDesc();
		Snake first = Env.snakes.get(0);
		Snake last = Env.snakes.get(Env.snakes.size() - 1);
		try {
			Env.snake_eggs.clear();
			for (int i = 0; i < Env.SNAKE_EGG_QTY; i++)
				Env.snake_eggs.add(new Egg(Env.snakes.get(i)));
			FileOutputStream fo = new FileOutputStream(Application.CLASSPATH + "snake_eggs.ser");
			ObjectOutputStream so = new ObjectOutputStream(fo);
			so.writeObject(Env.snake_eggs);
			so.close();
			System.out.print("Fist snake has " + first.organs.size() + " organs,  energy=" + first.energy);
			System.out.println(", Last snake has " + last.organs.size() + " organs,  energy=" + last.energy);
			System.out.println(
					"Saved " + Env.snake_eggs.size() + " eggs to file '" + Application.CLASSPATH + "snake_eggs.ser'");
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private static void sortSnakesOrderByEnergyDesc() {// 按能量多少给青蛙排序
		Collections.sort(Env.snakes, new Comparator<Snake>() {
			public int compare(Snake a, Snake b) {
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
		System.out.println("Delete exist egg file: '" + Application.CLASSPATH + "snake_eggs.ser'");
		LocalFileUtils.deleteFile(Application.CLASSPATH + "snake_eggs.ser");
	}

	/**
	 * 从磁盘读入一批snake Egg
	 */
	@SuppressWarnings("unchecked")
	public static void loadSnakeEggs() {
		boolean errorfound = false;
		try {
			FileInputStream eggsFile = new FileInputStream(Application.CLASSPATH + "snake_eggs.ser");
			ObjectInputStream eggsInputStream = new ObjectInputStream(eggsFile);
			Env.snake_eggs = (List<Egg>) eggsInputStream.readObject();
			System.out.println("Loaded " + Env.snake_eggs.size() + " eggs from file '" + Application.CLASSPATH
					+ "snake_eggs.ser" + "'.\n");
			eggsInputStream.close();
		} catch (Exception e) {
			errorfound = true;
		}
		if (errorfound) {
			Env.snake_eggs.clear();
			for (int j = 0; j < Env.SNAKE_EGG_QTY; j++) {
				Egg egg = new Egg();
				float r = 30;
				float h = 3;
				egg.organs.add(new SnakeMouth().setXYZRHN(0, 0, 0, 0, h, "Eat")); // SnakeMouth不是感觉或输出器官，没有位置和大小
				egg.organs.add(new FrogBigEye().setXYZRHN(190, 90, 500, r * 2, h, "BigEye"));// 大眼睛，永远加在第1位
				egg.organs.add(new Active().setXYZRHN(500, 600, 500, r, h, "Active")); // 永远激活
				egg.organs.add(new SnakeMoves.MoveUp().setXYZRHN(800, 300, 500, r, h, "Up"));
				egg.organs.add(new SnakeMoves.MoveDown().setXYZRHN(800, 600, 500, r, h, "Down"));
				egg.organs.add(new SnakeMoves.MoveLeft().setXYZRHN(700, 450, 500, r, h, "Left"));
				egg.organs.add(new SnakeMoves.MoveRight().setXYZRHN(900, 450, 500, r, h, "Right"));
				egg.organs.add(new SnakeEyes.SeeUp().setXYZRHN(200, 500 + 90, 500, r, h, "SeeUp"));
				egg.organs.add(new SnakeEyes.SeeDown().setXYZRHN(200, 500 - 90, 500, r, h, "SeeDown"));
				egg.organs.add(new SnakeEyes.SeeLeft().setXYZRHN(200 - 90, 500, 500, r, h, "SeeLeft"));
				egg.organs.add(new SnakeEyes.SeeRight().setXYZRHN(200 + 90, 500, 500, r, h, "SeeRight"));

				Env.snake_eggs.add(egg);
			}
			System.out.println("Fail to load snake egg file '" + Application.CLASSPATH + "snake_eggs.ser"
					+ "', created " + Env.snake_eggs.size() + " eggs to do test.");
		}

	}

}
