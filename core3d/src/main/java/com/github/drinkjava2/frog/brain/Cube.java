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
package com.github.drinkjava2.frog.brain;

import java.util.Arrays;

/**
 * Cube include 0~n cells and 0~n photons
 * 
 * Cube是脑的空间单位，是一个正立方体，里面可以存放多个脑细胞(Cell)和光子(Photon)
 * Cube和Cuboid的区别是，Cube是脑的最小空间单元，作为脑细胞存放和光子传播的介质，简化计算，而Cuboid通常是一个长方体，用来描述器官的大小和位置,
 * 器官的大小和位置是可以变异的
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Cube {
	/** Activity of current cube */
	private float active = 0; // 这个立方体的激活程度，允许是负值,它反映了在这个小立方体里所有光子的能量汇总值

	private Cell[] cells = null;

	private Photon[] photons = null;

	public Cell[] getCells() {// 为了节约内存，仅在访问cells时创建它的实例
		if (cells == null)
			cells = new Cell[] {};
		return cells;
	}

	public Photon[] getPhotons() {// 为了节约内存，仅在访问photons时创建它的实例
		if (photons == null)
			photons = new Photon[] {};
		return photons;
	}

	public void addCell(Cell cell) {// 每个方格空间可以存在多个脑细胞
		if (cells == null)
			cells = new Cell[] {};
		cells = Arrays.copyOf(cells, cells.length + 1);
		cells[cells.length - 1] = cell;
	}

	public void addPhoton(Photon p) {// 每个方格空间可以存在多个光子
		if (photons == null)
			photons = new Photon[] {};
		for (Photon old : photons)
			if (old.energy < 0.01 && p.energy > -0.01) {// 如果能量没有了，用新的代替空位
				old.x = p.x;
				old.y = p.y;
				old.z = p.z;
				old.energy = p.energy;
				return;
			}
		photons = Arrays.copyOf(photons, photons.length + 1);// 否则追加新光子到未尾
		photons[cells.length - 1] = p;
	}

	public float getActive() {
		return active;
	}

	public void setActive(float active) {
		this.active = active;
	}

}
