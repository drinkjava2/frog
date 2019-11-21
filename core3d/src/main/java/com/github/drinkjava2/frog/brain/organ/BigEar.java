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
import com.github.drinkjava2.frog.util.ColorUtils;

/**
 * BigEar can accept sound input
 * 
 * 大耳朵的信号输入区有10个声母区和10个韵母区，相当于最多可以输入10x10=100个不同发音的字
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
@SuppressWarnings("all")
public class BigEar extends Organ {// 大耳朵位于脑的顶上，也是长方体
	private static final long serialVersionUID = 1L;

	public BigEar() {
		this.shape = new Cuboid(12, 10, Env.FROG_BRAIN_ZSIZE - 1, 8, 8, 1);// 手工固定大耳区的大小
		this.type = Organ.EAR;
		this.organName = "ear";
		this.allowVary = false;// 不允许变异
		this.allowBorrow = false;// 不允许借出
		this.color = ColorUtils.BLUE;
	}

	public void hearSound(Frog f, String letter) {

	}

	/** 给这个耳朵听到一个字母，激活它的听觉输入区, 注意听觉输入区并不等于听觉成像区 */
	public void hearNothing(Frog f) {
		f.setCuboidVales((Cuboid) shape, 0);
	}

}
