/* Copyright 2018-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.github.drinkjava2.frog.objects;

import static com.github.drinkjava2.frog.Env.ENV_HEIGHT;
import static com.github.drinkjava2.frog.Env.ENV_WIDTH;
import static com.github.drinkjava2.frog.Env.FOOD_QTY;
import static com.github.drinkjava2.frog.Env.bricks;

import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.util.RandomUtils;

/**
 * Food randomly scatter on Env
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Food implements EnvObject {
	public static int food_ated = 0;

	@Override
	public void build() {
		food_ated = 0;
		if (!Env.FOOD_CAN_MOVE) {
			for (int i = 0; i < FOOD_QTY; i++) // 生成食物
				bricks[RandomUtils.nextInt(ENV_WIDTH)][RandomUtils.nextInt(ENV_HEIGHT)] = Material.FOOD;
			return;
		}
		for (int i = 0; i < FOOD_QTY / 4; i++) // 生成苍蝇1
			bricks[RandomUtils.nextInt(ENV_WIDTH)][RandomUtils.nextInt(ENV_HEIGHT)] = Material.FLY1;
		for (int i = 0; i < FOOD_QTY / 4; i++) // 生成苍蝇2
			bricks[RandomUtils.nextInt(ENV_WIDTH)][RandomUtils.nextInt(ENV_HEIGHT)] = Material.FLY2;
		for (int i = 0; i < FOOD_QTY / 4; i++) // 生成苍蝇3
			bricks[RandomUtils.nextInt(ENV_WIDTH)][RandomUtils.nextInt(ENV_HEIGHT)] = Material.FLY3;
		for (int i = 0; i < FOOD_QTY / 4; i++) // 生成苍蝇4
			bricks[RandomUtils.nextInt(ENV_WIDTH)][RandomUtils.nextInt(ENV_HEIGHT)] = Material.FLY4;
	}

	@Override
	public void destory() {
		for (int i = 0; i < ENV_WIDTH; i++) {// 清除食物
			for (int j = 0; j < ENV_HEIGHT; j++)
				if (bricks[i][j] >= Material.FOOD && bricks[i][j] <= Material.FLY4)
					bricks[i][j] = 0;
		}
	}

	@Override
	public void active(int screen) {
		if (RandomUtils.percent(96))
			return;
		for (int i = 1; i < ENV_WIDTH; i++) {// 水平移动FLY
			for (int j = 1; j < ENV_HEIGHT; j++) {
				if (bricks[i][j] == Material.FLY1) {
					bricks[i - 1][j] = Material.FLY1;
					bricks[i][j] = 0;
				}
				if (bricks[i][j] == Material.FLY2) {
					bricks[i][j - 1] = Material.FLY2;
					bricks[i][j] = 0;
				}
			}
		}

		for (int i = ENV_WIDTH - 2; i > 0; i--) {// 上下移动FLY
			for (int j = ENV_HEIGHT - 2; j > 0; j--) {
				if (bricks[i][j] == Material.FLY3) {
					bricks[i + 1][j] = Material.FLY3;
					bricks[i][j] = 0;
				}
				if (bricks[i][j] == Material.FLY4) {
					bricks[i][j + 1] = Material.FLY4;
					bricks[i][j] = 0;
				}
			}
		}

		for (int i = 0; i < ENV_WIDTH; i++) {
			bricks[i][0] = 0;
			bricks[i][ENV_HEIGHT - 1] = 0;
		}
		for (int i = 0; i < ENV_HEIGHT; i++) {
			bricks[0][i] = 0;
			bricks[ENV_WIDTH - 1][i] = 0;
		}
	}

}
