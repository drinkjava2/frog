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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.gitee.drinkjava2.frog.brain.Genes;
import com.gitee.drinkjava2.frog.brain.Line;
import com.gitee.drinkjava2.frog.egg.Egg;
import com.gitee.drinkjava2.frog.objects.Material;
import com.gitee.drinkjava2.frog.util.GeneUtils;
import com.gitee.drinkjava2.frog.util.RandomUtils;

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

    public static int CountsQTY = 100; //常量总数多少
    public float[] consts = new float[CountsQTY]; // 常量，范围0~1之间，这些常量并不常，会参与遗传算法筛选，规则是有大概率小变异，小概率大变异

    public ArrayList<int[]> lines = new ArrayList<>();

    /** brain cells，每个细胞对应一个神经元。long是64位，所以目前一个细胞只能允许最多64个基因，64个基因有些是8叉分裂，有些是4叉分裂
     *  如果今后要扩充到超过64个基因限制，可以定义多个三维数组，同一个细胞由多个三维数组相同坐标位置的基因共同表达
     */
    public long[][][] cells = new long[Env.BRAIN_SIZE][Env.BRAIN_SIZE][Env.BRAIN_SIZE]; //所有脑细胞

    public float[][][] energys = new float[Env.BRAIN_SIZE][Env.BRAIN_SIZE][Env.BRAIN_SIZE]; //每个细胞的能量值，细胞能量不参与打分。打分是由fat变量承担

    public int xPos; // animal在Env中的x坐标
    public int yPos; // animal在Env中的y坐标
    public long fat = 1000000000; // 青蛙的肥胖度, 只有胖的青蛙才允许下蛋, 以前版本这个变量名为energy，为了不和脑细胞的能量重名，从这个版本起改名为fat
    public boolean alive = true; // 设为false表示青蛙死掉了，将不参与计算和显示，以节省时间
    public int ateFood = 0; // 青蛙曾吃过的食物总数
    public int ateWrong = 0; // 青蛙咬了个空气的次数
    public int no; // 青蛙在Env.animals中的序号，从1开始， 会在运行期写到当前brick的最低位，可利用Env.animals.get(no-1)快速定位青蛙

    public int animalMaterial;
    public Image animalImage;

    public Animal(Egg egg) {//构造方法，Animal从蛋中诞生
        System.arraycopy(egg.consts, 0, this.consts, 0, consts.length);//从蛋中拷一份全局参数
        for (int i = 0; i < GENE_NUMBERS; i++) {
            genes.add(new ArrayList<>());
        }
        int i = 0;
        for (ArrayList<Integer> gene : egg.genes)//动物的基因是蛋的基因的拷贝 
            genes.get(i++).addAll(gene);
        lines.addAll(egg.lines); //复制蛋的所有线条
        i = 0;
        if (Env.BORN_AT_RANDOM_PLACE) { //是否随机出生在地图上?
            xPos = RandomUtils.nextInt(Env.ENV_WIDTH);
            yPos = RandomUtils.nextInt(Env.ENV_HEIGHT);
        } else {//否则出生成指定区域
            this.xPos = egg.x + RandomUtils.nextInt(80) - 40;
            this.yPos = egg.y + RandomUtils.nextInt(80) - 40;
            if (this.xPos < 0)
                this.xPos = 0;
            if (this.yPos < 0)
                this.yPos = 0;
            if (this.xPos >= (Env.ENV_WIDTH - 1))
                this.xPos = Env.ENV_WIDTH - 1;
            if (this.yPos >= (Env.ENV_HEIGHT - 1))
                this.yPos = Env.ENV_HEIGHT - 1;
        }
    }

    public void initAnimal() { // 初始化animal,生成脑细胞是在这一步，这个方法是在当前屏animal生成之后调用，比方说有一千个青蛙分为500屏测试，每屏只生成2个青蛙的脑细胞，可以节约内存
        constMutate();//常量基因突变, 线条的参数都在常量里
        Line.randomAddorRemoveLine(this);// //Line的随机增删变异

        GeneUtils.geneMutation(this); //分裂算法控制的基因突变
        if (RandomUtils.percent(5))
            for (ArrayList<Integer> gene : genes) //基因多也要适当小扣点分，防止基因无限增长
                fat -= gene.size();
        if (RandomUtils.percent(3)) //线条多也小扣点分，防止线条无限增长
            fat -= lines.size() / 10;
        GeneUtils.createCellsFromGene(this); //根据基因，分裂生成脑细胞
    }

    public boolean active(int step) {// 这个active方法在每一步循环都会被调用，是脑思考的最小帧，step是当前屏的帧数
        // 如果fat小于0、出界、与非食物的点重合则判死
        if (!alive) {
            return false;
        }
        if (fat <= 0 || Env.outsideEnv(xPos, yPos) || Env.bricks[xPos][yPos] >= Material.KILL_ANIMAL) {
            kill();
            return false;
        }
        this.setEng(Genes.ACT_POS, 1f); //ACT这个细胞就象太阳永远保持激活，某些情况下当无外界信号时，它是驱动系统运行的能量来源
        Genes.active(this, step); //调用每个细胞的活动，重要！
        Line.active(this, step); //调用每个连线(触突)的活动，重要！
        return alive;
    }

    public void constMutate() { // 全局参数变异, 这一个方法此动物个体的所有常量
        if (RandomUtils.percent(30)) //这个30%机率的变异方法让所有常量都有3%的机率随机在0~1之间重新取值
            for (int i = 0; i < CountsQTY; i++) { 
                if (RandomUtils.percent(3))
                    consts[i] = RandomUtils.nextFloat(); //debug
            }

        if (RandomUtils.percent(10)) //这个10%机率的变异方法让所有常量都有5%的机率随机在原基础上变异，即大概率有小变异，小概率有大变异
            for (int i = 0; i < CountsQTY; i++) {
                if (RandomUtils.percent(5))
                    consts[i] = RandomUtils.vary(consts[i]);
            }

    }

    private static final int MIN_FAT_LIMIT = Integer.MIN_VALUE + 5000;
    private static final int MAX_FAT_LIMIT = Integer.MAX_VALUE - 5000;

    //@formatter:off 下面几行是重要的奖罚方法，会经常调整或注释掉，集中放在一起，不要格式化为多行   
    public void changeFat(int fat_) {//正数为奖励，负数为惩罚， fat值是环境对animal唯一的奖罚，也是animal唯一的下蛋竞争标准
        fat += fat_;
        if (fat > MAX_FAT_LIMIT)
            fat = MAX_FAT_LIMIT;
        if (fat < MIN_FAT_LIMIT)
            fat = MIN_FAT_LIMIT;
    }
   
    //没定各个等级的奖罚值，目前是手工设定的常数
    public void awardAAAA()      { changeFat(2000);}
    public void awardAAA()   { changeFat(1000);}
    public void awardAA()     { changeFat(100);}      
    public void awardA()   { changeFat(10);}
    
    public void penaltyAAAA()    { changeFat(-2000);}
    public void penaltyAAA() { changeFat(-1000);}
    public void penaltyAA()   { changeFat(-100);}
    public void penaltyA()   { changeFat(-10);}
    public void kill() {  this.alive = false; changeFat(-5000000);  Env.clearMaterial(xPos, yPos, animalMaterial);  } //kill是最大的惩罚
    //@formatter:on

    public void showInEnv(Graphics g) {// 在虚拟环境显示当前动物，这个方法直接调用Env的Graphics对象
        if (g != null) //这个版本借用环境区测试模式功能，不需要显示青蛙，所以直接跳出
            return;
        if (no == (Env.current_screen * Env.FROG_PER_SCREEN + 1)) { //如果是当前第一个青蛙，给它画个红圈
            Color c = g.getColor();
            g.setColor(Color.red);
            g.drawArc(xPos - 15, yPos - 15, 30, 30, 0, 360);
            g.setColor(c);
        }
        if (!alive)
            return;
        g.drawImage(animalImage, xPos - 8, yPos - 8, 16, 16, null);// 减去坐标，保证嘴巴显示在当前x,y处 
    }

    /** Check if x,y,z out of animal's brain range */
    public static boolean outBrainRange(int x, int y, int z) {// 检查指定坐标是否超出animal脑空间界限
        return x < 0 || x >= Env.BRAIN_SIZE || y < 0 || y >= Env.BRAIN_SIZE || z < 0 || z >= Env.BRAIN_SIZE;
    }

    public boolean hasGene(int x, int y, int z, long geneMask) { //判断cell是否含某个基因 
        return (cells[x][y][z] & geneMask) > 0;
    }

    public boolean hasGene(int x, int y, int z) { //判断cell是否含任一基因 
        return cells[x][y][z] > 0;
    }

    public void setEng(int x, int y, int z, float e) { //打开指定的xyz坐标对应的cell能量值为极大
        if (e > 1)
            e = 1;
        if (e < 0)
            e = 0;
        energys[x][y][z] = e;
    }

    public void setEng(int[] a, float e) { //打开指定的xyz坐标对应的cell能量值为极大
        if (e > 1)
            e = 1;
        if (e < 0)
            e = 0;
        energys[a[0]][a[1]][a[2]] = e;
    }

    public void addEng(int[] a, float e) {//指定的a坐标对应的cell能量值加e
        addEng(a[0], a[1], a[2], e);
    }

    public void addEng(int x, int y, int z, float e) {//指定的a坐标对应的cell能量值加e
        if (cells[x][y][z] == 0)
            return;
        float eng = energys[x][y][z] + e;
        if (eng > 1) //如果饱和，不再增加，通过这个方法可以实现异或逻辑或更复杂的模式识别，详见TestInput3测试
            eng = 1;
        if (eng < 0) //回到传统方式，细胞不允许出现负能量。（但是能量可以出现负值，这个与实际细胞的抑制信号相似）
            eng = 0;
        energys[x][y][z] = eng;
    }

    public float getEng(int[] a) {//返回指定的a坐标对应的cell能量值
        return energys[a[0]][a[1]][a[2]];
    }

    public float getEng(int x, int y, int z) {//返回指定的a坐标对应的cell能量值
        return energys[x][y][z];
    }

}
