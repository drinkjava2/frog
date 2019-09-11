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

import java.io.Serializable;

import com.github.drinkjava2.frog.Env;

/**
 * Cube represents a cube zone in brain
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Cube implements Serializable {
	private static final long serialVersionUID = 1L;

	public float x;
	public float y;
	public float z;
	public float r;// r为这个矩形体边长的一半

	public Cube() {
		// 空构造器不能省
	}

	public Cube(float x, float y, float z, float r) {// 用x,y,z,r来构造
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
		if (this.x < 0)
			this.x = 0;
		if (this.y < 0)
			this.y = 0;
		if (this.x > Env.FROG_BRAIN_WIDTH)
			this.x = Env.FROG_BRAIN_WIDTH;
		if (this.y > Env.FROG_BRAIN_WIDTH)
			this.y = Env.FROG_BRAIN_WIDTH;
		if (this.z > Env.FROG_BRAIN_WIDTH)
			this.z = Env.FROG_BRAIN_WIDTH;
	}

	public Cube(Cube c) {// 用另一个Cube来构造
		this.x = c.x;
		this.y = c.y;
		this.z = c.z;
		this.r = c.r;
	}

	public boolean nearby(Cube z) {
		float dist = r + z.r;
		return (Math.abs(x - z.x) < dist && Math.abs(y - z.y) < dist);
	}

	public static void copyXYZ(Cube from, Cube to) {
		to.x = from.x;
		to.y = from.y;
		to.z = from.z;
	}

	public static void copyXYZR(Cube from, Cube to) {
		to.x = from.x;
		to.y = from.y;
		to.z = from.z;
		to.r = from.r;
	}

	public void setXYZR(float x, float y, float z, float r) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
	}

}
