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
 * Empty is a special organ has no action, only used as media for photon
 * 
 * Empty这个器官唯一目的只是充当光子媒介初始化cell单元格数组，否则光子如果没遇到cell会一直走下出，从脑图上就观察不到光子了
 * 
 * @author Yong Zhu
 */
public class Empty extends Organ {
	private static final long serialVersionUID = 1L;

	public Empty() {
		super();
		this.shape = new Cuboid(6, 2, 2, Env.FROG_BRAIN_XSIZE-7, Env.FROG_BRAIN_YSIZE-3, Env.FROG_BRAIN_ZSIZE-4);
		this.organName = "Empty";
		this.type = Organ.EMPTY; // Empty这个器官并不播种cell,它存在的唯一目的只是充当光子媒介，否则光子会一直走下去消失
		this.allowVary = false;// 不允许变异
		this.allowBorrow = false;// 不允许借出
	}

}
