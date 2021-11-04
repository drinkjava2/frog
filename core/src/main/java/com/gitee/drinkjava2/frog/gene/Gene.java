/* Copyright 2018-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.gitee.drinkjava2.frog.gene;

import java.awt.Color;
import java.util.List;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.brain.Cell;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Gene is a language, here give the keywords of this language
 * 
 * Gene是一个仿造大自然随机生成的语言。它采用类似BASIC的语法,只有少数几个字键字。这个类里定义语言的关键字常量和对这些关键字的解析行为, 
 * 这个语言主要的作用是实现分裂造形，包括 身体结构造形（不重要，但先实现这个)和脑细胞结构造形(重点)
 * 
 * Gene语法的每行由一个long编码表示，高32位是关键字，低32位是参数，由关键字决定，如:
 * 
 * 10 + 100 表示跳转到100行语句执行, 即10(=GOTO) + 100(行号) 
 * 11 + 0 表示结束执行，停止细胞分裂, 即11(结束) + 0(参数)
 * 
 * @author Yong Zhu
 * @since 2021-09-16
 */
public class Gene {// NOSONAR 

    private static int index = 9; //关键字是一个两位数字字符，从10开始依次往下排。关键字没有可读性，调试时要用printGene方法将关键字转为可读的语句
    public static String[] TEXT = new String[20]; //这里存放关键字的文字解释，供打印输出用

    //下面这些基因关键字是失败的尝试，从2021-10-13这个提交开始不再使用
    //    public static final int GOTO = nextKeyword("GOTO"); //GOTO关键字=10
    //    public static final int END = nextKeyword("END"); //结束执行=11
    //    public static final int SPLIT = nextKeyword("SPLIT"); //执行细胞分裂， 分裂方向由第二部分的数值决定，一个细胞有可能同时在多个方向分裂出多个细胞，有6个或27个方向等
    //    public static final int SPLIT_LIMIT = nextKeyword("SPLIT_LIMIT"); //细胞分裂寿命,  0表示可以无限分裂
    public static final int NEW_CELL = nextKeyword("NEW_CELL"); //在新位置新创建一个细胞，而不是由其它细胞分裂出来

    public static final int FIRST_KEYWORD = NEW_CELL;
    public static final int LAST_KEYWORD = index; //最后一个关键字

    static private int nextKeyword(String explain) {
        index++;
        TEXT[index] = explain;
        return index;
    }

    static final StringBuilder sb = new StringBuilder(); //用来将方向转为可读的英文缩写 

    public static int toCode(Long gene) {
        return (int) (gene >> 32);
    }

    public static int toParam(Long gene) {
        return (int) (gene & 0xffffffffL);
    }

    public static long toGene(int code, int param) {
        long code_ = code;
        return (code_ << 32) + param;
    }

    public static void printGene(Animal animal) {
        int i = 0;
        for (long gene : animal.gene) {
            int code = toCode(gene);
            int param = toParam(gene);
            String paramStr = "" + param;
            System.out.println(i++ + " " + TEXT[code] + " " + paramStr);
        }
    }

    public static void run(Animal animal) { //对于给定的细胞，由基因、这个细胞所处的行号、细胞的分裂寿命、细胞已分裂的次数、以及细胞所处的身体坐标、以及细胞周围是否有细胞包围来决定它的下一步分裂行为
        for (int i = 0; i < animal.gene.size(); i++) {
            long gene = animal.gene.get(i);
            int code = toCode(gene);
            int param = toParam(gene);
            if (code == NEW_CELL) { //新建一个细胞
                int x = param / 1000000;
                int y = (param - x * 1000000) / 1000;
                int z = param % 1000;
                Cell c = new Cell(animal, x, y, z, i);
                c.color = Color.RED;
            }
        }
    }

    private static Long randomGeneCode(Animal animal) {//生成一个随机的基因行
        long code = RandomUtils.nextInt(LAST_KEYWORD - FIRST_KEYWORD) + FIRST_KEYWORD;
        int param = randomGeneParam(animal, code);
        return (code << 32) + param;
    }

    public static int randomGeneParam(Animal animal, long code) {//根据基因code生成一个随机合理参数
        int param = 0;
        if (code == NEW_CELL) { //新的细胞坐标位置参数，用一个整数表示, 值为 X*1000000 + Y*1000 + Z
            if (animal.cells.isEmpty()) { //如果animal没有细胞，位置取脑的中间
                return Env.BRAIN_XSIZE / 2 * 1000000 + Env.BRAIN_YSIZE / 2 * 1000 + Env.BRAIN_ZSIZE / 2;
            } else { //如果脑细胞已有了，随机取一个脑细胞，返回它的一个相邻坐标位置
                Cell c = animal.getOneRandomCell();
                int x = c.x;
                int y = c.y;
                int z = c.z;
                x += RandomUtils.nextInt(5) - 2;
                y += RandomUtils.nextInt(5) - 2;
                z += RandomUtils.nextInt(5) - 2;
                return x * 1000000 + y * 1000 + z;
            }
        }
        return param;
    }

    public static void mutation(Animal animal) {//基因随机突变，分为：新增、删除、拷贝、改变、参数改变等情况
        List<Long> genes = animal.gene;

        if (RandomUtils.percent(10)) {
            genes.add(toGene(NEW_CELL, randomGeneParam(animal, NEW_CELL)));
            return;
        }

        float percent = 5; //注：percent这个魔数以后要写在基因里,成为基因的一部分
        if (RandomUtils.percent(percent * 3))
            genes.add(RandomUtils.nextInt(genes.size()), randomGeneCode(animal));

        if (genes.size() > 0 && RandomUtils.percent(percent)) //删除
            genes.remove(RandomUtils.nextInt(genes.size()));

        if (genes.size() > 0 && RandomUtils.percent(percent)) //改变
            genes.set(RandomUtils.nextInt(genes.size()), randomGeneCode(animal));

        if (genes.size() > 0 && RandomUtils.percent(percent)) { //改变参数
            int index = RandomUtils.nextInt(genes.size());
            long gene = genes.get(index);
            long code = toCode(gene);
            int param = randomGeneParam(animal, code); //参数是与code相关的，不同的code其合理参数范围是不一样的
            genes.set(index, (code << 32) + param);
        }

        if (genes.size() > 0 && RandomUtils.percent(percent)) { //批量拷贝,一次拷贝不超过基因长度的1/2
            genes.addAll(RandomUtils.nextInt(genes.size()), genes.subList(0, RandomUtils.nextInt(genes.size() / 2)));
        }

        if (genes.size() > 0 && RandomUtils.percent(percent)) { //批量删除
            int start = RandomUtils.nextInt(genes.size());
            int end = RandomUtils.nextInt(genes.size());
            if (start > end) {
                int tmp = start;
                start = end;
                end = tmp;
            }
            genes.subList(start, end).clear();
        }

    }
}
