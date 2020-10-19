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
 * GuaGua create sound
 */
public class FrogGuaGuaStop extends Organ {// FrogGuaGuaStop这个器官的唯一作用就是在激活时停止发出呱呱叫声
	private static final long serialVersionUID = 1L;

	@Override
	public void active(Animal a) {
		if (this.beActivedByCells(a)) {
			a.guagua = false;
		}
	}

}
