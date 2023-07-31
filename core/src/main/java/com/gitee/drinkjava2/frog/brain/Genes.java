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
import com.gitee.drinkjava2.frog.objects.Eye;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Genes代表不同的脑细胞参数，对应每个参数，用8叉/4叉/2叉树算法给每个细胞添加细胞参数和行为。
 * 每个脑细胞用一个long来存储，所以目前最多允许64个基因位, 多字节参数可以由多个基因位决定。每个基因位都由一个单独的阴阳8/4/2叉树控制，多个基因就组成了一个8/4/2叉树阵列
 * 基因+分裂算法=结构
 * 基因+分裂算法+遗传算法=结构的进化
 * 
 * 这个类里定义每个基因位的掩码以及对应基因的细胞行为, 脑结构的所有参数，都要用基因来控制。开始时可以有常量、魔数，但以后都放到基因里去自动进化。
 * 
 * @author Yong Zhu
 * @since 10.0
 */
@SuppressWarnings("all")
public class Genes { //Genes登记所有的基因， 指定每个基因允许分布的空间范围。注意登录完后还要并针对每个基因在active方法里写出它对应的细胞行为
    public static int GENE_MAX = 64; //目前最多允许64个基因

    public static int GENE_NUMBERS = 0; //这里统计定义了多少个基因
    private static int zeros = 0; //当前基因位掩码0个数

    public static boolean[] display_gene = new boolean[GENE_MAX]; //用来控制哪些基因需要显示在脑图上

    public static int[] xLimit = new int[GENE_MAX]; //用来手工限定基因分布范围，详见register方法
    public static int[] yLimit = new int[GENE_MAX];
    public static int[] zLimit = new int[GENE_MAX];

    /**
     * Register a gene 依次从底位到高位登记所有的基因掩码及对应的相关参数
     * 
     * @param maskBits how many mask bits 掩码位数，即有几个1
     * @param display whether to display the gene on the BrainPicture 是否显示在脑图
     * @param x_limit gene only allow on specified x layer 如x_layer大于-1，且y_layer=-1, 表示只生成在指定的x层对应的yz平面上，这时采用4叉树而不是8叉树以提高进化速度
     * @param y_limit gene only allow on specified x, y axis 如大于-1，表示只生成在指定的x,y坐标对应的z轴上，这时采用2叉阴阳树算法
     * @param z_limit gene only allow on specified x, y, z 点上, 表示手工指定基因位于x,y,z坐标点上
     * @return a long wtih mask bits 返回基因掩码，高位由maskBits个1组成，低位是若干个0，以后判断一个cell上是否含有这个基因，只需要用cell对应的long和这个 掩码做与运算即可
     */
    public static long register(int maskBits, boolean display, int x_limit, int y_limit, int z_limit) {
        for (int i = GENE_NUMBERS; i < GENE_NUMBERS + maskBits; i++) {
            display_gene[i] = display;
            xLimit[i] = x_limit;
            yLimit[i] = y_limit;
            zLimit[i] = z_limit;
        }

        String one = "";
        String zero = "";
        for (int i = 1; i <= maskBits; i++)
            one += "1";
        for (int i = 1; i <= GENE_NUMBERS; i++)
            zero += "0";
        zeros = GENE_NUMBERS;
        GENE_NUMBERS += maskBits;
        if (GENE_NUMBERS >= GENE_MAX) {//
            System.out.println("目前基因位数不能超过" + GENE_MAX);
            System.exit(-1);
        }
        return Long.parseLong(one + zero, 2); //将类似"111000"这种字符串转换为长整
    }

    public static long register(int[] pos) {
        return register(1, true, pos[0], pos[1], pos[2]);
    }

    private static boolean hasGene(long cell, long geneMask) { //判断cell是否含某个基因 
        return (cell & geneMask) > 0;
    }

    private static final boolean SHOW = true;
    private static final int NA = -1;
    private static final int CS4 = Env.BRAIN_CUBE_SIZE / 4;

    //============开始登记有名字的基因==========
    public static long EYE = register(1, SHOW, 0, 0, NA); //视网膜细胞，这个版本暂时只允许视网膜分布在x=0,y=0的z轴上，即只能看到一条线状图形
    public static long MEM = register(1, SHOW, 1, 0, NA); //记忆细胞，暂时只允许它分布在x=1,y=0的z轴上

    public static int[] BITE_POS = new int[]{2, 0, 0};
    public static long BITE = register(BITE_POS); //咬动作细胞定义在一个点上, 这个细胞如激活，就咬食物

 //   public static int[] NOT_BITE_POS = new int[]{2, 0, CS4};
 //   public static long NOT_BITE = register(NOT_BITE_POS); //不咬动作细胞定义在一个点上, 这个细胞如激活，就不咬食物

    public static int[] SWEET_POS = new int[]{2, 0, CS4 * 2};
    public static long SWEET = register(SWEET_POS); //甜味感觉细胞定义在一个点上, 当咬下后且食物为甜，这个细胞激活

//    public static int[] BITTER_POS = new int[]{2, 0, CS4 * 3};
 //   public static long BITTER = register(BITTER_POS); //苦味感觉细胞定义在一个点上, 当咬下后且食物为苦，这个细胞激活

    //public static long FULL = register(1, SHOW, 0, 0); // 饱感觉细胞
    //public static long HUNGRY = register(1, SHOW, 0, 0); // 饿感觉细胞
    //public static long ANTI_SIDE = register(1, SHOW, 0, 0); // 侧抑制基因，模仿眼睛的侧抑制

    //========开始登记无名字的基因 =========
    static {
    }

    //========= active方法在每个主循环都会调用，用来存放细胞的行为，这是个重要方法  ===========
    public static void active(Animal a) {
        //        if (true)
        //            return; //speeding
        for (int z = Env.BRAIN_CUBE_SIZE - 1; z >= 0; z--)
            for (int y = Env.BRAIN_CUBE_SIZE - 1; y >= 0; y--)
                for (int x = Env.BRAIN_CUBE_SIZE - 1; x >= 0; x--) {
                    long cell = a.cells[x][y][z];
                    float energy = a.energys[x][y][z];

                    if (hasGene(cell, BITE)) {//如果没有输入，咬细胞也是有可能随机激活的，所有感觉细胞都有可能随机激活
                        if (RandomUtils.percent(10))
                            a.add(BITE_POS, 1);
                    }

                    if (energy > 0) { //如果细胞激活了  
                        if (hasGene(cell, BITE)) { //如果是咬细胞
//                            if ((Eye.code % 3) == 1) { //咬错了，苦+罚
//                                a.add(BITTER_POS, 1);
//                                a.penaltyAAA();
//                            }
                            if ((Eye.code % 3) == 2) { //咬中了，甜+奖 
                                a.add(SWEET_POS, 10);
                                a.awardAAA();
                            }
                            for (int i = 0; i < Env.BRAIN_CUBE_SIZE; i++) {
                                a.digHole(x, y, z, x - 1, y, i);
                            }
                        }

//                        if (hasGene(cell, NOT_BITE)) { //如果是不咬细胞 ，松口！
//                            a.add(BITE_POS, -1);
//                        }

                        if (hasGene(cell, EYE)) {//如果是视网膜细胞，在记忆细胞上挖洞                            
                            a.digHole(x, y, z, x + 1, y, z);
                        }

                        if (hasGene(cell, MEM)) {//如果是记忆细胞，在当前细胞所有洞上反向发送能量
                            a.sendEng(x, y, z);
                        }

                        a.energys[x][y][z] = (float)(a.energys[x][y][z] / 1.2f);//能量随时间衰减
                    }
                }
    }

}
