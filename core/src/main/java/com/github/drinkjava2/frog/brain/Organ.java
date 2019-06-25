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

import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.organ.Eye;
import com.github.drinkjava2.frog.egg.OrganDesc;
import com.github.drinkjava2.frog.egg.Zone;
import com.github.drinkjava2.frog.env.Env;

/**
 * Organ is a sensor or execute part connected to frog's brain cell, like:
 * 器官，分为输入器官和输出器官两大类，它们在蛋里定义，数量、位置、大小可以随机变异进化，但目前在蛋里用硬编码写死，不允许器官进化,一个眼睛都没搞定，要进化出10个眼睛来会吓死人
 * 
 * <pre/>
 *  
 * Sensors:
 * hungry sensor,  eye, ear, smell, happy sensor
 * 
 * Executor:
 *  moves (up/down/left/right), eat ...
 * 
 * </pre>
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Organ extends Zone {
	private static final long serialVersionUID = 1L;
	public static final int HUNGRY = 0;
	public static final int UP = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	public static final int RIGHT = 4;
	public static final int EAT = 5;
	public static final int EYE = 6;

	public int type;

	public Organ(OrganDesc od) {
		super(od.x, od.y, od.radius);
		type = od.type;
	}

	public Organ(int type, float x, float y, float radius) {
		super(x, y, radius);
		this.type = type;
	}

	public void active(Frog f) {
		switch (type) {
		case HUNGRY:
			hungry(f);
			break;
		case UP:
			up(f);
			break;
		case DOWN:
			down(f);
			break;
		case LEFT:
			left(f);
			break;
		case RIGHT:
			right(f);
			break;
		case EAT:
			eat(f);
			break;
		case EYE:
			eye(f);
			break;
		default:
			break;
		}
	}

	public void hungry(Frog f) {
		for (Cell cell : f.cells) {
			if (cell.energy > 0)
				cell.energy--;

			if (f.energy < 10000 && cell.energy < 100)
				for (Input input : cell.inputs)
					if (input.nearby(this)) // input zone near by hungry zone
						cell.energy += 2;
		}
	}

	public void up(Frog f) {
		if (outputActive(f))
			f.y++;
	}

	public void down(Frog f) {
		if (outputActive(f))
			f.y--;
	}

	public void left(Frog f) {
		if (outputActive(f))
			f.x--;
	}

	public void right(Frog f) {
		if (outputActive(f))
			f.x++;
	}

	public void eat(Frog f) {
		int x = f.x;
		int y = f.y;
		if (x < 0 || x >= Env.ENV_WIDTH || y < 0 || y >= Env.ENV_HEIGHT) {// 越界者死！
			f.alive = false;
			return;
		}

		if (Env.foods[x][y]) {
			Env.foods[x][y] = false;
			f.energy = f.energy + 1000;// 吃掉food，能量境加
		}
	}

	public void eye(Frog f) {
		Eye.act(f, this);
	}

	// ======public methods========

	public boolean outputActive(Frog f) {
		for (Cell cell : f.cells) {
			for (Output output : cell.outputs) { //
				if (cell.energy > 10 && this.nearby(output)) {
					f.cellGroups[cell.group].fat++;
					cell.energy -= 30;
					return true;
				}
			}
		}
		return false;
	}

}
