/* Copyright 2018-2020 the original author or authors.
 *
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

import java.util.ArrayList;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.brain.Genes;

/**
 * TreeUtil  
 * 
 * @author Yong Zhu
 * @since 013
 */
public class CellUtil {
    
    public static void createCellsFromGene(Animal a) {//根据基因生成细胞参数  
        long geneMask = 1;
        for (int g = 0; g < GENE_NUMBERS; g++) {//动物有多条基因，一条基因控制一维细胞参数，最多有64维，也就是最多有64条基因
            ArrayList<Integer> gene = a.genes.get(g);
            int xLayer = Genes.xLayer[g];
            if (xLayer > -1) { //如果xLayer不为-1，表示此基因分布在平面上，此时使用4叉树在平面上分裂加速!!!!
                Tree4Util.knockNodesByGene(gene);//根据基因，把要敲除的4叉树节点作个标记
                for (int i = 0; i < Tree4Util.NODE_QTY; i++) {//再根据敲剩下的4叉树keep标记生成细胞参数
                    if (Tree4Util.keep[i] > 0) {
                        int[] node = Tree4Util.TREE4[i];
                        if (node[0] == 1) {//如果node边长为1，即不可以再分裂了，就在2维空间对间数组的位置把当前基因geneMask置1
                            a.cells[xLayer][node[1]][node[2]] = a.cells[xLayer][node[1]][node[2]] | geneMask; //在相应的细胞处把细胞参数位置1
                        }
                    }
                }
                geneMask <<= 1;
            } else {//否则使用8叉树在三维空间分裂!!!!
                Tree8Util.knockNodesByGene(gene);//根据基因，把要敲除的8叉树节点作个标记
                for (int i = 0; i < Tree8Util.NODE_QTY; i++) {//再根据敲剩下的8叉树keep标记生成细胞参数
                    if (Tree8Util.keep[i] > 0) {
                        int[] node = Tree8Util.TREE8[i];
                        if (node[0] == 1) {//如果node边长为1，即不可以再分裂了，就在三维空间对间数组的位置把当前基因geneMask置1
                            a.cells[node[1]][node[2]][node[3]] = a.cells[node[1]][node[2]][node[3]] | geneMask; //在相应的细胞处把细胞参数位置1
                        }
                    }
                }
                geneMask <<= 1;
            }

        }
    }
    
}
