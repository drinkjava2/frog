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

import static com.github.drinkjava2.frog.Env.FROG_BRAIN_ZSIZE;

import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Cuboid;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.objects.Material;
import com.github.drinkjava2.frog.util.ColorUtils;

/**
 * Eye can only see env material
 * 
 * @author Yong Zhu
 */
public class Eye extends Organ {// 眼睛是长方体
	private static final int EYE_SIZE = 6;

	private static final long serialVersionUID = 1L;

	public Eye() {
		this.shape = new Cuboid(3, 3, FROG_BRAIN_ZSIZE / 2, EYE_SIZE, EYE_SIZE, 1);// 眼晴位于脑的中部
		this.organName = "Eye";
		this.allowVary = false;// 不允许变异
		this.allowBorrow = false;// 不允许借出
		this.color = ColorUtils.GRAY;
	}

	public void cellAct(Frog f, Cell c) {// 眼细胞的作用是根据食物激活视网膜和眼下皮层
		f.x=0;f.y=0;
		for (int i = 0; i < 2; i++) {
			Env.bricks[i][0]=Material.FOOD;
			Env.bricks[i][1]=Material.FOOD;
			Env.bricks[i][32]=Material.FOOD;
		}
		if (Env.foundAnyThing(f.x - EYE_SIZE / 2 + c.x, f.y - EYE_SIZE / 2 + c.y))
			c.active();
		else
			c.deActive();
	}

}
