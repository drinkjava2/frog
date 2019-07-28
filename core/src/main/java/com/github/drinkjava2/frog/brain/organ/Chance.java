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
 * Chance is a random number generator
 * 
 * 这个器官是一个随机数发生器，用来打乱青蛙的思维，防止它围着一个食物打转出不来
 */
public class Chance extends Organ { // 至于这个器官能不能被选中，是另外一回事，听天由命了
	private static final long serialVersionUID = 1L;
	public int percent; // 初始化的机率为5%

	@Override
	public void initFrog(Frog f) {
		if (!initilized) {
			initilized = true;
			percent = 5;
		}
	}

	@Override
	public Organ[] vary() {
		if (RandomUtils.percent(5)) {
			percent = percent + 1 - 2 * RandomUtils.nextInt(2);
			if (percent < 1)
				percent = 1;
			if (percent > 98)
				percent = 98;
		}
		return new Organ[] { this };
	}

	@Override
	public void active(Frog f) {
		if (RandomUtils.percent(percent)) {// 如果靠近边界，痛苦信号生成
			for (Cell cell : f.cells) {
				if (cell.energy > 0)
					cell.energy--;
				if (cell.energy < Cell.MAX_ENERGY_LIMIT)
					for (Input input : cell.inputs)
						if (input.nearby(this)) // if input zone nearby this zone
							cell.energy += 30;
			}
		}
	}

}
