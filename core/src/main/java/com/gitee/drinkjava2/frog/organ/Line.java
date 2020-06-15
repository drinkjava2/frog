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
package com.gitee.drinkjava2.frog.organ;

import java.awt.Color;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.brain.BrainPicture;
import com.gitee.drinkjava2.frog.brain.Cell;
import com.gitee.drinkjava2.frog.brain.Organ;
import com.gitee.drinkjava2.frog.brain.Zone;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Line is a line between 2 organs or 2 lines
 * 
 * 这是一个随机方式连接两端的Organ,一个Organ对应一条线，它是从旧版的RandomConnectGroup改造过来，这是一种最简单的神经元排列方式和传导方式
 * 信号采用加、减方式，信号(能量强度)不会随时间遗失(熵守恒)。 Line的两头可以位于器官内的任一个位置，也可以位于另一个line的body上
 * 
 * 以后考虑添加以下不同类型的Line类器官：<br>
 * 1. 一个器官对应多个Line(神经元) 2.
 * 神经元的兴奋值与时间相关，比如随时间流逝按指数曲线递减、神经元之间的联系强度与重复次数相关，反映记忆典线规律 3.
 * 神经元有逻辑功能，对信号进行与、或、非转换
 * 
 * @author Yong Zhu
 * @since 1.0
 */

public class Line extends Organ {
	private static final long serialVersionUID = 1L;

	public Zone inputZone; // NOSONAR 输入触突区
	public Zone outputZone; // NOSONAR 输出触突区
	public Zone bodyZone; // NOSONAR 细胞本体所在位置，本体的作用是可以接收其它line的输出信号

	@Override
	public boolean allowBorrow() { // 是否允许在精子中将这个器官借出
		return true;
	}

	@Override
	public void initOrgan(Animal a) {
		if (!initilized) {
			initilized = true;
			inputZone = RandomUtils.randomZoneInOrgans(a);
			outputZone = RandomUtils.randomZoneInOrgans(a);
			bodyZone = new Zone(inputZone, outputZone);
		}
		this.fat = 0;// 每次fat清0，因为遗传下来的fat不为0
		Cell c = new Cell();
		c.input = inputZone;
		c.output = outputZone;
		c.body = bodyZone;
		c.organ = this;
		a.cells.add(c);
	}

	/** Line如果input是另一个line的body，吸收能量， Line如果output是另一个line的body,放出能量 */
	@Override
	public void active(Animal a, Cell c) {
		if (RandomUtils.percent(95)) // TODO: 待优化 这个会严重影响速度，所以降低它的机率
			return;
		for (Cell cell : a.cells) {
			if (cell.energy > organActiveEnergy && c.input.nearby(cell.body)) {
				c.organ.fat++;
				cell.energy -= organActiveEnergy;
				c.energy += organActiveEnergy;
			}
			if (c.energy >= organOutputEnergy && c.output.nearby(cell.body)) {
				c.energy -= organOutputEnergy;
				cell.energy += organOutputEnergy;
			}
		}
	}

	@Override
	public Organ[] vary() {
		organOutputEnergy = RandomUtils.varyInLimit(organOutputEnergy, -3, 3);
		if (fat <= 0 && RandomUtils.percent(30))// 如果胖值为0，表示这个组的细胞没有用到，可以小概率丢掉它了
			return new Organ[] {};
		if (RandomUtils.percent(3)) // 有3%的几率丢掉它，防止这个器官数量只增不减
			return new Organ[] {};
		return new Organ[] { this };
	}

	@Override
	public void drawOnBrainPicture(Animal a, BrainPicture pic) {// 把自已这个器官在脑图上显示出来
		if (fat <= 0)
			pic.setPicColor(Color.LIGHT_GRAY); // 没用到? 灰色
		else if (organOutputEnergy <= 0)
			pic.setPicColor(Color.BLUE);
		else
			pic.setPicColor(Color.red); // 用到了?红色
		pic.drawLine(inputZone, bodyZone);
		pic.drawLine(bodyZone, outputZone);
		pic.drawZone(bodyZone);
		pic.setPicColor(Color.red);
	}

}
