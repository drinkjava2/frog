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
import com.github.drinkjava2.frog.brain.organ.Active;
import com.github.drinkjava2.frog.snake.Snake;
import com.github.drinkjava2.frog.snake.brain.organ.SnakeEyes;
import com.github.drinkjava2.frog.snake.brain.organ.SnakeMouth;
import com.github.drinkjava2.frog.snake.brain.organ.SnakeMoves;
import com.github.drinkjava2.frog.util.LocalFileUtils;

/**
 * EggTool save eggs to disk
 * 
 * @author Yong Zhu
 * @since 1.0
 */
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
				float r = 40;
				egg.organs.add(new SnakeMouth().setXYZRN(0, 0, 0, 0, "Eat")); // SnakeMouth不是感觉或输出器官，没有位置和大小
				egg.organs.add(new Active().setXYZRN(500, 600, 500, 5, "Active")); // 永远激活
				egg.organs.add(new SnakeMoves.MoveUp().setXYZRN(800, 100, 500, r, "Up"));
				egg.organs.add(new SnakeMoves.MoveDown().setXYZRN(800, 400, 500, r, "Down"));
				egg.organs.add(new SnakeMoves.MoveLeft().setXYZRN(700, 250, 500, r, "Left"));
				egg.organs.add(new SnakeMoves.MoveRight().setXYZRN(900, 250, 500, r, "Right"));
				egg.organs.add(new SnakeEyes.SeeUp().setXYZRN(200, 300 + 90, 500, r, "SeeUp"));
				egg.organs.add(new SnakeEyes.SeeDown().setXYZRN(200, 300 - 90, 500, r, "SeeDown"));
				egg.organs.add(new SnakeEyes.SeeLeft().setXYZRN(200 - 90, 300, 500, r, "SeeLeft"));
				egg.organs.add(new SnakeEyes.SeeRight().setXYZRN(200 + 90, 300, 500, r, "SeeRight"));
				Env.snake_eggs.add(egg);
			}
			System.out.println("Fail to load snake egg file '" + Application.CLASSPATH + "snake_eggs.ser"
					+ "', created " + Env.snake_eggs.size() + " eggs to do test.");
		}

	}

}
