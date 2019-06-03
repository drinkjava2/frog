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
import com.github.drinkjava2.frog.brain.Organ;

/**
 * Move up frog 1 unit if outputs of nerve cells active in this zone
 */
public class Eat extends Organ {
	private static final long serialVersionUID = 1L;

	@Override
	public void active(Frog f) {
		int x = f.x;
		int y = f.y;
		if (x < 0 || x >= Env.ENV_WIDTH || y < 0 || y >= Env.ENV_HEIGHT) {// 越界者死！
			f.alive = false;
			return;
		}

		if (Env.foods[x][y]) {
			Env.foods[x][y] = false;
			f.energy = f.energy + 1000;// 吃掉food，能量境加
		}
	}

}
