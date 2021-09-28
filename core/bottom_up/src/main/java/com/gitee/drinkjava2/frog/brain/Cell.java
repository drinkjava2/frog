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

/**
 * Cell is the smallest unit of brain  
 * Cell是脑的最小单元， cell的行为由它的触突参数决定。脑的功能由cell的位置和cell的行为决定
 *

器官位置、方向、厚度、脑细胞分布直径、细胞发散或聚焦角度
单个细胞方向、能量吸收曲线、能量发送曲线(阀值、是否永久激活、延时发送、脉冲式发送）、发送方向(正、反、双向)
是否是视细胞、动作细胞
触突(hole)？(固定式触突，或动态生成触突?)


 * @author Yong Zhu
 * @since 1.0
 */
public class Cell { //cell数量非常庞大，不需要序列化
    public int x; //x,y,z 是细胞的中心点在脑中的位置
    public int y;
    public int z;

    public int geneIndex; //指向青蛙基因单例中的行号。每个细胞的基因都相同，但是不同的是在基因链中的行号

    public int splitCount; //从受精卵开始分裂到当前细胞时的分裂次数

    public int splitLimit; //从受精卵开始分裂到当前细胞时，基因中决定的细胞分裂次数限制值

    // energy of cell
    public float energy = 0; // 每个细胞当前的能量值

    public Cell(Animal animal, int x, int y, int z, int geneIndex, int splitCount, int splitLimit) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.geneIndex = geneIndex;
        this.splitCount = splitCount;
        this.splitLimit = splitLimit;
        animal.cells.add(this);
        animal.cells3D.putCell(this, animal.cells.size()); //在cell3D中登记cell序号
        animal.normalAward(); //TODO: 调试用，待删
    }

    public void split(Animal animal, int direction) {//细胞在一个或多个方向上分裂克隆，有6（对应立方体的6个面)、7(包含本身点)、27(包含立方体侧边、项点方向)等选项，这里先采用6个方向的方案
        int xx = x;
        int yy = y;
        int zz = z;
        if ((direction & 1) > 0) {//上
            zz++;
            clone(animal, xx, yy, zz); //简单在指定隔壁位置克隆，暂不采用推开其它细胞的高运算量方案，这个要等图型卡加速用上后再考虑推开其它细胞
        }
        if ((direction & 0b10) > 0) {//下
            zz--;
            clone(animal, xx, yy, zz);
        }
        if ((direction & 0b100) > 0) {//左
            xx--;
            clone(animal, xx, yy, zz);
        }
        if ((direction & 0b1000) > 0) {//右
            xx++;
            clone(animal, xx, yy, zz);
        }
        if ((direction & 0b10000) > 0) {//前
            yy--;
            clone(animal, xx, yy, zz);
        }
        if ((direction & 0b100000) > 0) {//后
            yy++;
            clone(animal, xx, yy, zz);
        }
    }

    public void clone(Animal animal, int xx, int yy, int zz) {//在指定坐标克隆当前细胞
        if (Animal.outBrainRange(xx, yy, zz))
            return;
        new Cell(animal, xx, yy, zz, geneIndex, splitCount, splitLimit); 
    }

    public void act() {
        //TODO:细胞动作
    }

}
