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
 * Gene语法的每行由若干部分组成，第一部分是关键字，占2格，第二、三、四等部分是可选内容，由关键字决定，如:
 * 
 * 10100 表示跳转到100行语句执行, 即10(=GOTO) + 100(行号) 
 * 110 表示结束执行，停止细胞分裂, 即11(结束) + 0(参数)
 * 
 * @author Yong Zhu
 * @since 2021-09-16
 */
public class Gene {// NOSONAR 
    private static int index = 9; //关键字是一个两位数字字符，从10开始依次往下排。关键字没有可读性，调试时要用printGene方法将关键字转为可读的语句

    public static final int FIRST_KEYWORD = 10;
    public static final int GOTO = nextKeyword(); //GOTO关键字=10
    public static final int END = nextKeyword(); //结束执行=11
    public static final int SPLIT = nextKeyword(); //执行细胞分裂， 分裂方向由第二部分的数值决定，一个细胞有可能同时在多个方向分裂出多个细胞，有6个或27个方向等
    public static final int SPLIT_LIMIT = nextKeyword(); //细胞分裂寿命,  0表示可以无限分裂
    public static final int IF_X_BIG = nextKeyword(); //IF X > 细胞在脑中的X位置大于
    public static final int IF_X_LESS = nextKeyword(); //IF X <
    public static final int IF_Y_BIG = nextKeyword(); //IF Y > 
    public static final int IF_Y_LESS = nextKeyword(); //IF X <
    public static final int IF_Z_BIG = nextKeyword(); //IF Z > 
    public static final int IF_Z_LESS = nextKeyword(); //IF Z <
    public static final int IF_SPLIT_BIG = nextKeyword(); //IF SPLIT > 细胞的分裂数大于 
    public static final int IF_SPLIT_LESS = nextKeyword(); //IF SPLIT < 
    public static final int LAST_KEYWORD = index; //最后一个关键字

    static private int nextKeyword() {
        return ++index;
    }

    public static String[] TEXT = new String[LAST_KEYWORD + 1]; //这里存放关键字的文字解释，供打印输出用
    static {
        TEXT[GOTO] = "GOTO";
        TEXT[END] = "END";
        TEXT[SPLIT] = "SPLIT";
        TEXT[SPLIT_LIMIT] = "SPLIT_LIMIT";
        TEXT[IF_X_BIG] = "IF X>";
        TEXT[IF_X_LESS] = "IF X<";
        TEXT[IF_Y_BIG] = "IF Y>";
        TEXT[IF_Y_LESS] = "IF Y<";
        TEXT[IF_Z_BIG] = "IF Z>";
        TEXT[IF_Z_LESS] = "IF Z<";
        TEXT[IF_SPLIT_BIG] = "IF SPLIT >";
        TEXT[IF_SPLIT_LESS] = "IF SPLIT <";
    }

    static final StringBuilder sb = new StringBuilder(); //用来将方向转为可读的英文缩写 

    public static void printGene(Animal animal) {
        int i = 0;
        for (String s : animal.gene) {
            int code = Integer.parseInt(s.substring(0, 2));
            String paramStr = s.substring(2);
            if (code == SPLIT) {
                sb.setLength(0);
                int direction = Integer.parseInt(paramStr);
                if ((direction & 1) > 0)
                    sb.append("U");//上
                if ((direction & 0b10) > 0)
                    sb.append("D");//下
                if ((direction & 0b100) > 0)
                    sb.append("L");//左
                if ((direction & 0b1000) > 0)
                    sb.append("R");//右
                if ((direction & 0b10000) > 0)
                    sb.append("F");//前
                if ((direction & 0b100000) > 0)
                    sb.append("B");//后
                paramStr = sb.toString();
            }
            System.out.println(i++ + " " + TEXT[code] + " " + paramStr);
        }
    }

