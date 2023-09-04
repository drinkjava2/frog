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
package com.gitee.drinkjava2.frog.util;

import static com.gitee.drinkjava2.frog.brain.Genes.GENE_NUMBERS;
import static com.gitee.drinkjava2.frog.util.RandomUtils.percent;

import java.util.ArrayList;
import java.util.Arrays;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.brain.Genes;

/**
 * 与Gene相关的工具方法
 * 
 * @author Yong Zhu
 * @since 10.0
 */
@SuppressWarnings("all")
public class GeneUtils {

    public static void createCellsFromGene(Animal a) {//根据基因生成细胞参数  
        for (int g = 0; g < GENE_NUMBERS; g++) {//动物有多条基因，一条基因控制一维细胞参数，目前最多有64维，也就是最多有64条基因
            long geneMask = 1l << g;
            ArrayList<Integer> gene = a.genes.get(g);
            int xLimit = Genes.xLimit[g];
            int yLimit = Genes.yLimit[g];
            int zLimit = Genes.zLimit[g];
            boolean fill = Genes.fill_gene[g];

            if (fill) { //如果这个基因是fill型的，会填充在指定区域的所有细胞中，不需要使用分裂算法来生成细胞
                if (xLimit < 0) { //如坐标一个也没有给出, 填充整个三维脑细胞空间  
                    for (int x = 0; x < Env.BRAIN_SIZE; x++)
                        for (int y = 0; y < Env.BRAIN_SIZE; y++)
                            for (int z = 0; z < Env.BRAIN_SIZE; z++)
                                a.cells[x][y][z] = a.cells[x][y][z] | geneMask;
                } else if (yLimit < 0) { // 如果只给出了x坐标, 填充此基因在脑坐标为x的yz平面上
                    for (int y = 0; y < Env.BRAIN_SIZE; y++)
                        for (int z = 0; z < Env.BRAIN_SIZE; z++)
                            a.cells[xLimit][y][z] = a.cells[xLimit][y][z] | geneMask;
                } else if (zLimit < 0) { // 如果只给出了x,y坐标，填充此基因在x,y指定的z轴上 
                    for (int z = 0; z < Env.BRAIN_SIZE; z++)
                        a.cells[xLimit][yLimit][z] = a.cells[xLimit][yLimit][z] | geneMask;
                } else { //如果x,y,z都给出了，填充此基因在x,y,z指定的点上
                    a.cells[xLimit][yLimit][zLimit] = a.cells[xLimit][yLimit][zLimit] | geneMask;
                }
                continue;
            }

            //以下开始使用分裂和随机算法来从基因链生成脑细胞
            if (xLimit < 0) { //如坐标一个也没有定义,使用阴阳8叉树分裂算法在三维脑细胞空间分裂,这个最慢但分布范围大  
                Tree8Util.knockNodesByGene(gene);//根据基因，把要敲除的8叉树节点作个标记
                for (int i = 0; i < Tree8Util.NODE_QTY; i++) {//再根据敲剩下的8叉树keep标记生成细胞参数
                    if (Tree8Util.keep[i] > 0) {
                        int[] node = Tree8Util.TREE8[i];
                        if (node[0] == 1) {//如果node边长为1，即不可以再分裂了，就在三维空间对应数组的位置把当前基因geneMask置1
                            a.cells[node[1]][node[2]][node[3]] = a.cells[node[1]][node[2]][node[3]] | geneMask; //在相应的细胞处把细胞参数位置1
                        }
                    }
                }
            } else if (yLimit < 0) { // 如果只定义了x坐标, 表示此基因分布在脑坐标x的yz平面上，此时使用阴阳4叉树分裂算法在此平面上分裂以加快速度
                Tree4Util.knockNodesByGene(gene);//根据基因，把要敲除的4叉树节点作个标记
                for (int i = 0; i < Tree4Util.NODE_QTY; i++) {//再根据敲剩下的4叉树keep标记生成细胞参数
                    if (Tree4Util.keep[i] > 0) {
                        int[] node = Tree4Util.TREE4[i];
                        if (node[0] == 1) {//如果node边长为1，即不可以再分裂了，就在2维空间对间数组的位置把当前基因geneMask置1
                            a.cells[xLimit][node[1]][node[2]] = a.cells[xLimit][node[1]][node[2]] | geneMask; //在相应的细胞处把细胞参数位置1
                        }
                    }
                }
            } else if (zLimit < 0) { // 如果只定义了x,y坐标，这时基因只能分布在x,y指定的z轴上，此时使用阴阳2叉树分裂算法
                Tree2Util.knockNodesByGene(gene);//根据基因，把要敲除的4叉树节点作个标记
                for (int i = 0; i < Tree2Util.NODE_QTY; i++) {//再根据敲剩下的4叉树keep标记生成细胞参数
                    if (Tree2Util.keep[i] > 0) {
                        int[] node = Tree2Util.TREE2[i];
                        if (node[0] == 1) {//如果node边长为1，即不可以再分裂了，就在2维空间对间数组的位置把当前基因geneMask置1
                            a.cells[xLimit][yLimit][node[1]] = a.cells[xLimit][yLimit][node[1]] | geneMask; //在相应的细胞处把细胞参数位置1
                        }
                    }
                }
            } else { //如果x,y,z都指定了，表示这个基因只能分布在一个点上, 这时只有0或1两种可能,如果基因不为空就认为它有
                if (!gene.isEmpty())
                    a.cells[xLimit][yLimit][zLimit] = a.cells[xLimit][yLimit][zLimit] | geneMask;
            }
        }
    }
 
