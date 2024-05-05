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
 * Line is line connect 2 cells, may have many attributes
 * Line是一个随机连接两个脑细胞的连线，连线有多种不同属性，不同的属性有不同的静态、动态参数，以及不同的行为
 * 因为Line数量太多了，对象的创建消毁太消耗资源，为了节约内存和提高速度，把Line设计成一个虚拟逻辑对象，不会实际创建它的实例。所以目前这个Line里只保留静态方法。

 * Line的动态参数保存在Animal.lines数列中，每个元素是一个整数数组，分别为 type,x1,y1,z1,x2,y2,z2,dres，解释见下：
 * type: 线的类型，同样类型的线，它的常量参数是相同的，常量参数保存在Animal.consts数组中，起点为type*countsQTY
 * x1,y1,z1为源，x2,y2,z2为目的。能量永远是从源到目流动，与能量的正负无关。要实现双向能量流动必须建立两根线。
 * dres： dynamic resistance 动态电阻值，动态电阻率敏感度大于0时，电阻在重复信号刺激下会变小，这样就可用电阻来作为记忆元件，这个值是个有状态的值，每根线条都要单独记录，不像其它参数是个常量
    
 *  Line的常量参数都取值在0~1之间，保存在Animal.consts数组中，起点为type*countsQTY，通过这种方法可以保证相同type的线它的常量参数相同(以便于将来移植到分裂算法上)，参数依次为：
 *  res: resistance, 常量电阻，即通常说的权重, 0时为断路，1时为全通，因为no相同的line常量相同，所以一条线只需要记录no，不需要记录每个常量参数
 *  drs: dynamic resistance sensitivity 动态电阻率敏感度，0时电阻率不会变动， 1时电阻率会随信号重复立即变到1(全通），其它值处于两者之间
 *  ovf：overflow 常量溢流阀值， 当传递的能量大于阀值时，高于阀值的部分能量传递
 *  ovb: overflow break 常量溃坝阀值， 当传递的能量大于阀值时, 能量全部传递
 *  not: not logic 反相器，如>0.5, 将会对通过的能量反相，即乘以-1
 *  mul: multiple 乘法器，会对通过的能量值乘以一个倍数，0为1，1时为10倍
 *  min: minus 扣能器， 会对细胞的能量扣除一部分，0为0，1时为1，直到细胞能量为0
 *  add: add 加能器，会对细胞能量增加一部分，0为0，1时为1,直到细胞能量为1

 *  常量参数是叠加的，一个Line可以同时具有常量电阻、动态电阻、溢流阀、反相器、扣能器等属性，优先级暂以上面顺序为准
   
 * 题外话:
 * 目前版本Line的数量少，可以为每个Line的参数在Animal里分配一个数组空间。当数量多后，将来打算可能用分裂算法来代替随机连线算法，分裂算法主要优点一是压缩率高，二是
 * 树形结构比较合理，节点的等级不同，一个节点的变异可能造成整个子树的增删，随机连线算法做不到这一点，所有节点都是同等级的，无法应对环境突变情况
 * 如果将来采用分裂算法，Line的所有动态、静态参数都要想办法挂靠在Cell上，比如源、目的坐标要改成相对当前细胞的偏移量，这样就把同一类细胞的绝对坐标动态参数变成了相对坐标的静态常量。
 * Line至少会有个动态电阻当前dres值，这是个有状态的值，无法变成常量，将来也要想办法把每根线的这个值挂靠在Cell上。反正目标就是消除Line这个对象的生成，让它变成一个虚拟对象，以节约内存和提高速度。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Line implements Serializable {
    private static final long serialVersionUID = 1L;

    public static int countsQTY = 8; // res, drs, ovf , ovb, not ...等常量参数 

    public static void randomAddorRemoveLine(Animal a) {//对animal的线条个数进行随机增减。线条参数的变异不在这个方法中，而是在常量变异，见anmial的constMutate方法
        if (RandomUtils.percent(10f)) { // 这两行随机加减Line
            int i = RandomUtils.nextInt(Genes.dots.size());
            Object[] d1 = Genes.dots.get(i);
            i = RandomUtils.nextInt(Genes.dots.size());
            Object[] d2 = Genes.dots.get(i);
            a.lines.add(new int[] { RandomUtils.nextInt(Animal.CountsQTY / countsQTY), (int) d1[1], (int) d1[2], (int) d1[3], (int) d2[1], (int) d2[2], (int) d2[3], 5000 });
        }
        if (!a.lines.isEmpty() && RandomUtils.percent(10f))
            a.lines.remove(RandomUtils.nextInt(a.lines.size())); 
    }

    // ========= active方法在每个主循环都会调用，调用animal所有Line的行为，这是个重要方法 ===========
    public static void active(Animal a, int step) {//
        for (int[] l : a.lines) {
            int start = l[0] * countsQTY; //start是line在常数数组的起始点
            int x1 = l[1];
            int y1 = l[2];
            int z1 = l[3];
            int x2 = l[4];
            int y2 = l[5];
            int z2 = l[6];
           // int dres = l[7]; //dres取值0~10000对应电阻率0~1
            float res = a.consts[start++]; //resistance, 常量电阻，即通常说的权重, 0时为断路，1时为全通，
            float drs = a.consts[start++]; //dynamic resistance sensitivity 动态电阻率敏感度，0时电阻率不会变动， 1时电阻率会随信号重复立即变到1(全通），其它值处于两者之间
            float ovf = a.consts[start++]; //overflow 常量溢流阀值， 当传递的能量大于阀值时，高于阀值的部分能量传递
            float ovb = a.consts[start++]; //overflow break 常量溃坝阀值， 当传递的能量大于阀值时, 能量全部传递
            float not = a.consts[start++]; //not logic 反相器，如>0.5, 将会对通过的能量反相，即乘以-1
//            float mul = a.consts[start++]; //乘法器，会对通过的能量值乘以一个倍数，0为1，1时为10倍
//            float min = a.consts[start++]; //扣能器， 会对细胞的能量扣除一部分，0为0，1时为1，直到细胞能量为0
//            float add = a.consts[start++]; //加能器，会对细胞能量增加一部分，0为0，1时为1

            float e = a.getEng(x1, y1, z1);
            if (e < 0.1f)
                continue;
            if (e > 0.3f) {
//                if (min > 0.1) { //扣能器
//                    float newSrcEng = a.getEng(x1, y1, z1) - min;
//                    a.setEng(x1, y1, z1, newSrcEng);
//                }
//                if (add > 0.1) { //加能器
//                    float newSrcEng = a.getEng(x1, y1, z1) + add;
//                    a.setEng(x1, y1, z1, newSrcEng);
//                }
            }
            if (e < ovb) //溃坝阀值
                continue;
            if (ovf > 0.1) { //溢流阀
                e = e - ovf;
            }
            e = e * res; //静态电阻
//            e = e * dres * .0001f; //动态电阻0~10000之间
//            if (drs > 0.1) { //如果动态电阻敏感系统存在，则每次传送一次能量，电阻就变小一次
//                dres += drs;
//                if (dres > 10000)
//                    dres = 10000;
//            }
            if (not > 0.5) //反相器
                e = -e;
//            if (mul > 0.1) {
//                e = e * 10 * mul; //mul是乘法器，mul在0~1之间，但是它控制的倍率在0~10倍之间
//            }
            a.addEng(x2, y2, z2, e); //能量传到target cell
        }

    }

    //TODO： 1 模式识别的环境模拟和判定   2.静态参数的初值、变异 ，上面这个方法只是随便写的，很可能跑不出结果，要调整为大多数参数不起作为,即大多数情况下设为0

    public static void drawOnBrainPicture(Animal a, BrainPicture pic) {// 画出所有细胞间连线
        for (int[] l : a.lines) { 
            float f1 = RandomUtils.nextFloat() * .1f;//稍微调整下位置好看出来是否有多条线在同一个位置
            float f2 = RandomUtils.nextFloat() * .1f;
            pic.drawCentLine(l[1] + f1, l[2] + f2, l[3] + f2, l[4] + f1, l[5], l[6]);
            pic.drawTextCenter(0.5f * (l[1] + l[4]), 0.5f * (l[2] + l[5]), 0.5f * (l[3] + l[6]), Integer.toString(l[0]), 0.15f);
            pic.drawPointCent(l[4] + f1, l[5], l[6], .1f);
        }
    }
}
