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

/**
 * Cone represents a cone zone in brain
 * 
 * Cone是一个锥形体，通常用来表示脑内器官的形状。另一个类似的常用形状是Cuboid长方体.
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Cone implements Serializable {
	private static final long serialVersionUID = 1L;

	public float x1; // 这6个变量定义了Cone的中心线起点和终点,器官不能拐弯，但拐弯可以用多个立方锥体器官首尾相接演化出来
	public float y1;
	public float z1;
	public float x2;
	public float y2;
	public float z2;

	public float r1 = 8; // 起点的半径，为了简化编程，通常是是指起点矩形边长的一半，因为圆形计算麻烦
	public float r2 = 8; // 终点的半径

	public Cone() {
		// 空构造器不能省
	}

	public Cone(float x1, float y1, float z1, float x2, float y2, float z2, float r1, float r2) {// 用x,y,z,r来构造
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.r1 = r1;
		this.r2 = r2;
	}

}
