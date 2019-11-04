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

/**
 * Cone represents a cone 3d zone in brain
 * 
 * Cone是一个锥形体(圆锥或棱锥)，通常用来表示脑内器官的形状。另一个常用形状是Cuboid长方体.
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
@SuppressWarnings("all")
public class Cone implements Shape {
	private static final long serialVersionUID = 1L;

	public int x1; // 这6个变量定义了Cone的中心线起点和终点,器官不能拐弯，但拐弯可以用一个锥体分成两个首尾相接的锥体再进一步变异演化出来
	public int y1;
	public int z1;
	public int x2;
	public int y2;
	public int z2;

	public int r1 = 8; // 起点的半径，为了简化编程，通常是是指起点矩形边长的一半，因为圆形计算麻烦
	public int r2 = 8; // 终点的半径

	public Cone() {
		// 空构造器不能省
	}

	public Cone(int x1, int y1, int z1, int x2, int y2, int z2, int r1, int r2) {// 用x,y,z,r来构造
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.r1 = r1;
		this.r2 = r2;
	}

	@Override
	public void drawOnBrainPicture(BrainPicture pic) {
		pic.drawCone(this);
	}

	@Override
	public void fillCellsWithAction(Frog f, Organ o) {
		// TODO 待添加Cone形器官播种脑细胞的代码
	}

	@Override
	public void createCells(Frog f, Organ o) {
		// TODO 待添加Cone形器官createCells的代码

	}

}
