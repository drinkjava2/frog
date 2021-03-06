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
package com.gitee.drinkjava2.frog.organ.frog;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.brain.Organ;

/**
 * GuaGua create GuaGua sound
 */
public class FrogGuaGua extends Organ {// 呱呱这个器官的作用就是会发出呱呱叫声，这个叫声会被其它蛙听到
    private static final long serialVersionUID = 1L;
 
    @Override
    public void active(Animal a) {
        if (a.x < Env.ENV_WIDTH / 2 && this.beActivedByCells(a)) { //只有青蛙位于左侧才能呱呱
            sound[this.roundX()] = true; //叫声的频道只取决于GuaGua器官在脑内的x位置
        } 
    }

}
