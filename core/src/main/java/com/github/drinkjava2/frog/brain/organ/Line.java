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

import java.io.Serializable;

import com.github.drinkjava2.frog.brain.Cell;

/**
 * Line is a line from cell1 to cell2
 * 
 * @author Yong Zhu
 * @since 2020-04-18
 */
public class Line implements Serializable {// Line代表一个从cell1到cell2的神经元连接,energy表示连接能量
	private static final long serialVersionUID = 1L;
	public float energy;
	public int fat = 0;
	public int x1, y1, z1, x2, y2, z2;

	public Line() {
		// 缺省构造器必有, 因为从磁盘上反序列化要先调用这个构造器
	}

	public Line(Cell c1, Cell c2) {
		this.x1 = c1.x;
		this.y1 = c1.y;
		this.z1 = c1.z;
		this.x2 = c2.x;
		this.y2 = c2.y;
		this.z2 = c2.z;
	}

}
