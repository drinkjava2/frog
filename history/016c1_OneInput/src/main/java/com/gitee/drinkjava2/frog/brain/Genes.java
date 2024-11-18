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

    private static boolean is(long cell, long geneMask) { // 判断cell是否含某个基因，这个不移位b
        return (cell & geneMask) > 0;
    }

    private static long b = 1; //以实现is(cell)方法每调用一次b就移位一次的效果，用全局静态变量可以省去方法调用时多传一个参数

    private static boolean is_(long cell) { // 判断cell是否含b这个基因掩码，并左移位全局静态变量b一位，用下划线命名表示移位
        boolean result = (cell & b) > 0;
        b = b << 1;
        if (b > totalGenesLenth)
            b = 1 / 0;
        return result;
    }

    private static void is_(Animal a, int z, long c, boolean bl) { // 判断cell是否含b这个基因掩码，并左移位全局静态变量b一位
        b = b << 1;
        if (b > totalGenesLenth)
            b = 1 / 0;
        if ((c & b) > 0 && bl)
            a.setEngZ(z, 1); 
    }

    private static int int_(long cell, int n) { //cell以当前基因掩码b开始，连续取n位成为0~2^n之间的整数返回，并把全局静态变量b左移n位
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

    private static long specialGenesStart = 1L << 16; //特殊基因的起点
    private static long specialGenesStart6 = specialGenesStart << 6; //再跳过6位，前6位已用于表示初始电阻了
    private static long totalGenesLenth = 1L << GENE_NUMBERS; //最大掩码位数不能超过登记的基因数量

    // ========= active方法在每个主循环都会调用，用来存放细胞的行为，这是个重要方法 ===========
    public static void active(Animal a, int step) {
        int start = 0; //start是常数数组的起始点    
        float cellValve = a.consts[start++]; //细胞激活的阀值，神经元细胞至少能量多少，才会对激活输出细胞 

        //===============首先是第一次调用时, 进行所有细胞的初始化设定==============
        if (1 == 0 && step == 0) {//一排细胞中特殊基因越多越好，基因种类多的加点分
            b = specialGenesStart;
            for (int i = 0; i < 16; i++) {
                for (int z = 0; z < Env.BRAIN_SIZE; z++) {//本版本所有细胞都排成一条线，位于 z轴上 
                    long c = a.cells[0][0][z];
                    if (is(c, b)) {
                        a.fat++;
                        b <<= 1;
                        break;
                    }
                }
            }
        }

        if (step == 0) {//初始化每个细胞的初始电阻，暂定单个细胞的所有正连线和负连线初始电阻相同
            for (int z = 0; z < Env.BRAIN_SIZE; z++) {//本版本所有细胞都排成一条线，位于 z轴上 
                long c = a.cells[0][0][z]; //当前细胞是一个long类型   
                b = specialGenesStart; //特殊基因从17开始，开头6位定义静态正负初始电阻
                float pRes = 0.14f * int_(c, 3); //res 在0~1之间，初始值由基因决定的常数位置的值决定
                float nRes = 0.14f * int_(c, 3); //res 在0~1之间
                for (int i = 0; i < Env.BRAIN_SIZE; i++) {
                    a.posWeight[z][i] = pRes;
                    a.negWeight[z][i] = nRes;
                }
            }
        }

        for (int z = 0; z < Env.BRAIN_SIZE; z++) {//本版本所有细胞都排成一条线，位于 z轴上 
            long c = a.cells[0][0][z];
            float e = a.getEng(0, 0, z);//当前细胞的能量

            //===============先根据细胞能量产生输出行动, 注意细胞能量是由上轮产生的 ==============
             b = specialGenesStart6; //基因本身没什么意义，你给它什么假设并且它正好进化出这个掩码位，它就拥有什么行为 
             
            if (is_(c) && e > cellValve) {//如果是咬细胞，且处于激活态，咬下
                a.bite = true;
            }

            if (is_(c) && e > cellValve) {//如果张嘴细胞激活，停止咬
                a.bite = false;
            }

            //===============然后根据所含的各种乱七八糟基因，激活细胞或关闭细胞 ==============


            if (is_(c)) {//如果有active基因, 此细胞始终激活, is_方法在判断c有无b掩码后，将b左移一位
                a.setEngZ(z, 1);
                e = 1f;
            }
 
            is_(a, z, c, a.seeFoodComing); //如果看到食物正在靠近，激活此细胞
            is_(a, z, c, a.seeEmptyComing); //如果看到空白正在靠近，激活此细胞

            if (e < 0.1f) //如果这个细胞没有能量可以传给其它细胞，就跳过这个细胞
                continue;

            //==================下面是细胞之间的能量传送=======1================   
            //下面要实现以下规则，规则针对细胞级别，产生的效果是宏观级别，宏观级别的效果可以用遗传算法来判定筛选。
            // 先按以下假定规则来编程：
            // 单个细胞反复活跃，增加它所有正权重, 宏观效果：单个视信号反复激活，会刺激无关的咬细胞激活
            // 两个细胞同时活跃，增加它们间的正权重， 宏观效果：两个不相干的信号相邻时间内激活，两个不相干信号会形成关联
            // 甜觉加强最近正权重，宏观效果：甜味加强正向行为条件反射
            // 痛觉加强最近负权重，宏观效果：苦味抑制正向条件反射，加强负向条件反射
 
            boolean hasPosLines = is_(c);//当前神经元是否有正权重连线
            boolean hasNegLines = is_(c);//当前神经元是否有负权重连线

            b = 1; //从头开始，处理与相邻16个细胞之间的正权重能量传递

            for (int i = 0; i < Env.BRAIN_SIZE; i++) {
                a.posActivity[z][i] = a.posActivity[z][i] * .97f; //活跃度随时间消失, 97魔数以后也要用基因控制
                a.negActivity[z][i] = a.negActivity[z][i] * .97f;
                a.setEngZ(z, e * .8f); //细胞的能量也随时间消失，不能一直激活，一直激活就相当于阻断了新的信号。所以细胞神经网络是脉冲神经网络，所有被激活的细胞,其细胞能量都在慢慢减小
            }
            
            //活跃度、权重、细胞能量的关系：
            //活跃度：表示最近细胞或连线有没有激活过，活跃度是时间信息存诸的地方，与时间相关最密切，活跃度与奖罚激素联合，可以方便地动态调控权重
            //活跃度是记录在细胞上还是记录在连线上？有疑问，记录在细胞上可以节约内存，记录在连线上可以独立控制每个连线
            //权重：即电阻，模拟触突，表示细胞之间的连线的能量通过能力，是静态信息保存的地方
            //能量：能量作用在细胞上，是当前信息暂存的载体，以脉冲形式在细胞间传播

            for (int i = 0; i < Env.BRAIN_SIZE; i++)
                if (is_(c)) {//如果包含某线胞的序号，就传送能量给这个细胞
                    if (hasPosLines) { //如有正权重线条
                        float et = e * a.posWeight[z][i]; //要传输的能量=细胞能量*连线权重
                        if (et > cellValve) {
                            a.posActivity[z][i] = (a.posActivity[z][i] + et) * 0.5f; //活跃度与最近能量传送相关和本身原有活跃度相关，取平均值。好比有没有吃饱不光和嘴上的食物有关，还和肚子里有多少食物有关
                            a.addEng(0, 0, i, et);
                        }
                    }
                    if (hasNegLines) { //如有负权重线条
                        float et = e * a.negWeight[z][i]; //要传输的能量=细胞能量*连线权重
                        if (et > cellValve) {
                            a.negActivity[z][i] = (a.negActivity[z][i] + et) * 0.5f; //活跃度与最近能量传送相关和本身原有活跃度相关
                            a.addEng(0, 0, i, -et);
                        }
                    }

                }

        }

    }

}
