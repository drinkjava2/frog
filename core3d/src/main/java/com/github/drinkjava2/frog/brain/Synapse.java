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
 * Synapse can be input, output, side synapse
 * 
 * 触突
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class Synapse implements Serializable {
	private static final long serialVersionUID = 1L;
	public int x; // 这个触突相对于细胞的x偏移坐标
	public int y;// 这个触突相对于细胞的y偏移坐标
	public int z;// 这个触突相对于细胞的z偏移坐标
	public int r; // 这个触突的作用范围
}
