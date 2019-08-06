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

import com.github.drinkjava2.frog.Application;
import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.BrainPicture;
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Input;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.brain.Zone;

/**
 * Eye is an organ can see environment, and active brain cells which inputs are
 * located in eye range
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class NewEye extends Organ {// 这个新版的眼睛有nxn个感光细胞，可以看到青蛙周围nxn网络内有没有食物
	private static final long serialVersionUID = 1L;
	public int n = 13; // 眼睛有n x n个感光细胞， 用随机试错算法自动变异(加1或减1，最小是3x3)

	@Override
	public void initFrog(Frog f) { // 仅在Frog生成时这个方法会调用一次，缺省啥也不干，通常用于Organ类的初始化
		if (!initilized) {
			initilized = true;
			organOutputEnergy = 30;
		}
	}

	@Override
	public void drawOnBrainPicture(Frog f, BrainPicture pic) {// 把自已这个器官在脑图上显示出来
		if (!Application.SHOW_FIRST_FROG_BRAIN)
			return;
		super.drawOnBrainPicture(f, pic);
		float r2 = r / n; // r2是每个感光细胞的半径
		float x0 = x - r;
		float y0 = y - r; // x0,y0是眼睛的左上角
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				Zone cell = new Zone(x0 + i * 2 * r2 + r2, y0 + j * 2 * r2 + r2, r2);
				if (Env.foundAnyThing(f.x - n / 2 + i, f.y - n / 2 + j))
					pic.fillZone(cell);
				else
					pic.drawZone(cell);
			}
		}
	}

	@Override
	public Organ[] vary() {
//		if (RandomUtils.percent(50)) {
//			n = n + 1 - 2 * RandomUtils.nextInt(2);
//			if (n < 3)
//				n = 3;
//			if (n > 12)
//				n = 12;
//		}
		return new Organ[] { this };
	}

	@Override
	public void active(Frog f) {// 如果看到食物就激活对应脑区的所有输入触突
		float r2 = r / n; // r2是每个感光细胞的半径
		float x0 = x - r;
		float y0 = y - r; // x0,y0是眼睛的左上角
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (Env.foundAnyThing(f.x - n / 2 + i, f.y - n / 2 + j)) {
					Zone eyeCell = new Zone(x0 + i * 2 * r2 + r2, y0 + j * 2 * r2 + r2, r2);
					for (Cell cell : f.cells)
						for (Input input : cell.inputs)
							if (input.nearby(eyeCell))
								input.cell.energy += organOutputEnergy;
				}
			}
		}
	}

}
