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
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Input;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.brain.Output;
import com.github.drinkjava2.frog.brain.Zone;
import com.github.drinkjava2.frog.util.RandomUtils;

/**
 * Line
 * 
 * 这是一个随机方式连接两端的Organ，它是从旧版的RandomConnectGroup改造过来，这是一种最简单的神经元排列方式，
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Line extends Organ { 
	private static final long serialVersionUID = 1L;

	public Zone inputZone; // 输入触突区
	public Zone outputZone; // 输出触突区

	@Override
	public boolean allowBorrow() { // 是否允许在精子中将这个器官借出
		return true;
	}

	@Override
	public void initFrog(Frog f) {
		if (!initilized) {
			initilized = true;
			// organWasteEnergy=.05f;
			x = Env.FROG_BRAIN_WIDTH / 2;
			y = Env.FROG_BRAIN_WIDTH / 2;
			r = Env.FROG_BRAIN_WIDTH / 2;
			inputZone = RandomUtils.randomPosInAnyFrogOrgan(f);
			outputZone = RandomUtils.randomPosInAnyFrogOrgan(f);
		}

		this.fat = 0;// 每次fat清0，因为遗传下来的fat不为0

		Cell c = new Cell();
		Input in = new Input(inputZone);
		in.cell = c;
		c.input = in;

		Output out = new Output(outputZone);
		out.cell = c;
		c.output = out;	

		c.organ = this;
		f.cells.add(c);
	}

	@Override
	public Organ[] vary() {
//		organOutputEnergy = RandomUtils.varyInLimit(organOutputEnergy, -3, 3);
		if (fat <= 0)// 如果胖值为0，表示这个组的细胞没有用到，可以小概率丢掉它了
			if (RandomUtils.percent(30))
				return new Organ[] {};
		if (RandomUtils.percent(3)) // 有3%的几率丢掉它，防止这个器官数量只增不减
			return new Organ[] {};
		return new Organ[] { this };
	}

	@Override
	public void drawOnBrainPicture(Frog f, BrainPicture pic) {// 把自已这个器官在脑图上显示出来
		if (fat <= 0)
			pic.setColor(Color.LIGHT_GRAY); // 没用到? 灰色
		else if (organOutputEnergy <= 0)
			pic.setColor(Color.BLUE);
		else
			pic.setColor(Color.red); // 用到了?红色
		pic.drawLine(inputZone, outputZone);
		pic.drawZone(this);
		pic.drawZone(inputZone);
		pic.fillZone(outputZone);
		pic.setColor(Color.red);

		pic.drawCircle(outputZone); // 如果胖了，表示激活过了，下次下蛋少不了这一组
	}

}
