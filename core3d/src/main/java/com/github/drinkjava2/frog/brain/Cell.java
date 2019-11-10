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
 * Cell is the smallest unit of brain space, a Cell can have many actions and
 * photons
 * 
 * Cell是脑的最小空间单元，可以存在多个行为(Action)和光子(Photon)，脑是由frog中的cells三维数组组成，但不是每一维都初始化过
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Cell {
	/** Active of current cell */
	private float active = 0; // 这个cell的激活程度，允许是负值,它反映了在这个cell里所有光子的能量汇总值

	public int[] organs = null; //// 每个Cell可以被多个Organ登记，这里保存organ在蛋里的序号

	public Photon[] photons = null;// 光子

	private int color;// Cell的颜色取最后一次被添加的光子的颜色，颜色不重要，但能方便观察

	private int photonQty = 0;

	public void regOrgan(int action) {// 每个Cell可以被多个Organ登记，通常只在青蛙初始化器官时调用这个方法
		if (organs == null)
			organs = new int[] {};
		organs = Arrays.copyOf(organs, organs.length + 1);
		organs[organs.length - 1] = action;
	}

	public void addPhoton(Photon p) {// 每个cell空间可以存在多个光子
		if (p == null)
			return;
		p.energy *= .78;
		if (p.energy < 0.1)
			return;
		photonQty++;
		color = p.color; // Cell的颜色取最后一次被添加的光子的颜色
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
		Cell cell = f.getCell(rx, ry, rz);
		if (cell != null) {
			cell.addPhoton(p);
		} else {
			// p.energy *= .6;// 真空中也要乘一个衰减系数，防止它走太远，占用计算资源
			photonWalk(f, p);// 递归，一直走下去，直到遇到cell或出界
		}
	}

	public float getActive() {// 获取cell的激活度
		return active;
	}

	public void setActive(float active) {// 设cell的激活度
		this.active = active;
	}

	public int getPhotonQty() {// 获取cell里的总光子数
		return photonQty;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

}
