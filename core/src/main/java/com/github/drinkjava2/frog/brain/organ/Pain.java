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
import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.BrainPicture;
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Input;
import com.github.drinkjava2.frog.brain.Organ;

/**
 * Pain zone active after some bad thing happen like close to edge, hurt...
 * 
 * 痛是一种惩罚，表示青蛙做错了什么，但是又不至严重到判其死亡的地步
 */
public class Pain extends Organ { // Pain器官目前激活的条件是离边境20个单元之类，痛苦和快感是条件反射形成的前题

	private static final long serialVersionUID = 1L;

	@Override
	public void initFrog(Frog f) {
		if (!initilized) {
			initilized = true;
			organOutputEnergy = 5;
		}
	}

	@Override
	public void drawOnBrainPicture(Frog f, BrainPicture pic) {// 把自已这个器官在脑图上显示出来
		if (!Application.SHOW_FIRST_FROG_BRAIN)
			return;
		if (Env.closeToEdge(f)) {
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
	public void active(Frog f) {
		if (Env.closeToEdge(f)) {// 如果靠近边界，痛苦信号生成
			for (Cell cell : f.cells) {
				if (cell.energy > 0)
					cell.energy--;
				if (cell.energy < Cell.MAX_ENERGY_LIMIT)
					for (Input input : cell.inputs)
						if (input.nearby(this)) // if input zone nearby this zone
							cell.energy += organOutputEnergy;
			}
		}
	}

}
