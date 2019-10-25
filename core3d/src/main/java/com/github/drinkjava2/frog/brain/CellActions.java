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
 * CellActions have many cell action stored
 * 
 * CellActions登记了许多硬编码的脑细胞活动单例，用act方法来调用这些活动，type参数来区分调用哪个单例的方法
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class CellActions {

	/*-
	 * Each cell's act method will be called once at each loop step
	 * 
	 * act方法是只有锥器官才具备的方法，在每个测试步长中act方法都会被调用一次，这个方法针对不同的细胞类型有不同的行为逻辑，这是硬
	 * 编码，所以要准备多套不同的行为（可以参考动物脑细胞的活动逻辑)，然后抛给电脑去随机筛选，不怕多。
	 * 
	 * 举例来说，以下是一些假想中的脑细胞行为：
	 * 一对一，穿透，光子会穿过细胞，细胞起到中继站的作用，如果没有细胞中继，光子在真空中传播(即三维数组的当前坐标没有初始化，为空值)会迅速衰减
	 * 一对一，转向，光子传播角度被改变成另一个绝对角度发出
	 * 一对一，转向，光子传播角度被改变成与器官有关的角度发出，可以模拟光线的发散(如视网膜细胞)和聚焦(如脑内成像，即沿光线发散的逆路径)
	 * 一对多，拆分，入射光子被拆分成多个光子，以一定的发散角发出，通常发散光子的总能量小于入射+细胞输出能量之和
	 * 一对多，拆分，入射光子被拆分成多个光子，发散角与器官相关 
	 * 多对一，聚合，入射光子被触突捕获
	 */
	public void act(Frog f, int type, Cell cell, int x, int y, int z) {
		switch (type) { //添加细胞的行为，这是硬编码
		case Organ.EYE:
			

			break;
		default:
			break;
		}
	}
}
