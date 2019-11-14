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

/**
 * Relation is the relationship betweeen 2 holes
 * 
 * Relation 表示细胞上的两个洞之间存在关联关系，多对多关系用很多个一对一关系来表达
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class Relation {
	public Hole h1;
	public Hole h2;
	public int strength = 1;

	public Relation(Hole h1, Hole h2) {
		this.h1 = h1;
		this.h2 = h2;
	}

}
