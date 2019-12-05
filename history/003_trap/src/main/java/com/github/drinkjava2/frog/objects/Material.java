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
 * Object means some thing in Env
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Material {
	public static final byte VISIBLE = 10; // if>=10 will visible to frog
	public static final byte KILLFROG = 20; // if>=20 will kill frog

	public static final byte NO = 0;
	public static final byte SEESAW_BASE = 1; // 1~9 is invisible to frog

	public static final byte FOOD = VISIBLE + 1;
	public static final byte SEESAW = VISIBLE + 2; // if <0 will not cause frog die

	public static final byte BRICK = KILLFROG + 1;
	public static final byte TRAP = KILLFROG + 2;

	public static Color color(byte material) {
		if (material == TRAP)
			return Color.LIGHT_GRAY;
		else
			return Color.BLACK;
	}
}
