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
 * Eye2
 * 这个眼睛能看到青蛙四周的nxn个像素点，作为模式识别的信号输入窗口
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Eye2 {

    /** First eye can only see if food nearby at 4 directions */
    public static void active(Animal a) {// 眼睛如发现任何物体，就将最上层对应像素点的细胞激活成1能量
        int r=Env.BRAIN_CUBE_SIZE / 2;
        for (int x = -r; x < r; x++)
            for (int y = -r; y < r; y++) {
                if(Env.foundAnyThingOrOutEdge(a.x+x, a.y+y)) {
                    a.cells[x+r][y+r][Env.BRAIN_ZSIZE-1]=1;
                }
            }
    }

}
