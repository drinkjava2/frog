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
 * Shape represents a 3d zone in brain
 * 
 * Shape用来表示脑内器官的形状,一个器官只能有一个shape，但是多个器官可以在脑内重合，也就是说一个Cell可以属于多个器官
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public interface Shape extends Serializable {
	/* Draw self on brain picture */
	public void drawOnBrainPicture(BrainPicture pic); // 把自己在脑图上画出来

	/* Organ will call this method to create cells or register organ in cells */
	public void createCellsRegOrgan(Frog f, int orgNo); // 在Shape所代表的脑区内找到或创建Cell对象，并将器官号orgNo登记在cell里

}
