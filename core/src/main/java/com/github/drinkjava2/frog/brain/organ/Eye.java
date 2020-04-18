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
 * Eye can only see env material
 * 
 * Eye被分成上下左右四个独立的感光细胞器官，这样方便编程
 * 
 * @author Yong Zhu
 */
public class Eye {// 这个眼睛是从青蛙视角来观察，因为青蛙生活在二次元空间，所以它只能观察上下左右4个方向有无食物
	private static final int SEE_DIST = 20; // 视距
	private static final int cx = 5; //中心点
	private static final int cy = 15;
	private static final int cz = FROG_BRAIN_XSIZE /2; // 中层

	public static class SeeUp extends Organ {// 这个感光细胞只能看到上方有没有物体
		private static final long serialVersionUID = 1L;

		public SeeUp() {
			shape = new Cuboid(cx, cy+2, cz, 1, 1, 1);
		}

		public void cellAct(Frog f, Cell c) {// 如果上方有物体就激活视网膜细胞
			int i;
			for (i = 1; i <= SEE_DIST; i++)
				if (Env.foundAnyThing(f.x, f.y + i))
					break;
			c.energy = (20 - i) * 50;// 看到物体距离越近，细胞激活度就越大
			// TODO：让信号参与模式识别，并最终存贮在脑皮层细胞里,即金字塔的底部。
		}
	}

	public static class SeeDown extends Organ {// 这个感光细胞只能看到下方有没有物体
		private static final long serialVersionUID = 1L;

		public SeeDown() {
			shape = new Cuboid(cx, cy-2, cz , 1, 1, 1);
		}

		public void cellAct(Frog f, Cell c) {// 如果上方有物体就激活视网膜细胞
			int i;
			for (i = 1; i <= SEE_DIST; i++)
				if (Env.foundAnyThing(f.x, f.y - i))
					break;
			c.energy = (20 - i) * 50;// 看到物体距离越近，细胞激活度就越大
		}
	}

	public static class SeeLeft extends Organ {// 这个感光细胞只能看到左边有没有物体
		private static final long serialVersionUID = 1L;

		public SeeLeft() {
			shape = new Cuboid(cx+2, cy, cz, 1, 1, 1);
		}

		public void cellAct(Frog f, Cell c) {// 如果上方有物体就激活视网膜细胞
			int i;
			for (i = 1; i <= SEE_DIST; i++)
				if (Env.foundAnyThing(f.x - i, f.y))
					break;
			c.energy = (20 - i) * 50;// 看到物体距离越近，细胞激活度就越大
		}
	}

	public static class SeeRight extends Organ {// 这个感光细胞只能看到右边有没有物体
		private static final long serialVersionUID = 1L;

		public SeeRight() {
			shape = new Cuboid(cx-2, cy , cz, 1, 1, 1);
		}

		public void cellAct(Frog f, Cell c) {// 如果上方有物体就激活视网膜细胞
			int i;
			for (i = 1; i <= SEE_DIST; i++)
				if (Env.foundAnyThing(f.x + i, f.y))
					break;
			c.energy = (20 - i) * 50;// 看到物体距离越近，细胞激活度就越大
		}
	}

}
