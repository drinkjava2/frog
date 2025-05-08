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

/**
 * Cell is a basic node of brain 3d array
 * 
 * 从效率的角度出发，Cell以及Cell上的Line这两个对象都不实际创建，因为Java中对象的操作比数组慢得多，
 * 所以Cell这个类里只放一些文档或静态方法，编程时采用数组来代替Cell对象或Line对象 ，属于Cell和Line对象的方法通常直接实作在Gene类里。
 * 
 * 
 * 关于Cell和Line的特性(假想)：
 * Cell相当于一个脑神经元细胞的细胞主体部分，是Line(树突、轴突)的根，基因只到Cell级别，Line作为Cell的属性存在。 
 * Cell激活后，给每个树突轴突（Line)发送能量（此处能量可以不守恒，由它的上层计算机系统注入能量即熵）。  
 * 
 * Line主要功能是能量传递， 但也有可能长久保存能量
 * cell和line有由基因来决定的各种属性如：传送给间隔时间、 阀值、能量下降曲线、能量扣除值、能量输出值等属性，它们的组合（cell-Line, Line-Line)非常复杂。 分裂和遗传算法的主要任务就是找出cell和Line的空间分布组合。
 * 目前编程先只考虑Cell-Line这种组合，暂不考虑Line-Line这种空间组合 

 * ======== Line可以是无状态或有状态的 =========
 * 如果不保存能量，Line就是无状态的，甚至可以不需要给Line分配内存， 
 * 打个比方比如
 * "6叉Line属性"会对四周的细胞发送能量，“特长上Line属性”会对上方100个细胞间隔的细胞发送能量，这些个行为实际上并不需要创建Line对象给它分配内存，而是成了Cell的一种行为。物理上必须用树突轴突来实
 * 现的功能，用电脑来模拟时可以直接用方法行为来代替。
 * 
 * 但是当Line是有状态时情况就不同了，必须给它分配内存空间，一个细胞有多个line，所以要占用海量的内存。有状态Line可以实现复杂的逻辑，可以把能量保存在line上实现记忆功能。

 * 
 * 总体来说，脑是简单的，决定它结构的熵并不多（一颗人类受精卵约包含750MB的信息），所以受精卵里并不记录每个细胞、每个连线的位置，而是大致决定有多少种类细胞（每类细胞上有相似的连线），然后交给分裂算法来
 * 生成海量的细胞，但是这个脑的复杂性与细胞数量无关，这个复杂性即熵量仅仅是由这750MB还要去除与脑结构不相关的其它部分的信息来决定的，脑本身的复杂度可能定义为"约50MB信息控制的100亿细胞的分形结构"。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Cell {
}
