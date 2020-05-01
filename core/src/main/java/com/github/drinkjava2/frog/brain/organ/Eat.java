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
 * Happy active after ate food
 */
public class Eat extends Organ { // Eat器官的作用就是如果位置与食物重合，增加frog的能量
	private static final long serialVersionUID = 1L;
	public int actEngery = 1000;

	public Organ[] vary(Frog f) {// 重写器官的very方法
		return new Organ[] { this };
	}

	@Override
	public void active(Frog f) {
		if (Env.foundAndAteFood(f.x, f.y)) {
			f.ateFood++;
			f.energy += actEngery;
		}
	}

}
