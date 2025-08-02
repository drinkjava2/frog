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
import com.gitee.drinkjava2.frog.objects.FoodJudge;

import static com.gitee.drinkjava2.frog.Env.BRAIN_SIZE;

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

    public static boolean is_(long cell, String name) { // 判断cell是否含b这个基因掩码，并左移位全局静态变量b一位，用下划线命名表示移位，第二个参数赋给基因一个名字以方便调试，名字不影响逻辑
        name_gene[bIndex] = name;
        checkIndex();
        boolean result = (cell & b) > 0;
        b = b << 1;
        bIndex++;
        return result;
    }

    public static void checkIndex() {//范围检查，使用的基因位数不能超过登记的位数, 这个方法
        if (bIndex > GENE_NUMBERS) {
            System.out.println("bIndex=" + bIndex);
            System.out.println(", b=" + Long.toBinaryString(b));
            System.out.println(", GENE_NUMBERS=" + GENE_NUMBERS);
            throw new RuntimeException("登记的基因位不够用，要再登记多一点");
        }
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
        checkIndex();
        if ((c & b) > 0 && bl)
            a.setEngZ(z, 1);
        b = b << 1;
        bIndex++;
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

    private static final int NA = -1; //NA表示基因将随机分布
    // ============开始登记基因==========

    // 登记细胞基因分布
    static {
        //先登记一些外设细胞如眼和嘴巴，布在x=0, y=0的z轴上， 每个位置的基因都唯一且顺序增加
        for (int z = 0; z < Env.BRAIN_SIZE; z++) {
            register(1, true, true, 0, 0, z);
        }

        register(1, true, true, 0, 1, NA); //这个是记忆细胞，单独占一行，

    }

    // ========= active方法在每个主循环都会调用，用来存放细胞的行为，这是个重要方法 ===========
    public static void active(Animal a) {
        int step = Env.step;
        if (step == 0) {//在每屏第一次调用时初始化工作
        }

        for (int y = 0; y <1; y++)  
        for (int z = 0; z < Env.BRAIN_SIZE; z++) {//本版本所有细胞都排成一条线，位于 z轴上 
            long c = a.cells[0][y][z];

            boolean engInput = false;
            from(0); //全局变量b从0开始根据所含的各种基因，实作细胞的逻辑

            if (is_(c, "激")) {//如果有激基因, 此细胞始终激活, 也就是说这个细胞自己会产生能量
                engInput = true;
            }

            if (is_(c, "训")) {//如果FoodJudge中有训练信号
                if (FoodJudge.train[step])
                    engInput = true;
            }

            if (is_(c, "0")) {//如果看到食物的第0位的像素点
                if (FoodJudge.foodBit0)
                    engInput = true;
            }

            if (is_(c, "1")) {//如果看到食物的第1位的像素点
                if (FoodJudge.foodBit1)
                    engInput = true;
            }

            if (engInput) //如果有信号输入则赋给细胞满能量1
                a.setEngZ(z, 1);
            else { //否则细胞能量衰减80%
                float e = a.getEng(0, 0, z); //当前细胞的能量
                e = e * .6f; //能量快速衰减 , 这个衰减率以后要改成基因或可进化常数控制
                a.setEngZ(z, e);
            }
            
            //TODO: work on memory cell
            if (is_(c, "忆")) {//如果是忆细胞
                if (FoodJudge.foodBit0)
                    engInput = true;
            }
            

        }
    }

}
