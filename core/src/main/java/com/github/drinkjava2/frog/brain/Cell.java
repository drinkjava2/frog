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
package com.github.drinkjava2.frog.brain;

import java.io.Serializable;

/**
 * Cell is the smallest unit of brain
 * 
 * Cell是脑的单元，cell必须属于一个器官Organ，同一个空间可以有多个不同的Cell重叠出现，cell之间互相影响是因为它们空间上的位置相同造成的
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Cell implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final float MAX_ENERGY_LIMIT = 100.0f;
	public Zone input;
	public Zone output;
	public Zone body; // 这个细胞的本体位置
	public float energy = 0;
	public Organ organ;

	public boolean inActive() {
		if (energy > 30) {
			energy -= 30;
			return true;
		}
		return false;
	}

	public void addEnergy(float e) {
		energy += e;
		if (energy > MAX_ENERGY_LIMIT)
			energy = MAX_ENERGY_LIMIT;
	}

	public void subEnergy(float e) {
		energy -= e;
		if (energy < 0)
			energy = 0;
	}
}
