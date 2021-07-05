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
package com.gitee.drinkjava2.frog.brain.organ;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.brain.Cuboid;
import com.gitee.drinkjava2.frog.brain.Organ;

/**
 * Move is a special organ the action move photon go to next cell
 * 
 * Move这个器官会让Cell中的光子沿着它的运动方向走到下一格
 * 
 * @author Yong Zhu
 */
public class Move1 extends Organ {
	private static final long serialVersionUID = 1L;

	public Move1() {
		super();
		this.shape = new Cuboid(0, 0, 0, 9 , Env.FROG_BRAIN_YSIZE, Env.FROG_BRAIN_ZSIZE-1);
		this.organName = "MOVE1";
		this.type = Organ.MOVE; // MOVE类型的细胞会保持光子的直线运动，并不在细胞上挖洞
		this.allowVary = false;// 不允许变异
		this.allowBorrow = false;// 不允许借出
	}

}
