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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.drinkjava2.frog.Frog;

/**
 * Egg is the static structure description of frog, can save as text file, to
 * build a frog, first need build a egg.<br/>
 * 
 * 蛋存在的目的是为了以最小的字节数串行化存储Frog,它是Frog的生成算法描述，而不是Frog本身，这样一来Frog就不能"永生"了，因为每一个egg都不等同于
 * 它的母体， 而且每一次测试，大部分条件反射的建立都必须从头开始训练，类似于人类，无论人类社会有多聪明， 婴儿始终是一张白纸，需要花大量的时间从头学习。
 * 
 */
public class Egg implements Serializable {
	private static final long serialVersionUID = 2L;
	public static final int BRAIN_WIDTH = 800;
	public int randomCellGroupQty = 100; // 随机生成多少个组
	public int randomCellQtyPerGroup = 3; // 每个组有多少个脑细胞
	public int randomInputQtyPerCell = 3;// 每个脑细胞有多少个输入触突
	public int randomOutQtyPerCell = 2; // 每个脑细胞有多少个输出触突

	public CellGroup[] cellGroups;

	public Egg() {
		// default constructor
	}

	private static Random r = new Random();

	// 我靠，两个蛋怎么合成一个蛋?看来要模拟XY染色体了，不能做加法，会撑暴内存的，但现在，每次只随机加一个
	public Egg(Egg x, Egg y) { // use 2 eggs to create a zygote
		// x里原来的CellGroup
		cellGroups = new CellGroup[x.cellGroups.length + 1 + randomCellGroupQty / 3];
		for (int i = 0; i < x.cellGroups.length; i++) {
			CellGroup oldCellGroup = x.cellGroups[i];
			CellGroup cellGroup = new CellGroup();
			cellGroups[i] = cellGroup;
			cellGroup.inherit = true;
			cellGroup.groupInputZone = new Zone(oldCellGroup.groupInputZone);
			cellGroup.groupOutputZone = new Zone(oldCellGroup.groupOutputZone);
			cellGroup.cellQty = oldCellGroup.cellQty;
			cellGroup.cellInputRadius = oldCellGroup.cellInputRadius;
			cellGroup.cellOutputRadius = oldCellGroup.cellOutputRadius;
			cellGroup.inputQtyPerCell = oldCellGroup.inputQtyPerCell;
			cellGroup.outputQtyPerCell = oldCellGroup.outputQtyPerCell;
		}

		// 从y里借一个CellGroup
		CellGroup randomY = y.cellGroups[r.nextInt(y.cellGroups.length)];
		CellGroup cellGroup = new CellGroup(randomY);
		cellGroups[x.cellGroups.length ] = cellGroup;

		//随机生成一批CellGroup
		for (int i = 0; i < randomCellGroupQty / 3; i++)
			cellGroups[i + x.cellGroups.length + 1] = new CellGroup(Egg.BRAIN_WIDTH, x.randomCellQtyPerGroup,
					x.randomInputQtyPerCell, x.randomOutQtyPerCell);
	}

	public static Egg createBrandNewEgg() { // create a brand new Egg
		Egg egg = new Egg();
		egg.cellGroups = new CellGroup[egg.randomCellGroupQty];
		for (int i = 0; i < egg.randomCellGroupQty; i++)
			egg.cellGroups[i] = new CellGroup(Egg.BRAIN_WIDTH, egg.randomCellQtyPerGroup, egg.randomInputQtyPerCell,
					egg.randomOutQtyPerCell);
		return egg;
	}

	private static boolean allowVariation = false;

	private static float percet1(float f) {
		if (!allowVariation)
			return f;
		return (float) (f * (0.99f + r.nextFloat() * 0.02));
	}

	private static boolean percent(int percent) {
		return r.nextInt(100) < percent;
	}

	private static float percet2(float f) {
		if (!allowVariation)
			return f;
		return (float) (f * (0.98f + r.nextFloat() * 0.04));
	}

	public Egg(Frog frog, boolean allowVariate) { // create a brand new Egg
		allowVariation = allowVariate;
		List<CellGroup> gpList = new ArrayList<>();
		for (int i = 0; i < frog.cellGroups.length; i++) {
			if (frog.cellGroups[i].fat <= 0) {
				 if (!  frog.cellGroups[i].inherit)
				 continue;// 从未激活过的神经元，并且就是本轮随机生成的，丢弃之
				 if (percent(10))
				continue;// 继承下来的神经元，但是本轮并没用到， 扔掉又可惜，可以小概率丢掉
			}
			CellGroup cellGroup = new CellGroup();
			CellGroup oldGp = frog.cellGroups[i];
			cellGroup.groupInputZone = new Zone(percet2(oldGp.groupInputZone.x), percet2(oldGp.groupInputZone.y),
					percet2(oldGp.groupInputZone.radius));
			cellGroup.groupOutputZone = new Zone(percet2(oldGp.groupOutputZone.x), percet2(oldGp.groupOutputZone.y),
					percet2(oldGp.groupOutputZone.radius));
			cellGroup.cellQty = Math.round(percet2(oldGp.cellQty));
			cellGroup.cellInputRadius = percet1(oldGp.cellInputRadius);
			cellGroup.cellOutputRadius = percet1(oldGp.cellOutputRadius);
			cellGroup.inputQtyPerCell = Math.round(percet2(oldGp.inputQtyPerCell));
			cellGroup.outputQtyPerCell = Math.round(percet2(oldGp.outputQtyPerCell));
			cellGroup.inherit = true;
			gpList.add(cellGroup);
		}
		cellGroups = gpList.toArray(new CellGroup[gpList.size()]);
	}

}
