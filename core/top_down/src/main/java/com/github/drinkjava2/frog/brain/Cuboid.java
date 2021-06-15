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
 * Cuboid represents a rectangular prism 3d zone in brain
 * 
 * Cuboid是一个长方体，通常用来表示脑内器官的形状。另一个功能类似的形装是Cone锥形体。
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
@SuppressWarnings("all")
public class Cuboid implements Shape {
	private static final long serialVersionUID = 1L;

	public int x;// x,y,z是长方体的左下角坐标
	public int y;
	public int z;
	public int xe;// xe,ye,ze分别是长方体三边长
	public int ye;
	public int ze;

	public Cuboid() {
		// 空构造器不能省
	}

	public Cuboid(int x, int y, int z, int xe, int ye, int ze) {// 用x,y,z,r来构造
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

	@Override
	public void drawOnBrainPicture(BrainPicture pic) {
		pic.drawCuboid(this);
	}

	@Override
	public void createCellsRegOrgan(Frog f, int orgNo) {// 创建Cell并登记Organ， 先忽略密度分布等参数
		for (int i = x; i < x + xe; i++)
			for (int j = y; j < y + ye; j++)
				for (int k = z; k < z + ze; k++) {
					Cell cell = f.getOrCreateCell(i, j, k);
					if (cell != null)
						cell.regOrgan(orgNo);
				}
	}

	public void createCells(Frog f) {// 创建Cell， 先忽略密度分布等参数
		for (int i = x; i < x + xe; i++)
			for (int j = y; j < y + ye; j++)
				for (int k = z; k < z + ze; k++)
					f.getOrCreateCell(i, j, k);
	}
}
