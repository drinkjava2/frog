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
 * CellAction defines cell action
 * 
 * CellAction的实现类只有一个act方法，它会根据细胞器官所属的类型作为不同的行为
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public interface CellAction {

	/**
	 * Each cell's act method will be called once at each loop step
	 * 
	 * act方法是只有锥器官才具备的方法，在每个测试步长中act方法都会被调用一次，这个方法针对不同的细胞类型有不同的行为逻辑，这是硬
	 * 编码，所以要准备多套不同的行为（可以参考动物脑细胞的活动逻辑)，然后抛给电脑去随机筛选，不怕多。
	 * 
	 */
	public void act(Cell cell, int x, int y, int z);
}
