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
import com.github.drinkjava2.frog.brain.Line;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.util.RandomUtils;

/**
 * If move cell active, frog will move
 * 
 * Move被分成上下左右四个独立的运动输出器官，这样方便编程
 * 
 * @author Yong Zhu
 */
public class Lines extends Organ {// Lines器官很重要，它是神经元之间的连线，由随机生成，由生存竟争淘汰
	private static final long serialVersionUID = 1L;
	private static final int LINE_QTY = 100;// 总共允许最多有多少根线条
	public Line[] lines = new Line[LINE_QTY];

	public Lines() {
		shape = null;
		organName = null;
		if (lines == null)
			lines = new Line[LINE_QTY];
	}

	public void init(Frog f, int orgNo) { // 在青蛙生成时会调用这个方法，进行一些初始化
		if (RandomUtils.percent(5f)) // 生成线
			addLine(f);
		if (RandomUtils.percent(5f)) // 丢弃线
			forgetLine(f);
	}

	/**
	 * 有两个任务：1.生成或丢弃线 2.用线在细胞间传输能量
	 */
	public void active(Frog f) {
		for (Line line : lines) {
			if (line == null)
				continue;
			if (line.c1.energy > 0) {
				line.c1.energy -= Math.abs(line.value);
				if (line.c1.energy < 0)
					line.c1.energy = 0;
				line.c2.energy += line.value;
				if (line.c2.energy < 0)
					line.c2.energy = 0;
				if (line.c2.energy > 100)
					line.c2.energy = 100;
			}
		}
	}

	public void addLine(Frog f) {// 随机生成线
		Cell c1 = RandomUtils.getRandomCell(f);
		if (c1 == null)
			return;
		Cell c2 = RandomUtils.getRandomCell(f);
		if (c2 == null)
			return;
		for (int i = 0; i < lines.length; i++) {// 找空位插入新的线
			if (lines[i] == null) {
				lines[i] = new Line(30, c1, c2);
				break;
			}
		}
		// 没找到? 随便找个位置顶替原来的线
		lines[RandomUtils.nextInt(LINE_QTY)] = new Line(30, c1, c2);
	}

	public void forgetLine(Frog f) {// 随机丢弃线
		lines[RandomUtils.nextInt(LINE_QTY)] = null;
	}

	/** Child class can override this method to drawing picture */
	public void drawOnBrainPicture(Frog f, BrainPicture pic) { // 把器官的轮廓显示在脑图上
		if (!Env.SHOW_FIRST_FROG_BRAIN || !f.alive) // 如果不允许画或青蛙死了，就直接返回
			return;
		for (Line line : lines) {
			if (line == null)
				continue;
			if (line.value > 0) // 正值用兰色，负值用红色表示
				pic.setPicColor(Color.BLUE);
			else
				pic.setPicColor(Color.RED);
			pic.drawLine(line.c1, line.c2);
		}
	}

}
