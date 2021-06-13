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
package com.gitee.drinkjava2.frog.objects;

import static com.gitee.drinkjava2.frog.Env.ENV_HEIGHT;
import static com.gitee.drinkjava2.frog.Env.ENV_WIDTH;
import static com.gitee.drinkjava2.frog.Env.FOOD_QTY;
import static com.gitee.drinkjava2.frog.Env.bricks;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Food randomly scatter on Env
 * 生成食物（静态食物或苍蝇，苍蝇如果Env中FOOD_CAN_MOVE=true,会向四个方向移动)
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Food implements EnvObject {

	@Override
	public void build() {
		if (!Env.FOOD_CAN_MOVE) {
			for (int i = 0; i < FOOD_QTY; i++) // 生成食物
				Env.setMaterial(RandomUtils.nextInt(ENV_WIDTH), RandomUtils.nextInt(ENV_HEIGHT), Material.FOOD);
			return;
		}
		for (int i = 0; i < FOOD_QTY / 4; i++) { // 生成苍蝇1，苍蝇是NPC，没有智能
			int x = RandomUtils.nextInt(ENV_WIDTH);
			int y = RandomUtils.nextInt(ENV_HEIGHT);
			Env.setMaterial(x, y, Material.FLY1);
		}
		for (int i = 0; i < FOOD_QTY / 4; i++) { // 生成苍蝇2
			int x = RandomUtils.nextInt(ENV_WIDTH);
			int y = RandomUtils.nextInt(ENV_HEIGHT);
			Env.setMaterial(x, y, Material.FLY2);
		}
		for (int i = 0; i < FOOD_QTY / 4; i++) { // 生成苍蝇3
			int x = RandomUtils.nextInt(ENV_WIDTH);
			int y = RandomUtils.nextInt(ENV_HEIGHT);
			Env.setMaterial(x, y, Material.FLY3);
		}
		for (int i = 0; i < FOOD_QTY / 4; i++) { // 生成苍蝇4
			int x = RandomUtils.nextInt(ENV_WIDTH);
			int y = RandomUtils.nextInt(ENV_HEIGHT);
			Env.setMaterial(x, y, Material.FLY4);
		}

	}

	@Override
	public void destory() {
		for (int i = 0; i < ENV_WIDTH; i++) {// 清除食物
			for (int j = 0; j < ENV_HEIGHT; j++)
				Env.clearMaterial(i, j, Material.ANY_FOOD);
		}
	}

	@Override
	public void active() {
		if (!Env.FOOD_CAN_MOVE)// 如果食物不能移动
			return;
		if (RandomUtils.percent(96))// 用机率来调整食物(苍蝇)的速度
			return;
		for (int i = 1; i < ENV_WIDTH; i++) {// 水平移动FLY
			for (int j = 1; j < ENV_HEIGHT; j++) {
				if (bricks[i][j] == Material.FLY1) {
					Env.setMaterial(i - 1, j, Material.FLY1);
					Env.clearMaterial(i, j, Material.FLY1);
				}
				if (bricks[i][j] == Material.FLY2) {
					Env.setMaterial(i, j - 1, Material.FLY2);
					Env.clearMaterial(i, j, Material.FLY2);
				}
			}
		}

		for (int i = ENV_WIDTH - 2; i > 0; i--) {// 上下移动FLY
			for (int j = ENV_HEIGHT - 2; j > 0; j--) {
				if (bricks[i][j] == Material.FLY3) {
					Env.setMaterial(i + 1, j, Material.FLY3);
					Env.clearMaterial(i, j, Material.FLY3);
				}
				if (bricks[i][j] == Material.FLY4) {
					Env.setMaterial(i, j + 1, Material.FLY4);
					Env.clearMaterial(i, j, Material.FLY4);
				}
			}
		}

		for (int i = 0; i < ENV_WIDTH; i++) {
			Env.clearMaterial(i, 0, Material.ANY_FOOD);
			Env.clearMaterial(i, ENV_HEIGHT - 1, Material.ANY_FOOD);
		}
		for (int i = 0; i < ENV_HEIGHT; i++) {
			Env.clearMaterial(0, i, Material.ANY_FOOD);
			Env.clearMaterial(ENV_WIDTH - 1, i, Material.ANY_FOOD);
		}
	}

	@Override
	public void display() {  
	}
}
