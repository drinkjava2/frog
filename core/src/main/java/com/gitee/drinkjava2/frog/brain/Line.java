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


//=================================================
//TODO:这个类将推翻全部重写，打算只保留静态方法，因为Line数量太多了，不应该给它分配内存。现在虽然用随机连线算法，也可以先为分裂算法做准备，省得到时逻辑相差太大，基本要重写一遍
//基本思路是将Line归类，同样参数的Line放在一组数组里进化。然后随机算法生成的线条只需要对应一个Line类型序号即可，如果用分裂算法，一个基因位可以对应一类细胞，一类细胞又可以包含一群Line
//   
//=================================================



// 以下内容作废

/**
 * Line is line connect 2 cells, may have many attributes
 * 
 * Line是一个随机连接两个脑细胞的连线，连线有多种不同属性，但每个不同属性只有一个，因为每种属性通常只定义了一个变量保存它的属性值
 * 最初打算是用不同的type表示不同的线条，现在改成属性值只要不为零即生效, 去除type这个字段
 * 
 * 目前线数量少，可以用随机连线算法，当 数量多后，将可能用分裂算法来代替随机连线算法，分裂算法 主要优点是树形结构比较合理，节点的等级不同，一个节
 * 点的变异可能造成整个子树的增删，随机连线算法做不到这一点，所有节点都是同等级的，无法应对环境突变情况
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Line implements Serializable {
    private static final long serialVersionUID = 1L;

    public int x1, y1, z1, x2, y2, z2; // 连线的源位头尾两个细胞坐标位置， 能量总是从源向目地流动，永远是单向的，无大小无关。如果要产生双向信号必须建立两根线。

    public float res; // resistance, 电阻率，取值-10到+10之间，即通常说的权重，电阻率是负值时表示对原信号产生反相作用(即产生了非门逻辑)，0时为断路，1时为全通
                      // 电阻绝对值大于1时表示对信号产生了放大作用，打破了能量守恒，从而有可能形成循环回路，
                      // 大多数情况下电阻应该绝对值不大于1，因为传到细胞后能量上限是1，大太多没有意义
    public float dor; // dynamic rate of resistance
                      // 电阻值动态系数，这个取代以前的电容思路，电阻动态系数越大，电阻越容易在外界信号刺激下变小，这样就可用电阻来作为记忆元件，经常活跃的信号会导致电阻降到0产生直接通路
                      // 电阻的电阻降低、电阻回复特性基本可以参照生物脑细胞的学习和遗忘曲线
    public float ovf; // overflow溢流阀值， 当传递的能量大于阀值时，高于阀值的部分能量传递
    public float ovb; // overflow break溃坝阀值， 当传递的能量大于阀值时, 能量全部传递

    public Line() { // 缺省构造器从蛋里读出时要调用这个
    }

    public Line(int[] from, int[] to) { // 这个构造方法第一次随机生成线条时手工调用
        x1 = from[0];
        y1 = from[1];
        z1 = from[2];
        x2 = to[0];
        y2 = to[1];
        z2 = to[2];

        // 以下的初值随机生成，初值不重要，因为这个值会存到蛋里去并由遗传算法选出最佳值，而且每个line的值也会自己变异
        // 如果今后采用分裂算法，无数个线就有无数个初值，蛋就放不下了，这时初值就不能直接存到蛋里去了，而是要占用若干个基因位，由2/4/8黑白树基因来控制海量的初值，相同基因的细胞初值相同
        res = RandomUtils.nextNegOrPosFloat();
        dor = RandomUtils.nextNegOrPosFloat();
        ovb = RandomUtils.nextNegOrPosFloat();
    }

    public void vary() { // Line参数的自变异， 随机连线算法在蛋孵化时调用一次，如采用分裂算法，基因的变异即代表初值的变异
        if (RandomUtils.percent(2))
            res = RandomUtils.varyInLimit(res, -1, 1);
        if (RandomUtils.percent(2))
            dor = RandomUtils.varyInLimit(dor, -1, 1);
        if (RandomUtils.percent(2))
            ovb = RandomUtils.varyInLimit(ovb, -1, 1);
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

        if (ovb > 0) { // 溢流阀，能量从高到低并且只传送高出的部分
            eTo = max1(eFrom + eTo);
            eFrom = 0;
            a.setEng(x1, y1, z1, eFrom);
            a.setEng(x2, y2, z2, eTo);
        }

    }

    private static final float pm = .01f; // plus正数最小值
    private static final float mm = -.01f; // minus负值最小值

    private static String r2(float f) {// 返回小数后两位
        return String.format("%.2f", f);
    }

    public void drawOnBrainPicture(Animal a, BrainPicture pic) {// 把自已这个器官在脑图上显示出来
        pic.drawCentLine(x1, y1, z1, x2, y2, z2);
        StringBuilder name = new StringBuilder();
        name.append("r").append(r2(res));
        name.append("d").append(r2(dor));
        name.append("b").append(r2(ovb));
        pic.drawTextCenter(0.5f * (x1 + x2), 0.5f * (y1 + y2), 0.5f * (z1 + z2) + res * .2f, name.toString(), 0.1f);
    }

}
