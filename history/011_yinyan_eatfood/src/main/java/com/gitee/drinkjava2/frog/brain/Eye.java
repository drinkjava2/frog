/*
 * Copyright 2018 the original author or authors. 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.gitee.drinkjava2.frog.brain;

import java.awt.Color;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.objects.Material;

/**
 * Eye, if found food then set cell energy at top level 
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Eye {

    /** First eye can only see if food nearby at 4 directions */
    public static void active(Animal a) {// 眼睛只能观察上、下、左、右四个方向有没有食物，如发现食物，就将最上层的四个边的细胞激活成1能量
        int seeDist = 15; //眼睛能看多远
        for (int i = 1; i < seeDist; i++)
            if (Env.hasMaterial(a.x, a.y + i, Material.FOOD)) {//up 
                for (int x = 1; x < Env.BRAIN_XSIZE; x++)
                    a.energys[x][Env.BRAIN_YSIZE - 1][Env.BRAIN_ZSIZE - 1] = 1;
                break;
            }

        for (int i = 1; i < seeDist; i++)
            if (Env.hasMaterial(a.x, a.y - i, Material.FOOD)) { //down 
                for (int x = 0; x < Env.BRAIN_XSIZE - 1; x++)
                    a.energys[x][0][Env.BRAIN_ZSIZE - 1] = 1;
                break;
            }

        for (int i = 1; i < seeDist; i++)
            if (Env.hasMaterial(a.x - i, a.y, Material.FOOD)) { //left
                for (int y = 1; y < Env.BRAIN_YSIZE; y++)
                    a.energys[0][y][Env.BRAIN_ZSIZE - 1] = 1;
                break;
            }

        for (int i = 1; i < seeDist; i++)
            if (Env.hasMaterial(a.x + i, a.y, Material.FOOD)) {//right
                for (int y = 0; y < Env.BRAIN_YSIZE - 1; y++)
                    a.energys[Env.BRAIN_XSIZE - 1][y][Env.BRAIN_ZSIZE - 1] = 1;
                break;
            }

    }

    public static void drawEye(BrainPicture bp) {//在脑图中画出眼睛
        bp.setPicColor(Color.RED);
        for (int x = 1; x < Env.BRAIN_XSIZE; x++)
            bp.drawCircle(x + 0.5f, Env.BRAIN_YSIZE - 0.5f, Env.BRAIN_ZSIZE - 0.5f, 1);
        bp.setPicColor(Color.YELLOW);
        for (int x = 0; x < Env.BRAIN_XSIZE - 1; x++)
            bp.drawCircle(x + 0.5f, 0.5f, Env.BRAIN_ZSIZE - 0.5f, 1);
        bp.setPicColor(Color.BLUE);
        for (int y = 1; y < Env.BRAIN_YSIZE; y++)
            bp.drawCircle(0.5f, y + 0.5f, Env.BRAIN_ZSIZE - 0.5f, 1);
        bp.setPicColor(Color.GREEN);
        for (int y = 0; y < Env.BRAIN_YSIZE - 1; y++)
            bp.drawCircle(Env.BRAIN_XSIZE - 0.5f, y + 0.5f, Env.BRAIN_ZSIZE - 0.5f, 1);

    }

}
