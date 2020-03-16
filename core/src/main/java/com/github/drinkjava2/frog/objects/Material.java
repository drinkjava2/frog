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
 * 用不同的数字常量代表虚拟环境中不同的组成材料，0为空，小于10的不可见，大于20的将杀死在同一位置出现的青蛙,例如砖头和青蛙不可以重叠出现在同一位置
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Material {
	public static final byte NO = 0; // nothing
	public static final byte SEESAW_BASE = 1; // 1~9 is invisible to frog

	public static final byte VISIBLE = 10; // if>=10 will visible to frog
	public static final byte FOOD = VISIBLE + 1;
	public static final byte SEESAW = VISIBLE + 2;

	public static final byte KILLFROG = 20; // if>=20 will kill frog
	public static final byte BRICK = KILLFROG + 1;// brick will kill frog
	public static final byte TRAP = KILLFROG + 2; // trap will kill frog

	public static Color color(byte material) {
		if (material == TRAP)
			return Color.LIGHT_GRAY;
		else
			return Color.BLACK;
	}
}
