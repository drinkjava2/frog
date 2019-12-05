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

import java.awt.Color;

import com.github.drinkjava2.frog.Application;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.BrainPicture;
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Input;
import com.github.drinkjava2.frog.brain.Organ;

/**
 * Hungry will active cell's inputs, if frog's energy not enough
 */
public class Hungry extends Organ {
	private static final long serialVersionUID = 1L;

	@Override
	public void initFrog(Frog f) { // 仅在Frog生成时这个方法会调用一次，缺省啥也不干，通常用于Organ类的初始化
		if (!initilized) {
			initilized = true;
			// organWasteEnergy = 20f;
			organOutputEnergy = 2;
		}
	}

	@Override
	public void drawOnBrainPicture(Frog f, BrainPicture pic) {// 把自已这个器官在脑图上显示出来
		if (!Application.SHOW_FIRST_FROG_BRAIN)
			return;
		if (f.energy < 10000) {
			pic.fillZone(this);
		} else {
			pic.setColor(Color.white);
			pic.fillZone(this);
			pic.setColor(Color.BLACK);
			pic.drawZone(this);
		}
		if (this.name != null)
			pic.drawText(this, String.valueOf(this.name));
	}

	@Override
	public Organ[] vary() {
		// if (RandomUtils.percent(20)) // 有20机率权重变化
		// organOutputEnergy = RandomUtils.vary(organOutputEnergy);
		return new Organ[] { this };
	}

	@Override
	public void active(Frog f) {
		if (f.energy < 10000)// 所有的硬编码都是bug
			for (Cell cell : f.cells) {
				if (cell.energy > 0)
					cell.energy--;
				if (cell.energy < Cell.MAX_ENERGY_LIMIT)
					for (Input input : cell.inputs)
						if (input.nearby(this)) // input zone near by hungry zone
							cell.energy += 2;
			}
	}

}
