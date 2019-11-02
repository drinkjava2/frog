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

	private int color;// Room的颜色取最后一次被添加的光子的颜色，颜色不重要，但能方便观察

	private int photonQty = 0;

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

	public void addCell(Cell cell) {// 每个方格空间可以存在多个脑细胞，这个方法通常只在青蛙初始化器官时调用
		if (cells == null)
			cells = new Cell[] {};
		cells = Arrays.copyOf(cells, cells.length + 1);
		cells[cells.length - 1] = cell;
	}

	public void addPhoton(Photon p) {// 每个room空间可以存在多个光子
		p.energy*=.75;
		if (p == null || p.energy < 0.1)
			return;
		photonQty++;
		color = p.color; // Room的颜色取最后一次被添加的光子的颜色
		if (photons == null)
			photons = new Photon[] {};// 创建数组
		else
			for (int i = 0; i < photons.length; i++) { // 找空位插入
				if (photons[i] == null || photons[i].energy < 0.1) {
					photons[i] = p;
					return; // 如找到就插入，返回
				}
			}
		photons = Arrays.copyOf(photons, photons.length + 1);// 否则追加新光子到未尾
		photons[photons.length - 1] = p;
	}

	public void removePhoton(int i) {// 删第几个光子
		if (photons[i] != null) {
			photons[i] = null;
			photonQty--;
		}
	}

	/** Photon always walk */
	public void photonWalk(Frog f, Photon p) { // 光子自已会走到下一格，如果下一格为空，继续走,直到能量耗尽或出界
		if (p.energy < 0.1)
			return;// 能量小的光子直接扔掉
		if (p.outBrainBound())
			return; //// 出界的光子直接扔掉
		p.x += p.mx;
		p.y += p.my;
		p.z += p.mz;
		int rx = Math.round(p.x);
		int ry = Math.round(p.y);
		int rz = Math.round(p.z);
		if (Frog.outBrainBound(rx, ry, rz))
			return;// 出界直接扔掉
		Room room = f.getRoom(rx, ry, rz);
		if (room != null) {
			room.addPhoton(p);
		} else {
			p.energy *= .95;//
			photonWalk(f, p);// 递归，一直走下去，直到遇到room或出界
		}
	}

	/** Move photons and call cell's execute method */
	public void execute(Frog f, int activeNo, int x, int y, int z) {// 主运行方法，进行实际的脑细经元的行为和光子移动
		if (cells != null)
			for (Cell cell : cells) // cell会生成或处理掉所在room的光子
				CellActions.act(f, this, cell, x, y, z);
		// 剩下的，或由细胞新产生的光子，自已会走到下一格，光子自己是会走的，而且是直线传播
		if (photons != null)
			for (int i = 0; i < photons.length; i++) {
				Photon p = photons[i];
				if (p == null || p.activeNo == activeNo)// 同一轮的不再走了
					continue;
				p.activeNo = activeNo;
				removePhoton(i);// 原来的位置的先清除，设为null
				photonWalk(f, p); // 让光子自已往下走
			}
	}

	public float getActive() {// 获取room的激活度
		return active;
	}

	public void setActive(float active) {// 设room的激活度
		this.active = active;
	}

	public int getPhotonQty() {// 获取room里的总光子数
		return photonQty;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

}
