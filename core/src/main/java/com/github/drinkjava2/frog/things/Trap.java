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
package com.github.drinkjava2.frog.things;

import static com.github.drinkjava2.frog.Env.*;

import com.github.drinkjava2.frog.Frog;

/**
 * Trap is
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Trap implements Thing {
	private static final int TRAP_WIDTH = 350; // 陷阱高, 0~200
	private static final int TRAP_HEIGHT = 20; // 陷阱宽, 0~200

	@Override
	public void build() {
		for (int x = ENV_WIDTH / 2 - TRAP_WIDTH / 2; x < ENV_WIDTH / 2 + TRAP_WIDTH / 2; x++)
			for (int y = ENV_HEIGHT / 2 - TRAP_HEIGHT / 2; y < ENV_HEIGHT / 2 + TRAP_HEIGHT / 2; y++)
				bricks[x][y] = BRICK_TYPE_TRAP;
	}

	@Override
	public void destory() {
		for (int x = ENV_WIDTH / 2 - TRAP_WIDTH / 2; x < ENV_WIDTH / 2 + TRAP_WIDTH / 2; x++)
			for (int y = ENV_HEIGHT / 2 - TRAP_HEIGHT / 2; y < ENV_HEIGHT / 2 + TRAP_HEIGHT / 2; y++)
				bricks[x][y] = 0;
	}

	@Override
	public void active() {

	}

	public static boolean inTrap(Frog f) {
		return f.x >= ENV_WIDTH / 2 - TRAP_WIDTH / 2 && f.x <= ENV_WIDTH / 2 + TRAP_WIDTH / 2
				&& f.y >= ENV_HEIGHT / 2 - TRAP_HEIGHT / 2 && f.y <= ENV_HEIGHT / 2 + TRAP_HEIGHT / 2;
	}

}
