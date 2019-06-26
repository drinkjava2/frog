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

/**
 * Happy zone active after ate food
 */
public class Happy extends Organ { // Happy器官是进食后的产生的快感，痛苦和快感是条件反射形成的前题
	private static final long serialVersionUID = 1L;
	public float happy = 0; // happy初始值为0, 进食后将由eat器官增加happy值

	@Override
	public void active(Frog f) {
		if (happy > 0) {
			happy--;
			for (Cell cell : f.cells) {
				if (cell.energy > 0)
					cell.energy--;
				if (cell.energy < Cell.MAX_ENERGY_LIMIT)
					for (Input input : cell.inputs)
						if (input.nearby(this)) // if input zone near by happy zone
							cell.energy += happy / 10; // 所有的硬编码都是bug，包括这个2和10
			}
		}
	}

}
