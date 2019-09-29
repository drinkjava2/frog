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
 * Eye can only see env material
 * 
 * @author Yong Zhu
 */
public class Eye extends Organ {//这个眼睛有nxn个感光细胞，可以看到青蛙周围nxn网络内有没有食物
	private static final long serialVersionUID = 1L;
	public int n = 18; // 眼睛有n x n个感光细胞， 用随机试错算法自动变异(加1或减1，最小是3x3)

	@Override
	public void init(Frog f) { // 仅在Frog生成时这个方法会调用一次，缺省啥也不干，通常用于Organ类的初始化
		if (!initilized) {
			initilized = true; 
		}
	} 

	public Eye() {
		x = 10;
		y = 10;
		z = Env.FROG_BRAIN_ZSIZE-1;
		xe = 10;
		ye = xe;
		ze = 1;
	}

	@Override
	public void active(Frog f) {// 如果看到食物就在视网膜所在位置的cube上产生一些光子
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (Env.foundAnyThing(f.x - n/2 + i, f.y - n /2 + j)) {
					 
				}
			}
		}
	}

}
