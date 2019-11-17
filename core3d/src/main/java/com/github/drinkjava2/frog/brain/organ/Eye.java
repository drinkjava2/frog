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
import com.github.drinkjava2.frog.util.ColorUtils;

/**
 * Eye can only see env material
 * 
 * @author Yong Zhu
 */
public class Eye extends Organ {// 眼睛是长方体
	private static final long serialVersionUID = 1L;

	public Eye() {
		this.shape = new Cuboid(0, 5, 5, 1, 13, 13);
		this.type = Organ.EYE;
		this.organName = "eye";
		this.allowVary = false;// 不允许变异
		this.allowBorrow = false;// 不允许借出
		this.color = ColorUtils.GRAY;
	}

	/** Clear image on retina */
	public void seeNothing(Frog f) {// 外界可以直接调用这个方法，清除视网膜图像
		f.setCuboidVales((Cuboid) shape, 0);
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

		// 在视网膜上产生字母像素点阵，即激活这个脑视网膜所在的cells区，然后由器官播种出的脑细胞负责将激活能量转为光子输送、存贮到其它位置
		for (int px = 0; px < w; px++)
			for (int py = 0; py < h; py++)
				if (pixels[px][py] > 0)
					f.getOrCreateCell(0, c.y + c.ye - px - 1, c.z + py).setEnergy(100);
	}
}
