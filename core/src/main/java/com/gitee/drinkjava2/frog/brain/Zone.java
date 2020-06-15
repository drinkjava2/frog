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
package com.gitee.drinkjava2.frog.brain;

import java.io.Serializable;

import com.gitee.drinkjava2.frog.Env;

/**
 * Zone represents a cuboid zone in brain, but x,y,z is in the center
 * 
 * Zone为一个上表面为正方形的立方体，x,y,z位于立方体的中心，r为上表面正方形边长的一半，h为立方体厚度
 * 
 * @author Yong Zhu
 * @since 1.0
 */
@SuppressWarnings("all")
public class Zone implements Serializable { // zone 代表脑空间中的一块立方区域， 以x,y,z为中心， 以r为边长的一半
	private static final long serialVersionUID = 1L;

	public float x;
	public float y;
	public float z;
	public float r;// r为这个立方矩形上边长的一半
	public float h;// h为这个立方矩形厚度

	public Zone() {
		// 空构造器不能省
	}

	public Zone(float x, float y, float z, float r) {// 用x,y,z, r来构造
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
		this.h = r + r;
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

	public Zone(float x, float y, float z, float r, float h) {// 用x,y,z, r来构造
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
		this.h = h;
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
		this.h = z.h;
	}

	public Zone(Zone a, Zone b) {// 用两个Zone来构造，新的zone位于两个zone的中间
		this.x = (a.x + b.x) / 2;
		this.y = (a.y + b.y) / 2;
		this.z = (a.z + b.z) / 2 - 20; // -20表示它是下一层的连线
		this.r = 5;
		this.h = (a.h + b.h) / 2;
	}

	public boolean nearby(Zone o) {
		if (o == null)
			return false;
		float dist = r + o.r;
		return Math.abs(x - o.x) < dist && Math.abs(y - o.y) < dist && Math.abs(z - o.z) < dist;
	}

	public boolean nearby(float x, float y, float z, float r) {
		float dist = this.r + r;
		return Math.abs(this.x - x) < dist && Math.abs(this.y - y) < dist && Math.abs(this.z - z) < dist;
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

	public static void copyXYZRH(Zone from, Zone to) {
		to.x = from.x;
		to.y = from.y;
		to.z = from.z;
		to.r = from.r;
		to.h = from.h;
	}

	public void setXYZRH(float x, float y, float z, float r, float h) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
		this.h = h;
	}

	public String debugInfo() {
		return new StringBuilder().append("zone x=").append(x).append(", y=").append(y).append(", z=").append(z)
				.append(", r=").append(r).toString();
	}
}
