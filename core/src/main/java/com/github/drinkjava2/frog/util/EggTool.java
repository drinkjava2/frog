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
package com.github.drinkjava2.frog.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.egg.Egg;
import com.github.drinkjava2.frog.env.Application;
import com.github.drinkjava2.frog.env.Env;

/**
 * Egg is the static structure description of frog, can save as text file, to
 * build a frog, first need build a egg.
 */
public class EggTool {

	public static final boolean JSON_FILE_FORMAT = false; // JSON is slow but easier to debug

	/**
	 * 利用Java串行机制存盘。 能量多(也就是吃的更多，更fat)的Frog下蛋并存盘, 以进行下一伦测试，能量少的Frog被淘汰，没有下蛋的资格。
	 */
	public static void layEggs(Env env) {
		sortFrogsOrderByEnergyDesc(env);
		System.out.print("First frog has " + env.frogs.get(0).cellGroups.length + " cellgroups, energy="
				+ env.frogs.get(0).energy);
		System.out.print(",  Last frog energy=" + env.frogs.get(env.frogs.size() - 1).energy + ",  ");
		try {
			List<Egg> newEggs = new ArrayList<Egg>();
			for (int i = 0; i < env.EGG_QTY; i++)
				newEggs.add(  new Egg(env.frogs.get(i), true));
			System.out.print(",  EggCellGroups="+newEggs.get(0).cellGroups.length);
 
			if (JSON_FILE_FORMAT) {
				String newEggsString = JSON.toJSONString(newEggs);
				FrogFileUtils.writeFile(Application.CLASSPATH + "eggs.json", newEggsString, "utf-8");
			} else {
				FileOutputStream fo = new FileOutputStream(Application.CLASSPATH + "eggs.ser");
				ObjectOutputStream so = new ObjectOutputStream(fo);
				so.writeObject(newEggs);
				so.close();
			}
			env.eggs = newEggs;
			System.out
					.println(" Saved " + env.eggs.size() + " eggs to file '" + Application.CLASSPATH + "eggs.ser" + "'");
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private static void sortFrogsOrderByEnergyDesc(Env env) {
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
		FrogFileUtils.deleteFile(Application.CLASSPATH + "eggs.json");
		FrogFileUtils.deleteFile(Application.CLASSPATH + "eggs.ser");
	}

	/**
	 * 从磁盘读入一批Egg
	 */
	@SuppressWarnings("unchecked")
	public static void loadEggs(Env env) {
		boolean errorfound = false;
		if (JSON_FILE_FORMAT) {
			String eggsString = FrogFileUtils.readFile(Application.CLASSPATH + "eggs.json", "utf-8");
			if (eggsString != null) {
				List<JSONObject> jsonEggs = (List<JSONObject>) JSON.parse(eggsString);
				env.eggs = new ArrayList<Egg>();
				for (JSONObject json : jsonEggs) {
					Egg egg = json.toJavaObject(Egg.class);
					env.eggs.add(egg);
				}
				System.out.println(
						"Loaded " + env.eggs.size() + " eggs from file '" + Application.CLASSPATH + "eggs.json" + "'.");
			} else
				errorfound = true;
		} else {
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
		}
		if (errorfound) {
			System.out.println("No eggs files in path '" + Application.CLASSPATH + "' found, created " + env.EGG_QTY
					+ " new eggs to do test.");
			env.eggs = new ArrayList<Egg>();
			for (int i = 0; i < env.EGG_QTY; i++)
				env.eggs.add(Egg.createBrandNewEgg());
		}
	}

}
