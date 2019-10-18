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
package com.github.drinkjava2.frog.brain.organ;

import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.Cuboid;

/**
 * Eye can only see env material
 * 
 * @author Yong Zhu
 */
public class Eye extends Organ {// 眼睛是长方体，所以它的cuboid不为空
	private static final long serialVersionUID = 1L;
	public int n = 18; // 眼睛有n x n个感光细胞， 用随机试错算法自动变异(加1或减1，最小是3x3)

	public Eye() {
		this.cuboid = new Cuboid(0, 5, 5, 1, 10, 10);
	}

	/**
	 * Accept a byte[x][y] array, active tubes located on eye's retina
	 * 
	 * 接收一个二维数组，激活它视网膜所在的脑空间
	 */
	public void seeImage(Frog f, byte[][] pixels) {// 外界可以直接调用这个方法，硬塞一个象素图到视网膜上
		if (!f.alive)
			return;
		int w = pixels.length;
		int h = pixels[0].length;

		// 在视网膜上产生字母像素点阵，即激活这个脑视网膜所在的cubes区，然后由器官播种出的脑细胞负责将激活能量转为光子输送、存贮到其它位置
		for (int px = 0; px < w; px++)
			for (int py = 0; py < h; py++)
				if (pixels[px][py] > 0)
					f.getCube(0, this.cuboid.y + this.cuboid.ye - px, this.cuboid.z + py).setActive(20);
	}

}
