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
import com.gitee.drinkjava2.frog.util.Logger;

/**
 * Cells代表不同的脑细胞参数，对应每个参数，用8叉树算法生成不同的细胞。
 * 每个脑细胞用一个long来存储，所以最多允许64个基因位, 有时一个参数由多个基因位决定。每个基因位都由一个单独的阴阳8叉树控制，多个基因就组成了一个8叉树阵
 * 基因+分裂算法=结构。这个类里定义每个基因位的掩码和含义
 * 脑结构的所有参数，都要用基因来控制。开始时可以有常量、魔数，但以后都放到基因里去自动进化。
 * 
 * 
 * @author Yong Zhu
 * @since 10.0
 */
@SuppressWarnings("all")
public class Cells {
    public static long EXIST = /*                    */0b1L; //细胞存在否,1为存在,0为不存在
    public static long POSITIVE = /*               */ 0b10L; //细胞信号,1为正信号,0为负(抑制)信号
    public static long ZTX = /*                  */ 0b1100L; //axon x offset, 轴突x方向 (2个0), 轴突方向由x,y,z三个方向的参数组合决定
    public static long ZTY = /*                 */0b110000L; //轴突y方向 (4个0)
    public static long ZTZ = /*               */0b11000000L; //轴突z方向 (6个0)
    public static long ZT_LONG = /*        */0b11100000000L; //轴突长度 (8个0)
    public static long ST_LONG = /*      */0b1100000000000L; //dendrite length, 树突长度 (11个0)
    public static long ACTIVED = /*      */0b10000000000000L; //如此点为1，则此细胞位置处赋能量

    public static int GENE_NUMBERS = 14; //目前共有多少基因位    

    public static void active(Animal a) {//这个方法的功能是根据细胞的参数，在细胞间传输能量（即信息的载体)
        for (int z = Env.BRAIN_CUBE_SIZE - 1; z >= 0; z--)
            for (int y = Env.BRAIN_CUBE_SIZE - 1; y >= 0; y--)
                for (int x = Env.BRAIN_CUBE_SIZE - 1; x >= 0; x--) {
                    long cell = a.cells[x][y][z];
                    if ((cell & EXIST) == 0) //如细胞不存在，
                        continue;
                    if((cell & ACTIVED)>0)
                        a.energys[x][y][z]=10;
                    float e = a.energys[x][y][z];
                    if (e > 0) { //如细胞能量大于某阀值，则输出能量到位于轴突顶尖位置处，然后它们的树突如果在这个位置就会收到一份能量(即信息)
                        int x_ = (int) ((cell & ZTX) >> 2) - 2;//让x_位于-2,-1,1,2这个个数值中，表示x方向的坐标方向偏移，下同
                        if (x_ >= 0)
                            x_++;
                        int y_ = (int) ((cell & ZTY) >> 4) - 2;
                        if (y_ >= 0)
                            y_++;
                        int z_ = (int) ((cell & ZTZ) >> 6) - 2;
                        if (y_ >= 0)
                            y_++;
                        int zt_long = (int) ((cell & ZT_LONG) >> 8) + 1; //轴突长度, 大小为1~8
                        int xx = x + x_ * zt_long;
                        int yy = y + y_ * zt_long;
                        int zz = z + z_ * zt_long;
                        if (Env.insideBrain(xx, yy, zz)) {
                            if (a.energys[xx][yy][zz] < 5)
                                a.energys[xx][yy][zz]++;
                            if (a.energys[x][y][z] > 1)
                                a.energys[x][y][z]--;
                        }
                    }
                }
    }

}
