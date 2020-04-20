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

import static com.github.drinkjava2.frog.Env.FROG_BRAIN_XSIZE;

import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Cuboid;
import com.github.drinkjava2.frog.brain.Organ;

/**
 * BigEye can see 8x8 square around of current frog
 * 
 * @author Yong Zhu
 */
public class BigEye extends Organ {// 这个大眼睛是青蛙从上帝视角来观察，产生一个8x8的图像区，用来作为模式识别功能的输入区
	private static final int EYE_RADIUS = 4;
	private static final int cx = 6; // 中心点
	private static final int cy = 6;
	private static final int cz = FROG_BRAIN_XSIZE / 2; // 中层

	private static final long serialVersionUID = 1L;

	public BigEye() {
		// 大眼晴位于脑的中部，模仿视神经通到脑的中部，而不是通到脑的皮层。视信号从脑的中部传到脑皮层的过程就是模式识别的筛选过程，筛选结果记录在脑皮层里。
		// 模式识别的基本原理是反复发生的信号会到达并存贮在脑皮层(挖洞)，不常发生的信号不会在脑皮层留下印象(或洞很快就自愈了)。
		// 脑皮层的回忆原理是B角度入射的信号会触发脑皮层细胞原存有的A角度的洞，信号从A洞开始沿A信号曾经射入的方向反射回去
		this.shape = new Cuboid(cx - EYE_RADIUS, cy - EYE_RADIUS, cz, EYE_RADIUS * 2, EYE_RADIUS * 2, 1);
	}

	@Override
	public void cellAct(Frog f, Cell c) {// 大眼睛根据虚拟环境是否有物体，激活视网膜细胞, 以及与它底层相邻的脑细胞(待加)
		// 根据距中心点(cx,cy)的偏移，来映射虚拟环境的物体到视网膜上
		if (Env.foundAnyThing(f.x + c.x - cx, f.y - c.y + cy))
			c.addEnergy(30);
		else
			c.subEnergy(30);
		// TODO：激活眼下细胞，让信号进入模式识别之旅，并最终存贮在脑皮层细胞里,即金字塔的底部。
	}

}
