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

import java.io.Serializable;

import com.gitee.drinkjava2.frog.Animal;

/**
 * Line是一个随机方式两个脑细胞的连线，连线并不是简单直连，每个连线可以有一或多个属性attr：
 * 直连：信号无损双向传输
 * 二极管：信号只能单向传输
 * 电容：信号会累加存贮在线上
 * 电阻：信号传输会有衰减
 * 溢流阀：仅当信号大于一个阀值时才会传输通过
 * 。。。。。。
 * 
 * 本版本中Line还是先用传统的随机生成的方式生成，将来再考虑用分裂算法由基因来生成
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Line implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int LINE = 1; //直连  
    public static final int DIO = 2; //二级管
    public static final int CAP = 4; //电容
    public static final int RES = 8; //电阻
    public static final int OVR = 16; //溢流阀

    public int attr = 0; // attr中哪个位如果为1表示上面的某个属性

    public int x1, y1, z1, x2, y2, z2; //连线的头尾两个细胞坐标位置

    public float res = 0; //仅当attr有RES属性时有效，这里保存电阻值 
    public float cap = 0; //仅当attr有CAP属性时有效，这里记忆电容量 
    public float ovr = 0; //仅当attr有OVR属性时有效，这里保存溢流阀值 

    public void drawOnBrainPicture(Animal f, BrainPicture pic) {// 把自已这个器官在脑图上显示出来
        pic.drawCentLine(x1, y1, z1, x2, y2, z2);
        float x = x1 * 0.5f + x2 * 0.5f;
        float y = y1 * 0.5f + y2 * 0.5f;
        float z = z1 * 0.5f + z2 * 0.5f;
        if ((attr & CAP) > 0)
            pic.drawCircle(x, y, z, 0.2f);
    }

}