    public static void geneMutation(Animal a) { //基因变异,注意这一个方法同时变异所有条基因
        for (int g = 0; g < GENE_NUMBERS; g++)
            if (percent(50)) {
                if (Genes.fill_gene[g]) //如果这个基因是fill型的，永远会存在指定区域的所有细胞中，所以不需要参与变异
                    continue;

                int n = 5; //这是个魔数，今后可以考虑放在基因里去变异，8\4\2\1叉树的变异率可以不一样

                //随机新增阴节点基因，注意只是简单地随机新增，所以可能有重复基因
                ArrayList<Integer> gene = a.genes.get(g);
                int geneMaxLength; //8叉、4叉树、2叉树的节点最大序号不同，基因随机生成时要限制它不能大于最大序号
                if (Genes.xLimit[g] < 0) { //如x没定义,使用阴阳8叉树分裂算法
                    geneMaxLength = Tree8Util.NODE_QTY;
                } else if (Genes.yLimit[g] < 0) { // 如果x>=0, y没定义, 表示此基因分布在坐标x的yz平面上，此时使用阴阳4叉树分裂算法
                    geneMaxLength = Tree4Util.NODE_QTY;
                } else if (Genes.zLimit[g] < 0) { // 如果x>=0, y>=0，z没定义，这时基因只能分布在x,y指定的z轴上，此时使用阴阳2叉树分裂算法
                    geneMaxLength = Tree2Util.NODE_QTY;
                } else { //如果x,y,z都指定了，表示这个基因只能分布在一个点上, 这时只有0或1两种可能, 如果基因不为空就认为它有。用随机算法
                    if (percent(n)) {
                        if (gene.isEmpty())
                            gene.add(1);
                        else
                            gene.clear();
                    }
                    continue;
                }

                if (percent(n)) //随机生成负节点号，对应阴节点, 
                    gene.add(-RandomUtils.nextInt(geneMaxLength));

                if (percent(n)) //生成随机负正节点号，对应阳节点
                    gene.add(RandomUtils.nextInt(geneMaxLength));

                if (percent(n * 2 + 2)) //随机删除一个节点，用这种方式来清除节点，防止节点无限增长，如果删对了，就不会再回来，如果删错了，系统就会把这个青蛙整个淘汰，这就是遗传算法的好处
                    if (!gene.isEmpty())
                        gene.remove(RandomUtils.nextInt(gene.size()));

            }
    }

}
