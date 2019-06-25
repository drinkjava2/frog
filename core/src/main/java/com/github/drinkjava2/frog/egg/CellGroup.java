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
 * CellGroup represents a bunch of similar nerve cells <br/>
 * 
 * CellGroup代表了一束相同功能和结构、分布位置相近的脑神经元，目的是为了下蛋时简化串行化海量的神经元,
 * 只需要在egg里定义一组cellGroup就行了，不需要将海量的一个个的神经元串行化存放到egg里，这样一来Frog就不能"永生"了，因为每一个egg都不等同于
 * 它的母体， 而且每一次测试，一些复杂的条件反射的建立都必须从头开始训练，在项目后期，有可能每个frog生命的一半时间都花在重新建立条件反射的学习过程中。
 * 
 * 模拟一公一母两个蛋受精，CellGroup叠加也许很fun,这样可以将不同环境训练出的蛋叠加成一个。但现在暂时不考虑。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class CellGroup implements Serializable {
	private static final long serialVersionUID = 1L;
	public Zone groupInputZone; // input distribute zone

	public Zone groupOutputZone; // output distribute zone

	public float cellInputRadius; // input radius of each cell
	public float cellOutputRadius; // output radius of each cell

	public float cellQty; // how many nerve cells in this CellGroup

	public float inputQtyPerCell; // input qty per cell
	public float outputQtyPerCell; // output qty per cell

	public long fat = 0; // if activate times=0 ,this cellgroup may be ignored in egg
	public boolean inherit = false; // set to true if is inherited from egg, not by random

	private static final Random r = new Random();

	public CellGroup() {

	}

	public CellGroup(CellGroup g) {// clone old CellGroup
		groupInputZone = new Zone(g.groupInputZone);
		groupOutputZone = new Zone(g.groupOutputZone);
		cellInputRadius = g.cellInputRadius;
		cellOutputRadius = g.cellOutputRadius;
		cellQty = g.cellQty;
		inputQtyPerCell = g.inputQtyPerCell;
		outputQtyPerCell = g.outputQtyPerCell;
		fat = g.fat;
		inherit = g.inherit;
	}

	public CellGroup(float brainWidth, int randomCellQtyPerGroup, int randomInputQtyPerCell, int randomOutQtyPerCell) {
		inherit = false;
		groupInputZone = new Zone(r.nextFloat() * brainWidth, r.nextFloat() * brainWidth,
				(float) (r.nextFloat() * brainWidth * .01));
		groupOutputZone = new Zone(r.nextFloat() * brainWidth, r.nextFloat() * brainWidth,
				(float) (r.nextFloat() * brainWidth * .01));
		cellQty = 1 + r.nextInt(randomCellQtyPerGroup);
		cellInputRadius = (float) (0.001 + r.nextFloat() * 2);
		cellOutputRadius = (float) (0.001 + r.nextFloat() * 2);
		inputQtyPerCell = 1 + r.nextInt(randomInputQtyPerCell);
		outputQtyPerCell = 1 + r.nextInt(randomOutQtyPerCell);
	}

}
