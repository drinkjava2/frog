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

    public static boolean[] display_gene = new boolean[GENE_MAX]; // 如果这个参数为真，此基因显示在脑图上,此设定不影响逻辑
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
            dots.add(new Object[]{name, x_limit, y_limit, z_limit});
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

    public static boolean is(long cell, long geneMask) { // 判断cell是否含某个基因
        return (cell & geneMask) > 0;
    }

    private static final int NA = -1;
    private static final int CS4 = Env.BRAIN_SIZE / 4;

    // ============开始登记基因==========

    // 登记细胞间关联(触突树突)
    static {
        register(null, 24, true, false, 0, 0, -1); //先登记一些基因位，每个基因位的作用（对应各种细胞类型、行为）后面再说
    }

    // ========= active方法在每个主循环都会调用，用来存放细胞的行为，这是个重要方法 ===========
    public static void active(Animal a, int step) {
        int start = 0; //start是常数数组的起始点 ， 下面这些常量，先假设所有类型的细胞都共用相同的一组常量参数           
        float res = a.consts[start++]; //resistance, 常量电阻，即通常说的权重, 0时为断路，1时为全通，
        float not = a.consts[start++]; //not logic 反相器，如>0.5, 将会对通过的能量反相，即乘以-1
        float cellValve = a.consts[start++]; //细胞激活的阀值，神经元细胞至少能量多少，才会对激活输出细胞
        

        int hasSee1=0,hasSee2=0,hasBite=0,hasnotBite=0,hasSweet=0, hasBitter=0, hasActive=0;
        
        for (int z = 0; z < Env.BRAIN_SIZE; z++) {//本版本所有细胞都排成一条线，位于 z轴上 
            long c = a.cells[0][0][z];//当前细胞是一个long类型           
            float e = a.getEng(0, 0, z);//当前细胞的能量
            
            long b=1l<<17; //特殊基因从32开始,基因本身没什么意义，你给它什么意义它就代表什么
            boolean see1 = is(c, b); //是视细胞1?
            b=b<<1;
            boolean bite = is(c, b); //是咬细胞?
            b=b<<1;
            boolean notBite = is(c, b); //是不咬细胞?即张嘴
            b=b<<1;
            boolean sweet = is(c, b); //是甜细胞?
            b=b<<1;
            boolean bitter = is(c, b); //是苦细胞?
            b=b<<1;
            boolean active = is(c, b); //是活细胞? 
            
            if(see1)hasSee1=1; 
            if(bite)hasBite=1;
            if(notBite)hasnotBite=1;
            if(sweet)hasSweet=1;
            if(bitter)hasBitter=1;
            if(active)hasActive=1;
            
            if(active)
                a.setEngZ(z, 1);
                
            if (see1 & a.see1)//如果青蛙看到食物像素1，激活对应包含视细胞像素1基因的本细胞
                a.setEngZ(z, 1);
            else
                a.setEngZ(z, 0); 
           
            if (bite && e > cellValve) {//如果咬细胞激活，咬下
                a.bite = true;
            }

            if (notBite && e > cellValve) {//如果张嘴细胞激活，停止咬
                a.bite = false;
            }

            //下面是细胞之间的能量传送           
            if (e < 0.1f) //能量太小就跳过
                continue;
            e = e * res; //静态电阻 //如果有静态电阻，能量要损耗一些
            
            if (not > 0.5) //如果有反相器，能量要反相
                e = -e;
            
            b = 1;
            for (int i = 0; i < Env.BRAIN_SIZE; i++) { //能量传到target cell
                b=b<<1;
               if (is(c, b << i)) //如果包含某线胞的序号，就传送能量给这个细胞
                    a.addEng(0, 0, i, e);
            }
        }
         a.fat+=(hasSee1+hasBite+hasnotBite+hasSweet+ hasBitter+ hasActive); //如果有基因在细胞里，加点分  
    }

}
