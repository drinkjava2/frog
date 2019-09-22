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
 * @author Yong Zhu
 * @since 1.0
 */
public class Cube {
	Cell[] cells = new Cell[] {};

	Photon[] photons = new Photon[] {};

	public void addCell(Cell cell) {// 每个方格空间可以存在多个脑细胞
		cells = Arrays.copyOf(cells, cells.length + 1);
		cells[cells.length - 1] = cell;
	}

	public void addPhoton(Photon p) {// 每个方格空间可以存在多个光子
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

	public void removePhoton(int i) {// 清除光子
		photons[i].energy = 0;
	}
}
