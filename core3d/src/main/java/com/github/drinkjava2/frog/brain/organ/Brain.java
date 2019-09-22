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
import com.github.drinkjava2.frog.brain.Cube;
import com.github.drinkjava2.frog.brain.Organ;

/**
 * Brain one used to define the brain position and size
 * 
 * 脑的形状是一个扁平的长立方体。
 * 脑的内部分布着各种各样的神经元，每个神经元的位置位于青蛙的cells3维数组里，神经元的撒布和对信息的处理特性是由其它器官来决定，器官的数量、位置、大小、
 * 对信息的处理策略参数是随机生成、变异的，由生存竟争来淘汰不合理的器官
 * 
 * 分布在表面的神经元，称为外表的感觉和输出细胞，同时，脑的内部也有一些不可见的输入、输出器官
 * 脑可以处理信息，每个青蛙脑被分成许多小Cube空间，每个cube里可以存在0到多个神经元，也可以存在0到多个信息元(即光子)，
 * 信息元(Photon)是有方向和强度的矢量。 光子的产生是由神经元（如感觉神经元或拆分神经元)产生，光子的消灭是由神经元（例如运动神经元)吸收。
 * 光子是有强度的，最普通的信息处理策略是每经过一个神经元就削弱它的强度，用抽取它代表的能量来增肥神经元，这就模拟了波的传导。当多个不同方向的光子
 * 同时到达一个神经元时，神经元被多个信号同时增强，形成驻点。有些神经元的策略是如果驻点太肥就被穿透，存放不了这么多能量，光子传向下一个cell寻求处理。
 * 所有驻点随时间减肥，以倒指数形式直到归0,这是信息的遗忘。 信息不是由一两个神经元存贮，而是由无数的驻点来存贮。 光子可以是负能量值。
 * 
 * @author Yong Zhu
 */
public class Brain extends Organ {
	private static final long serialVersionUID = 1L;

	public Brain() {
		x = 0;
		y = 0;
		z = 0;
		xe = Env.FROG_BRAIN_XSIZE;
		ye = Env.FROG_BRAIN_YSIZE;
		ze = Env.FROG_BRAIN_ZSIZE;
	}

	@Override
	public void init(Frog f) {
		f.cubes = new Cube[Env.FROG_BRAIN_XSIZE][Env.FROG_BRAIN_YSIZE][Env.FROG_BRAIN_ZSIZE];
	}

}
