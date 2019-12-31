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
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.brain.Output;

/**
 * LogicAnd has a "AND" logic, i.e. if 2 cells active it at same time, it active
 * 
 * 与门
 */
public class LogicAnd extends Organ { // 随意布置一些"与门"在脑里,至于能不能被选中，就听天由命了
	private static final long serialVersionUID = 1L;

	@Override
	public void active(Frog f) {
		Cell c1 = null;
		Cell c2 = null;
		for (Cell cell : f.cells) {
			if (cell.energy > organActiveEnergy)
				for (Output output : cell.outputs) { //
					if (this.nearby(output)) {
						if (c1 == null)
							c1 = cell;
						else {
							c2 = cell;
							break;
						}
					}
				}
		}
		if (c2 != null) {// 同时找到两个cell激活同一个区了，开始实施与门的逻辑，激活其它与这个器官相连的神经
			c1.organ.fat++;
			c1.energy -= 10;//
			c2.organ.fat++;
			c2.energy -= 10;//
			activeOtherCells(f);
		}
	}

}
