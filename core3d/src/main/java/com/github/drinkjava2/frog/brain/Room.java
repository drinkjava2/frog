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

import com.github.drinkjava2.frog.Frog;

/**
 * Room is the smallest unit of brain space, a room can have 0~n cells and 0~n
 * photons
 * 
 * Room是脑的最小空间单元，里面可以存放多个脑细胞(Cell)和光子(Photon)，脑是由frog中的rooms三维数组组成，但不是每一维都初始化过
 * 以前Room名为Cube, 现改名为Room
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Room {
	/** Activity of current room */
	private float active = 0; // 这个Room的激活程度，允许是负值,它反映了在这个小立方体里所有光子的能量汇总值,room总是随时间自动衰减

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
			photons = new Photon[] {};// 创建数组
		else
			for (int i = 0; i < cells.length; i++) { // 找空位插入
				if (photons[i] == null || photons[i].energy < 0.1) {
					photons[i] = p;
					return; // 如找到就插入，返回
				}
			}
		photons = Arrays.copyOf(photons, photons.length + 1);// 否则追加新光子到未尾
		photons[cells.length - 1] = p;
	}

	public void removePhoton(int i) {// 删第几个光子
		photons[i] = null;
	}

	/** Photon always walk */
	public void photonWalk(Frog f, Photon p) { // 光子如果没有被处理，它自已会走到下一格，如果下一格为空，继续走,直到能量耗尽或出界
		if (p.energy < 0.1)
			return;// 能量小的光子直接扔掉
		p.x += p.mx;
		p.y += p.my;
		p.z += p.mz;
		int rx = Math.round(p.x);
		int ry = Math.round(p.y);
		int rz = Math.round(p.z);
		if (f.existRoom(rx, ry, rz))
			;
	}

	/** Move photons and call cell's execute method */
	public void execute(Frog f, int x, int y, int z) {// 主运行方法，进行实际的脑细经元的行为和光子移动
		if (cells != null)
			for (Cell cell : cells) // cell会生成或处理掉所在room的光子
				CellActions.act(f, this, cell, x, y, z);
		// 剩下的，或由细胞新产生的光子，自已会走到下一格，别忘了，光子是会走的，而且是直线传播
		if (photons != null)
			for (int i = 0; i < photons.length; i++) {
				Photon p = photons[i];
				if (p == null)
					continue;
				photons[i] = null;// 原来位置的光子先清除
				photonWalk(f, p); // TODO: 写到这里
			}
	}

	public float getActive() {
		return active;
	}

	public void setActive(float active) {
		this.active = active;
	}

}
