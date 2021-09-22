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

/**
 * Cell is the smallest unit of brain  
 * Cell是脑的最小单元， cell的行为由它的器官类型决定
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

    public int geneLine; //指向青蛙基因单例中的行号。每个细胞的基因都相同，但是不同的是在基因链中的行号

    public int splitCount; //从受精卵开始分裂到当前细胞时的分裂次数

    public int splitLimit; //从受精卵开始分裂到当前细胞时，基因中决定的细胞分裂次数限制值

    // energy of cell
    public float energy = 0; // 每个细胞当前的能量值

    public Cell(int x, int y, int z, int geneLine, int splitCount, int splitLimit) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.geneLine = geneLine;
        this.splitCount = splitCount;
        this.splitLimit = splitLimit;
    }

    public void act() {
        //TODO:细胞动作
    }

}
