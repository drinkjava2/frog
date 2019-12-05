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
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.env.Env;

/**
 * Egg is the static structure description of frog, can save as text file, to
 * build a frog, first need build a egg.<br/>
 * 
 * 蛋存在的目的是为了以最小的字节数串行化存储Frog,它是Frog的生成算法描述，而不是Frog本身，这样一来Frog就不能"永生"了，因为每一个egg都不等同于
 * 它的母体， 而且每一次测试，大部分条件反射的建立都必须从头开始训练，类似于人类，无论人类社会有多聪明， 婴儿始终是一张白纸，需要花大量的时间从头学习。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Egg implements Serializable {
	private static final long serialVersionUID = 1L;
	public int randomCellGroupQty = 30; // 随机生成多少个组
	public int randomCellQtyPerGroup = 3; // 每个组有多少个脑细胞
	public int randomInputQtyPerCell = 3;// 每个脑细胞有多少个输入触突
	public int randomOutQtyPerCell = 2; // 每个脑细胞有多少个输出触突

	public CellGroup[] cellGroups;

	public List<OrganDesc> organDescs;

	public Egg() {
		// default constructor
	}

	private static Random r = new Random();

	/** Create a egg by join 2 eggs, x+y=zygote */
	public Egg(Egg x, Egg y) {
		// 模拟XY染色体，不能做简单加法，会撑暴内存的，现在每次只随机加一个y的cellgroup进来，这也不太好，因为基因会越加越多，只好用用进废退原则来加大淘汰率。

		// x里原来的CellGroup
		cellGroups = new CellGroup[x.cellGroups.length + 1 + randomCellGroupQty];
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
		cellGroups[x.cellGroups.length] = cellGroup;

		// 随机生成一批CellGroup
		for (int i = 0; i < randomCellGroupQty; i++)
			cellGroups[i + x.cellGroups.length + 1] = new CellGroup(Env.FROG_BRAIN_WIDTH, x.randomCellQtyPerGroup,
					x.randomInputQtyPerCell, x.randomOutQtyPerCell);

		addOrganDescs();
	}

	/** create a brand new Egg */
	public static Egg createBrandNewEgg() { // 无中生有，创建一个蛋，先有蛋，后有鸡
		Egg egg = new Egg();
		egg.cellGroups = new CellGroup[egg.randomCellGroupQty];
		for (int i = 0; i < egg.randomCellGroupQty; i++)
			egg.cellGroups[i] = new CellGroup(Env.FROG_BRAIN_WIDTH, egg.randomCellQtyPerGroup,
					egg.randomInputQtyPerCell, egg.randomOutQtyPerCell);
		egg.addOrganDescs();
		return egg;
	}

	private static boolean percent(int percent) {
		return r.nextInt(100) < percent;
	}

	private static float vary(float f) { // 大部分时候不变，有极小机会变异,有极极小机会大变异，有极极极小机会大大大变异
		int i=r.nextInt(100);
		if (i<95)
			return f;
		float rate = .05f;
		if(i>97)
			rate = .1f;
		return (float) (f * ((1 - rate) + r.nextFloat() * rate * 2));
	}

	/** Create egg from frog */
	public Egg(Frog frog) { // 青蛙下蛋，蛋的基因生成遵循用进废退、随机变异两个原则
		List<CellGroup> gpList = new ArrayList<>();
		for (int i = 0; i < frog.cellGroups.length; i++) {
			if (frog.cellGroups[i].fat <= 0) {
				if (!frog.cellGroups[i].inherit)
					continue;// 从未激活过的神经元，并且就是本轮随机生成的，丢弃之
				if (percent(5))
					continue;// 继承下来的神经元，但是本轮并没用到， 扔掉又可惜，可以小概率丢掉
			}
			CellGroup cellGroup = new CellGroup();
			CellGroup oldGp = frog.cellGroups[i];
			cellGroup.groupInputZone = new Zone(vary(oldGp.groupInputZone.x), vary(oldGp.groupInputZone.y),
					vary(oldGp.groupInputZone.radius));
			cellGroup.groupOutputZone = new Zone(vary(oldGp.groupOutputZone.x), vary(oldGp.groupOutputZone.y),
					vary(oldGp.groupOutputZone.radius));
			cellGroup.cellQty = Math.round(vary(oldGp.cellQty));
			cellGroup.cellInputRadius = vary(oldGp.cellInputRadius);
			cellGroup.cellOutputRadius = vary(oldGp.cellOutputRadius);
			cellGroup.inputQtyPerCell = Math.round(vary(oldGp.inputQtyPerCell));
			cellGroup.outputQtyPerCell = Math.round(vary(oldGp.outputQtyPerCell));
			cellGroup.inherit = true;
			gpList.add(cellGroup);
		}
		cellGroups = gpList.toArray(new CellGroup[gpList.size()]);
		addOrganDescs();
	}

	/** Hard code add organs */
	public void addOrganDescs() { // 硬编码添加器官，将来考虑器官的数量、位置、大小也可以遗传、变异、进化
		organDescs = new ArrayList<>();
		organDescs.add(new OrganDesc(Organ.HUNGRY, 300, 100, 100));
		organDescs.add(new OrganDesc(Organ.DOWN, 800, 100, 60));
		organDescs.add(new OrganDesc(Organ.UP, 800, 400, 60));
		organDescs.add(new OrganDesc(Organ.LEFT, 700, 250, 60));
		organDescs.add(new OrganDesc(Organ.RIGHT, 900, 250, 60));
		organDescs.add(new OrganDesc(Organ.EAT, 0, 0, 0));
		organDescs.add(new OrganDesc(Organ.EYE, 100, 400, 100));
		//organDescs.add(new OrganDesc(Organ.EYE, 100, 700, 100));
	}

}
