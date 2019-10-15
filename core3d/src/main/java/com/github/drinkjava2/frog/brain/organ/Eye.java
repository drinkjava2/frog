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

/**
 * Eye can only see env material
 * 
 * @author Yong Zhu
 */
public class Eye extends FixedOrgan {// 这个眼睛有nxn个感光细胞，可以看到青蛙周围nxn网络内有没有食物
	private static final long serialVersionUID = 1L;
	public int n = 18; // 眼睛有n x n个感光细胞， 用随机试错算法自动变异(加1或减1，最小是3x3)

	public Eye() {
		x = 0;
		y = 5;
		z = 5;
		xe = 1;
		ye = 10;
		ze = 10;
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
					f.getCube(0, this.y + this.ye - px, this.z + py).setActive(20);
	}

	@Override
	public void active(Frog f) {// 这个是正常眼睛激活应该重写的方法，应该根据frog附件环境，激活脑内对应的cubes区
		// 暂空，因为在做字母测试，已经由外界直接塞字母的象素图到视网膜上了
	}

}
