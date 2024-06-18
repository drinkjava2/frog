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

import java.util.ArrayList;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;

/**
 * Genes代表不同的脑细胞参数，对应每个参数，用8叉/4叉/2叉树算法给每个细胞添加细胞参数和行为。
 * 每个脑细胞用一个long来存储，所以目前最多允许64个基因位,
 * 多字节参数可以由多个基因位决定。每个基因位都由一个单独的阴阳8/4/2叉树控制，多个基因就组成了一个8/4/2叉树阵列 基因+分裂算法=结构
 * 基因+分裂算法+遗传算法=结构的进化
 * 
 * 这个类里定义每个基因位的掩码以及对应基因的细胞行为, 脑结构的所有参数，都要用基因来控制。开始时可以有常量、魔数，但以后都放到基因里去自动进化。
 * 
 * @author Yong Zhu
 * @since 10.0
 */
@SuppressWarnings("all")
public class Genes { // Genes登记所有的基因， 指定每个基因允许分布的空间范围。注意登录完后还要并针对每个基因在active方法里写出它对应的细胞行为
    public static int GENE_MAX = 64; // 目前最多允许64个基因

    public static int GENE_NUMBERS = 0; // 这里统计定义了多少个基因
    private static int zeros = 0; // 当前基因位掩码0个数

    public static boolean[] display_gene = new boolean[GENE_MAX]; // 如果这个参数为真，此基因显示在脑图上
    public static boolean[] fill_gene = new boolean[GENE_MAX]; // 如果这个参数为真，此基因填充指定的区域，而不是由分裂算法随机生成

    public static int[] xLimit = new int[GENE_MAX]; // 用来手工限定基因分布范围，详见register方法
    public static int[] yLimit = new int[GENE_MAX];
    public static int[] zLimit = new int[GENE_MAX];

    public static ArrayList<Object[]> dots = new ArrayList<>(); // 临时，如果登录的范围是个三座标点，把它放在这里，以方便随机连线只落在登记的细胞上

    /**
     * Register a gene 依次从底位到高位登记所有的基因掩码及对应的相关参数
     * 
     * @param name
     *            基因的名字，这个可选， 缺省为null
     * @param maskBits
     *            how many mask bits 掩码位数，即有几个1
     * @param display
     *            whether to display the gene on the BrainPicture 是否显示在脑图
     * @param fill
     *            whether to fill to specified 3D/2D/1D/1Point area
     *            是否直接用此基因填充指定的区域，区域可以是三维、二维、线状及一个点
     * @param x_limit
     *            gene only allow on specified x layer 如x_layer大于-1，且y_layer=-1,
     *            表示只生成在指定的x层对应的yz平面上，这时采用4叉树而不是8叉树以提高进化速度
     * @param y_limit
     *            gene only allow on specified x, y axis
     *            如大于-1，表示只生成在指定的x,y坐标对应的z轴上，这时采用2叉阴阳树算法
     * @param z_limit
     *            gene only allow on specified x, y, z 点上, 表示手工指定基因位于x,y,z坐标点上
     * @return a long wtih mask bits
     *         返回基因掩码，高位由maskBits个1组成，低位是若干个0，以后判断一个cell上是否含有这个基因，只需要用cell对应的long和这个
     *         掩码做与运算即可
     */
    public static long register(String name, int maskBits, boolean display, boolean fill, int x_limit, int y_limit, int z_limit) {
        if (x_limit > -1 && y_limit > -1 && z_limit > -1) {// 临时，如果登录的范围是个三座标点，把它放在这里，以方便随机连线只落在登记的细胞上
            dots.add(new Object[] {name, x_limit, y_limit, z_limit });
        }

        for (int i = GENE_NUMBERS; i < GENE_NUMBERS + maskBits; i++) {
            display_gene[i] = display;
            fill_gene[i] = fill;
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
        return Long.parseLong(one + zero, 2); // 将类似"111000"这种字符串转换为长整
    }

    public static long register(int... pos) {// 登记并指定基因允许分布的位置
        return register(null, 1, true, false, pos[0], pos[1], pos[2]);
    }

    public static long registerFill(int... pos) {// 登记并手工指定基因填满的位置
        return register(null, 1, true, true, pos[0], pos[1], pos[2]);
    }

    public static long registerFill(String name, int... pos) {// 登记并手工指定基因填满的位置
        return register(name, 1, true, true, pos[0], pos[1], pos[2]);
    }

    public static boolean hasGene(long cell, long geneMask) { // 判断cell是否含某个基因
        return (cell & geneMask) > 0;
    }

    private static final int NA = -1;
    private static final int CS4 = Env.BRAIN_SIZE / 4;

    // ============开始登记基因==========
       

    // 登记细胞间关联(触突树突)
    static {
        register(null, 32,true, false, 0,0,-1); //先登记32个基因位，每个基因位的作用（对应各种细胞类型、行为）后面再说
    }

    // ========= active方法在每个主循环都会调用，用来存放细胞的行为，这是个重要方法 ===========
    //但是本版本因为没用到分裂算法，所以这个方法体为空
    public static void active(Animal a, int step) {

    }

}
