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

/**
 * Genes代表不同的脑细胞参数，对应每个参数，用8叉/4叉/2叉树算法给每个细胞添加细胞参数和行为。
 * 每个脑细胞用一个long来存储，所以目前最多允许64个基因位,
 * 多字节参数可以由多个基因位决定。每个基因位都由一个单独的阴阳8/4/2叉树控制，多个基因就组成了一个8/4/2叉树阵列 基因+分裂算法=结构
 * 基因+分裂算法+遗传算法=结构的进化
 * 
 * 这个类里定义每个基因位的掩码以及对应基因的细胞行为, 脑结构的所有参数，都要用基因来控制。开始时可以有常量、魔数，但以后都放到基因里去自动进化。
 * 
 * 原则：
 * 1.复杂的人脑是由单个细胞通过随机算法和遗传算法进化出来，所以所有算法都只需要针对一个细胞进行编程就可以了。
 * 2.随机的组合如果想要获得唯一的复杂结果，哪么随机的因素必然不能太多，道理很简单，因为如果随机因素太多，那么从概率上就是极小概率事件，也就不可能发生。
 * 3.神经网络即是决定性的，也是无法预测的。当太多细胞组合后，就相当于有无数个三体，所以虽然系统是决定性的，但也是无法预测行为的。但因为随机的因素必然不能太多,所以宏观表现上也有共性，即动物和人的思维表现有相似的现象。
 * 
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
    public static String[] name_gene = new String[GENE_MAX]; // 如果这个参数为真，此基因填充指定的区域，而不是由分裂算法随机生成

    public static int[] xLimit = new int[GENE_MAX]; // 用来手工限定基因分布范围，详见register方法
    public static int[] yLimit = new int[GENE_MAX];
    public static int[] zLimit = new int[GENE_MAX];

    public static float cCellValve; //细胞激活的常量阀值，神经元细胞至少能量多少，才会对激活输出细胞 
    public static float cWeigthLostRate; //权重随时间的遗忘率
    public static float cActiveLostRate; //活跃度随时间的遗忘率
    public static float cWeightSweetAddRate; //权重随奖励的增加率
    public static float cWeightBitterAddRate; //权重随苦味的增加率
    public static float cEnergyLostRate; //每个细胞能量丢失的速度

    /**
     * Register a gene 依次从底位到高位登记所有的基因掩码及对应的相关参数
     * 
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
    private static int bIndex = 0; //ib从1到64，当前b对应的序号 

    public static void from(int n) {//b从哪一位开始, 第一个序号为0依次类推
        b = 1L << n;
        bIndex = n;
    }

    public static boolean is_(long cell) { // 判断cell是否含b这个基因掩码，并左移位全局静态变量b一位，用下划线命名表示移位
        checkIndex();
        boolean result = (cell & b) > 0;
        b = b << 1;
        bIndex++; 
        return result;
    }

    public static boolean is_(long cell, String name) { // 判断cell是否含b这个基因掩码，并左移位全局静态变量b一位，用下划线命名表示移位
        name_gene[bIndex] = name;
        return is_(cell);
    }

    public static void checkIndex() {//范围检查，使用的基因位数不能超过登记的位数, 这个方法
        if (bIndex > GENE_NUMBERS) {
            System.out.println("bIndex=" + bIndex);
            System.out.println(", b=" + Long.toBinaryString(b));
            System.out.println(", GENE_NUMBERS=" + GENE_NUMBERS);
            throw new RuntimeException("登记的基因位不够用，要再登记多一点");
        }
    }

    public static void name(String name) {//给当前基因起一个名字
        name_gene[bIndex] = name;
    }

    private static void is_(Animal a, int z, long c, boolean bl) { // 判断c是否含当前b这个基因掩码且符合条件bl,则左移位全局静态变量b一位且激活当前细胞c
        checkIndex();
        if ((c & b) > 0 && bl)
            a.setEngZ(z, 1);
        b = b << 1;
        bIndex++;
    }

    private static void is_(Animal a, int z, long c, boolean bl, String name) {
        name_gene[bIndex] = name;
        is_(a, z, c, bl);
    }

    private static int int_(long cell, int n) { //cell以当前基因掩码b开始，连续取n位成为0~2^n之间的整数返回，并把全局静态变量b左移n位
        int result = 0;
        long bb = 1L;
        for (int i = 0; i < n; i++) {
            checkIndex();
            if ((cell & b) > 0)
                result += bb;
            bb = bb << 1;
            bIndex++;
            b = b << 1; 
        }
        return result;
    }

    private static final int NA = -1;
    // ============开始登记基因==========

    // 登记细胞间关联(触突树突)
    static {
        register(16 + 18, true, false, 0, 0, NA); //先登记一些基因位，每个基因位的作用（对应各种细胞类型、行为）后面再说
    }

    public static void printDebug() {
        System.out.println("=======Genes debug info========");
        System.out.println("cCellValve=" + cCellValve);
        System.out.println("cWeigthLostRate=" + cWeigthLostRate);
        System.out.println("cActiveLostRate=" + cCellValve);
        System.out.println("cWeightSweetAddRate=" + cWeightSweetAddRate);
        System.out.println("cWeightBitterAddRate=" + cWeightBitterAddRate);
        System.out.println("cEnergyLostRate=" + cEnergyLostRate);
    }

    private static void init(Animal a) {
        {//初始化每个细胞的初始电阻，暂定单个细胞的所有正连线和负连线初始电阻相同
            for (int z = 0; z < Env.BRAIN_SIZE; z++) {//本版本所有细胞都排成一条线，位于 z轴上 
                long c = a.cells[0][0][z]; //当前细胞是一个long类型   
                from(16); //16~22这6位定义静态正负初始电阻
                float pRes = 0.14f * int_(c, 3); //res 在0~1之间，初始值由基因决定的常数位置的值决定
                float nRes = 0.14f * int_(c, 3); //res 在0~1之间
                for (int i = 0; i < Env.BRAIN_SIZE; i++) {
                    a.posWeight[z][i] = pRes;
                    a.negWeight[z][i] = nRes;
                }
            }
        }
    }

    // ========= active方法在每个主循环都会调用，用来存放细胞的行为，这是个重要方法 ===========
    public static void active(Animal a) {
        if (Env.step == 0)
            init(a);
        int start = 0; //start是常数数组的起始点
        cCellValve = a.consts[start++]; //细胞激活的常量阀值，神经元细胞至少能量多少，才会对激活输出细胞 
        cWeigthLostRate = a.consts[start++]; //权重随时间的遗忘率
        cActiveLostRate = a.consts[start++]; //活跃度随时间的遗忘率
        cWeightSweetAddRate = a.consts[start++]; //权重随奖励的增加率
        cWeightBitterAddRate = a.consts[start++]; //权重随苦味的增加率
        cEnergyLostRate = a.consts[start++]; //每个细胞能量丢失的速度

        //===============首先是第一次调用时, 进行所有细胞的初始化设定==============

        for (int z = 0; z < Env.BRAIN_SIZE; z++) {//本版本所有细胞都排成一条线，位于 z轴上 
            long c = a.cells[0][0][z];
            float e = a.getEng(0, 0, z);//当前细胞的能量
            from(16 + 6); //从22开始， 因为0~15保存是否与其它细胞存在连线，16~21保存初始化常量，这里跳过前16个

            boolean hasPosLines = is_(c,"正");//当前神经元是否有正权重连线
            boolean hasNegLines = is_(c,"负");//当前神经元是否有负权重连线

            //===============先根据细胞能量产生输出行动, 注意细胞能量是由上轮产生的 ==============
            if (is_(c,"咬") && e > cCellValve) {//如果是咬细胞，且处于激活态，咬下，（上版本有个bug只有能量才会调用is_方法是不对的）
                a.bite = true;
            }

            if (is_(c,"停") && e > cCellValve) {//如果张嘴细胞激活，停止咬
                a.bite = false;
            }

            //===============然后根据所含的各种乱七八糟基因，激活细胞或关闭细胞 ==============

            if (is_(c,"反")) {//如果有反相器基因，将能量反相，即实现非门功能
                e = 1f - e;
                e = e < 0 ? 0 : e;
                e = e > 1f ? 1 : e;
            }

            if (is_(c,"激")) {//如果有active基因, 此细胞始终激活, is_方法在判断c有无b掩码后，将b左移一位
                a.setEngZ(z, 1);
                e = 1f;
            }

            is_(a, z, c, a.seeFoodComing,"近"); //如果看到食物正在靠近，激活此细胞
            is_(a, z, c, a.seeEmptyComing,"白"); //如果看到空白正在靠近，激活此细胞
            is_(a, z, c, a.sweet,"甜"); //如果尝到甜味，激活此脑细胞
            is_(a, z, c, a.bitter,"苦"); //如果尝到苦味，激活此脑细胞

            if (e < 0.1f) //所有能量操作都是针对一个细胞的传出（没有接收能量的编程，因为传出就相当于另一个细胞在接收能量），如果细胞都没有能量就不可能传出，所以就跳过这个细胞
                continue;

            boolean sweetEvent = a.sweet; //sweetEvent为true时，将产生激素群发消息
            boolean bitterEvent = a.bitter;

            if (is_(c,"SE")) {//如果激活了且包含sweetEvent基因,  则激活青蛙甜味事件
                sweetEvent = true; //也就是说sweetEvent除了由外界甜信号激活，也可以由大脑活动激活
            }

            if (is_(c,"BE")) {//如果激活了且包含bitterEvent基因, 则激活青蛙苦味事件
                bitterEvent = true;
            }

            //==================下面是细胞之间的能量传输=======================    

            from(0); //从头开始，处理与相邻16个细胞之间的正权重能量传递

            for (int i = 0; i < Env.BRAIN_SIZE; i++) {
                a.posActivity[z][i] = a.posActivity[z][i] * cActiveLostRate; //活跃度随时间消失
                a.negActivity[z][i] = a.negActivity[z][i] * cActiveLostRate; //活跃度随时间消失
                a.setEngZ(z, e * cEnergyLostRate); //细胞的能量也随时间消失，不能一直激活，一直激活就相当于阻断了新的信号。所以细胞神经网络是脉冲神经网络，所有被激活的细胞,其细胞能量都在慢慢减小
            }

            for (int i = 0; i < Env.BRAIN_SIZE; i++)
                if (is_(c)) {//如果包含某线胞的序号，就传送能量给这个细胞
                    if (hasPosLines) { //如有正权重线条
                        float w = a.posWeight[z][i];
                        float p = a.posActivity[z][i];
                        float et = e * w; //要传输的能量=细胞能量*连线权重
                        if (et > cCellValve) {
                            a.posActivity[z][i] = (p + et) * 0.5f; //活跃度与最近能量传送相关和本身原有活跃度相关，取平均值。好比有没有吃饱不光和嘴上的食物有关，还和肚子里有多少食物有关
                            a.addEng(0, 0, i, et);
                        }

                        if (sweetEvent) { //如果最近尝到甜味，正权重增加
                            w = w * (1 + cWeightSweetAddRate * 5);
                            w = w > 1 ? 1 : w;
                            a.posWeight[z][i] = w;
                        }
                    }
                }

            from(0); //从头开始，处理与相邻16个细胞之间的负权重能量传递

            for (int i = 0; i < Env.BRAIN_SIZE; i++)
                if (is_(c)) {//如果包含某线胞的序号，就传送能量给这个细胞 
                    if (hasNegLines) { //如有负权重线条
                        float w = a.negWeight[z][i];
                        float p = a.negActivity[z][i];
                        float et = e * w; //要传输的能量=细胞能量*连线权重
                        if (et > cCellValve) {
                            a.negActivity[z][i] = (w + et) * 0.5f; //活跃度与最近能量传送相关和本身原有活跃度相关
                            a.addEng(0, 0, i, -et);
                        }

                        if (a.bitter) { //如果最近尝到苦味，负权重增加
                            w = w * (1 + cWeightBitterAddRate * 5);
                            w = w > 1 ? 1 : w;
                            a.negWeight[z][i] = w;
                        }
                    }
                }

        }
    }

}
