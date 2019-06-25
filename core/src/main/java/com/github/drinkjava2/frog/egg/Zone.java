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
package com.github.drinkjava2.frog.egg;

import java.io.Serializable;

import com.github.drinkjava2.frog.env.Env;

/**
 * Zone represents a rectangle zone in brain
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Zone implements Serializable {
	private static final long serialVersionUID = 1L;
	public float x;
	public float y;
	public float radius;// so width of the zone= radius*2

	public Zone() {
		// 空构造器不能省，FastJSON实例化时要用到
	}

	public Zone(float x, float y, float radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		if (this.x < 0)
			this.x = 0;
		if (this.y < 0)
			this.y = 0;
		if (this.x > Env.FROG_BRAIN_WIDTH)
			this.x = Env.FROG_BRAIN_WIDTH;
		if (this.y > Env.FROG_BRAIN_WIDTH)
			this.y = Env.FROG_BRAIN_WIDTH;
	}

	public Zone(Zone z) {
		this.x = z.x;
		this.y = z.y;
		this.radius = z.radius;
	}

	public boolean nearby(Zone z) {
		float dist = radius + z.radius;
		return (Math.abs(x - z.x) < dist && Math.abs(y - z.y) < dist);
	}

	public int roundX() {
		return Math.round(x);
	}

	public int roundY() {
		return Math.round(y);
	}

	public static void copyXY(Zone from, Zone to) {
		to.x = from.x;
		to.y = from.y;
	}
}
