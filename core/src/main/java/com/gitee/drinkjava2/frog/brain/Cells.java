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

/**
 * Cells代表不同的脑细胞参数，对应每个参数，用8叉树或4叉树算法生成不同的细胞。
 * 每个脑细胞用一个long来存储，所以最多允许64个基因位, 多字节参数可以由多个基因位决定。每个基因位都由一个单独的阴阳8/4叉树控制，多个基因就组成了一个8/4叉树阵列
 * 基因+分裂算法=结构
 * 基因+分裂算法+遗传算法=结构的进化
 * 这个类里定义每个基因位的掩码, 脑结构的所有参数，都要用基因来控制。开始时可以有常量、魔数，但以后都放到基因里去自动进化。
 * 
 * 注：记忆细胞因为与所有脑核细胞有全连接，没必要安排记忆细胞的结构进化，所以这个类里省略所有记忆细胞的参数基因
 * 
 * @author Yong Zhu
 * @since 10.0
 */
@SuppressWarnings("all")
public class Cells { //Cells登记所有的基因(目前最多64个)， 指定每个基因允许分布的空间范围。并针对每个基因写出它所在的细胞对应的特性或行为。Cells这个名称改为Genes应该更合适，以后再说。
    public static boolean SHOW = true;
    public static int TREE8 = -1;

    public static int GENE_NUMBERS = 0;
    private static int zeros = 0; //当前基因位掩码0个数

    public static boolean[] display_gene = new boolean[64]; //用来控制哪些基因需要显示在脑图上

    /**
    如xLayer[g]=-1, 表示g这个基因允行分布在Cells的三维数组空间上，将采用阴阳8叉树3维细胞分裂算法
    如xLayer[g]=0~n-1, yLayer[g]=-1, 表示g这个基因允行分布在Cells[xLayer[g]]所在的yz二维数组平面上， 将采用阴阳4叉树平面分裂算法以提高效率。目前只能位于yz平面上，因为Java三维数组写一个下标返回一个二维数组。
    如xLayer[g]=0~n-1, yLayer[g]=0~n-1, 表示g这个基因允行分布在Cells[xLayer[g]][yLayer[g]]所在的z单维数组轴线上，将采用阴阳2叉树单轴分裂算法以提高效率。目前只能位于z轴上，因为Java三维数组写2个下标返回一个单维数组。
    */
    public static int[] xLayer = new int[64];
    public static int[] yLayer = new int[64];

    static {
        for (int i = 0; i < xLayer.length; i++)
            xLayer[i] = -1;
    }

    // 登记基因， register方法有四个参数，详见方法注释。每个基因登记完后，还要在active方法里写它的细胞行为。
    //public static long ANTI_SIDE = register(1, SHOW, 0); // 侧抑制基因，只分布在0层上，模仿眼睛的侧抑制

    static {
       // register(1, true, D2Judge.pic1.xLayer, -1);
        

    }

    /**
     * Register a gene 依次从底位到高位登记所有的基因掩码及对应的相关参数如是否显示在脑图上或是否只生成在某个x坐标对应的yz平面上，或xy坐标对应的z轴上
     * 
     * @param maskBits how many mask bits 掩码位数，即有几个1
     * @param display whether to display the gene on the BrainPicture 是否显示在脑图
     * @param x_layer gene only allow on specified x layer 如x_layer大于-1，且y_layer=-1, 表示只生成在指定的x层对应的yz平面上，这时采用4叉树而不是8叉树以提高进化速度
     * @param y_layer gene only allow on specified x, y axis 如大于-1，表示只生成在指定的x,y坐标对应的z轴上，这时采用2叉阴阳树算法
     * @return a long wtih mask bits 返回基因掩码，高位由maskBits个1组成，低位是若干个0，以后判断一个cell上是否含有这个基因，只需要用cell对应的long和这个 掩码做与运算即可
     */
    public static long register(int maskBits, boolean display, int x_layer, int y_layer) {
        for (int i = GENE_NUMBERS; i < GENE_NUMBERS + maskBits; i++) {
            display_gene[i] = display;
            xLayer[i] = x_layer;
            yLayer[i] = y_layer;
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
        return Long.parseLong(one + zero, 2); //将类似"111000"这种字符串转换为长整
    }

    public static void active(Animal a) {//active方法在每个主循环都会调用，通常用来存放细胞的行为，这是个重要方法
        //if(true)return; //speeding
        //        for (int z = Env.BRAIN_CUBE_SIZE - 1; z >= 0; z--)
        //            for (int y = Env.BRAIN_CUBE_SIZE - 1; y >= 0; y--)
        //                for (int x = Env.BRAIN_CUBE_SIZE - 1; x >= 0; x--) {
        //                    long cell = a.cells[x][y][z];
        //                    float e = a.energys[x][y][z];
        //                    //TODO work on here
        //                }
    }
}
