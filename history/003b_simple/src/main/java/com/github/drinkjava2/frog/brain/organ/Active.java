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
import com.github.drinkjava2.frog.util.RandomUtils;

/**
 * Active always keep active
 * 
 * 这个器官永远激活
 */
public class Active extends Organ {// 以前的实验发现添加一个始终激活的区比用Hungry来驱动更能提高找食效率

	private static final long serialVersionUID = 1L;

	@Override
	public void initFrog(Frog f) {
		if (!initilized) {
			initilized = true;
			organOutputEnergy = 2f;
		}
	}

	@Override
	public void active(Frog f) {
		for (Cell cell : f.cells) {
			if (cell.energy > 0)
				cell.energy--;
			if (cell.energy < Cell.MAX_ENERGY_LIMIT)
				for (Input input : cell.inputs)
					if (input.nearby(this)) // if input zone near by happy zone
						cell.energy += organOutputEnergy;
		}
	}

}
