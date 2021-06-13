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
package com.gitee.drinkjava2.frog.util;

/**
 * PixelUtils used to do some transform of pixel picture
 * 
 * 针对给定的二维图像，进行一些变换， [0][0]是图像左下角素
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class PixelsUtils {

	public static byte[][] offset(byte[][] pixels, int xOff, int yOff) {// 对图像进行xOff和yOff偏移变换
		int w = pixels.length;
		int h = pixels[0].length;
		byte[][] result = new byte[w + Math.abs(xOff)][h + Math.abs(yOff)];
		for (int y = 0; y < h + Math.abs(yOff); y++) {
			for (int x = 0; x < w + Math.abs(xOff); x++) {
				int newX = x - xOff;
				int newY = y - yOff;
				if (newX >= 0 && newX < w && newY >= 0 && newY < h && pixels[newX][newY] != 0)
					result[x][y] = 1;
				else
					result[x][y] = 0;
			}
		}
		return result;
	}
}
