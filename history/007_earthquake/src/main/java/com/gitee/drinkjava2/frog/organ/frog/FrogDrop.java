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
package com.gitee.drinkjava2.frog.organ.frog;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.brain.Organ;

/**
 * Frog drop let frog go back to ground 
 * 
 * 这个器官激活，青蛙会回到地上
 * 
 */
public class FrogDrop extends Organ {// FrogDrop这个器官如果激活会让青蛙回到地上
	private static final long serialVersionUID = 1L;

	@Override
	public void active(Animal a) {
		if (beActivedByCells(a)) {
			a.high = 0; // 跳起来了的青蛙用画小黄点表示，见Frog.show()方法，落回地上的没有小点
		} 
	}

}