    //execute gene language for one cell only 
    public static void run(Animal animal, Cell cell) { //对于给定的细胞，由基因、这个细胞所处的行号、细胞的分裂寿命、细胞已分裂的次数、以及细胞所处的身体坐标、以及细胞周围是否有细胞包围来决定它的下一步分裂行为
        if (cell.geneIndex < 0)
            return;
        if (cell.geneIndex >= animal.gene.size() || cell.splitCount >= cell.splitLimit) {
            cell.geneIndex = 0;
            return;
        }

        String oneLine = animal.gene.get(cell.geneIndex);
        int code = Integer.parseInt(oneLine.substring(0, 2));
        if (code == END) {//如果是END, 结束分裂，参数就不需要解读了
            cell.geneIndex = -1;//改为-1,以后直接跳过这个细胞，不再执行上面的Integer.parseInt
            return;
        }
        int param; //每行基因分为代码和参数两个部分，参数暂定为一个整数
        try {
            param = Integer.parseInt(oneLine.substring(2));
        } catch (NumberFormatException e) { //除了END等关键字外，下面的code都需要参数, 如果参数不是整数杀死青蛙
            animal.kill();
            return;
        }

        if (code == GOTO) {
            if (param < 0 || param >= animal.gene.size()) {//行号太大、太小都不行
                animal.kill();
                return;
            }
            if (cell.splitCount < 10)
                cell.geneIndex = param;
            else
                cell.geneIndex++;
        } else if (code == SPLIT_LIMIT) {//重定义细胞寿命
            cell.splitLimit = param;
            cell.geneIndex++;
        } else if (code == SPLIT) { //执行细胞分裂
            cell.geneIndex++;
            cell.splitCount++;
            if (param < 0 || param > 63) //如果是分裂的话，param应该随机生成落在0~63之内，每个二进制的一个位代表1个分裂方向，共有上下左右前后6个方向
                return;
            cell.split(animal, param);//cell在参数代表的方向进行分裂克隆，可以同时在多个方向克隆出多个细胞
        }
        //下面是执行IF语句，如果条件成立，执行下一行，否则跳过下一行
        //@formatter:off
        else if (code == IF_X_BIG) { if (cell.x > param)cell.geneIndex++;else cell.geneIndex += 2; }
        else if (code == IF_X_LESS) { if (cell.x < param)cell.geneIndex++;else cell.geneIndex += 2; }
        else if (code == IF_Y_BIG) { if (cell.y > param)cell.geneIndex++;else cell.geneIndex += 2; }
        else if (code == IF_Y_LESS) { if (cell.y < param)cell.geneIndex++;else cell.geneIndex += 2; }
        else if (code == IF_Z_BIG) { if (cell.z > param)cell.geneIndex++;else cell.geneIndex += 2; }
        else if (code == IF_Z_LESS) { if (cell.z < param)cell.geneIndex++;else cell.geneIndex += 2; }
        else if (code == IF_SPLIT_BIG) { if (cell.splitCount > param)cell.geneIndex++;else cell.geneIndex += 2; }
        else if (code == IF_SPLIT_LESS) { if (cell.splitCount < param)cell.geneIndex++;else cell.geneIndex += 2; }
        //@formatter:on
    }

    private static String randomGeneCode(Animal animal) {//生成一个随机的基因行
        int code = RandomUtils.nextInt(LAST_KEYWORD + 1 - FIRST_KEYWORD) + FIRST_KEYWORD;
        return "" + code + randomGeneParam(animal, code);
    }

    private static int randomGeneParam(Animal animal, int code) {//根据基因code生成一个随机合理参数
        int param = 0;
        if (code == GOTO) {
            param = RandomUtils.nextInt(animal.gene.size());
        } else if (code == SPLIT_LIMIT) {//细胞寿命
            param = RandomUtils.nextInt(10) + 1; //注： 细胞寿命这个魔数以后要写在基因里
        } else if (code == SPLIT) { //细胞分裂，暂定只有6个方向，用整数的6个位表示
            param = RandomUtils.nextInt(2);
            for (int j = 0; j < 5; j++)
                param = param * 2 + RandomUtils.nextInt(2);
        } else if (code >= IF_X_BIG && code <= IF_SPLIT_LESS) {
            param = RandomUtils.nextInt(Env.BRAIN_XSIZE);
        }
        return param;
    }

    public static void mutation(Animal animal) {//基因随机突变，分为：新增、删除、拷贝、改变、参数改变等情况 
        List<String> genes = animal.gene;
        float percent = 5; //注：percent这个魔数以后要写在基因里,成为基因的一部分
        if (RandomUtils.percent(percent * 3))
            genes.add(RandomUtils.nextInt(genes.size()), randomGeneCode(animal));

        if (genes.size() > 0 && RandomUtils.percent(percent)) //删除
            genes.remove(RandomUtils.nextInt(genes.size()));

        if (genes.size() > 0 && RandomUtils.percent(percent)) //改变
            genes.set(RandomUtils.nextInt(genes.size()), randomGeneCode(animal));

        if (genes.size() > 0 && RandomUtils.percent(percent)) { //改变参数
            int index = RandomUtils.nextInt(genes.size());
            String gene = genes.get(index);
            int code = Integer.parseInt(gene.substring(0, 2));
            int param = randomGeneParam(animal, code); //参数是与code相关的，不同的code其合理参数范围是不一样的
            genes.set(index, "" + code + param);
        }

        if (genes.size() > 0 && RandomUtils.percent(percent)) { //批量拷贝,一次拷贝不超过基因长度的1/2
            genes.addAll(RandomUtils.nextInt(genes.size()), genes.subList(0, RandomUtils.nextInt(genes.size() / 2)));
        }

        if (genes.size() > 0 && RandomUtils.percent(percent)) { //批量删除，一次删除不超过基因长度的1/2
            genes.subList(0, RandomUtils.nextInt(genes.size() / 2)).clear();
        }

        //TODO: 分叉，在任意位置分叉，即拷贝相同一段内容，但是沿不同方向开始分裂

    }

}
