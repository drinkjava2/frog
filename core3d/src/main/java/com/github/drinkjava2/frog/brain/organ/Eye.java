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

import com.github.drinkjava2.frog.brain.Organ;

/**
 * Eye can only see env material
 * 
 * @author Yong Zhu
 */
public class Eye extends Organ {
	private static final long serialVersionUID = 1L;

	public Eye() {
		x = 0 - Brain.XR / 2;
		y = 0;
		z = Brain.ZR - .5f;
		xr = Brain.XR / 4;
		yr = xr;
		zr = 0.5f;
	}

	public boolean allowBorrow() { // 暂时只有一个眼睛，不允许在精子中将这个器官借出
		return false;
	}
}
