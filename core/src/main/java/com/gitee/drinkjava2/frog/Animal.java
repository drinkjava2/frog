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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.gitee.drinkjava2.frog.brain.Cell;
import com.gitee.drinkjava2.frog.brain.Cells3D;
import com.gitee.drinkjava2.frog.brain.Organ;
import com.gitee.drinkjava2.frog.egg.Egg;
import com.gitee.drinkjava2.frog.judge.BrainShapeJudge;
import com.gitee.drinkjava2.frog.objects.Material;
import com.gitee.drinkjava2.frog.util.EightTreeUtil;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Animal is all artificial lives' father class
 * 
 * Animal是所有动物（青蛙、蛇等）的父类, animal是由蛋孵出来的，蛋里保存着脑细胞结构生成的基因
 * 
 * @author Yong Zhu
 * 
 * @since 1.0
 */
public abstract class Animal {// 这个程序大量用到public变量而不是getter/setter，主要是为了编程方便和简洁，但缺点是编程者需要小心维护各个变量
    public static BufferedImage FROG_IMAGE;
    public static BufferedImage snakeImage;
    transient public ArrayList<Integer> gene = new ArrayList<>(); // Animal的基因只保存一份，这是人工生命与实际生物（每个细胞都保留一份基因）的最大不同

    static {
        try {
            FROG_IMAGE = ImageIO.read(new FileInputStream(Application.CLASSPATH + "frog.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** brain cells */
    public List<Cell> cells = new ArrayList<>();
    public Cells3D cells3D = new Cells3D(this);

    /** brain organs */
    public List<Organ> organs = new ArrayList<>();
    public Cells3D organ3D = new Cells3D(this);

    public int x; // animal在Env中的x坐标
    public int y; // animal在Env中的y坐标
    public long energy = 100000; // 青蛙的能量为0则死掉
    public boolean alive = true; // 设为false表示青蛙死掉了，将不参与计算和显示，以节省时间
    public int ateFood = 0; // 青蛙曾吃过的食物总数，下蛋时如果两个青蛙能量相等，可以比数量
    public int no; // 青蛙在Env.animals中的序号，从1开始， 会在运行期写到当前brick的最低位，可利用Env.animals.get(no-1)快速定位青蛙

    public int animalMaterial;
    public Image animalImage;

    public Animal(Egg egg) {// x, y 是虑拟环境的坐标
        this.gene.addAll(egg.gene); //动物的基因是蛋的基因的拷贝
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

    private static final int MIN_ENERGY_LIMIT = Integer.MIN_VALUE + 5000;
    private static final int MAX_ENERGY_LIMIT = Integer.MAX_VALUE - 5000;

    //energy大小是环境对animal唯一的奖罚，也是animal唯一的下蛋竟争标准。调用下面几个方法来进行不同程度的奖罚

    public void adjustEnergy(int en) {
        energy += en;
        if (energy > MAX_ENERGY_LIMIT)
            energy = MAX_ENERGY_LIMIT;
        if (energy < MIN_ENERGY_LIMIT)
            energy = MIN_ENERGY_LIMIT;
    }

    //@formatter:off 下面几行是重要的奖罚方法，会经常调整或注释掉，集中放在一起，不要格式化为多行   
    public void bigAward()      { adjustEnergy(500);}
    public void normalAward()   { adjustEnergy(50);}
    public void tinyAward()     { adjustEnergy(1);}
    public void bigPenalty()    { adjustEnergy(-500);}
    public void normalPenalty() { adjustEnergy(-100);}
    public void tinyPenalty()   { adjustEnergy(-1);}
    public void kill() {  this.alive = false; adjustEnergy(-5000);  Env.clearMaterial(x, y, animalMaterial);  } //kill是最大的惩罚
    //@formatter:on

    public void initAnimal() { // 初始化animal,生成脑细胞是在这一步，这个方法是在当前屏animal生成之后调用，比方说有一千个青蛙分为500屏测试，每屏只生成2个青蛙的脑细胞，可以节约内存
        geneMutation(); //有小概率基因突变
        createCellsFromGene(); //运行基因语言，生成脑细胞
        BrainShapeJudge.judge(this); //重要，对细胞的形状是否符合模子的形状进行能量奖励或扣分
    }

    public boolean active() {// 这个active方法在每一步循环都会被调用，是脑思考的最小帧
        // 如果能量小于0、出界、与非食物的点重合则判死
        if (!alive) {
            energy = MIN_ENERGY_LIMIT; // 死掉的青蛙确保淘汰出局
            return false;
        }
        if (energy <=  MIN_ENERGY_LIMIT || Env.outsideEnv(x, y) || Env.bricks[x][y] >= Material.KILL_ANIMAL) {
            kill();
            return false;
        }
        //energy -= 20;
        // 依次调用每个cell的active方法
        //for (Cell cell : cells)
        //    cell.organ.active(this, cell);
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

    public Cell getOneRandomCell() {
        if (cells.isEmpty())
            return null;
        return cells.get(RandomUtils.nextInt(cells.size()));
    }

    private void geneMutation() { //基因变异

    }

    private void createCellsFromGene() {//根据基因生成细胞
        for (int i = 0; i < EightTreeUtil.EIGHT_TREE.length; i++) {
            int[] p = EightTreeUtil.EIGHT_TREE[i];
            if (p[3] == 1) {
                new Cell(this, p[0], p[1], p[2]);
            }
        }
    }
}
