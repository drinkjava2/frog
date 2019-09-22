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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * StringPixelUtils used to get pixel array from a given string
 * 
 * 根据给定的字体和字符串，返回它的像素点阵，返回结果是boolean[][]二维数组
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class StringPixelUtils {
	private static final Map<String, boolean[][]> lettersMap = new HashMap<>();

	public static boolean[][] getSanserif12Pixels(String s) {
		return getStringPixels(Font.SANS_SERIF, Font.PLAIN, 12, s);
	}

	/* 在内存 BufferedImage里输出文本并获取它的像素点 */
	public static boolean[][] getStringPixels(String fontName, int fontStyle, int fontSize, String s) {
		String key = new StringBuilder(fontName).append("_").append(fontStyle).append("_").append(fontSize).append("_")
				.append(s).toString();
		if (lettersMap.containsKey(key))
			return lettersMap.get(key);
		Font font = new Font(fontName, fontStyle, fontSize);

		BufferedImage bi = new BufferedImage(fontSize * 8, fontSize * 50, BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();
		int strHeight = fm.getAscent() + fm.getDescent() - 3;
		int strWidth = fm.stringWidth(s); 
		g2d.drawString(s, 0, fm.getAscent() - fm.getLeading() - 1); 
		boolean[][] b = new boolean[strHeight][strWidth];
		for (int y = 0; y < strHeight; y++)
			for (int x = 0; x < strWidth; x++)
				if (bi.getRGB(x, y) == -1)
					b[y][x] = true;
				else
					b[y][x] = false;
		lettersMap.put(key, b);
		return b;
	}

	public static void main(String[] args) {
		boolean[][] c = getStringPixels(Font.SANS_SERIF, Font.PLAIN, 10, "∮ABCD中国人");
		for (int y = 0; y < c.length; y++) {
			boolean[] line = c[y];
			for (int x = 0; x < line.length; x++)
				if (c[y][x])
					System.out.print("*");
				else
					System.out.print(" ");
			System.out.println();
		}
	}
}
