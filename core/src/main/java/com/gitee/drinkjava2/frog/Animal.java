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
package com.gitee.drinkjava2.frog;

import static com.gitee.drinkjava2.frog.brain.Genes.GENE_NUMBERS;
import static com.gitee.drinkjava2.frog.util.RandomUtils.percent;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.gitee.drinkjava2.frog.brain.Genes;
import com.gitee.drinkjava2.frog.egg.Egg;
import com.gitee.drinkjava2.frog.objects.Material;
import com.gitee.drinkjava2.frog.util.RandomUtils;
import com.gitee.drinkjava2.frog.util.Tree2Util;
import com.gitee.drinkjava2.frog.util.Tree4Util;
import com.gitee.drinkjava2.frog.util.Tree8Util;

/**
 * Animal is all artificial lives' father class
 * Animal only keep one copy of genes from egg, not store gene in cell  
 * Animal是所有动物（青蛙、蛇等）的父类, animal是由蛋孵出来的，蛋里保存着脑细胞结构生成的基因, Animal只保存一份基因而不是每个细胞都保存一份基因，这是人工生命与实际生物的最大不同
 * 基因是一个list<list>结构, 每一条list代表一条由深度树方式存储的基因树，分表控制细胞的一个参数，当cell用长整数表示时最多可以表达支持64个参数
 * 
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public abstract class Animal {// 这个程序大量用到public变量而不是getter/setter，主要是为了编程方便和简洁，但缺点是编程者需要小心维护各个变量
    public static BufferedImage FROG_IMAGE;

    static {
        try {
            FROG_IMAGE = ImageIO.read(new FileInputStream(Application.CLASSPATH + "frog.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<Integer>> genes = new ArrayList<>(); // 基因是多个数列，有点象多条染色体。每个数列都代表一个基因的分裂次序(8叉/4叉/2叉)。

    /** brain cells，每个细胞对应一个神经元。long是64位，所以目前一个细胞只能允许最多64个基因，64个基因有些是8叉分裂，有些是4叉分裂
     *  如果今后要扩充到超过64个基因限制，可以定义多个三维数组，同一个细胞由多个三维数组相同坐标位置的基因共同表达
     */
    public long[][][] cells = new long[Env.BRAIN_CUBE_SIZE][Env.BRAIN_CUBE_SIZE][Env.BRAIN_CUBE_SIZE];

    public float[][][] energys = new float[Env.BRAIN_CUBE_SIZE][Env.BRAIN_CUBE_SIZE][Env.BRAIN_CUBE_SIZE]; //每个细胞的能量值，这些不参与打分。打分是由Animan的energy字段承担

    public int x; // animal在Env中的x坐标
    public int y; // animal在Env中的y坐标
    public long energy = 1000000000; // 青蛙的能量为0则死掉
    public boolean alive = true; // 设为false表示青蛙死掉了，将不参与计算和显示，以节省时间
    public int ateFood = 0; // 青蛙曾吃过的食物总数，下蛋时如果两个青蛙能量相等，可以比数量
    public int no; // 青蛙在Env.animals中的序号，从1开始， 会在运行期写到当前brick的最低位，可利用Env.animals.get(no-1)快速定位青蛙

    public int animalMaterial;
    public Image animalImage;

    public Animal(Egg egg) {// x, y 是虑拟环境的坐标
        for (int i = 0; i < GENE_NUMBERS; i++) {
            genes.add(new ArrayList<>());
        }
        int i = 0;
        for (ArrayList<Integer> gene : egg.genes)//动物的基因是蛋的基因的拷贝 
            genes.get(i++).addAll(gene);
        i = 0;
        if (Env.BORN_AT_RANDOM_PLACE) { //是否随机出生在地图上?
            x = RandomUtils.nextInt(Env.ENV_WIDTH);
            y = RandomUtils.nextInt(Env.ENV_HEIGHT);
        } else {//否则出生成指定区域
            this.x = egg.x + RandomUtils.nextInt(80) - 40;
            this.y = egg.y + RandomUtils.nextInt(80) - 40;
            if (this.x < 0)
                this.x = 0;
            if (this.y < 0)
                this.y = 0;
            if (this.x >= (Env.ENV_WIDTH - 1))
                this.x = Env.ENV_WIDTH - 1;
            if (this.y >= (Env.ENV_HEIGHT - 1))
                this.y = Env.ENV_HEIGHT - 1;
        }
    }

    public void initAnimal() { // 初始化animal,生成脑细胞是在这一步，这个方法是在当前屏animal生成之后调用，比方说有一千个青蛙分为500屏测试，每屏只生成2个青蛙的脑细胞，可以节约内存
        //TODO: for 2D genes need use 4-tree instead of 8-tree 平面的要改成4叉树以加快速度

        geneMutation(); //有小概率基因突变
        if (RandomUtils.percent(50))
            for (ArrayList<Integer> gene : genes) //基因多也要适当小扣点分，防止基因无限增长
                energy -= gene.size();
        createCellsFromGene(); //根据基因，分裂生成脑细胞
    }

    private static final int MIN_ENERGY_LIMIT = Integer.MIN_VALUE + 5000;
    private static final int MAX_ENERGY_LIMIT = Integer.MAX_VALUE - 5000;

    //@formatter:off 下面几行是重要的奖罚方法，会经常调整或注释掉，集中放在一起，不要格式化为多行   
    public void changeEnergy(int energy_) {//正数为奖励，负数为惩罚， energy大小是环境对animal唯一的奖罚，也是animal唯一的下蛋竞争标准
        energy += energy_;
        if (energy > MAX_ENERGY_LIMIT)
            energy = MAX_ENERGY_LIMIT;
        if (energy < MIN_ENERGY_LIMIT)
            energy = MIN_ENERGY_LIMIT;
    }
   
    //如果改奖罚值，就可能出现缺色，这个要在基因变异算法（从上到下，从下到上）和环境本身奖罚合理性上下功夫
    public void awardAAAA()      { changeEnergy(2000);}
    public void awardAAA()   { changeEnergy(10);}
    public void awardAA()     { changeEnergy(5);}      
    public void awardA()   { changeEnergy(2);}
    
    public void penaltyAAAA()    { changeEnergy(-2000);}
    public void penaltyAAA() { changeEnergy(-10);}
    public void penaltyAA()   { changeEnergy(-5);}
    public void penaltyA()   { changeEnergy(-2);}
    public void kill() {  this.alive = false; changeEnergy(-5000000);  Env.clearMaterial(x, y, animalMaterial);  } //kill是最大的惩罚
    //@formatter:on

    public boolean active() {// 这个active方法在每一步循环都会被调用，是脑思考的最小帧
        // 如果能量小于0、出界、与非食物的点重合则判死
        if (!alive) {
            return false;
        }
        if (energy <= 0 || Env.outsideEnv(x, y) || Env.bricks[x][y] >= Material.KILL_ANIMAL) {
            kill();
            return false;
        }

        this.energys[0][0][0] = 10; //设某个细胞固定激活
        //Eye.active(this); //如看到食物，给顶层细胞赋能量
        Genes.active(this); //细胞之间互相传递能量
        //
        //        if (Food.foundAndAteFood(this.x, this.y)) { //如当前位置有食物就吃掉，并获得奖励
        //            this.awardAAAA();
        //            this.ateFood++;
        //        }
        return alive;
    }

    public void show(Graphics g) {// 显示当前动物
        if (!alive)
            return;
        g.drawImage(animalImage, x - 8, y - 8, 16, 16, null);// 减去坐标，保证嘴巴显示在当前x,y处
    }

    /** Check if x,y,z out of animal's brain range */
    public static boolean outBrainRange(int x, int y, int z) {// 检查指定坐标是否超出animal脑空间界限
        return x < 0 || x >= Env.BRAIN_XSIZE || y < 0 || y >= Env.BRAIN_YSIZE || z < 0 || z >= Env.BRAIN_ZSIZE;
    }

    private void createCellsFromGene() {//根据基因生成细胞参数  
        for (int g = 0; g < GENE_NUMBERS; g++) {//动物有多条基因，一条基因控制一维细胞参数，目前最多有64维，也就是最多有64条基因
            long geneMask = 1l << g;
            ArrayList<Integer> gene = genes.get(g);
            int xLayer = Genes.xLayer[g];
            int yLayer = Genes.yLayer[g];
            if (xLayer < 0) { //如xLayer没定义,使用阴阳8叉树分裂算法在三维空间分裂,这个最慢但分布范围大  
                Tree8Util.knockNodesByGene(gene);//根据基因，把要敲除的8叉树节点作个标记
                for (int i = 0; i < Tree8Util.NODE_QTY; i++) {//再根据敲剩下的8叉树keep标记生成细胞参数
                    if (Tree8Util.keep[i] > 0) {
                        int[] node = Tree8Util.TREE8[i];
                        if (node[0] == 1) {//如果node边长为1，即不可以再分裂了，就在三维空间对间数组的位置把当前基因geneMask置1
                            cells[node[1]][node[2]][node[3]] = cells[node[1]][node[2]][node[3]] | geneMask; //在相应的细胞处把细胞参数位置1
                        }
                    }
                }
            } else if (yLayer < 0) { // 如果xLayer>=0, yLalyer没定义, 表示此基因分布在坐标x的yz平面上，此时使用阴阳4叉树分裂算法在此平面上分裂加速!!!!
                Tree4Util.knockNodesByGene(gene);//根据基因，把要敲除的4叉树节点作个标记
                for (int i = 0; i < Tree4Util.NODE_QTY; i++) {//再根据敲剩下的4叉树keep标记生成细胞参数
                    if (Tree4Util.keep[i] > 0) {
                        int[] node = Tree4Util.TREE4[i];
                        if (node[0] == 1) {//如果node边长为1，即不可以再分裂了，就在2维空间对间数组的位置把当前基因geneMask置1
                            cells[xLayer][node[1]][node[2]] = cells[xLayer][node[1]][node[2]] | geneMask; //在相应的细胞处把细胞参数位置1
                        }
                    }
                }
            } else { // 如果xLayer>=0, yLalyer>=0，这时基因只能分布在x,y指定的z轴上，此时使用阴阳2叉树分裂算法
                Tree2Util.knockNodesByGene(gene);//根据基因，把要敲除的4叉树节点作个标记
                for (int i = 0; i < Tree2Util.NODE_QTY; i++) {//再根据敲剩下的4叉树keep标记生成细胞参数
                    if (Tree2Util.keep[i] > 0) {
                        int[] node = Tree2Util.TREE2[i];
                        if (node[0] == 1) {//如果node边长为1，即不可以再分裂了，就在2维空间对间数组的位置把当前基因geneMask置1
                            cells[xLayer][yLayer][node[1]] = cells[xLayer][yLayer][node[1]] | geneMask; //在相应的细胞处把细胞参数位置1
                        }
                    }
                }
            }
        }
    }

    private void geneMutation() { //基因变异,注意这一个方法同时变异青蛙的所有条基因
        if (percent(90))
            for (int g = 0; g < GENE_NUMBERS; g++) {//随机新增阴节点基因，注意只是简单地随机新增，所以可能有重复基因
                ArrayList<Integer> gene = genes.get(g);
                
                int geneMaxLength; //8叉、4叉树、2叉树的节点最大序号不同，基因随机生成时要限制它不能大于最大序号
                if (Genes.xLayer[g] < 0) { //如xLayer没定义,使用阴阳8叉树分裂算法
                    geneMaxLength= Tree8Util.NODE_QTY;
                } else if (Genes.yLayer[g] < 0) { // 如果xLayer>=0, yLalyer没定义, 表示此基因分布在坐标x的yz平面上，此时使用阴阳4叉树分裂算法
                    geneMaxLength= Tree4Util.NODE_QTY;
                } else { // 如果xLayer>=0, yLalyer>=0，这时基因只能分布在x,y指定的z轴上，此时使用阴阳2叉树分裂算法
                    geneMaxLength= Tree2Util.NODE_QTY;
                }
                
                
                int n=3; //这是个魔数，今后可以考虑放在基因里去变异，8\4\2叉树的变异率可以不一样
                if (percent(n)) //生成随机负节点号，对应阴节点, 
                    gene.add(-RandomUtils.nextInt(geneMaxLength));

                if (percent(n)) //生成随机负正节点号，对应阳节点
                    gene.add(RandomUtils.nextInt(geneMaxLength));

                if (percent(n+n)) //随机删除一个节点，用这种方式来清除节点，防止节点无限增长，如果删对了，就不会再回来，如果删错了，系统就会把这个青蛙整个淘汰，这就是遗传算法的好处
                    if (!gene.isEmpty())
                        gene.remove(RandomUtils.nextInt(gene.size()));

            }
    }

    public void open(int x, int y, int z) { //打开指定的xyz坐标对应的cell能量值为极大
        energys[x][y][z] = 99999f;
    }

    public void open(int[] a) { //打开指定的a坐标对应的cell能量值为极大
        energys[a[0]][a[1]][a[2]] = 99999f;
    }

    public void close(int x, int y, int z) { //关闭指定的xyz坐标对应的cell能量值为0
        energys[x][y][z] = 0;
    }

    public void close(int[] a) {//关闭指定的a坐标对应的cell能量值为0
        energys[a[0]][a[1]][a[2]] = 0;
    }

    public float get(int[] a) {//返回指定的a坐标对应的cell能量值
        return energys[a[0]][a[1]][a[2]];
    }
}
