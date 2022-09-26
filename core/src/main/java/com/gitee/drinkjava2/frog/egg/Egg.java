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
package com.gitee.drinkjava2.frog.egg;

import java.io.Serializable;
import java.util.ArrayList;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Egg is the static structure description of brain cells
 * 
 * 蛋存在的目的是为了以最小的字节数串行化存储脑细胞,它是海量脑细胞的生成算法描述，而不是脑细胞本身
 * 蛋和基因的关系：基因是一种语言，相当于染色体，不存在坐标位置。蛋则是基因的载体，有x,y坐标，表示在虚拟环境中蛋存在的位置。
 * 
 * 另外青蛙本身也是基因的载体，所以青蛙里有一个gene属性
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Egg implements Serializable {
    private static final long serialVersionUID = 1L;
    public int x; // 蛋的x位置
    public int y; // 蛋的y位置

    // gene record the 8-tree structure of brain cells
    // 基因是随机生成的8叉树数据结构，和实际生物每个细胞都要保存一份基因不同，程序中每个脑细胞并不需要保存基因的副本，这样可以极大地减少内存占用
    public ArrayList<ArrayList<Integer>> genes = new ArrayList<>();

    public Egg() {// 无中生有，创建一个蛋，先有蛋，后有蛙
        x = RandomUtils.nextInt(Env.ENV_WIDTH);
        y = RandomUtils.nextInt(Env.ENV_HEIGHT);
    }

    /** Create egg from animal */
    public Egg(Animal a) { // 下蛋，每个器官会创建自已的副本或变异，可以是0或多个
        x = a.x;
        y = a.y;
        for (ArrayList<Integer> gene : a.genes) {//下蛋就是把动物的基因拷贝到新蛋里，并有可能变异
            ArrayList<Integer> g = new ArrayList<>();
            g.addAll(gene);
            genes.add(g);
        }
    }

    /**
     * Create a egg by join 2 eggs, x+y=zygote 模拟X、Y 染色体合并，两个蛋生成一个新的蛋
     */
    public Egg(Egg a, Egg b) {//两个蛋的基因混合, 生成一个新蛋
        x = a.x;
        y = a.y;
        genes.addAll(a.genes);
        if (RandomUtils.percent(20)) //插入蛋B的基因到A蛋中
            for (int i = 0; i < b.genes.size(); i++) {
                if (RandomUtils.percent(2)) {
                    ArrayList<Integer> aGene = a.genes.get(i);
                    ArrayList<Integer> bGene = b.genes.get(i);
                    if (bGene.size() > 1) {//随机插入一个B的基因，不用担心基因越来越多，因为随机删除的速度大于增长的
                        aGene.add(bGene.get(RandomUtils.nextInt(bGene.size())));
                    }
                }
            }
    }

}
