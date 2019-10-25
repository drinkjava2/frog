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
import com.github.drinkjava2.frog.brain.Organ;

/**
 * Eye can only see env material
 * 
 * @author Yong Zhu
 */
public class Eye extends Organ {// 眼睛是长方体
	private static final long serialVersionUID = 1L;

	public Eye() {
		this.shape = new Cuboid(0, 5, 5, 1, 10, 10);
		this.organName = "eye";
		this.allowVary = false;
		this.allowBorrow = false;
	}

	public void init(Frog f) { // 重写父类方法，播种视网膜细胞，它会将视网膜的激活转变成固定向右发散的多个光子，摸拟波源
		shape.fillCells(f, this);
	}

	/** each step will call Organ's active methodd */
	public void active(Frog f) { // 每一步测试都会调用active方法，通常用于手动生成的器官
		// do nothing
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
		Cuboid c = (Cuboid) shape;

		// 在视网膜上产生字母像素点阵，即激活这个脑视网膜所在的rooms区，然后由器官播种出的脑细胞负责将激活能量转为光子输送、存贮到其它位置
		for (int px = 0; px < w; px++)
			for (int py = 0; py < h; py++)
				if (pixels[px][py] > 0)
					f.getRoom(0, c.y + c.ye - px, c.z + py).setActive(20);
	}

}
