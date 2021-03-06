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

import com.gitee.drinkjava2.frog.Frog;

/**
 * Egg is the static structure description of frog, can save as file, to build a
 * frog, first need build a egg.<br/>
 * 
 * 蛋存在的目的是为了以最小的字节数串行化存储Frog的基因树,它是Frog的生成算法描述，而不是Frog本身，这样一来Frog就不能"永生"了，因为每一个egg都不等同于
 * 它的母体， 而且每一次测试，除了少量固有反射外，大部分条件反射的建立都必须从头开始训练，类似于人类，无论人类社会有多聪明， 婴儿始终是一张白纸，除了需要花大量的时间从头学习。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Egg implements Serializable {
    private static final long serialVersionUID = 1L;

    public Egg() {// 无中生有，创建一个蛋，先有蛋，后有蛙

    }

    /** Create egg from frog */
    public Egg(Frog frog) { // 青蛙下蛋，每个青蛙的器官会创建自已的副本或变异，可以是0或多个

    }

    /**  Create a egg by join 2 eggs   */
    public Egg(Egg x, Egg y) { //模拟X、Y 染色体合并，两个蛋生成一个新的蛋
    }

}
