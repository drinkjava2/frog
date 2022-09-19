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
    public static void active(Animal a) {// 眼睛只能观察上、下、左、右四个方向有没有食物，如发现食物，就将最上层的四个细胞激活成10能量
        int seeDist = 10; //眼睛能看多远
        for (int i = 1; i < seeDist; i++)
            if (Env.hasMaterial(a.x, a.y + i, Material.FOOD)) {//up 
                a.energys[Env.BRAIN_XSIZE / 2][Env.BRAIN_YSIZE - 1][Env.BRAIN_ZSIZE - 1] = 10;
                break;
            }

        for (int i = 1; i < seeDist; i++)
            if (Env.hasMaterial(a.x, a.y - i, Material.FOOD)) { //down 
                a.energys[Env.BRAIN_XSIZE / 2][0][Env.BRAIN_ZSIZE - 1] = 10;
                break;
            }

        for (int i = 1; i < seeDist; i++)
            if (Env.hasMaterial(a.x - i, a.y, Material.FOOD)) { //left
                a.energys[0][Env.BRAIN_YSIZE / 2][Env.BRAIN_ZSIZE - 1] = 10;
                break;
            }

        for (int i = 1; i < seeDist; i++)
            if (Env.hasMaterial(a.x + i, a.y, Material.FOOD)) {//right
                a.energys[Env.BRAIN_XSIZE - 1][Env.BRAIN_YSIZE / 2][Env.BRAIN_ZSIZE - 1] = 10;
                break;
            }

    }

}
