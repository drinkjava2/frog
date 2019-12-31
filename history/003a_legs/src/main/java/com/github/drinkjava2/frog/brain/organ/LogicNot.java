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
import com.github.drinkjava2.frog.brain.Organ;

/**
 * LogicAnd has a "NOT" logic, i.e. if any cells active it, it doesnot active,
 * if no cell active it, it active
 * 
 * 非门
 */
public class LogicNot extends Organ { // 随意布置一些"非门"在脑里,至于能不能被选中，就听天由命了
	private static final long serialVersionUID = 1L;

	@Override
	public void active(Frog f) {// 非门的逻辑最简单，如果它被激活，则没有输出，如果它没被激活，则输出信号
		if (activedByCells(f))
			return;
		activeOtherCells(f);
	}

}
