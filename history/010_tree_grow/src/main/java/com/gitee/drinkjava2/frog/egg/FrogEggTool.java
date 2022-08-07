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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Application;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.util.LocalFileUtils;
import com.gitee.drinkjava2.frog.util.Logger;

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
			Logger.info("Fist frog energy={}, gene size={}, Last frog energy={}", first.energy, getGeneSize(first), last.energy);
            if (Env.SAVE_EGGS_FILE) {
                FileOutputStream fo = new FileOutputStream(Application.CLASSPATH + "frog_eggs.ser");
                ObjectOutputStream so = new ObjectOutputStream(fo);
                so.writeObject(Env.frog_eggs);
                so.close();
				Logger.info(". Saved {} eggs to file '{}frog_eggs.ser'", Env.frog_eggs.size(), Application.CLASSPATH);
            }
        } catch (IOException e) {
			Logger.error(e);
		}
	}

	private static String getGeneSize(Frog f) {//按genes类型汇总每种基因的个数
	    StringBuilder sb=new StringBuilder("[");
	    for (int i = 0; i < f.genes.size(); i++) 
	        sb.append(f.genes.get(i).size()).append(",");
	    if(sb.length()>1)
	        sb.setLength(sb.length()-1);
	    sb.append("]");
	    return sb.toString();
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
		Logger.info("Delete exist egg file: '{}frog_eggs.ser'", Application.CLASSPATH);
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
			Logger.info("Loaded " + Env.frog_eggs.size() + " eggs from file '" + Application.CLASSPATH
					+ "frog_eggs.ser" + "'.\n");
			eggsInputStream.close();
		} catch (Exception e) {
			errorfound = true;
		}
		if (errorfound) {
			Env.frog_eggs.clear();
			for (int j = 0; j < Env.FROG_EGG_QTY; j++) {
				Egg egg = new Egg();
				Env.frog_eggs.add(egg);
			}
			Logger.info("Fail to load frog egg file '" + Application.CLASSPATH + "frog_eggs.ser" + "', created "
					+ Env.frog_eggs.size() + " eggs to do test.");
		}

	}

}
