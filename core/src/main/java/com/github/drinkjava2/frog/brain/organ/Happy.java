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

import static com.github.drinkjava2.frog.Env.FROG_BRAIN_ZSIZE;

import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Cuboid;
import com.github.drinkjava2.frog.brain.Organ;

/**
 * Happy active after ate food
 */
public class Happy extends Organ { // 进食后，Happy器官会激活，痛苦和快感是条件反射形成的前题

	private static final long serialVersionUID = 1L;
	public float happy = 0; // happy初始值为0, 进食后将由eat器官增加happy值
	public int foundFood = 0;

	public Happy() {
		this.shape = new Cuboid(15, 13, FROG_BRAIN_ZSIZE / 2 + 3, 1, 1, 1);
	}

	@Override
	public void active(Frog f) {
		if (Env.foundAndAteFood(f.x, f.y)) {
			f.energy += 3000;
			//foundFood = 10;
		}
	}

	@Override
	public void cellAct(Frog f, Cell c) {
//		if ((foundFood--) > 0)
//			c.energy=100;
	}

}
