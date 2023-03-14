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
 * Cells代表不同的脑细胞参数，对应每个参数，用8叉树算法生成不同的细胞。
 * 每个脑细胞用一个long来存储，所以最多允许64个基因位, 有时一个参数由多个基因位决定。每个基因位都由一个单独的阴阳8叉树控制，多个基因就组成了一个8叉树阵列
 * 基因+分裂算法=结构
 * 基因+分裂算法+遗传算法=结构的进化
 * 这个类里定义每个基因位的掩码, 脑结构的所有参数，都要用基因来控制。开始时可以有常量、魔数，但以后都放到基因里去自动进化。
 * 
 * @author Yong Zhu
 * @since 10.0
 */
@SuppressWarnings("all")
public class Cells {
    public static int GENE_NUMBERS = 0;
    private static int zeros = 0; //当前基因位掩码0个数
    public static boolean[] display_gene = new boolean[64]; //用来控制哪些基因需要显示在脑图上

    public static int[] xLayer = new int[64]; //当内容大于-1时,表示基因只生成在脑核cell[x]单层2维数组上，此基因将采用4叉树平面分裂算法以提高效率
                                              //目前层只能位于yz平面上，因为Java的三维数组只写一个下标时正好返回一个二维数组    

    static {
        for (int i = 0; i < xLayer.length; i++)
            xLayer[i] = -1;
    }

    // 登记三个基因， register方法有三个参数，详见方法注释
 

    /**
     * Register a gene 依次从底位到高位登记所有的基因掩码及对应的相关参数如是否显示在脑图上或是否只生成在某个yz平面上
     * 
     * @param maskBits how many mask bits 掩码位数，即有几个1
     * @param display whether to display the gene on the BrainPicture 是否显示在脑图
     * @param x_layer gene only allow on specified layer 如大于-1，表示只生成在指定的x层对应的yz平面上
     * @return a long wtih mask bits 返回基因掩码，高位由n个1组成，低位是若干个0                                                                    *  
     */
    public static long register(int maskBits, boolean display, int x_layer) {
        for (int i = GENE_NUMBERS; i < GENE_NUMBERS + maskBits; i++) {
            display_gene[i] = display;
            xLayer[i] = x_layer;
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

    }

}
