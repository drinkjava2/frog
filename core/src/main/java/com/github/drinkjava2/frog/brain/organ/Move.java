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
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Cuboid;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.brain.Photon;

/**
 * Move is a special organ the action move photon go to next cell
 * 
 * Move这个器官会让Cell中的光子沿着它的运动方向走到下一格
 * 
 * @author Yong Zhu
 */
public class Move extends Organ {
	private static final long serialVersionUID = 1L;

	public Move() {
		super();
		this.shape = new Cuboid(0, 0, 0, Env.FROG_BRAIN_XSIZE - 5, Env.FROG_BRAIN_YSIZE, Env.FROG_BRAIN_ZSIZE);
		this.organName = "Move"; 
		this.allowVary = false;// 不允许变异
		this.allowBorrow = false;// 不允许借出
	}

	public void cellAct(Frog frog, Cell c, int activeNo) {
		if (c.x == 0 || c.z == Env.FROG_BRAIN_ZSIZE - 1) {// 但是对于输入区，将删除光子，并合计一共收到多少
			if (c.photonQty > 0) {
				c.photonSum += c.photonQty;
				c.photons = null;
			}
			return;
		}
		if (c.photons != null) {
			for (int ii = 0; ii < c.photons.length; ii++) {
				Photon p = c.photons[ii];
				if (p == null || p.activeNo == activeNo)// 同一轮新产生的光子或处理过的光子不再走了
					continue;
				p.activeNo = activeNo;
				c.removePhoton(ii);
				frog.addAndWalk(p); // 让光子自已往下走，走到哪就停到哪个细胞里
			}
		}

	}
}
