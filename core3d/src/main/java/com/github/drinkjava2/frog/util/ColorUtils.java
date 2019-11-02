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
package com.github.drinkjava2.frog.util;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.MAGENTA;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.awt.Color.YELLOW;

import java.awt.Color;

/**
 * Color Utilities used in this project
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class ColorUtils {
	private static final Color[] rainbow = new Color[] { RED, ORANGE, YELLOW, GREEN, CYAN, BLUE, MAGENTA };
	private static int nextColor = 0;

	private ColorUtils() {// default private constr
	}

	public static int nextColorCode() {
		return nextColor++;
	}

	public static Color nextRainbowColor() {// 返回下一个彩虹色
		if (nextColor == rainbow.length)
			nextColor = 0;
		return rainbow[nextColor++];
	}

	public static Color colorByCode(int i) {// 数值取模后返回一个固定彩虹色
		return rainbow[i % 7];
	}

	public static Color rainbowColor(float i) { // 根据数值大小范围，在8种彩虹色中取值
		if (i == 0)
			return BLACK;
		if (i == 1)
			return RED;
		if (i <= 3)
			return ORANGE;
		if (i <= 10)
			return YELLOW;
		if (i <= 20)
			return GREEN;
		if (i <= 50)
			return CYAN;
		if (i <= 100)
			return BLUE;
		return MAGENTA;
	}
}
