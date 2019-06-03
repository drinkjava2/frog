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

import com.github.drinkjava2.frog.brain.Zone;

/**
 * CellGroup has big change from v2.0
 * 
 * CellGroup代表了一组平均分布于一个正方形内的细胞群，细胞数量、每个细胞的触突连接方式等参数由当前CellGroup决定。
 * CellGroup会参与遗传和进化，但是它生成的细胞不会参与遗传。 
 * 各个CellGroups生成的细胞相加总和就是脑细胞总数。  Cellgroup在脑活动中不起作用，可以把CellGroup比作播种机，把种子排列好后，就撒手不管了。
 * 蛋里存放着所有CellGroups的位置、大小、内部参数等信息，但是蛋里面不保存具体的细胞。这样通过控制有多少个"播种机"，就可以控制大脑的结构了。
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
