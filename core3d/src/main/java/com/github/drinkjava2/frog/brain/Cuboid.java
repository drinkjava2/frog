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
 * Cuboid represents a rectangular prism zone in brain
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Cuboid implements Serializable {
	private static final long serialVersionUID = 1L;

	public float x;
	public float y;
	public float z;
	public float xr;// xr为这个矩形体x边长的一半
	public float yr;// yr为这个矩形体y边长的一半
	public float zr;// zr为这个矩形体z边长的一半

	public Cuboid() {
		// 空构造器不能省
	}

	public Cuboid(float x, float y, float z, float r) {// 用x,y,z,r来构造
		this.x = x;
		this.y = y;
		this.z = z;
		this.xr = r;
		this.yr = r;
		this.zr = r;
		if (this.x < 0)
			this.x = 0;
		if (this.y < 0)
			this.y = 0;
		if (this.x > Env.FROG_BRAIN_RADIUS)
			this.x = Env.FROG_BRAIN_RADIUS;
		if (this.y > Env.FROG_BRAIN_RADIUS)
			this.y = Env.FROG_BRAIN_RADIUS;
		if (this.z > Env.FROG_BRAIN_RADIUS)
			this.z = Env.FROG_BRAIN_RADIUS;
	}

	public Cuboid(float x, float y, float z, float xr, float yr, float zr) {// 用x,y,z,r来构造
		this.x = x;
		this.y = y;
		this.z = z;
		this.xr = xr;
		this.yr = yr;
		this.zr = zr;
	}

	public Cuboid(Cuboid c) {// 用另一个Cube来构造
		this.x = c.x;
		this.y = c.y;
		this.z = c.z;
		this.xr = c.xr;
		this.yr = c.yr;
		this.zr = c.zr;
	}

	public static void copyXYZ(Cuboid from, Cuboid to) {
		to.x = from.x;
		to.y = from.y;
		to.z = from.z;
	}

	public static void copyXYZR(Cuboid from, Cuboid to) {
		to.x = from.x;
		to.y = from.y;
		to.z = from.z;
		to.xr = from.xr;
		to.xr = from.xr;
		to.xr = from.xr;
	}

}
