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

/**
 * Line is a line from cell1 to cell2
 * 
 * @author Yong Zhu
 * @since 2020-04-18
 */
public class Line implements Serializable {// Line代表一个从cell1到cell2的神经元连接,value表示传递能量的能力
	private static final long serialVersionUID = 1L;
	public float value;
	public Cell c1;
	public Cell c2;

	public Line(float value, Cell c1, Cell c2) {
		this.value = value;
		this.c1 = c1;
		this.c2 = c2;
	}

}
