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

import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Food randomly scatter on Env
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Food implements EnvObject {

	@Override
	public void build() {
		for (int i = 0; i < FOOD_QTY; i++) // 生成食物
			bricks[RandomUtils.nextInt(ENV_WIDTH)][RandomUtils.nextInt(ENV_HEIGHT)] = Material.FOOD;
	}

	@Override
	public void destory() {
		for (int i = 0; i < ENV_WIDTH; i++) {// 清除食物
			for (int j = 0; j < ENV_HEIGHT; j++)
				if (bricks[i][j] == Material.FOOD)
					bricks[i][j] = 0;
		}
	}

	@Override
	public void active(int screen) {
		// Food do not have any active
	}

}
