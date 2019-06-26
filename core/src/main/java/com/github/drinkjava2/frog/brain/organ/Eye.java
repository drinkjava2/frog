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
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Input;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.brain.Zone;

/**
 * Eye is an organ can see environment, and active brain cells which inputs are
 * located in eye range
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Eye extends Organ {// Eye类需要重构，目前只有4个感光细胞，不够，Eye要改成Group的子类，Eye只负责感光细胞的排列，感光细胞自已负责变异
	private static final long serialVersionUID = 1L;

	@Override
	public void initFrog(Frog f) { // 仅在Frog生成时这个方法会调用一次，缺省啥也不干，通常用于Organ类的初始化
		if (!initilized) {
			initilized = true;
			organOutputEnergy = 30;
		}
	}

	public void drawOnBrainPicture(BrainPicture pic) {// 把自已这个器官在脑图上显示出来
		super.drawOnBrainPicture(pic);
		float qRadius = r / 4;
		float q3Radius = (float) (r * .75);
		Zone seeUp = new Zone(x, y + q3Radius, qRadius);
		Zone seeDown = new Zone(x, y - q3Radius, qRadius);
		Zone seeLeft = new Zone(x - q3Radius, y, qRadius);
		Zone seeRight = new Zone(x + q3Radius, y, qRadius);
		pic.drawZone(pic.getGraphics(), seeUp);
		pic.drawZone(pic.getGraphics(), seeDown);
		pic.drawZone(pic.getGraphics(), seeLeft);
		pic.drawZone(pic.getGraphics(), seeRight);
	}

	@Override
	public Organ[] vary() {
		return new Organ[] { this };
	}

	@Override
	public void active(Frog f) {
		// 第一个眼睛只能观察上、下、左、右四个方向有没有食物
		float qRadius = r / 4;
		float q3Radius = (float) (r * .75);
		Zone seeUp = new Zone(x, y + q3Radius, qRadius);
		Zone seeDown = new Zone(x, y - q3Radius, qRadius);
		Zone seeLeft = new Zone(x - q3Radius, y, qRadius);
		Zone seeRight = new Zone(x + q3Radius, y, qRadius);

		boolean seeFood = false;
		boolean foodAtUp = false;
		boolean foodAtDown = false;
		boolean foodAtLeft = false;
		boolean foodAtRight = false;

		int seeDist = 10;
		for (int i = 1; i < seeDist; i++)
			if (Env.foundFood(f.x, f.y + i)) {
				seeFood = true;
				foodAtUp = true;
				break;
			}

		for (int i = 1; i < seeDist; i++)
			if (Env.foundFood(f.x, f.y - i)) {
				seeFood = true;
				foodAtDown = true;
				break;
			}

		for (int i = 1; i < seeDist; i++)
			if (Env.foundFood(f.x - i, f.y)) {
				seeFood = true;
				foodAtLeft = true;
				break;
			}

		for (int i = 1; i < seeDist; i++)
			if (Env.foundFood(f.x + i, f.y)) {
				seeFood = true;
				foodAtRight = true;
				break;
			}

		if (seeFood)
			for (Cell cell : f.cells) {
				if (cell.energy < 100)
					for (Input input : cell.inputs) {
						if (input.nearby(this)) {
							if (foodAtUp && input.nearby(seeUp)) {
								input.cell.energy += organOutputEnergy;
							}
							if (foodAtDown && input.nearby(seeDown)) {
								input.cell.energy += organOutputEnergy;
							}
							if (foodAtLeft && input.nearby(seeLeft)) {
								input.cell.energy += organOutputEnergy;
							}
							if (foodAtRight && input.nearby(seeRight)) {
								input.cell.energy += organOutputEnergy;
							}
						}
					}
			}

	}

}
