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
package com.gitee.drinkjava2.frog.brain.organ;

import java.awt.Color;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.brain.BrainPicture;
import com.gitee.drinkjava2.frog.brain.Cuboid;
import com.gitee.drinkjava2.frog.brain.Organ;

/**
 * Move is a special organ the action move photon go to next cell
 * 
 * MOVE_DIG类型的细胞会保持光子直线运动，并在下一个细胞上挖洞
 * 
 * @author Yong Zhu
 */
public class MoveDig extends Organ {
    private static final long serialVersionUID = 1L;

    public MoveDig() {
        super();
        this.shape = new Cuboid(9, 0, 0, 2, Env.FROG_BRAIN_YSIZE, Env.FROG_BRAIN_ZSIZE - 2);
        this.organName = "MOVE_DIG";
        this.type = Organ.MOVE_DIG; // MOVE_DIG类型的细胞会保持光子直线运动，并在下一个细胞上挖洞
        this.allowVary = false;// 不允许变异
        this.allowBorrow = false;// 不允许借出
    }

    @Override
    public void drawOnBrainPicture(Frog f, BrainPicture pic) { // 把器官的轮廓显示在脑图上
        if (!Env.SHOW_FIRST_FROG_BRAIN || !f.alive) // 如果不允许画或青蛙死了，就直接返回
            return;
        pic.setPicColor(Color.GREEN); // 缺省是灰色
        shape.drawOnBrainPicture(pic);
    }

}
