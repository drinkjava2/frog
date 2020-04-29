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

import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.BrainPicture;
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.util.RandomUtils;

/**
 * Line is a line from cell1 to cell2
 * 
 * @author Yong Zhu
 * @since 2020-04-18
 */
public class Line extends Organ {// Line代表一个从cell1到cell2的神经元连接,energy表示连接能量
	private static final long serialVersionUID = 1L;

	public int eng = 30;
	public int fat = 0;
	public int x1, y1, z1, x2, y2, z2;

	public Line(Cell c1, Cell c2) {
		this.x1 = c1.x;
		this.y1 = c1.y;
		this.z1 = c1.z;
		this.x2 = c2.x;
		this.y2 = c2.y;
		this.z2 = c2.z;
	}

	public void active(Frog f) {// 重写active方法,line的作用就是在细胞c1,c2间传送能量(即信息)
		if (!f.alive)
			return;
		if (RandomUtils.percent(5))
			eng = RandomUtils.varyInLimit(eng, 1, 70);// 传输的能量也参与进化
		Cell c1 = f.getCell1(this);
		if (c1 == null)
			return;
		Cell c2 = f.getCell2(this);
		if (c2 == null)
			return;
		if (c1.energy > eng) { // 为了保证能量(即熵)守恒，传出的能量要不大于输入能量
			fat++;
			c1.subEnergy(eng);
			c2.addEnergy(eng);
		}
	}

	@Override
	public Organ[] vary(Frog f) {
		if (fat <= 0)// 如果胖值为0，表示这个组的细胞没有用到，可以小概率丢掉它了
			if (RandomUtils.percent(30))
				return new Organ[] {};
		if (RandomUtils.percent(3)) // 有3%的几率丢掉它，防止这个器官数量只增不减
			return new Organ[] {};
		return new Organ[] { this };
	}

	public void drawOnBrainPicture(Frog f, BrainPicture pic) {
		pic.drawLine(this);
		pic.drawPoint(this.x2 + .5f, this.y2 + .5f, this.z2 + .5f, 5);
		pic.drawText((x1 + x2) / 2, (y1 + y2) / 2, (z1 + z2) / 2, "" + eng, 1);
	}
}
