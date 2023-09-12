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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.gitee.drinkjava2.frog.brain.Consts;
import com.gitee.drinkjava2.frog.brain.Genes;
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

    public int[] consts = new int[Consts.LENGTH]; //常量基因，用来存放不参与分裂算法的全局常量，这些常量也参与遗传算法筛选，规则是有大概率小变异，小概率大变异，见constGenesMutation方法

    /** brain cells，每个细胞对应一个神经元。long是64位，所以目前一个细胞只能允许最多64个基因，64个基因有些是8叉分裂，有些是4叉分裂
     *  如果今后要扩充到超过64个基因限制，可以定义多个三维数组，同一个细胞由多个三维数组相同坐标位置的基因共同表达
     */
    public long[][][] cells = new long[Env.BRAIN_SIZE][Env.BRAIN_SIZE][Env.BRAIN_SIZE]; //所有脑细胞

    public float[][][] energys = new float[Env.BRAIN_SIZE][Env.BRAIN_SIZE][Env.BRAIN_SIZE]; //每个细胞的能量值，细胞能量不参与打分。打分是由fat变量承担

    public int[][][][] holes = new int[Env.BRAIN_SIZE][Env.BRAIN_SIZE][Env.BRAIN_SIZE][]; //每个细胞的洞（相当于触突）

    public int xPos; // animal在Env中的x坐标
    public int yPos; // animal在Env中的y坐标
    public long fat = 1000000000; // 青蛙的肥胖度, 只有胖的青蛙才允许下蛋, 以前版本这个变量名为energy，为了不和脑细胞的能量重名，从这个版本起改名为fat
    public boolean alive = true; // 设为false表示青蛙死掉了，将不参与计算和显示，以节省时间
    public int ateFood = 0; // 青蛙曾吃过的食物总数
    public int ateWrong = 0; // 青蛙咬了个空气的次数
    public int no; // 青蛙在Env.animals中的序号，从1开始， 会在运行期写到当前brick的最低位，可利用Env.animals.get(no-1)快速定位青蛙

    public int animalMaterial;
    public Image animalImage;

    public Animal(Egg egg) {// x, y 是虑拟环境的坐标
        System.arraycopy(egg.constGenes, 0, this.consts, 0, consts.length);//从蛋中拷一份全局参数
        for (int i = 0; i < GENE_NUMBERS; i++) {
            genes.add(new ArrayList<>());
        }
        int i = 0;
        for (ArrayList<Integer> gene : egg.genes)//动物的基因是蛋的基因的拷贝 
            genes.get(i++).addAll(gene);
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
        GeneUtils.geneMutation(this); //有小概率基因突变
        Consts.constMutation(this);//常量基因突变  
        if (RandomUtils.percent(40))
            for (ArrayList<Integer> gene : genes) //基因多也要适当小扣点分，防止基因无限增长
                fat -= gene.size();
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

        holesReduce(); //所有细胞上的洞都随时间消逝，即信息的遗忘，旧的不去新的不来
        Genes.active(this, step); //调用每个细胞的活动，重要！
        return alive;
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
    public void awardAA()     { changeFat(60);}      
    public void awardA()   { changeFat(10);}
    
    public void penaltyAAAA()    { changeFat(-2000);}
    public void penaltyAAA() { changeFat(-1000);}
    public void penaltyAA()   { changeFat(-60);}
    public void penaltyA()   { changeFat(-10);}
    public void kill() {  this.alive = false; changeFat(-5000000);  Env.clearMaterial(xPos, yPos, animalMaterial);  } //kill是最大的惩罚
    //@formatter:on



    public void show(Graphics g) {// 显示当前动物
        if (!alive)
            return;
        //g.drawImage(animalImage, xPos - 8, yPos - 8, 16, 16, null);// 减去坐标，保证嘴巴显示在当前x,y处
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

    public void addEng(int[] a, float e) {//指定的a坐标对应的cell能量值加e
        addEng(a[0], a[1], a[2], e);
    }

    public void addEng(int x, int y, int z, float e) {//指定的a坐标对应的cell能量值加e
        if (cells[x][y][z] == 0)
            return;
        energys[x][y][z] += e;
        if (energys[x][y][z] > 300)
            energys[x][y][z] = 300;
        if (energys[x][y][z] < -300)
            energys[x][y][z] = -300;
    }

    public float get(int x, int y, int z) {//返回指定的a坐标对应的cell能量值
        return energys[x][y][z];
    }

    static final int HOLE_MAX_SIZE = 1000 * 1000;

    public void digHole(int[] srcPos, int[] targetPos, int holeSize, int fresh) {
        digHole(srcPos[0], srcPos[1], srcPos[2], targetPos[0], targetPos[1], targetPos[2], holeSize, fresh);
    }

    public void digHole(int sX, int sY, int sZ, int[] targetPos, int holeSize, int fresh) {
        digHole(sX, sY, sZ, targetPos[0], targetPos[1], targetPos[2], holeSize, fresh);
    }

    public static final int HOLE_ARR_SIZE = 5; //洞由几个参数构成 

    public void digHole(int sX, int sY, int sZ, int tX, int tY, int tZ, int size, int fresh) {//在t细胞上挖洞，将洞的方向链接到源s，如果洞已存在，扩大洞, 新洞大小为1，洞最大不超过100
        if (!hasGene(tX, tY, tZ))
            return;
        if (!Env.insideBrain(sX, sY, sZ))
            return;
        if (!Env.insideBrain(tX, tY, tZ))
            return;
        if (get(tX, tY, tZ) < 1) //要调整
            addEng(tX, tY, tZ, 1); //要调整

        int[] cellHoles = holes[tX][tY][tZ];
        if (cellHoles == null) { //洞不存在，新建一个， 洞参数是一个一维数组，分别为源坐标X,Y,Z, 洞的大小，洞的新鲜度 
            holes[tX][tY][tZ] = new int[]{sX, sY, sZ, size, fresh}; //
            return;
        } else {
            int emptyPos = -1; //找指定源坐标已存在的洞，如果不存在，如发现空洞也可以占用
            for (int i = 0; i < cellHoles.length / HOLE_ARR_SIZE; i++) {
                int n = i * HOLE_ARR_SIZE;
                if (cellHoles[n] == sX && cellHoles[n + 1] == sY && cellHoles[n + 2] == sZ) {//找到已有的洞了
                    if (cellHoles[n + 3] < 1000) //要改成由基因调整
                        cellHoles[n + 3] += size;
                    if (cellHoles[n + 4] < 1000) //要改成由基因调整
                        cellHoles[n + 4] += fresh;
                    return;
                }
                if (emptyPos == -1 && cellHoles[n + 3] <= 1)//如发现空洞也可以，先记下它的位置
                    emptyPos = n;
            }

            if (emptyPos > -1) { //找到一个空洞
                cellHoles[emptyPos] = sX;
                cellHoles[emptyPos + 1] = sY;
                cellHoles[emptyPos + 2] = sZ;
                if (cellHoles[emptyPos + 3] < 1000) //要改成由基因调整
                    cellHoles[emptyPos + 3] += size;
                if (cellHoles[emptyPos + 4] < 1000) //要改成由基因调整
                    cellHoles[emptyPos + 4] += fresh;
                return;
            }

            int length = cellHoles.length; //没找到已有的洞，也没找到空洞，新建一个并追加到原洞数组未尾
            int[] newHoles = new int[length + HOLE_ARR_SIZE];
            System.arraycopy(cellHoles, 0, newHoles, 0, length);
            newHoles[length] = sX;
            newHoles[length + 1] = sY;
            newHoles[length + 2] = sZ;
            newHoles[length + 3] = size; //要改成由基因调整
            newHoles[length + 4] = fresh; //要改成由基因调整
            holes[tX][tY][tZ] = newHoles;
            return;
        }
    }

    public void holeSendEngery(int[] pos, float e) {//在当前细胞所有洞上反向发送能量（光子)，le是向左边的细胞发， re是向右边的细胞发
        holeSendEngery(pos[0], pos[1], pos[2], e);
    }

    public void holeSendEngery(int x, int y, int z, float e) {//在当前细胞所有洞上反向发送能量（光子)，le是向左边的细胞发， re是向右边的细胞发
        int[] cellHoles = holes[x][y][z]; //cellHoles是单个细胞的所有洞(触突)，4个一组，前三个是洞的坐标，后一个是洞的大小
        if (cellHoles == null) //如洞不存在，不发送能量 
            return;
        for (int i = 0; i < cellHoles.length / HOLE_ARR_SIZE; i++) {
            int n = i * HOLE_ARR_SIZE;
            float size = cellHoles[n + 3];
            if (size > 1) {
                addEng(cellHoles[n], cellHoles[n + 1], cellHoles[n + 2], e + cellHoles[n + 3] + cellHoles[n + 4]); //向源细胞反向发送常量大小的能量 
            }
        }
    }

    public void holesReduce() {//所有hole大小都会慢慢减小，模拟触突连接随时间消失，即细胞的遗忘机制，这保证了系统不会被信息撑爆
        for (int x = 0; x < Env.BRAIN_SIZE - 1; x++)
            for (int y = 0; y < Env.BRAIN_SIZE - 1; y++)
                for (int z = 0; z < Env.BRAIN_SIZE - 1; z++) {
                    int[] cellHoles = holes[x][y][z];
                    if (cellHoles != null)
                        for (int i = 0; i < cellHoles.length / HOLE_ARR_SIZE ; i++) {
                            int n = i * HOLE_ARR_SIZE;
                            int size = cellHoles[n + 3];
                            if (size > 0)
                                cellHoles[n + 3] = (int) (size * 0.9);//要改成由基因调整
                            int  fresh = cellHoles[n + 4];
                            if (fresh > 0)
                                cellHoles[n + 4] -= Consts.HOLE_REDUCE  ;//要改成由基因调整
                            
                        }
                }
    }

}
