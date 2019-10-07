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

import com.github.drinkjava2.frog.brain.Organ;

/**
 * Cone is a cone-cylinder shape brain organ, its size, angle, location and cell
 * parameters are randomly created
 * 
 * Cone 是一种外形为锥圆柱体的脑内器官，它的作用是在它的形状范围内播种脑细胞， 这是第一个也是最重要的脑随机播种器官，它的数
 * 量、形状、大小、角度、位置、神经元分布方式、神经元内部参数完全是随机的生成的，Cone可以遗传变异，并由生存竟争来淘汰筛选。
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class Cone extends Organ {
	private static final long serialVersionUID = 1L;
	public float startX; // 这6个变量定义了cone的中心线起点和终点
	public float startY;
	public float startZ;
	public float endX;
	public float endY;
	public float endZ;

	public float startR = 8; // 起点的半径
	public float endR = 8; // 终点的半径

	public int startType = 0; // 起点端面类型， 0表示范围一直延长到边界, 1表示是个以startR为半径的球面
	public int endType = 0; // 终点端面类型， 0表示范围一直延长到边界, 1表示是个以startR为半径的球面

}
