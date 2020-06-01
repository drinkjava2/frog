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
 * Zone represents a cube zone in brain
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Zone implements Serializable { // zone 代表脑空间中的一块立方区域， 以x,y,z为中心， 以r为边长的一半
	private static final long serialVersionUID = 1L;

	public float x;
	public float y;
	public float z;
	public float r;// r为这个立方矩形边长的一半

	public Zone() {
		// 空构造器不能省
	}

	public Zone(float x, float y, float z, float r) {// 用x,y,z, r来构造
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
		if (this.x < 0)
			this.x = 0;
		if (this.y < 0)
			this.y = 0;
		if (this.z < 0)
			this.z = 0;
		if (this.x > Env.FROG_BRAIN_XSIZE)
			this.x = Env.FROG_BRAIN_XSIZE;
		if (this.y > Env.FROG_BRAIN_YSIZE)
			this.y = Env.FROG_BRAIN_YSIZE;
		if (this.z > Env.FROG_BRAIN_ZSIZE)
			this.z = Env.FROG_BRAIN_ZSIZE;
	}

	public Zone(Zone z) {// 用另一个Zone来构造
		this.x = z.x;
		this.y = z.y;
		this.z = z.z;
		this.r = z.r;
	}

	public Zone(Zone a, Zone b) {// 用两个Zone来构造，新的zone位于两个zone的中间
		this.x = (a.x + b.x) / 2;
		this.y = (a.y + b.y) / 2;
		this.z = (a.z + b.z) / 2 ;
		this.r = (a.r + b.r) / 2;
	}

	public boolean nearby(Zone o) {
		if (o == null)
			return false;
		float dist = r + o.r;
		return Math.abs(x - o.x) < dist && Math.abs(y - o.y) < dist && Math.abs(z - o.z) < dist;
	}

	public int roundX() {
		return Math.round(x);
	}

	public int roundY() {
		return Math.round(y);
	}

	public int roundZ() {
		return Math.round(z);
	}

	public static void copyXYZ(Zone from, Zone to) {
		to.x = from.x;
		to.y = from.y;
		to.z = from.z;
	}

	public static void copyXYZR(Zone from, Zone to) {
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

	public String debugInfo() {
		return new StringBuilder().append("zone x=").append(x).append(", y=").append(y).append(", z=").append(z)
				.append(", r=").append(r).toString();
	}
}
