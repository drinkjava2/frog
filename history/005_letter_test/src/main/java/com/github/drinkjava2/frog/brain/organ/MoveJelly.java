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
import com.github.drinkjava2.frog.brain.Cuboid;
import com.github.drinkjava2.frog.brain.Organ;

/**
 * Move is a special organ the action move photon go to next cell
 * 
 * Move这个器官会让Cell中的光子沿着它的运动方向走到下一格
 * 
 * @author Yong Zhu
 */
public class MoveJelly extends Organ {
	private static final long serialVersionUID = 1L;

	public MoveJelly() {
		super();
		this.shape = new Cuboid(0, 0, 0, Env.FROG_BRAIN_XSIZE - 5, Env.FROG_BRAIN_YSIZE, Env.FROG_BRAIN_ZSIZE);
		this.organName = "MoveJelly";
		this.type = Organ.MOVE_JELLY; // Empty这个器官并不播种cell,它存在的唯一目的只是充当光子媒介，否则光子会一直走下去消失
		this.allowVary = false;// 不允许变异
		this.allowBorrow = false;// 不允许借出
	}

}
