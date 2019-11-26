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

import com.github.drinkjava2.frog.util.ColorUtils;
import com.github.drinkjava2.frog.util.RandomUtils;

/**
 * Cell is the smallest unit of brain space, a Cell can have many actions and
 * photons and holes and relations
 * 
 * Cell是脑的最小空间单元，可以存在多个行为(由organs中的器官代号来表示)和光子(Photon)和洞（Hole)和关联(Relation)，脑是由frog中的cells三维数组组
 * 成，但不是每一维都初始化过。
 * 
 * Cell原则上只有数据，应该把所有方法移出去，放到具体的器官行为中，见CellActions类，但暂时图省事，在Cell里放一些常用方法
 * 
 * Jelly或MoveJelly器官有一种特殊行为，它可以产生动态触突效果，可以把具备动态触突的神经元比作一个果冻，光子来了,在上面撞了一个坑（hole)并损失能量，
 * 如果来的多，或者速度快(能量大)，一部分的光子就被从果冻的另一头撞出去了(光直线传播，寻找下一个神经元，增加信息存储单元，实现体全息存贮)。如果在另一个角度又
 * 来了新的光子，同样的过程在发生，只不过在撞击的过程中，以前被撞出的坑里有可能被撞出光子来，沿着撞击坑的路径直线逆向返回（即波的逆向成像，两个撞击事件,如果
 * 在短期内同时发生，或长期反复发生，就形成比较稳定的关联，这个与神经网络的hebb规则相符合，以后在这个基础上，看看能不能将深度学习的分层结构借签进来，用图形化
 * 表示，并有可能是无级分层的，层与层之前没有明显界限。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Cell {
	public int x;
	public int y;
	public int z;

	public Cell(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/** Active of current cell */
	public boolean hasInput = false; // 这个细胞是否有外界信号（如光、声音)输入

	public int[] organs = null; //// 每个Cell可以被多个Organ登记，这里保存organ在蛋里的序号

	public Photon[] photons = null;// 光子

	public Hole[] holes = null;// 洞（即动态突触），洞由光子产生，洞由时间抹平

	public Relation[] relations = null;// 洞的关联关系

	public int color;// Cell的颜色取最后一次被添加的光子的颜色，颜色不重要，但能方便观察

	public int photonQty = 0; // 这个细胞里当前包含的光子总数

	public int photonSum = 0; // 这个细胞里曾经收到的光子总数

	public void regOrgan(int orgNo) {// 每个Cell可以被多个Organ登记，通常只在青蛙初始化器官时调用这个方法
		if (organs == null)
			organs = new int[] {};
		organs = Arrays.copyOf(organs, organs.length + 1);
		organs[organs.length - 1] = orgNo;
	}

	public void addPhoton(Photon p) {// 每个cell可以存在多个光子
		if (p == null)
			return;
		photonQty++;
		photonSum++;
		color = p.color; // Cell的颜色取最后一次被添加的光子的颜色
		if (photons == null) {
			photons = new Photon[] { p };// 创建数组
			return;
		} else
			for (int i = 0; i < photons.length; i++) { // 找空位插入,尽量重复利用内存
				if (photons[i] == null) {
					photons[i] = p;
					return; // 如找到就插入，返回
				}
			}
		photons = Arrays.copyOf(photons, photons.length + 1);// 否则追加新光子到未尾
		photons[photons.length - 1] = p;
	}

	public void digHole(Photon p) {// 根据光子来把洞挖大一点，同时，如果有洞和它年龄相近，将把它们绑定起来，如果已有绑定的洞，有可能在绑定的洞里撞出光子来
		if (p == null || p.isBackway())// 反向的光子不参与挖坑
			return;
		if (holes == null) {// 如果没有坑，就新挖一个
			holes = new Hole[] { new Hole(p) }; // 新挖的坑不参与绑定
			return;
		}

		if(RandomUtils.percent(2))
		for (int i = 0; i < holes.length; i++) { // 这部分很关键，光子如果与坑同向或角度相近，会在与坑绑定的坑上撞出新的光子反向飞回，注意只针对绑定的坑
			Hole h = holes[i];
			if (h != null ) {
				float r = h.angleCompare(p);
				if(r<0.01) {
					if(RandomUtils.percent(100-r*100))
				createBackPhoton(h);
				}
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
				h.age = 0;
				break;
			}
		}

		if (found != null) { // 如果第二次扩洞，且光子和洞不是同一个器官产生的，这时可以把这个洞和其它洞关联起来了
			for (Hole hole : holes) {
				if (hole != found && found.organNo != hole.organNo && (Math.abs(found.age - hole.age) < 80)) {// TODO:不应用固定值
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
			Hole f = null;
			if (h.equals(r.h1))
				f = r.h2;// h2与h是一对绑定的
			else if (h.equals(r.h2))
				f = r.h1; // h1与h是一对绑定的
			if (f != null) {
				Photon back = new Photon(-1, ColorUtils.RED, f.x, f.y, f.z, -f.mx, -f.my, -f.mz, 90);// 生成反向的光子
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

	public void deleteAllPhotons() {
		photons = null;
		photonQty = 0;
	}

}
