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
import java.util.Arrays;

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
    public static long register(int maskBits, boolean display, boolean fill, int x_limit, int y_limit, int z_limit) {

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
        return register(1, true, false, pos[0], pos[1], pos[2]);
    }

    public static long registerFill(int... pos) {// 登记并手工指定基因填满的位置
        return register(1, true, true, pos[0], pos[1], pos[2]);
    }

    private static boolean is(long cell, long geneMask) { // 判断cell是否含某个基因
        return (cell & geneMask) > 0;
    }

    private static long b = 1; //全局静态变量，以实现is(cell)方法每调用一次b就移位一次的效果

    private static boolean is(long cell) { // 判断cell是否含b这个基因掩码，并左移位全局静态变量b一位
        boolean result = (cell & b) > 0;
        b = b << 1;
        if (b > totalGenesLenth)
            b = 1 / 0;
        return result;
    }

    private static int num(long cell, int n) { //cell以当前基因掩码b开始，连续取n位成为0~2^n之间的整数返回，并把全局静态变量b左移n位
        int result = 0;
        long bb = 1L;
        for (int i = 0; i < n; i++) {
            if ((cell & b) > 0)
                result += bb;
            bb = bb << 1;
            b = b << 1;
            if (b > totalGenesLenth)
                b = 1 / 0;
        }
        return result;
    }

    private static final int NA = -1;
    // ============开始登记基因==========

    // 登记细胞间关联(触突树突)
    static {
        register(16 + 16, true, false, 0, 0, NA); //先登记一些基因位，每个基因位的作用（对应各种细胞类型、行为）后面再说
    }

    private static long specialGenesStartFrom = 1L << 17;
    private static long totalGenesLenth = 1L << GENE_NUMBERS; //最大掩码不能超过这个值

    // ========= active方法在每个主循环都会调用，用来存放细胞的行为，这是个重要方法 ===========
    public static void active(Animal a, int step) {
        int start = 0; //start是常数数组的起始点 ，起点随机最小是0，最大是8*10;     
        float cellValve = a.consts[start++]; //细胞激活的阀值，神经元细胞至少能量多少，才会对激活输出细胞 

        if (step == 0) {//在首次调用时，统计一排细胞中如果至少有一个特殊基因在细胞里，加点分
            b = specialGenesStartFrom;
            for (int i = 0; i < 16; i++) {
                for (int z = 0; z < Env.BRAIN_SIZE; z++) {//本版本所有细胞都排成一条线，位于 z轴上 
                    long c = a.cells[0][0][z];
                    if (is(c, b)) {
                        a.fat++;
                        b = b << 1;
                        break;
                    }
                }
            }
        }

        if (step == 0) {//在首次调用时，初始化每个细胞的初始电阻，暂定一个细胞的所有连线初始电阻相同
            for (int z = 0; z < Env.BRAIN_SIZE; z++) {//本版本所有细胞都排成一条线，位于 z轴上 
                long c = a.cells[0][0][z]; //当前细胞是一个long类型   
                b = specialGenesStartFrom;
                float pRes = 0.14f * num(c, 3); //res 在0~1之间，初始值由基因决定的常数位置的值决定
                float nRes = 0.14f * num(c, 3); //res 在0~1之间
                for (int i = 0; i < Env.BRAIN_SIZE; i++) {
                    a.posWeight[z][i] = pRes;
                    a.negWeight[z][i] = nRes;
                }
            }
        }

        long bstart = specialGenesStartFrom << 6;
        for (int z = 0; z < Env.BRAIN_SIZE; z++) {//本版本所有细胞都排成一条线，位于 z轴上 
            long c = a.cells[0][0][z];
            float e = a.getEng(0, 0, z);//当前细胞的能量

            b = bstart; //特殊基因从17开始,基因本身没什么意义，你给它什么假设并且它正好进化出这个掩码位，它就拥有什么行为 

            if (is(c)) {//如果有active基因，此细胞始终激活. is方法在判断c有无b掩码后，将b左移一位
                a.setEngZ(z, 1);
            }

            if (is(c) && a.see1)//如果有像素1基因，且食物像素1出现，激活此细胞
                a.setEngZ(z, 1);
            else
                a.setEngZ(z, 0); //否则关闭此细胞激活

            if (is(c) && a.seeFoodComing)//如果看到食物正在靠近，激活此基因
                a.setEngZ(z, 1);
            else
                a.setEngZ(z, 0); //否则关闭此细胞激活

            if (is(c) && a.seeEmptyComing)//如果看到空白正在靠近，激活此基因
                a.setEngZ(z, 1);
            else
                a.setEngZ(z, 0); //否则关闭此细胞激活

            //下面是细胞之间的能量传送           
            if (e <= 0.1f) //能量太小不到阀值就跳过
                continue;

            if (is(c) && e > cellValve) {//如果是咬细胞，且处于激活态，咬下
                a.bite = true;
            }

            if (is(c) && e > cellValve) {//如果张嘴细胞激活，停止咬
                a.bite = false;
            }

            //下面要实现以下规则，规则针对细胞级别，产生的效果是宏观级别，宏观级别的效果可以用遗传算法来判定筛选。
            // 单个细胞反复活跃，增加它所有正权重, 宏观效果：单个视信号反复激活，会刺激无关的咬细胞激活
            // 两个细胞同时活跃，增加它们间的正权重， 宏观效果：两个不相干的信号相邻时间内激活，两个不相干信号会形成关联
            // 甜觉加强最近正权重，宏观效果：甜味加强正向行为条件反射
            // 痛觉加强最近负权重，宏观效果：苦味抑制正向条件反射，加强负向条件反射

            //TODO ================下面要改进，不是说有能量就要传的，两点模式下有的模式是毒食物，所以要利用甜苦味觉和记忆细胞，现学现改=============  
            //正权重和负权重每根线条上都是不同的，并不是一个可以共享的常量，存贮这些权重要花很多空间,如果一个细胞有16个正负连线，16*2就要有32个字节来保存权重

            boolean hasPosLines = is(c);//神经元是否有正权重连线
            boolean hasNegLines = is(c);//神经元是否有负权重连线

            b = 1; //从头开始，处理与相邻16个细胞之间的正权重能量传递
            if (hasPosLines) {//正权重线条
                for (int i = 0; i < Env.BRAIN_SIZE; i++) {
                    if (is(c)) {//如果包含某线胞的序号，就传送能量给这个细胞
                        float et = e * a.posWeight[z][i]; //要传输的能量=细胞能量*连线权重
                        a.addEng(0, 0, i, et);
                    }
                }
            }
            b = 1; //从头开始，处理与相邻16个细胞之间的负权重线条能量传递
            if (hasNegLines) {//负权重线条
                for (int i = 0; i < Env.BRAIN_SIZE; i++) {
                    if (is(c)) {//如果包含某线胞的序号，就传送能量给这个细胞
                        float et = -e * a.negWeight[z][i]; //要传输的能量=细胞能量*连线权重
                        a.addEng(0, 0, i, et);
                    }
                }
            }

        }

    }

}
