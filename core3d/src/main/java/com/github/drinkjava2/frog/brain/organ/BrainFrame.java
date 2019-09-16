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
import com.github.drinkjava2.frog.brain.Organ;

/**
 * BrainFrame one used to drawing the brain frame in BrainPicture
 * 
 * @author Yong Zhu
 */
public class BrainFrame extends Organ {
	private static final long serialVersionUID = 1L;

	public BrainFrame() {
		x = 0;
		y = 0;
		z = 0;
		xr = Env.FROG_BRAIN_RADIUS / 2;
		yr = xr;
		zr = xr;
	}

	public boolean allowBorrow() { // 是否允许在精子中将这个器官借出
		return false;
	}
}
