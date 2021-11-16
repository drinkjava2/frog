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

import java.util.ArrayList;

import com.gitee.drinkjava2.frog.Env;

/**
 * Tree8Util used to store a pre-order Traversal tree array to speed 
 * 
 * 这里缓存着一个前序排列的八叉树用来在细胞生成时加快速度和简化运算，关于树结构可用深度树数组来表达的知识可以参见这里：https://my.oschina.net/drinkjava2/blog/1818631
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Tree8Util {

    //EIGHT_TREE store a pre-order Traversal tree array  
    public static final int NODE_QTY = calculateNodeSize(Env.BRAIN_CUBE_SIZE);

    public static int[][] TREE8 = new int[NODE_QTY][4]; //八叉数用数组表示，第一维是深度树的行号，第二维是一个整数数组,内容是深度树表示的八叉树细胞的enable, size, x, y, z值

    public static boolean[] ENABLE = new boolean[NODE_QTY]; //这里记录树的敲除记录，被敲除的节点用false表示

    public static int ENABLE_NODE_QTY = NODE_QTY; //这里记录未被敲除的总节点数，好用来下次继续敲除

    private static int index = 0;
    static {
        tree8Split(new Cube(0, 0, 0, Env.BRAIN_CUBE_SIZE));
    }

    static int calculateNodeSize(int n) {//计算8叉树全展开的总节点数
        if (n == 1)
            return 1;
        return n * n * n + calculateNodeSize(n / 2);
    }

    //if cube can split, then split it to 8 small cubes
    private static void tree8Split(Cube c) {//如立方体可分裂，就继续递归分裂成8个
        TREE8[index++] = new int[]{c.size, c.x, c.y, c.z}; //这里size类似于深度树中的level，只不过是size从大到小，level是从小到大，原理一样
        if (c.size == 1)
            return;
        int size = c.size / 2;//每个细胞可以分裂成8个size为原来1/2的小细胞
        tree8Split(new Cube(c.x, c.y, c.z, size));//开始分裂成8个
        tree8Split(new Cube(c.x + size, c.y, c.z, size));
        tree8Split(new Cube(c.x, c.y + size, c.z, size));
        tree8Split(new Cube(c.x + size, c.y + size, c.z, size));
        tree8Split(new Cube(c.x, c.y, c.z + size, size));
        tree8Split(new Cube(c.x + size, c.y, c.z + size, size));
        tree8Split(new Cube(c.x, c.y + size, c.z + size, size));
        tree8Split(new Cube(c.x + size, c.y + size, c.z + size, size));
    }

    public static void knockNodesByGene(ArrayList<Integer> gene) {//根据基因，把要敲除的8叉树节点作个标记0
        for (int i = 0; i < Tree8Util.NODE_QTY; i++)
            ENABLE[i] = true;
        ENABLE_NODE_QTY = NODE_QTY;
        for (int g : gene) {
            if (Tree8Util.ENABLE[g]) {
                int gSize = Tree8Util.TREE8[g][0]; //
                for (int i = g; i < Tree8Util.NODE_QTY; i++) {
                    int iSize = Tree8Util.TREE8[i][0];
                    if (i > g && iSize >= gSize)
                        break;
                    else {
                        if (Tree8Util.ENABLE[i]) {
                            ENABLE_NODE_QTY--;
                            Tree8Util.ENABLE[i] = false; //作敲除标记
                        }
                    }
                }
            }
        }
    }

}
