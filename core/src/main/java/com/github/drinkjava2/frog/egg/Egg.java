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
import java.util.Random;

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
	public static final int CELL_GROUP_QTY = 30;
	public float brainRadius = 1000;
	public CellGroup[] cellgroups;

	public static Egg createBrandNewEgg() {
		Egg egg = new Egg();
		Random r = new Random();
		egg.cellgroups = new CellGroup[CELL_GROUP_QTY];
		for (int i = 0; i < CELL_GROUP_QTY; i++) {
			CellGroup cellGroup = new CellGroup();
			egg.cellgroups[i] = cellGroup;
			cellGroup.groupInputZone = new Zone(r.nextFloat() * egg.brainRadius, r.nextFloat() * egg.brainRadius,
					(float) (r.nextFloat() * egg.brainRadius * .01));
			cellGroup.groupOutputZone = new Zone(r.nextFloat() * egg.brainRadius, r.nextFloat() * egg.brainRadius,
					(float) (r.nextFloat() * egg.brainRadius * .01));
			cellGroup.cellQty = r.nextInt(10);
			cellGroup.cellInputRadius = (float) (r.nextFloat() * 0.001);
			cellGroup.cellOutputRadius = (float) (r.nextFloat() * 0.001);
			cellGroup.inputQtyPerCell = r.nextInt(10);
			cellGroup.outputQtyPerCell = r.nextInt(5);
		}
		return egg;
	}

}
