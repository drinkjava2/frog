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
    public static final int TREE8_NODE_QTY=calculateNodeSize(Env.BRAIN_CUBE_SIZE);
    
    public static int[][] TREE8 = new int[TREE8_NODE_QTY ][]; //八叉数用数组表示，第一维是深度树的行号，第二维是一个整数数组,内容是深度树表示的八叉树细胞的enable, size, x, y, z值

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
        TREE8[index++] = new int[]{1, c.size, c.x, c.y, c.z}; //这里第一个位置为1时表示可，0为禁用，用size类似于深度树中的level，只不过是size从大到小，level是从小到大，原理一样
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

    public static void resetTree8Enable() {//TREE8是全局变量，所以每次使用前要调用这个方法重置所有节点的enable标记为1 
        for (int i = 0; i < Tree8Util.TREE8.length; i++) {
            int[] p = Tree8Util.TREE8[i];
            p[0] = 1;
        }
    }

    /*-
    public static void main(String[] args) {//调试输出，打印出深度树表示的8叉树
       System.out.println(cubeSize(Env.BRAIN_CUBE_SIZE));
       for (int[] a : TREE8) {
           System.out.println();
           for (int i = 0; i < a.length; i++) {
               System.out.print(a[i] + " ");
           }
       }
    }
    */
}
