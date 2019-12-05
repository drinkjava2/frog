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
package com.github.drinkjava2.frog.brain.group;

import com.github.drinkjava2.frog.brain.Organ;

/**
 * Group presents a rectangle organ zone, Group only arranges a group of cells
 * 
 * 在2.0之前，CellGroup是线型，不具备可进化性。在2.0之后引入Group概念,Group是矩形，大小可以覆盖整个大脑，也可以小到单个细胞。可以进行位置、大小变动、复制、分裂等多种形式的变异
 * 
 * 为了与旧版CellGroup区分，新版的这个类命名为Group，它代表了一组分布于一个正方形内的细胞群，细胞数量、每个细胞的触突连接方式等参数由当前Group决定,
 * 可以说每一种Group代表了一种神经网络算法， 通过无数个Group随机的分布、进化、变异，达到最终脑结构适应环境的变化
 * Group会参与遗传和进化，但是它生成的细胞不会参与遗传。 各个Group生成的细胞相加总和就是脑细胞总数。
 * 
 * Group在脑活动中不起作用，可以把Group比作播种机，把种子排列好后，就撒手不管了,在遗传过程中有一个fat参数，如果细胞活跃多，则Group保留及变异的可能性大，反之则舍弃掉。
 * Group是器官的一种，所以蛋里存放着所有Group的位置、大小、内部参数等信息，但是蛋里面不保存具体的细胞。这样通过控制有多少个"播种机"，就可以控制大脑的结构了，这样可以缩小蛋的尺寸。
 * 原则上Group遗传的一下代与父代是同一个播种(算法)类型，但不排除也有可能突变成另一个类型的Group。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public abstract class Group extends Organ {
	private static final long serialVersionUID = 1L;

}
