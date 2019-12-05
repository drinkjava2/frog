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
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Input;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.egg.Zone;
import com.github.drinkjava2.frog.env.Env;

/**
 * Eye is an organ can see environment, and active brain cells which inputs are
 * located in eye range
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Eye {

	private static boolean hasFood(int x, int y) {
		return x >= 0 && y >= 0 && x < Env.ENV_WIDTH && y < Env.ENV_HEIGHT && Env.foods[x][y];
	}

	/** First eye can only see if food nearby at 4 directions */
	public static void act(Frog f, Organ eye) {// 第一个眼睛只能观察上、下、左、右四个方向有没有食物 
		float qRadius = eye.radius / 4;
		float q3Radius = (float) (eye.radius * .75);
		Zone seeUp = new Zone(eye.x, eye.y + q3Radius, qRadius);
		Zone seeDown = new Zone(eye.x, eye.y - q3Radius, qRadius);
		Zone seeLeft = new Zone(eye.x - q3Radius, eye.y, qRadius);
		Zone seeRight = new Zone(eye.x + q3Radius, eye.y, qRadius);

		boolean seeFood = false;
		boolean foodAtUp = false;
		boolean foodAtDown = false;
		boolean foodAtLeft = false;
		boolean foodAtRight = false;

		int seeDist = 10;
		for (int i = 1; i < seeDist; i++)
			if (hasFood(f.x, f.y + i)) {
				seeFood = true;
				foodAtUp = true;
				break;
			}

		for (int i = 1; i < seeDist; i++)
			if (hasFood(f.x, f.y - i)) {
				seeFood = true;
				foodAtDown = true;
				break;
			}

		for (int i = 1; i < seeDist; i++)
			if (hasFood(f.x - i, f.y)) {
				seeFood = true;
				foodAtLeft = true;
				break;
			}

		for (int i = 1; i < seeDist; i++)
			if (hasFood(f.x + i, f.y)) {
				seeFood = true;
				foodAtRight = true;
				break;
			}

		if (seeFood)
			for (Cell cell : f.cells) {
				if (cell.energy < 100)
					for (Input input : cell.inputs) {
						if (input.nearby(eye)) {
							if (foodAtUp && input.nearby(seeUp)) {
								input.cell.energy += 30;
							}
							if (foodAtDown && input.nearby(seeDown)) {
								input.cell.energy += 30;
							}
							if (foodAtLeft && input.nearby(seeLeft)) {
								input.cell.energy += 30;
							}
							if (foodAtRight && input.nearby(seeRight)) {
								input.cell.energy += 30;
							}
						}
					}
			}
	}

}
