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
 * 虚拟环境中每个点由一个int代表，多个材料可以同时出现在同一个点，每种材料用int中的一个bit位来表示，
 * 小于等于16384的位数用来标记青蛙序号，可利用Env.frogs.get(no-1)获取青蛙对象，其它各种材料用整数中其它位来表示
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Material {// NOSONAR

    public static final int FROG_TAG = 0b11111111111111; // 16383 小于等于16384的位数用来标记青蛙序号，可利用Env.frogs.get(no-1)快速定位青蛙

    private static int material = FROG_TAG + 1; // 大于16384用来作为各种材料的标记

    public static final int FOOD = nextMaterial();
    public static final int SNAKE = nextMaterial(); // 蛇的图形
    public static final int KILL_ANIMAL = nextMaterial(); // if>=KILLFROG will kill animal
    public static final int BRICK = nextMaterial();// brick will kill frog
    public static final int TRAP = nextMaterial(); // trap will kill frog

    private static int nextMaterial() {// 每次将material左移1位
        material = material << 1;
        if (material < 0)
            throw new IllegalArgumentException("Material out of maximum range");
        return material;
    }

    public static Color color(int material) {
        if ((material & TRAP) > 0)
            return Color.LIGHT_GRAY;
        else
            return Color.BLACK;
    }

}
