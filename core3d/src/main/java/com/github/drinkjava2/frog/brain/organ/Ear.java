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
import com.github.drinkjava2.frog.brain.BrainPicture;
import com.github.drinkjava2.frog.brain.Cuboid;
import com.github.drinkjava2.frog.brain.Organ;

/**
 * Ear can accept letter information input
 * 耳朵可以接收外界的语音信号输入，开始阶段一个字母对应一个活跃区，以后会改成用声母+韵母或Unicode内码的编码来表示语音信号
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
@SuppressWarnings("all")
public class Ear extends Organ {// 耳朵也是长方体，我为什么要用也?
	private static final long serialVersionUID = 1L;
	public int n = 18; // 眼睛有n x n个感光细胞， 用随机试错算法自动变异(加1或减1，最小是3x3)
	public Cuboid a = new Cuboid(10, 10, Env.FROG_BRAIN_ZSIZE - 1, 3, 3, 1);
	public Cuboid b = new Cuboid(10, 15, Env.FROG_BRAIN_ZSIZE - 1, 3, 3, 1);
	public Cuboid c = new Cuboid(15, 10, Env.FROG_BRAIN_ZSIZE - 1, 3, 3, 1);
	public Cuboid d = new Cuboid(15, 15, Env.FROG_BRAIN_ZSIZE - 1, 3, 3, 1);

	public Ear() {
		this.shape = new Cuboid(10, 10, Env.FROG_BRAIN_ZSIZE - 1, 8, 8, 1);
		this.organName = "ear";
		this.allowVary = false;
		this.allowBorrow=false;
	}

	public void drawOnBrainPicture(Frog f, BrainPicture pic) {// 把自已这个器官在脑图上显示出来，子类可以重写这个方法
		super.drawOnBrainPicture(f, pic);
		pic.drawCuboid(a);// 显示abcd位置在脑图上
		pic.drawCuboid(b);
		pic.drawCuboid(c);
		pic.drawCuboid(d);
	}

	/** 给这个耳朵听到一个字母，激活它的听觉输入区, 注意听觉输入区并不等于脑内虚拟听觉成像区，但是第一版...先共用一个区吧 */
	public void hearSound(Frog f, String letter) {
		f.setCuboidVales(getCuboidByStr(letter), 20);
	}

	/** 给这个耳朵听到一个字母，激活它的听觉输入区, 注意听觉输入区并不等于听觉成像区 */
	public void hearNothing(Frog f) {
		f.setCuboidVales(a, 0);
		f.setCuboidVales(b, 0);
		f.setCuboidVales(c, 0);
		f.setCuboidVales(d, 0);
	}

	public Cuboid getCuboidByStr(String s) {
		if ("A".equalsIgnoreCase(s))
			return a;
		if ("B".equalsIgnoreCase(s))
			return b;
		if ("C".equalsIgnoreCase(s))
			return c;
		if ("D".equalsIgnoreCase(s))
			return d;
		return null;
	}

}
