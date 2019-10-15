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

import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.BrainPicture;

/**
 * Organ is a part of frog, organ can be saved in egg
 * 
 * 器官是脑的一部分，多个器官在脑内可以允许部分或完全重叠出现在同一脑内位置，器官有些是不可以变异的，有些是可以变异的。
 * 器官主要分为两大类，一种是固定器官，通常手工创建，作用是进行信号的输入输出，在脑的指定位置的cube中产生光子或将光子转化为成青蛙的运动输出<br/>
 * 另一种是可变器官，比如ConeOrgan, 它负责在青蛙脑生成时播种脑细胞, 这类器官由随机变异机制生成。
 * 
 * Organ实例因为要保存在蛋里，所以要继承Serializable接口
 * 
 * @author Yong Zhu
 * @since 1.0.4
 */
public interface Organ extends Serializable {
	/** If allow borrowed in egg */
	public boolean allowBorrow();// 是否允许在精子和卵子结合时将这个器官借出，通常固定器官不允许借出，不参与进化

	/** Only call once when frog created , Child class can override this method */
	public void init(Frog f); // 在青蛙生成时调用这个方法，进行一些初始化，通常是根据参数来撒种脑细胞

	/** Each loop step call active method, Child class can override this method */
	public void active(Frog f); // 每一步测试都会调用器官的active方法 ，缺省啥也不干

	/** Child class can override this method to drawing picture */
	public void drawOnBrainPicture(Frog f, BrainPicture pic); // 把器官的轮廓显示在脑图上

	/** Only call once after organ be created by new() method */
	public Organ[] vary(); // 在下蛋时每个器官会调用这个方法，缺省返回一个类似自已的副本，子类通常要覆盖这个方法

}
