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

import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Cuboid;
import com.github.drinkjava2.frog.brain.Organ;

/**
 * Ether is a special organ, it fill all rooms with dynamic type cells
 * 
 * Ether（以太)是个假想的特殊的器官，它均匀地在所有room放置类型为dynamic的脑细胞，也就是说细胞的所有触突是动态生成的，这是个临时
 * 类，试图模拟出波的驻点逆向成像，不清楚在没有引入更复杂的器官之前，是否仅凭单一的动态细胞加上其内部神经元参数的调整就可以形成复杂的网络，完成
 * 模式识别工作。基本原理是如果几个不同方向的信号传来，会在这几个方向上动态生成触突，以后只要有一个信号激活，其它方向也会产生反向的激活信号(光子)，多余的能量
 * 则贯穿细胞，送到下一个相邻的细胞去处理。
 * 
 * 以太这个器官因为会填充所有网格，当脑的维数变大时，将很快耗尽内存，所以只是暂时用来验证思路，以后这个器官会被删除，用正常的随机生成器官来代替。随机生成器官
 * 可以将内存溢出（捕获OutofMemoryException)作为一个扣分条件,再加上越多的脑细胞本身会消耗越多的能量，这样就可以会限制脑的发展大小不会超过虚拟机
 * 允许分配的内存大小。
 * 
 * @author Yong Zhu
 */
public class Ether extends Organ {
	private static final long serialVersionUID = 1L;

	public Ether() {
		super();
		this.shape = new Cuboid(0, 0, 0, Env.FROG_BRAIN_XSIZE, Env.FROG_BRAIN_YSIZE, Env.FROG_BRAIN_ZSIZE);
		this.organName = "Ether";
		this.type = Organ.DYNAMIC;
		this.allowVary = false;
		this.allowBorrow = false;
	}

	public void init(Frog f) { // 重写父类方法，均匀播种脑细胞
		for (int x = 0; x < Env.FROG_BRAIN_XSIZE; x++)
			for (int y = 0; y < Env.FROG_BRAIN_YSIZE; y++)
				for (int z = 0; z < Env.FROG_BRAIN_ZSIZE; z++) {
					f.getRoom(x, y, z).addCell(new Cell(this));
				}
	}

}
