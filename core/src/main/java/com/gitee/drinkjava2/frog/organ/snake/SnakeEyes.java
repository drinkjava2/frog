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
package com.gitee.drinkjava2.frog.organ.snake;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.brain.Organ;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * SnakeEyes can only see 4 direction's frog
 * 
 * 蛇的眼睛看不到food, 只能看到青蛙，也就是说它把青蛙当成食物
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class SnakeEyes {

	public static class SeeUp extends Organ {// 只能看到上方青蛙
		private static final long serialVersionUID = 1L;
		public int seeDistance; // 眼睛能看到的距离

		@Override
		public void initOrgan(Animal a) { // 仅在Snake生成时这个方法会调用一次
			if (!initilized) {
				initilized = true;
				seeDistance = 8;
			}
		}

		@Override
		public Organ[] vary() {
			seeDistance = RandomUtils.varyInLimit(seeDistance, 5, 20);
			if (RandomUtils.percent(5)) { // 可视距离有5%的机率变异
				seeDistance = seeDistance + 1 - 2 * RandomUtils.nextInt(2);
				if (seeDistance < 1)
					seeDistance = 1;
				if (seeDistance > 50)
					seeDistance = 50;
			}
			return new Organ[] { this };
		}

		@Override
		public void active(Animal a) {
			for (int i = 1; i < seeDistance; i++)
				if (Env.foundFrogOrOutEdge(a.x, a.y + i)) {
					activeInput(a, 30);
					return;
				}
		}
	}

	public static class SeeDown extends SeeUp {// 只能看到下方青蛙
		private static final long serialVersionUID = 1L;

		@Override
		public void active(Animal a) {
			for (int i = 1; i < seeDistance; i++)
				if (Env.foundFrogOrOutEdge(a.x, a.y - i)) {
					activeInput(a, 30);
					return;
				}
		}
	}

	public static class SeeLeft extends SeeUp {// 只能看到左方青蛙
		private static final long serialVersionUID = 1L;

		@Override
		public void active(Animal a) {
			for (int i = 1; i < seeDistance; i++)
				if (Env.foundFrogOrOutEdge(a.x - i, a.y)) {
					activeInput(a, 30);
					return;
				}
		}
	}

	public static class SeeRight extends SeeUp {// 只能看到右方青蛙
		private static final long serialVersionUID = 1L;

		@Override
		public void active(Animal a) {
			for (int i = 1; i < seeDistance; i++)
				if (Env.foundFrogOrOutEdge(a.x + i, a.y)) {
					activeInput(a, 30);
					return;
				}
		}
	}

}
