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

/**
 * Cuboid represents a rectangular prism 3d zone in brain
 * 
 * Cuboid是一个长方体，通常用来表示脑内器官的形状
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
@SuppressWarnings("all")
public class Cuboid   {
	private static final long serialVersionUID = 1L;

	public float x;// x,y,z是长方体的左下角坐标
	public float y;
	public float z;
	public float xe;// xe,ye,ze分别是长方体三边长
	public float ye;
	public float ze;

	public Cuboid() {
		// 空构造器不能省
	}

	public Cuboid(float x, float y, float z, float xe, float ye, float ze) {// 用x,y,z,r来构造
		this.x = x;
		this.y = y;
		this.z = z;
		this.xe = xe;
		this.ye = ye;
		this.ze = ze;
	}

	public Cuboid(Cuboid c) {// 用另一个Cuboid来构造
		this.x = c.x;
		this.y = c.y;
		this.z = c.z;
		this.xe = c.xe;
		this.ye = c.ye;
		this.ze = c.ze;
	}
 

}
