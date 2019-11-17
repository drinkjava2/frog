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

import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.util.ColorUtils;

/**
 * Cell is the smallest unit of brain space, a Cell can have many actions and
 * photons and holes and relations
 * 
 * Cell是脑的最小空间单元，可以存在多个行为(由organs中的器官代号来表示)和光子(Photon)和洞（Hole)和关联(Relation)，脑是由frog中的cells三维数组组
 * 成，但不是每一维都初始化过。
 * 
 * 可以把具备动态触突的神经元比作一个果冻，光子来了,在上面撞了一个坑（hole)并损失能量，如果来的多，或者速度快(能量大)，一部分的光子就被
 * 从果冻的另一头撞出去了(光直线传播，寻找下一个神经元，增加信息存储单元，实现体全息存贮)。如果在另一个角度又来了新的光子，同样的过程在发生，只不过在撞击的
 * 过程中，以前被撞出的坑里有可能被撞出光子来，沿着撞击坑的路径直线逆向返回（即波的逆向成像，两个撞击事件,如果在短期内同时发生，或长期反复发生，就形成比较稳
 * 定的关联
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

	public Relation[] relations = null;// 洞的关联关系

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
		if (p.organNo == 0 && p.x < 3)
			return;// 这是个临时限制，防止反向的光子落到视网膜上
		if (p.organNo == 0 && p.z > Env.FROG_BRAIN_ZSIZE - 2)
			return;// 这是个临时限制，防止反向的光子落到耳朵上

		energy += 1000;
		if (energy > 10000)
			energy = 10000;
		// //p.energy *= .7;
		// if (p.energy < 0.1)
		// return;
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

	public void digHole(Photon p) {// 根据光子来把洞挖大一点，同时，如果有洞和它年龄相近，将把它们绑定起来，如果已有绑定的洞，有可能在撞出的洞里撞出光子来
		if (p == null || p.organNo == 0)// 反向的光子不参与挖坑
			return;
		if (holes == null) {// 如果没有坑，就新挖一个
			holes = new Hole[] { new Hole(p) }; // 新挖的坑不参与绑定
			return;
		}

		if (energy > 90)
			for (int i = 0; i < holes.length; i++) { // 这部分很关键，光子如果与坑同向或角度相近，会在与坑绑定的坑上撞出新的光子，注意只针对绑定的坑
				Hole h = holes[i];
				if (h != null && h.ifSimilarWay(p)) {
					createBackPhoton(h);
				}
			}

		Hole found = null;
		for (int i = 0; i < holes.length; i++) { // 先看看已存在的洞是不是与光子同向，是的话就把洞挖大一点
			Hole h = holes[i];
			if (h != null && h.ifSameWay(p)) { // 找到了与入射光子同向的洞,实际上就是同一个波源发来的
				found = h;
				h.size *= 1.2f;
				if (h.size > 10000)
					h.size = 10000;
				break;
			}
		}

		if (found != null) { // 如果第二次扩洞，且光子和洞不是同一个器官产生的，这时可以把这个洞和其它洞关联起来了
			for (Hole hole : holes) {
				if (hole != found && found.organNo != hole.organNo && (Math.abs(found.age - hole.age) < 9)) {// TODO:不应用固定值
					bind(found, hole);
				}
			}
		}

		if (found == null) {// 如果还没有找到旧坑，只好挖一个新坑到未尾
			holes = Arrays.copyOf(holes, holes.length + 1);
			holes[holes.length - 1] = new Hole(p);
		}
	}

	private void createBackPhoton(Hole h) { // 根据给定的洞，把所有与它绑定的洞上撞出光子来
		if (relations == null)
			return;
		for (Relation r : relations) {
			if (energy < 90)
				return;
			Hole f = null;
			if (h.equals(r.h1))
				f = r.h2;// h2与h是一对绑定的
			else if (h.equals(r.h2))
				f = r.h1; // h1与h是一对绑定的
			if (f != null) {
				Photon back = new Photon(0, ColorUtils.RED, f.x, f.y, f.z, -f.mx, -f.my, -f.mz, 90);// 生成反向的光子
				addPhoton(back);
				// energy -= 90;
			}
		}
	}

	public void bind(Hole a, Hole b) {// 将两个坑绑定，以后只要有一个坑激活，另一个坑也会产生出光子
		if (relations == null) {
			relations = new Relation[] { new Relation(a, b) };
			return;
		}
		for (Relation r : relations) {// 先看看是不是已绑过,绑过就把强度乘1.5
			if ((r.h1 == a && r.h2 == b) || (r.h2 == a && r.h1 == b)) {
				r.strength *= 1.5;
				if (r.strength > 100000) // TODO: strength要有遗忘机制
					r.strength = 100000;
				return;
			}
		}
		relations = Arrays.copyOf(relations, relations.length + 1);
		relations[relations.length - 1] = new Relation(a, b);
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
