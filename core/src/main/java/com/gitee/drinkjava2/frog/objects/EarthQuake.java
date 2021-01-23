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
import java.awt.Graphics;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * EarthQuake make noise in a very tiny zone, after a while will hurt frogs on
 * ground
 * 
 * 地震产生很小范围的声音，但是过一会后所有在地上没跳起的青蛙将受到伤害，能量被减除
 * 
 * 地震是听力/发音实验中的一个测试物体
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class EarthQuake implements EnvObject {
    public static int activate = 0;

    @Override
    public void build() {
    }

    @Override
    public void destory() {
    }

    @Override
    public void active() {
        if (activate == 0 && RandomUtils.percent(0.5f)) { // 有小机率启动地震
            activate = 1;
        }

        if (activate > 0)
            activate++; // 地震如果启动，强度就开始变大

        if (activate > 50)
            activate = 0; // 直到最大值后归零

        if (activate > 0) {// 地震开始杀所有没跳在空中的青蛙
            for (Frog frog : Env.frogs) {
                if (frog.high == 0)
                    frog.energy -=1000;
            }
        }
    }

    @Override
    public void display() {
        if (activate > 0) {//如果地震了，在地图上画出红框
            Graphics g = Env.buffImg.getGraphics();
            g.setColor(Color.RED);
            g.drawRect(0, 0, Env.ENV_WIDTH - 1, Env.ENV_HEIGHT - 1);
        }
    }

}
