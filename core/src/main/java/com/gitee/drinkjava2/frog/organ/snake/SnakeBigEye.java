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
package com.gitee.drinkjava2.frog.organ.snake;

import java.awt.Color;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.brain.BrainPicture;
import com.gitee.drinkjava2.frog.brain.Cell;
import com.gitee.drinkjava2.frog.brain.Zone;
import com.gitee.drinkjava2.frog.organ.frog.FrogBigEye;

/**
 * SnakeBigEye is an organ can only see frog
 * 
 * 青蛙与蛇眼的区别是青蛙能看到所有，而蛇眼只能看到青蛙, 因为蛇在这个系统中是个配角, 地位只比NPC高一点点
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class SnakeBigEye extends FrogBigEye {// 这个新版的眼睛有nxn个感光细胞，可以看到青蛙周围nxn网络内有没有frog
	private static final long serialVersionUID = 1L;

	@Override
	public void drawOnBrainPicture(Animal a, BrainPicture pic) {// 把自已这个器官在脑图上显示出来
		if (!Env.SHOW_FIRST_ANIMAL_BRAIN)
			return;
		super.drawOnBrainPicture(a, pic);
		float r2 = r / n; // r2是每个感光细胞的半径
		float x0 = x - r;
		float y0 = y - r; // x0,y0是眼睛的左上角
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				Zone cell = new Zone(x0 + i * 2 * r2 + r2, y0 + j * 2 * r2 + r2, z, r2, h);
				if (Env.foundFrogOrOutEdge(a.x - n / 2 + i, a.y + n / 2 - j)) {
					Color old = pic.getPicColor();
					pic.setPicColor(Color.RED);
					pic.drawPoint(x0 + i * 2 * r2 + r2, y0 + j * 2 * r2 + r2, z, r2);
					pic.setPicColor(old);
				}
				pic.drawZone(cell);
			}
		}
	}

	@Override
	public void active(Animal a) {// 如果看到食物就激活对应脑区的所有输入触突
		float r2 = r / n; // r2是每个感光细胞的半径
		float x0 = x - r;
		float y0 = y - r; // x0,y0是眼睛的左上角
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (Env.foundFrogOrOutEdge(a.x - n / 2 + i, a.y - n / 2 + j)) {
					for (Cell cell : a.cells)
						if (cell.input.nearby(x0 + i * 2 * r2 + r2, y0 + j * 2 * r2 + r2, z, r2))
							cell.energy += organOutputEnergy;
				}
			}
		}
	}

}
