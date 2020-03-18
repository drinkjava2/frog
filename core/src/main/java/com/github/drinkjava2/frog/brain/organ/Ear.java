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
package com.github.drinkjava2.frog.brain.organ;

import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Cuboid;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.brain.Photon;
import com.github.drinkjava2.frog.util.ColorUtils;
import com.github.drinkjava2.frog.util.RandomUtils;

/**
 * Ear can accept sound input
 * 
 * 耳朵的信号输入区能输入多少个信号取决于它的长度，它的每个输入点都与视觉视号正交
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
@SuppressWarnings("all")
public class Ear extends Organ {// 耳朵位于脑的顶上，也是长方体
	private static final long serialVersionUID = 1L;

	public Ear() {
		this.shape = new Cuboid(15, 5, Env.FROG_BRAIN_ZSIZE - 1, 1, 10, 1);// 手工固定耳区的大小 
		this.organName = "Ear";
		this.allowVary = false;// 不允许变异
		this.allowBorrow = false;// 不允许借出
		this.color = ColorUtils.BLUE;
	}

	public void cellAct(Frog frog, Cell c, int activeNo) {
		if (c.hasInput && RandomUtils.percent(40)) {// 随机数的作用是减少光子数，加快速度
			for (float xx = -0.3f; xx <= 0.3f; xx += 0.15) {// 形成一个扇面向下发送
				for (float yy = -1f; yy <= 1f; yy += 0.06) {
					Photon p = new Photon(organNo, this.color, c.x, c.y, c.z, xx, yy, -1);
					p.activeNo = activeNo;
					frog.addAndWalk(p);// 光子不是直接添加，而是走一格后添加在相邻的细胞上
				}
			}
		}
	}

	public void hearSound(Frog f, int code) {
		Cuboid c = (Cuboid) this.shape;
		f.getOrCreateCell(c.x, c.y + code, c.z).hasInput = true;
	}

	public int readcode(Frog f) {// 找出收取光子数最多的点
		int temp = -10000;
		int yPos = -1;
		Cuboid c = (Cuboid) this.shape;
		System.out.print("Ear received photons qty: ");
		for (int y = 0; y < 10; y++) {
			int sum = f.getOrCreateCell(c.x, c.y + y, c.z).photonSum;
			System.out.print(sum + ",");
			if (sum > temp) {
				yPos = y;
				temp = sum;
			}
		}
		System.out.println();
		return yPos;
	}

	/** 给这个耳朵听到一个字母，激活它的听觉输入区, 注意听觉输入区并不等于听觉成像区 */
	public void hearNothing(Frog f) {
		f.setCuboidVales((Cuboid) shape, false);
	}

}
