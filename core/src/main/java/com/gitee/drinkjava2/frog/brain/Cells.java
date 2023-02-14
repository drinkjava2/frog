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
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Cells代表不同的脑细胞参数，对应每个参数，用8叉树算法生成不同的细胞。
 * 每个脑细胞用一个long来存储，所以最多允许64个基因位, 有时一个参数由多个基因位决定。每个基因位都由一个单独的阴阳8叉树控制，多个基因就组成了一个8叉树阵列
 * 基因+分裂算法=结构
 * 基因+分裂算法+遗传算法=结构的进化
 * 这个类里定义每个基因位的掩码, 脑结构的所有参数，都要用基因来控制。开始时可以有常量、魔数，但以后都放到基因里去自动进化。
 * 
 * 
 * @author Yong Zhu
 * @since 10.0
 */
@SuppressWarnings("all")
public class Cells {
    public static int GENE_NUMBERS = 0;
    private static int zeros = 0; //当前基因位掩码0个数
    public static boolean[] display_gene = new boolean[64]; //用来控制哪些基因需要显示在脑图上

    public static int[] layer = new int[64]; //当内容大于-1时,表示基因只生成在脑核cell[x]单层2维数组上，此基因将采用4叉树平面分裂算法以提高效率
                                             //目前层只能位于yz平面上，因为Java的三维数组只写一个下标时正好返回一个二维数组    

    static {
        for (int i = 0; i < layer.length; i++)
            layer[i] = -1; 
    }

    public static long EXIST = register(1, true, -1); // 细胞存在否,1为存在,0为不存在。 register方法有三个参数，详见方法注释
    public static long MOVE_UP = register(1, true, -1); //如此点为1，则此细胞如有能量，青蛙向上移动
    public static long MOVE_DOWN = register(1, true, -1); //如此点为1，则此细胞如有能量，青蛙向下移动
    public static long MOVE_LEFT = register(1, true, -1); //如此点为1，则此细胞如有能量，青蛙向左移动
    public static long MOVE_RIGHT = register(1, true, -1); //如此点为1，则此细胞如有能量，青蛙向右移动
    public static long ZT_LONG = register(3, false, -1); //轴突长度
    public static long ZT_LONG0 = zeros; //如果参数由多位组成，用同名+0变量表示有几个0，移位运算时用来去除0。下同
    public static long ZT = register(1, true, -1);//axon exist 轴突是否存在
    public static long ZTX = register(1, false, -1);//axon x offset, 轴突x方向, 轴突方向由x,y,z三个方向的参数组合决定
    public static long ZTX0 = zeros; //x方向暂定1位，如果2位也可以，角度更细，但出结果的时间会比较长
    public static long ZTY = register(1, false, -1); //轴突y方向
    public static long ZTY0 = zeros;
    public static long ZTZ = register(1, false, -1); //轴突z方向
    public static long ZTZ0 = zeros;
    public static long ZT_MINUS = register(1, false, -1);//细胞信号,1为正信号,0为负(抑制)信号

    //    public static long RANDOM_ACTIVE = mask(1, true);//这个基因控制细胞随机产生能量，对结果影响不大，会让静止的青蛙颤抖

    //    public static long ST_LONG = mask(2, false); //dendrite radius, 树突长度半径，待后继版本用到
    //    public static long ST_LONG0 = zeros;
    //    public static long HAPPY = mask(1, false); //吃食后产生奖励信号，待后继版本用到

    /**
     * Register a gene 依次从底位到高位登记所有的基因掩码及对应的相关参数如是否显示在脑图上或是否只生成在某个yz平面上
     * 
     * @param maskBits how many mask bits 掩码位数
     * @param display whether to display the gene on the BrainPicture 是否显示在脑图
     * @param x gene only allow on specified layer 是否只生成在指定的x层对应的yz平面上
     * @return a long wtih mask bits 返回基因掩码，高位由n个1组成，低位是若干个0                                                                    *  
     */
    public static long register(int maskBits, boolean display, int x) {
        for (int i = GENE_NUMBERS; i < GENE_NUMBERS + maskBits; i++) {
            display_gene[i] = display;
            layer[i]=x;
        }

        String one = "";
        String zero = "";
        for (int i = 1; i <= maskBits; i++)
            one += "1";
        for (int i = 1; i <= GENE_NUMBERS; i++)
            zero += "0";
        zeros = GENE_NUMBERS;
        GENE_NUMBERS += maskBits;
        if (GENE_NUMBERS >= 64) {//
            System.out.println("目前基因位数不能超过64");
            System.exit(-1);
        }
        return Long.parseLong(one + zero, 2); //将"111000"这种字符串转换为长整
    }

    public static void active(Animal a) {//这个方法的功能是根据细胞的参数，在细胞间传输能量（即信息的载体)
        //if(true)return; //speeding
        for (int z = Env.BRAIN_CUBE_SIZE - 1; z >= 0; z--)
            for (int y = Env.BRAIN_CUBE_SIZE - 1; y >= 0; y--)
                for (int x = Env.BRAIN_CUBE_SIZE - 1; x >= 0; x--) {
                    long cell = a.cells[x][y][z];

                    if ((cell & EXIST) == 0) //如细胞不存在，
                        continue;

                    //                    if ((cell & RANDOM_ACTIVE) > 0) //随机产生细胞能量，会让青蛙颤抖
                    //                        if (RandomUtils.percent(5))
                    //                            a.energys[x][y][z] = 1;

                    float e = a.energys[x][y][z];
                    if (e > 0 && ((cell & ZT) > 0)) { //如有轴突基因，则当前细胞如存在能量，会输送到轴突端点处
                        int x_ = (int) ((cell & ZTX) >> ZTX0); //把掩码转为坐标偏移量，因为暂定只有1位，所以只有1,-1
                        if (x_ == 0)
                            x_ = -1;
                        int y_ = (int) ((cell & ZTY) >> ZTY0);
                        if (y_ == 0)
                            y_ = -1;
                        int z_ = (int) ((cell & ZTZ) >> ZTZ0);
                        if (z_ == 0)
                            z_ = -1;
                        int zt_long = (int) ((cell & ZT_LONG) >> ZT_LONG0) + 1; //轴突长度, 大小为1~8
                        int xx = x + x_ * zt_long;
                        int yy = y + y_ * zt_long;
                        int zz = z + z_ * zt_long;
                        if (Env.insideBrain(xx, yy, zz)) {
                            if (a.energys[xx][yy][zz] < 10) {
                                if ((cell & ZT_MINUS) > 0) //如果轴空是负信号
                                    a.energys[xx][yy][zz]--;
                                else
                                    a.energys[xx][yy][zz]++;
                                if (a.energys[x][y][z] > 0)
                                    a.energys[x][y][z]--;
                            }
                        }
                    }

                    e = a.energys[x][y][z];
                    if ((e > 0) && (z < Env.BRAIN_ZSIZE - 1)) { //如当前细胞有能量，且不和眼睛在同一层，且有移动细胞，则青蛙移动
                        if ((cell & MOVE_UP) > 0) {
                            a.energys[x][y][z] = 0;
                            a.y++;
                        }
                        if ((cell & MOVE_DOWN) > 0) {
                            a.energys[x][y][z] = 0;
                            a.y--;
                        }
                        if ((cell & MOVE_LEFT) > 0) {
                            a.energys[x][y][z] = 0;
                            a.x--;
                        }
                        if ((cell & MOVE_RIGHT) > 0) {
                            a.energys[x][y][z] = 0;
                            a.x++;
                        }
                    }

                }
    }

}
