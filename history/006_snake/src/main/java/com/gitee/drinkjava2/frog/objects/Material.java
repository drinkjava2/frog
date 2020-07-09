/* Copyright 2018-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.gitee.drinkjava2.frog.objects;

import java.awt.Color;

/**
 * Material store material types
 * 
 * 小于等于16384的位数用来标记青蛙序号，可利用Env.frogs.get(no-1)快速定位青蛙，其它各种材料用整数中其它位来表示
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Material {// NOSONAR

	public static final int FROG_TAG = 0b11111111111111; // 16383 小于等于16384的位数用来标记青蛙序号，可利用Env.frogs.get(no-1)快速定位青蛙

	private static int origin = FROG_TAG + 1; // 大于16384用来作为各种材料的标记

	private static int nextLeftShift() {// 每次将origin左移1位
		origin = origin << 1;
		if (origin < 0)
			throw new IllegalArgumentException("Material out of maximum range");
		return origin;
	}

	public static final int FOOD = nextLeftShift();
	public static final int FLY1 = nextLeftShift();// FLY1苍蝇是一种会移动的Food
	public static final int FLY2 = nextLeftShift();// FLY2苍蝇是一种会移动的Food
	public static final int FLY3 = nextLeftShift();// FLY3苍蝇是一种会移动的Food
	public static final int FLY4 = nextLeftShift();// FLY4苍蝇是一种会移动的Food
	public static final int ANY_FOOD = FOOD + FLY1 + FLY2 + FLY3 + FLY4;// ANY_FOOD是几种FOOD的位叠加

	public static final int SNAKE = nextLeftShift(); // 蛇的图形
	public static final int KILL_ANIMAL = nextLeftShift(); // if>=KILLFROG will kill animal
	public static final int BRICK = nextLeftShift();// brick will kill frog
	public static final int TRAP = nextLeftShift(); // trap will kill frog

	// 大于INVISIBLE的材料不显示在环境里，但有可能被青蛙或蛇看到，这是为了简化模式识别，蛇显示为蛇图形，但实际上目前它在环境中用一根线条来代表，以简化模型

	public static Color color(int material) {
		if ((material & TRAP) > 0)
			return Color.LIGHT_GRAY;
		else
			return Color.BLACK;
	}

}
