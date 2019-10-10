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

import com.github.drinkjava2.frog.Frog;

/**
 * CellTemplate is the cell template, a group of CellTemplate be saved in egg
 * 
 * CellTemplate描述细胞的形状和行为，其中触突数量和参数是有可能随机变异的。行为则是硬编码不可以变异，模拟单个神经元的逻辑，不同的细胞有不同的
 * 行为，通过cellType来区分，虽然行为不可以变异，但是可以写出尽可能多种不同的行为，由生存竟争来筛选。
 * CellTemplate是可串行化的，一组CellTemplate实例会保存在蛋里面。
 * 每个脑细胞都指向某个type类型，CellTemplate本身都是单例，不占内存，从命名上可以看出它只是个模板，它是无状态的，只影响 cell处理
 * 信息（光子）的逻辑
 * 
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class CellTemplate implements Serializable {
	private static final long serialVersionUID = 1L;
	public int type; // 当创建CellType实例
	public Synapse[] inputs; // 输入触突，位置是相对细胞而言的
	public Synapse[] sides; // 侧面（通常是抑制，是负光子输出)输出触突们，才从脉冲神经网络了解到有这种侧向抑制
	public Synapse[] outputs; // 输出触突

	/**
	 * Each cell's act method will be called once at each loop step
	 * 
	 * 在轮循时，每个细胞的act方法都会被调用一次，这个方法针对不同的细胞类型有不同的行为逻辑，这是硬编码，要 多尝试各种不同的行为，然后抛给电脑去筛选。
	 * 
	 */
	public void act(Frog f, Cell cell, int x, int y, int z) {
		switch (type) { // TODO 待添加细胞的行为，这是硬编码
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		default:
			break;
		}
	}
}
