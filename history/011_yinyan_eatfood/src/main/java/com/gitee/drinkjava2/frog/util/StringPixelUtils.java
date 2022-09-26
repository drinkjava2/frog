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
 * 根据给定的字体和字符串，返回它的像素点阵，lettersMap[0][0]是左下角素
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class StringPixelUtils {
	private static final Map<String, byte[][]> lettersMap = new HashMap<>();//cache

	public static byte[][] getSanserif10Pixels(String s) {
		return getStringPixels(Font.SANS_SERIF, Font.PLAIN, 10, s);
	}
	
	public static byte[][] getSanserif12Pixels(String s) {
		return getStringPixels(Font.SANS_SERIF, Font.PLAIN, 12, s);
	}
	
	public static byte[][] getSanserifItalic10Pixels(String s) {
		return getStringPixels(Font.SANS_SERIF, Font.ITALIC, 10, s);
	}

    /* 在内存 BufferedImage里输出文本并获取它的像素点 */
    public static byte[][] getStringPixels(String fontName, int fontStyle, int fontSize, String s) {
        String key = new StringBuilder(fontName).append("_").append(fontStyle).append("_").append(fontSize).append("_")
                .append(s).toString();
        if (lettersMap.containsKey(key))
            return lettersMap.get(key);
        Font font = new Font(fontName, fontStyle, fontSize);

        BufferedImage bi = new BufferedImage(fontSize * 10, fontSize * 50, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int strHeight = fm.getAscent() + fm.getDescent() - 1;
        int strWidth = fm.stringWidth(s);
        g2d.drawString(s, 0, fm.getAscent() - fm.getLeading() -1);
        
        int ystart;//改进：在命令行和eclipse下会有不同的空行，所以要用ystart和yend来限定只获取有效象素行数
        loop1: for (ystart = 0; ystart < strHeight; ystart++)
            for (int x = 0; x < strWidth; x++) {
                if (bi.getRGB(x, ystart) == -1)
                    break loop1;
            }
        
        int yend;
        loop2: for (yend = strHeight; yend >= 0; yend--)
            for (int x = 0; x < strWidth; x++) {
                if (bi.getRGB(x, yend) == -1)
                    break loop2;
            }

        byte[][] b = new byte[strWidth][yend-ystart+1];
        for (int y = ystart; y <= yend; y++)
            for (int x = 0; x < strWidth; x++)
                if (bi.getRGB(x, y ) == -1)
                    b[x][yend-y] = 1;
                else
                    b[x][yend-y] = 0;
        lettersMap.put(key, b);
        return b;
    }

	/*- 这个是测试输出，平时不需要用 
	public static void main(String[] args) {
	    System.out.println("===============");
		byte[][] c = getStringPixels(Font.SANS_SERIF, Font.PLAIN, 12, "FROG");
		int w = c.length;
		int h = c[0].length;
	
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (c[x][h - y - 1] > 0)
					System.out.print("*");
				else
					System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println("===============");
	}
	*/
}
