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
import static com.github.drinkjava2.frog.Env.bricks;

import com.github.drinkjava2.frog.Frog;

/**
 * Trap will kill all frogs inside of it, if frog's position has material and
 * it's not food, frog will die
 * 
 * @author Yong Zhu
 * @since 2019-08-05
 */
@SuppressWarnings("all")
public class Trap implements EnvObject {
	private static final int X1 = ENV_WIDTH / 2 - 350 / 2; // 陷阱左上角
	private static final int Y1 = ENV_HEIGHT / 2 - 20 / 2; // 陷阱左上角
	private static final int X2 = ENV_WIDTH / 2 + 350 / 2; // 陷阱右下角
	private static final int Y2 = ENV_HEIGHT / 2 + 20 / 2; // 陷阱右下角

	@Override
	public void build() {
		for (int x = X1; x <= X2; x++)
			for (int y = Y1; y <= Y2; y++)
				bricks[x][y] = Material.TRAP;
	}

	@Override
	public void destory() {
		for (int x = X1; x <= X2; x++)
			for (int y = Y1; y <= Y2; y++)
				bricks[x][y] = 0;
	}

	@Override
	public void active(int screen) {

	}

}
