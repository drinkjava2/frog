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

import java.awt.Color;

import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;

/**
 * Organ is a part of frog, organ can be saved in egg
 * 
 * 器官是脑的一部分，多个器官在脑内可以允许重叠出现在同一位置，器官有小概率随机生成，但大多数时候是稳定的。 有些器官会负
 * 
 * @author Yong Zhu
 * @since 1.0.4
 */
public class Organ extends Cuboid {
	private static final long serialVersionUID = 1L;
	public long fat = 0; // 如果活跃多，fat值高，则保留（及变异）的可能性大，反之则很可能丢弃掉
	public boolean initilized; // 通过这个标记判断是否需要手工给定它的参数初值

	public boolean allowBorrow() { // 是否允许在精子中将这个器官借出
		return false;
	}

	/** Only call once when frog created , Child class can override this method */
	public void init(Frog f) { // 仅在Frog生成时这个方法会调用一次，缺省啥也不干，通常用于在这一步播种脑细胞
	}

	/** Each loop step call active method, Child class can override this method */
	public void active(Frog f) { // 每一步都会调用器官的active方法 ，缺省啥也不干
	}

	/** Child class can override this method to drawing picture */
	public void drawOnBrainPicture(Frog f, BrainPicture pic) {// 把自已这个器官在脑图上显示出来，子类可以重写这个方法
		if (!Env.SHOW_FIRST_FROG_BRAIN)
			return;
		pic.setColor(Color.BLACK); // 缺省是黑色
		pic.drawCuboid(this);
	}

	/** Only call once after organ be created by new() method */
	public Organ[] vary() { // 在下蛋时每个器官会调用这个方法，缺省返回一个类似自已的副本，子类通常要覆盖这个方法
		Organ newOrgan = null;
		try {
			newOrgan = this.getClass().newInstance();
		} catch (Exception e) {
			throw new UnknownError("Can not make new Organ copy for " + this);
		}
		copyXYZE(this, newOrgan);
		newOrgan.fat = this.fat;
		return new Organ[] { newOrgan };
	}

}
