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

import java.awt.Color;

/**
 * Color Utilities used in this project
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class ColorUtils {

    private static final Color[] rainbow = new Color[]{Color.GRAY, Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.MAGENTA, Color.CYAN};

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
        return rainbow[i % rainbow.length];
    }

    public static Color rainbowColor(float i) { // 根据数值大小范围，在8种彩虹色中取值
        if (i <= 20)
            return Color.GRAY;
        if (i <= 30)
            return Color.BLACK;
        if (i <= 50)
            return Color.RED;
        return Color.MAGENTA;
    }

    public static Color grayColor(float f) { // 根据数值大小范围0~1000，返回一个灰度色，越大越黑
        if (f > 1000)
            f = 1000;
        int i1 = 255 - (int) Math.round(f * .255);
        int i2 = 200 - (int) Math.round(f * .200);
        int i3 = 150 - (int) Math.round(f * .150);
        return new Color(i1, i2, i3);
    }
}
