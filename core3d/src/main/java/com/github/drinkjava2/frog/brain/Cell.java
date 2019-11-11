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
import com.github.drinkjava2.frog.util.ColorUtils;

/**
 * Cell is the smallest unit of brain space, a Cell can have many actions and
 * photons
 * 
 * Cell是脑的最小空间单元，可以存在多个行为(由organs中的器官代号来表示)和光子(Photon)，脑是由frog中的cells三维数组组成，但不是每一维都初始化过
 * 
 * 可以把具备动态触突的神经元比作一个果冻，光子来了,在上面撞了一个坑（hole)并损失能量，如果来的多，或者速度快(能量大)，一部分的光子就被
 * 从果冻的另一头撞出去了(光直线传播，寻找下一个神经元，增加信息存储单元，实现体全息存贮)。如果在另一个角度又来了新的光子，同样的过程在发生，只不过在撞击的
 * 过程中，以前被撞出的坑里有可能被撞出光子来，沿着撞击坑的路径直线逆向返回（即波的逆向成像，两个撞击事件，在神经元级别就被关联起来，关联的相关度取决于它们
 * 撞击坑的大小)，原来的坑越多大，则被撞出来的机率就越大(短期发生的事最先被回忆出来)，随时间流逝，撞击坑也会自动回复(动态触突消失，脑神经又可以接收新的信
 * 息了)。如果反复地有光子撞击在同一个坑，这个坑就会变大，这个修复的过程会变慢，坑变大了后果就是，即使只有轻微的撞击，也可以很容易将大坑里的光子撞出来（即记忆
 * 曲线，复习的效果优于单次长时间学习)。在某些位置，撞击太频繁、太强烈,不是在果冻上撞出坑，而是撞出一条通路，光子可以微损耗地通过，就形成了一个个传导通
 * 道(神经纤维)，直到光子被某个果冻拦截为止。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Cell {
	/** Active of current cell */
	private float energy = 0; // 这个cell的激活能量，允许是负值（但暂时只用到正值),它反映了在这个cell里所有光子的能量汇总值

	public int[] organs = null; //// 每个Cell可以被多个Organ登记，这里保存organ在蛋里的序号

	public Photon[] photons = null;// 光子

	public Hole[] holes = null;// 洞（即动态突触），洞由光子产生，洞由时间抹平

	private int color;// Cell的颜色取最后一次被添加的光子的颜色，颜色不重要，但能方便观察

	private int photonQty = 0;

	public void regOrgan(int action) {// 每个Cell可以被多个Organ登记，通常只在青蛙初始化器官时调用这个方法
		if (organs == null)
			organs = new int[] {};
		organs = Arrays.copyOf(organs, organs.length + 1);
		organs[organs.length - 1] = action;
	}

	public void addPhoton(Photon p) {// 每个cell可以存在多个光子
		if (p == null)
			return;
		if (p.goBack && p.x < 3)
			return;// 这是个临时限制，防止反向的光子落到视网膜上
		energy += p.energy * .3;
		if (energy > 100)
			energy = 100;
		p.energy *= .7;
		if (p.energy < 0.1)
			return;
		photonQty++;
		color = p.color; // Cell的颜色取最后一次被添加的光子的颜色
		if (photons == null) {
			photons = new Photon[] { p };// 创建数组
			digHole(p);
			return;
		} else
			for (int i = 0; i < photons.length; i++) { // 找空位插入,尽量重复利用内存
				if (photons[i] == null || photons[i].energy < 0.1) {
					photons[i] = p;
					digHole(p);
					return; // 如找到就插入，返回
				}
			}
		photons = Arrays.copyOf(photons, photons.length + 1);// 否则追加新光子到未尾
		photons[photons.length - 1] = p;
		digHole(p);
	}

	public void digHole(Photon p) {// 根据光子来把洞挖大一点，在挖洞的同时，有可能把别的洞中撞出光子来，反向的洞撞出光子的机率更大，有点象碰球的动量守恒
		if (p == null || p.goBack)
			return;
		if (holes == null) {// 如果没有坑，就新挖一个
			holes = new Hole[] { new Hole(p) };
			return;
		}

		for (int i = 0; i < holes.length; i++) { // 这部分很关键，光子会在不同向的坑上撞出新的光子
			Hole h = holes[i];
			if (h != null) {
				float angle = h.angleCompare(p);
				if (angle > .9f && energy > 90) {
					Photon back = new Photon(ColorUtils.RED, h.x, h.y, h.z, -h.mx, -h.my, -h.mz, 90);
					back.goBack = true;
					addPhoton(back);
					energy -= 90;
				}
			}
		}

		// 否则不光要扩坑或新挖坑，还要利用这个光子在旧坑里撞出新的光子来
		boolean foundHole = false;
		for (int i = 0; i < holes.length; i++) { // 先看看已存在的洞是不是与光子同向，是的话就把洞挖大一点
			Hole h = holes[i];
			if (h != null && h.ifSameWay(p)) {
				foundHole = true;
				h.size *= 1.2f;
				if (h.size > 10000)
					h.size = 10000;
			}
		}
		if (!foundHole)// 如果没找到现在的坑，就在旧坑快平复的地方就地重新开一个洞，以重复利用内存
			for (int i = 0; i < holes.length; i++) {
				Hole h = holes[i];
				if (h == null || h.size < 1) {
					foundHole = true;
					holes[i] = new Hole(p);
					return;
				}
			}
		if (!foundHole) {// 如果还没有找到旧坑，只好挖一个新坑到未尾
			holes = Arrays.copyOf(holes, holes.length + 1);
			holes[holes.length - 1] = new Hole(p);
		}

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

	public float getEnergy() {// 获取cell的能量
		return energy;
	}

	public void setEnergy(float energy) {// 设cell的能量
		this.energy = energy;
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
