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

import static com.gitee.drinkjava2.frog.Env.ENV_HEIGHT;
import static com.gitee.drinkjava2.frog.Env.ENV_WIDTH;
import static com.gitee.drinkjava2.frog.Env.FOOD_QTY;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Food randomly scatter on Env
 * 食物
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public enum Food implements EnvObject {
    FOOD; //FOOD是一个枚举型单例，整个环境只允许有一个FOOD实例

    public static final int SMELL_RANGE = 3;
    
    public static int food_ated=0;

    public static int[][] smell = new int[ENV_WIDTH][ENV_HEIGHT];//食物的香味, 这个香味是为了优化速度，和算法无关。有香味，说明食物在附近，程序才会启动眼睛，在视网膜产生光子,没有香味就不启动眼睛以加快速度

    @Override
    public void build() {
        food_ated=0;
        for (int i = 0; i < FOOD_QTY; i++) { // 随机位置生成食物
            int x = RandomUtils.nextInt(ENV_WIDTH);
            int y = RandomUtils.nextInt(ENV_HEIGHT);
            if (!Env.hasMaterial(x, y, Material.FOOD)) {
                Env.setMaterial(x, y, Material.FOOD); //在环境里标记上FOOD
                changeSmell(x, y, 1); //产生此食物的香气
            }
        }
    }

    @Override
    public void destory() {
        food_ated=0;
        for (int x = 0; x < ENV_WIDTH; x++) // 清除食物
            for (int y = 0; y < ENV_HEIGHT; y++) {
                Env.clearMaterial(x, y, Material.FOOD);
                smell[x][y] = 0; //清除所有香气
            }
    }

    @Override
    public void active() {
        //食物除了被吃，它自己没有什么活动
    }

    private static void changeSmell(int x, int y, int value) { //在食物的附近增加或减少它的香味 
        for (int xx = x - SMELL_RANGE; xx <= x + SMELL_RANGE; xx++)
            for (int yy = y - SMELL_RANGE; yy <= y + SMELL_RANGE; yy++)
                if (Env.insideEnv(xx, yy))
                    smell[xx][yy] += value;
    }

    public static boolean foundAndAteFood(int x, int y) {// 如果x,y有食物，将其清0，返回true
        if (Env.hasMaterial(x, y, Material.FOOD)) {
            food_ated++;
            Env.clearMaterial(x, y, Material.FOOD);//在环境里清除FOOD
            changeSmell(x, y, -1); //仅清除此食物产生的香气
            return true;
        }
        return false;
    }

}
