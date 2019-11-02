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

import com.github.drinkjava2.frog.Env;

/**
 * Photon has direction and strength
 * 
 * 用光子来代表信息传递的载体，这是一个矢量，具有方向和能量，能量可以为负值，x,y,z值表示它的脑空间的坐标值，mx,my,mz表示每次移动多少
 * 光子永远在移动，直到被吸收转化为Cell的能量贮存或出界
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class Photon {
	public float x;
	public float y;
	public float z;
	public float mx;
	public float my;
	public float mz;
	public float energy;
	public int color;// 每个光子都有自已的颜色，与产生光子的器官的颜色来决定,颜色不重要，但能方便观察
	public int activeNo;// 每一轮循环都有一个编号，光子走一格后就加上这个编号，同一个循环如果遇到相同编号的光子就不跳过，防止光子被一直赶着走多步

	public Photon() { // 缺省构造器
	}

	public Photon(int color, float x, float y, float z, float mx, float my, float mz, float energy) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.mx = mx;
		this.my = my;
		this.mz = mz;
		this.energy = energy;
		this.color = color;
	}

	/** Check if x,y,z out of frog's brain bound */
	public boolean outBrainBound() {// 检查指定坐标是否超出frog脑空间界限
		return x < 0 || x >= Env.FROG_BRAIN_XSIZE || y < 0 || y >= Env.FROG_BRAIN_YSIZE || z < 0
				|| z >= Env.FROG_BRAIN_ZSIZE;
	}

}
