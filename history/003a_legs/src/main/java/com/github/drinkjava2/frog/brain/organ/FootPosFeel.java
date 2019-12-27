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
import com.github.drinkjava2.frog.brain.BrainPicture;
import com.github.drinkjava2.frog.brain.Organ;

/**
 * Move down frog 1 unit if outputs of nerve cells active in this zone
 */
public class FootPosFeel extends Organ {
	public static final int BFOOTPOSSTART_X = 70;
	public static final int RFOOTPOSSTART_X = 570;
	private static final long serialVersionUID = 1L;

	@Override
	public void drawOnBrainPicture(Frog f, BrainPicture pic) {// 把自已这个器官在脑图上显示出来
		super.drawOnBrainPicture(f, pic);
		if (x < 500) { // x小于700的器官是底脚的
			int pos = Math.round((x - BFOOTPOSSTART_X) / 30) - 5;
			if (f.bFootPos == pos)
				pic.fillZone(this);
		} else {// 否则就是右脚
			int pos = Math.round((x - RFOOTPOSSTART_X) / 30) - 5;
			if (f.rFootPos == pos)
				pic.fillZone(this);
		}
	}

	@Override
	public void active(Frog f) {
		if (x < 500) {
			int pos = Math.round((x - BFOOTPOSSTART_X) / 30) - 5;
			if (f.bFootPos == pos)
				activeOtherCells(f);
		} else {
			int pos = Math.round((x - RFOOTPOSSTART_X) / 30) - 5;
			if (f.rFootPos == pos)
				activeOtherCells(f);
		}
	}

}
