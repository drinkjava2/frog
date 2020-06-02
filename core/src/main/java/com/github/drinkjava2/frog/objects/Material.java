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
package com.github.drinkjava2.frog.objects;

import java.awt.Color;

/**
 * Material store material types
 * 
 * 用不同的数字常量代表虚拟环境中不同的材料，0为空，每个材料用整数中的一位表示, 所以一个整数中可以表达15种不同的材料，并且这些材料可以同时出现
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Material {
	private static int origin = 1;

	private static int next() {// 每次将origin左移1位
		origin = origin << 1;
		if (origin < 0)
			throw new RuntimeException("");
		return origin;
	}

	public static final int NO = 0; // nothing
	public static final int VISIBLE = next(); // if visible to frog
	public static final int SEESAW = next();
	public static final int FOOD = next();
	public static final int FLY1 = next();// FLY1苍蝇是一种会移动的Food
	public static final int FLY2 = next();// FLY2苍蝇是一种会移动的Food
	public static final int FLY3 = next();// FLY3苍蝇是一种会移动的Food
	public static final int FLY4 = next();// FLY4苍蝇是一种会移动的Food

	public static final int KILLFROG = next(); // if>=KILLFROG will kill frog
	public static final int BRICK = next();// brick will kill frog
	public static final int TRAP = next(); // trap will kill frog

	public static Color color(int material) {
		if (material == TRAP)
			return Color.LIGHT_GRAY;
		else
			return Color.BLACK;
	}
}
