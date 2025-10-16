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

import java.util.ArrayList;

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

    public static ArrayList<Long> assignGene = new ArrayList<Long>(); //四位一组，前三位表示坐标，后一位表示基因, 用的时候顺序遍历4个一组取出来

    /**
     * Register one bit gene 登记一位基因及对应的相关参数，只是登记基因的分布，细胞生成后要根据这些数据在细胞里分布对应基因
     * 
     * @param display
     *            whether to display the gene on the BrainPicture 是否显示在脑图
     * @param fill
     *            whether to fill to specified 3D/2D/1D/1Point area 是否直接用此基因填充指定的区域，区域可以是三维、二维、线状及一个点
     *           
     * @param x_limit
     *            gene only allow on specified x layer 如x_layer大于-1，且y_layer=-1, 表示只生成在指定的x层对应的yz平面上，这时采用4叉树而不是8叉树以提高进化速度 
     * @param y_limit
     *            gene only allow on specified x, y axis 如大于-1，表示只生成在指定的x,y坐标对应的z轴上，这时采用2叉阴阳树算法
     *            
     * @param z_limit
     *            gene only allow on specified x, y, z 点上, 表示手工指定基因位于x,y,z坐标点上
     *            
     * @return a long integer mask 返回代表当前基因掩码的一个长整数，要判断一个细胞是否含有此基因只要与其作and运算即可
     */
    public static long register(boolean display, boolean fill, int x_limit, int y_limit, int z_limit) {
       return register(display, fill, x_limit, y_limit, z_limit, null);
    }

    public static long register(boolean display, boolean fill, int x_limit, int y_limit, int z_limit,
            String geneNname) {
        display_gene[GENE_NUMBERS] = display;
        fill_gene[GENE_NUMBERS] = fill;
        xLimit[GENE_NUMBERS] = x_limit;
        yLimit[GENE_NUMBERS] = y_limit;
        zLimit[GENE_NUMBERS] = z_limit;
        if (geneNname != null)
            name_gene[GENE_NUMBERS] = geneNname;
        if (++GENE_NUMBERS >= GENE_MAX) {//
            System.out.println("目前基因位数不能超过" + GENE_MAX);
            System.exit(-1);
        }
        return 1L<<(GENE_NUMBERS-1);
    }

    /**把最后一位分配的基因指定到xyz坐标上
     */
    public static void copyLastGeneTo(int x, int y, int z) {
        assignGene.add((long) x);
        assignGene.add((long) y);
        assignGene.add((long) z);
        assignGene.add(1l << (GENE_NUMBERS - 1)); //四位一组，前三位表示坐标，后一位表示基因
    }

    public static void register(int... pos) {// 登记并指定基因允许分布的位置
        register(true, false, pos[0], pos[1], pos[2]);
    }

    public static void registerFill(int... pos) {// 登记并手工指定基因填满的位置
        register(true, true, pos[0], pos[1], pos[2]);
    }

    public static boolean is(long cell, long geneMask) { // 判断cell是否含某个基因的掩码
        return (cell & geneMask) > 0;
    }
    
    public static boolean isNo(long cell, int geneNo) { // 判断cell是否含某个基因的序号
        return (cell & (1l<<geneNo)) > 0;
    }

    private static long b = 1; //以实现is(cell)方法每调用一次b就移位一次的效果，用全局静态变量可以省去方法调用时多传一个参数
    private static int bIndex = 0; //ib从1到64，当前b对应的序号 

    public static void from(int n) {//b从哪一位开始, 第一个序号为0依次类推
        b = 1L << n;
        bIndex = n;
    }

    public static void checkIndex() {//范围检查，使用的基因位数不能超过登记的位数, 这个方法
        if (bIndex > GENE_NUMBERS) {
            System.out.println("bIndex=" + bIndex);
            System.out.println(", b=" + Long.toBinaryString(b));
            System.out.println(", GENE_NUMBERS=" + GENE_NUMBERS);
            throw new RuntimeException("登记的基因位不够用，要再登记多一点");
        }
    }
   
    private static final int NA = -1; //NA表示基因将随机分布
    // ============开始登记基因==========

    // 登记细胞基因分布 
    public static int memGeneFrom;
    public static long 点0;
    public static long 点1;
    public static long 训;
    public static long 忆;
    public static long 咬;
    public static long 甜;
    public static long 苦;
    
    static {
        //先登记一些基因顺序分布在x=0, y=0的z轴上， 每个位置的基因都唯一且顺序增加 
        点0=register(true, true, 0, 0, 0, "点0"); //先登记一个忆基因  
        点1=register(true, true, 0, 0, 1, "点1"); //先登记一个忆基因
        训=register(true, true, 0, 0, 2, "训"); //先登记一个忆基因
         
        忆=register(true, true, 0, 0, 0, "忆"); //先登记一个忆基因  
        for (int z = 0; z < Env.BRAIN_SIZE; z++) //再把忆基因拷贝到整行
          copyLastGeneTo(0, 0, z);

    }

    // ========= active方法在每个主循环都会调用，用来存放细胞的行为，这是个重要方法 ===========
    public static void active(Animal a) {
        int step = Env.step;
        if (step == 0) {//在每屏第一次调用时初始化工作
        }

        for (int y = 0; y < 2; y++)
            for (int z = 0; z < Env.BRAIN_SIZE; z++) {//遍历所有脑细胞 
                long c = a.cells[0][y][z];

                float e = a.getEng(0, 0, z); //当前细胞的能量
                e = e * .6f; //能量快速衰减，常量以后要改成进化调节 
                a.setEngZ(z, e); 
                 
                if (is(c, 点0)) {//如果看到食物的第0位的像素点
                    if (FoodJudge.foodBit0)
                        a.setEngZ(z, FoodJudge.ep ); //点亮视细胞0
                }

                if (is(c, 点1)) {//如果看到食物的第1位的像素点
                    if (FoodJudge.foodBit1)
                        a.setEngZ(z, FoodJudge.ep); //点亮视细胞1
                }
                
                if (is(c, 训)) {//如果FoodJudge中有训练信号
                    if (FoodJudge.train[step])
                        a.setEngZ(z, FoodJudge.ep);
                }

                if (is(c, 忆)) {//如果FoodJudge中有忆基因 
                    //TODO 忆基因的行为是把相邻两个细胞如果都激活，就增加它们的连线权值，如果有甜激素，就显著增加正连线权值，如果有苦激素，就显著增加负连线权值，如果没有激素，就少量增加正连线权值
                }
                
            }
    }

}