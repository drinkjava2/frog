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

import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Cuboid;
import com.github.drinkjava2.frog.brain.Organ;

/**
 * Active always active
 * 
 * @author Yong Zhu
 */
public class Active extends Organ {// 这个器官的作用总是激活一个固定区，它有可能会被自然选择选中
	private static final long serialVersionUID = 1L;

	public Active() {
		this.shape = new Cuboid(15, 10, FROG_BRAIN_ZSIZE / 2 + 3, 1, 1, 1);
	}

	@Override
	public void cellAct(Frog f, Cell c) {
		addLineEnergy(f, c);
	}
}
