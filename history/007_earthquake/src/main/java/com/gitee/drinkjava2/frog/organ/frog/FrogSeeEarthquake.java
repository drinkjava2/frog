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
import com.gitee.drinkjava2.frog.objects.EarthQuake;

/**
 * Ear hear sound
 */
public class FrogSeeEarthquake extends Organ {// 当青蛙位于左侧，能看到是否地震
    private static final long serialVersionUID = 1L;

    @Override
    public void active(Animal a) {
        if( EarthQuake.activate > 0  && a.x<Env.ENV_WIDTH/2  ){
            activeCells(a, 30);
            return;
        }
    }

}
