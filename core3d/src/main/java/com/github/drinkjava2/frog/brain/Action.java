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
 * Action is the action of cell, one cell can have multiple actions
 * 
 * Action是细胞的行为，一个细胞Cell可以拥用多个action
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class Action { 
	public Organ organ; // 细胞属于哪个器官

	public Action(Organ organ) {// Action不保存在蛋里，不需要定义空构造器
		this.organ = organ;
	}

}
