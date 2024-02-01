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
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Line is line connect 2 cells, may have one or more
 * line/diode/resistance/capacity attributes(types)
 * 
 * Line是一个随机方式两个脑细胞的连线，连线并不是简单直连，每个连线可以有一或多个属性，详见变量定义的注释部分
 * 本版本中Line还是先用传统的随机生成的方式生成，将来再考虑用分裂算法由基因来生成，
 * 感觉分裂算法优点是适合控制海量细胞，当细胞数量少时随机连线方法使用起来更方便快捷。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Line implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int _index = 1;

    // 以下属性是串联生效的, 二极管的优先权最高
    public static final int DIO = 1; // 二级管，能量只能单向流动, 二极管有最高优先级，当其它属性带来的能量流动方向与二级管相反时会被阻止
    public static final int OVF = 1 << _index++; // (overFlow)溢流阀, 当细胞问有能差且大于阀值时，能量差减去阀值后的能量从高向低传递
    public static final int OVB = 1 << _index++; // (overBreak)溃坝阀, 当两个细胞之间能量差大于阀值后溃坝, 能量差从高向低传递不再减去阀值
    public static final int RES = 1 << _index++; // 电阻, 能量从高向低传递并乘以电阻率, 电阻率为1时相当于无电阻直连，为0时相当于断路，为-1时相当于产生负信号(非门)
    public static final int CAP = 1 << _index++; // 电容，能量会在这里暂时保存，作为记忆的基础器件。电容比较复杂，充电、放电、遗忘特性基本参照生物脑细胞的学习和遗忘曲线

    // 三级管？连线没有这种属性，不知道实际生物有没有三级管这种脑细胞。简单来说就是，目前打算用以上元件组装一台电脑，但是不允许使用三级管

    public int type = 0; // type中哪个位如果为1表示上面的某个属性

    public int x1, y1, z1, x2, y2, z2; // 连线的源位头尾两个细胞坐标位置

    public float res; // 电阻值，即通常说的权重值，电阻相对固定，电容则是动态的
    public float ovf; // 溢流阀值
    public float ovb; // 溃坝阀值
    public float cap; // 电容值

    public Line() { // 缺省构造器从蛋里读出时要调用这个
    }

    public Line(int[] from, int[] to) { // 这个构造方法第一次随机生成线条时手工调用
        this.type = 1 << RandomUtils.nextInt(_index); // 属性随机生成，当前版先只允许一个属性
        x1 = from[0];
        y1 = from[1];
        z1 = from[2];
        x2 = to[0];
        y2 = to[1];
        z2 = to[2];

        // 以下的初值随机生成，初值不重要，因为这个值会存到蛋里去并由遗传算法选出最佳值，而且每个line的值也会自己变异
        // 如果今后采用分裂算法，无数个线就有无数个初值，蛋就放不下了，这时初值就不能直接存到蛋里去了，而是要占用若干个基因位，由2/4/8黑白树基因来控制海量的初值，相同基因的细胞初值相同
        res = RandomUtils.nextNegOrPosFloat();
        ovf = RandomUtils.nextNegOrPosFloat();
        ovb = RandomUtils.nextNegOrPosFloat();
        cap = RandomUtils.nextNegOrPosFloat();
    }

    public void vary() { // Line参数的自变异， 随机连线算法在蛋孵化时调用一次，如采用分裂算法，基因的变异即代表初值的变异
        if (RandomUtils.percent(2))
            res = RandomUtils.varyInLimit(res, -1, 1);
        if (RandomUtils.percent(2))
            ovf = RandomUtils.varyInLimit(ovf, -1, 1);
        if (RandomUtils.percent(2))
            cap = RandomUtils.varyInLimit(cap, -1, 1);
    }

    public static float max1(float f) {
        if (f > 1)
            return 1;
        if (f < -1)
            return -1;
        return f;
    }

    public void active(Animal a) {// line干的活，就是各种花式把能量在细胞间搬来搬去，这个行为允许type有多种属性，虽然当前版type只允许存在一个属性
        float eFrom = a.energys[x1][y1][z1];
        float eTo = a.energys[x2][y2][z2];

        if ((type & DIO) > 0 && eFrom > eTo) { // 二极管，能量单向流动,不管大小，从from到to能量全传给它
            eTo = max1(eFrom + eTo);
            eFrom = 0;
            a.setEng(x1, y1, z1, eFrom);
            a.setEng(x2, y2, z2, eTo);
        }

        if ((type & RES) > 0) {// 电阻，能量从高到低流动
            eTo = max1((eFrom* res + eTo) * .5f );
            eFrom = eFrom* res;
            a.setEng(x1, y1, z1, eFrom);
            a.setEng(x2, y2, z2, eTo);
        }

        if ((type & OVF) > 0) { // 溢流阀，能量从高到低并且只传送高出的部分
            eTo = max1(eFrom + eTo);
            eFrom = 0;
            a.setEng(x1, y1, z1, eFrom);
            a.setEng(x2, y2, z2, eTo);
        }

    }

    public void drawOnBrainPicture(Animal f, BrainPicture pic) {// 把自已这个器官在脑图上显示出来
        pic.drawCentLine(x1, y1, z1, x2, y2, z2);
        StringBuilder name = new StringBuilder();
        if ((type & DIO) > 0)
            name.append("D");
        if ((type & OVF) > 0)
            name.append("O");
        if ((type & OVB) > 0)
            name.append("B");
        if ((type & RES) > 0)
            name.append("R");
        if ((type & CAP) > 0)
            name.append("C");
        float f1 = RandomUtils.nextFloat() * 0.2f - 0.1f; // 随机略改变一下名字输出位置，这样可以根据文字有多少知道有多少根线在同一位置
        pic.drawTextCenter(0.5f * (x1 + x2), 0.5f * (y1 + y2) + f1, 0.5f * (z1 + z2) + f1, name.toString(), 0.2f); // pic.drawCircle(x,// y,z, 0.2f);
    }

}
