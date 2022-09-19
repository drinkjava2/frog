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

/**
 * Move, if found cell energy at bottom level, then move animal 
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Move {

    /** First eye can only see if food nearby at 4 directions */
    public static void active(Animal a) {// Move器官，如发现最下面一层细胞有能量，就移动青蛙 
        if (a.energys[Env.BRAIN_XSIZE / 2][Env.BRAIN_YSIZE - 1][1] > 0) {
            a.energys[Env.BRAIN_XSIZE / 2][Env.BRAIN_YSIZE - 1][1]--;
            a.y++;
        }
        if (a.energys[Env.BRAIN_XSIZE / 2][0][1] > 0) {
            a.energys[Env.BRAIN_XSIZE / 2][0][1]--;
            a.y--;
        }

        if (a.energys[0][Env.BRAIN_YSIZE / 2][1] > 0) {
            a.energys[0][Env.BRAIN_YSIZE / 2][1]--;
            a.x++;
        }

        if (a.energys[Env.BRAIN_XSIZE - 1][Env.BRAIN_YSIZE / 2][1] > 0) {
            a.energys[Env.BRAIN_XSIZE - 1][Env.BRAIN_YSIZE / 2][1]--;
            a.x--;
        }

    }

}
