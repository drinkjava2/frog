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

import java.awt.Color;

import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.BrainPicture;
import com.github.drinkjava2.frog.brain.Cuboid;

/**
 * FixedOrgan is fixed organ usually located on brain surface
 * 
 * 固定器官通常位于脑的表面，用来进行输入、输出动作，眼睛、移动等都属于固定器官，在项目前期，固定器官通常由手工来指定它的位置和尺寸
 * 固定只是相对的，固定器官在项目后期也会参与变异和淘汰，另外，固定器官与它是否在脑的表面没关系，也允许出现在脑内，例如耳朵也可以放在脑内感受声音
 * 信号，但通常感觉器官都放在脑的表面,比较符合生物的形态。输出器官如移动肌、动眼肌等也可以允许放在脑内，比较虚幻的是脑内视觉和听觉成像区是自动进化
 * 形成的，分散在网格节点中存储，现在还看不出眉目，但为了方便单元测试，先让听觉成像区和听力输入区共用一个区，否则连个测试基准都没有了,这点与真实的
 * 听力系统不一样，真实的生物脑，听力输入细胞是在表面，是单向传播的，脑内部听力成像区的激活不会逆向传递到听力输入细胞。
 * 
 * 固定器官继承于长方体，便于显示和编程
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class FixedOrgan extends Cuboid implements Organ {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean allowBorrow() { // 是否允许在精子中将这个器官借出
		return false;
	}

	@Override
	public void init(Frog f) { // 仅在Frog生成时这个方法会调用一次，缺省啥也不干，通常用于在这一步播种脑细胞
	}

	@Override
	public void active(Frog f) { // 每一步都会调用器官的active方法 ，缺省啥也不干
	}

	@Override
	public void drawOnBrainPicture(Frog f, BrainPicture pic) {// 把自已这个器官在脑图上显示出来，子类可以重写这个方法
		if (!Env.SHOW_FIRST_FROG_BRAIN)
			return;
		pic.setColor(Color.BLACK); // 缺省是黑色
		pic.drawCuboid(this);// 固定器官继承于长方体，所以可以直接调用drawCuboid方法显示在脑图上
	}

	@Override
	public FixedOrgan[] vary() { // 在下蛋时每个器官会调用这个方法，缺省返回一个类似自已的副本，子类通常要覆盖这个方法
		FixedOrgan newOrgan = null;
		try {
			newOrgan = this.getClass().newInstance();
		} catch (Exception e) {
			throw new UnknownError("Can not make new Organ copy for " + this);
		}
		copyXYZE(this, newOrgan);
		return new FixedOrgan[] { newOrgan };
	}

}
