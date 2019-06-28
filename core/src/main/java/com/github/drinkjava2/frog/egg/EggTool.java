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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.github.drinkjava2.frog.Application;
import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.util.FrogFileUtils;

/**
 * EggTool save eggs to disk
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class EggTool {

	/**
	 * Frogs which have higher energy lay eggs
	 * 
	 * 利用Java串行机制存盘。 能量多(也就是吃的更多，更fat)的Frog下蛋并存盘, 以进行下一伦测试，能量少的Frog被淘汰，没有下蛋的资格。
	 * 用能量的多少来简化生存竟争模拟
	 */
	public static void layEggs(Env env) {
		sortFrogsOrderByEnergyDesc(env);

		Frog first = env.frogs.get(0);
		Frog last = env.frogs.get(env.frogs.size() - 1);

		if (Env.DEBUG_MODE)
			for (int i = 0; i < first.organs.size(); i++) {
				Organ org = first.organs.get(i);
				System.out.println("Organ(" + i + ")=" + org + ", fat=" + org.fat + ", organWasteEnergy="
						+ org.organActiveEnergy + ", outputEnergy=" + org.organOutputEnergy);
			}

		System.out.print("First frog has " + first.organs.size() + " organs, energy=" + first.energy);
		System.out.print(", Last frog has " + last.organs.size() + " organs, energy=" + last.energy);

		try {
			List<Egg> newEggs = new ArrayList<>();
			for (int i = 0; i < Env.EGG_QTY; i++)
				newEggs.add(new Egg(env.frogs.get(i)));
			System.out.print(", organs =" + newEggs.get(0).organs.size() + ", ");

			FileOutputStream fo = new FileOutputStream(Application.CLASSPATH + "eggs.ser");
			ObjectOutputStream so = new ObjectOutputStream(fo);
			so.writeObject(newEggs);
			so.close();

			env.eggs = newEggs;
			System.out.println(
					", Saved " + env.eggs.size() + " eggs to file '" + Application.CLASSPATH + "eggs.ser" + "'");
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private static void sortFrogsOrderByEnergyDesc(Env env) {// 按能量多少给青蛙排序
		Collections.sort(env.frogs, new Comparator<Frog>() {
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
		System.out.println("Delete exist egg file: '" + Application.CLASSPATH + "eggs.ser'");
		FrogFileUtils.deleteFile(Application.CLASSPATH + "eggs.ser");
	}

	/**
	 * 从磁盘读入一批Egg
	 */
	@SuppressWarnings("unchecked")
	public static void loadEggs(Env env) {
		boolean errorfound = false;
		try {
			FileInputStream eggsFile = new FileInputStream(Application.CLASSPATH + "eggs.ser");
			ObjectInputStream eggsInputStream = new ObjectInputStream(eggsFile);
			env.eggs = (List<Egg>) eggsInputStream.readObject();
			System.out.println(
					"Loaded " + env.eggs.size() + " eggs from file '" + Application.CLASSPATH + "eggs.ser" + "'.");
			eggsInputStream.close();
		} catch (Exception e) {
			errorfound = true;
		}
		if (errorfound) {
			System.out.println("Fail to load egg file at path '" + Application.CLASSPATH + "', created " + Env.EGG_QTY
					+ " new eggs to do test.");
			env.eggs = new ArrayList<Egg>();
			for (int i = 0; i < Env.EGG_QTY; i++)
				env.eggs.add(new Egg());
		}
	}

}
