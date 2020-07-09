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
package com.gitee.drinkjava2.frog.organ.frog;

import java.awt.Color;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.brain.BrainPicture;
import com.gitee.drinkjava2.frog.brain.Organ;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Eye can only see 4 direction
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class FrogEyes {

	public static class SeeUp extends Organ {// 只能看到上方食物
		private static final long serialVersionUID = 1L;
		public int seeDistance; // 眼睛能看到的距离
		public transient boolean seeSomeThing = false;

		@Override
		public void initOrgan(Animal a) { // 仅在生成时这个方法会调用一次
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
		public void drawOnBrainPicture(Animal a, BrainPicture pic) {// 把自已这个器官在脑图上显示出来，子类可以重写这个方法
			if (!Env.SHOW_FIRST_ANIMAL_BRAIN)
				return;
			if (seeSomeThing)
				pic.setPicColor(Color.RED); // 缺省是黑色
			else
				pic.setPicColor(Color.BLACK); // 缺省是黑色
			pic.drawZone(this);
			if (this.name != null)
				pic.drawText(x, y, z, String.valueOf(this.name));
		}

		@Override
		public void active(Animal a) {
			for (int i = 1; i < seeDistance; i++)
				if (Env.foundAnyThingOrOutEdge(a.x, a.y + i)) {
					activeInput(a, 30);
					seeSomeThing = true;
					return;
				}
			seeSomeThing = false;
		}
	}

	public static class SeeDown extends SeeUp {// 只能看到下方食物
		private static final long serialVersionUID = 1L;

		@Override
		public void active(Animal a) {
			for (int i = 1; i < seeDistance; i++)
				if (Env.foundAnyThingOrOutEdge(a.x, a.y - i)) {
					activeInput(a, 30);
					seeSomeThing = true;
					return;
				}
			seeSomeThing = false;
		}
	}

	public static class SeeLeft extends SeeUp {// 只能看到左方食物
		private static final long serialVersionUID = 1L;

		@Override
		public void active(Animal a) {
			for (int i = 1; i < seeDistance; i++)
				if (Env.foundAnyThingOrOutEdge(a.x - i, a.y)) {
					activeInput(a, 30);
					seeSomeThing = true;
					return;
				}
			seeSomeThing = false;
		}
	}

	public static class SeeRight extends SeeUp {// 只能看到右方食物
		private static final long serialVersionUID = 1L;

		@Override
		public void active(Animal a) {
			for (int i = 1; i < seeDistance; i++)
				if (Env.foundAnyThingOrOutEdge(a.x + i, a.y)) {
					activeInput(a, 30);
					seeSomeThing = true;
					return;
				}
			seeSomeThing = false;
		}
	}

}
