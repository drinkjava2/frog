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
import static com.gitee.drinkjava2.frog.Env.bricks;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * This is a seesaw to train frog's balance
 * 
 * @author Yong Zhu
 * @since 2.0.1
 */
public class SeeSaw implements EnvObject {
	private static final int LEGNTH = 300;
	private static final int CENTER_X = Env.ENV_WIDTH / 2;
	private static final int CENTER_Y = Env.ENV_HEIGHT / 2;

	private double angle = 0;// -PI/4 to PI/4
	private double leftWeight = 0;
	private double rightWeight = 0;

	@Override
	public void build() {
		angle = 0;
	}

	@Override
	public void destory() {
		// do nothing
	}

	@Override
	public void active(int screen) {
		for (int i = 0; i < ENV_WIDTH; i++) {// 清除食物
			for (int j = 0; j < ENV_HEIGHT; j++)
				if (bricks[i][j] == Material.SEESAW)
					bricks[i][j] = 0;
		}

		if (RandomUtils.percent(2))
			leftWeight = RandomUtils.nextFloat() * 3;
		if (RandomUtils.percent(2))
			rightWeight = RandomUtils.nextFloat() * 3;
		Frog f = Env.frogs.get(screen);

		if (f.x < (CENTER_X - LEGNTH / 2) || f.x > (CENTER_X + LEGNTH / 2))
			f.energy -= 100000; // 如果走出跷跷板外则扣分，出局
		double left = leftWeight - (f.x - CENTER_X);
		double right = rightWeight + (f.x - CENTER_X);
		// right - left need in -100 to +100
		angle = angle + (right - left) * Math.PI * .000001;
		if (angle > Math.PI / 6) {
			angle = Math.PI / 6;
			f.energy -= 200;
		}
		if (angle < -Math.PI / 6) {
			angle = -Math.PI / 6;
			f.energy -= 200;
		}
		f.y = CENTER_Y + (int) Math.round((f.x - CENTER_X) * Math.tan(angle));
		f.energy -= Math.abs(angle) * 180; // 角度越大，扣分越多
		int x;
		int y;
		for (int l = -LEGNTH / 2; l <= LEGNTH / 2; l++) {
			x = (int) Math.round(l * Math.cos(angle));
			y = (int) Math.round(l * Math.sin(angle));
			Env.bricks[CENTER_X + x][CENTER_Y + y] = Material.SEESAW;
		}

		// 画底座
		for (int i = 1; i < 10; i++) {
			Env.bricks[CENTER_X - i][CENTER_Y + i] = Material.SEESAW_BASE;
			Env.bricks[CENTER_X + i][CENTER_Y + i] = Material.SEESAW_BASE;
		}
		for (int i = -10; i < 10; i++)
			Env.bricks[CENTER_X + i][CENTER_Y + 10] = Material.SEESAW_BASE;

	}
}
