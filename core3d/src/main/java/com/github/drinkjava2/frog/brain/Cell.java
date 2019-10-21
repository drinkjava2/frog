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
package com.github.drinkjava2.frog.brain;

/**
 * Cell is the basic unit of frog's brain
 * 
 * Cell这个类只保存很少的信息，可节约内存，它的触突分布参数、对信号(即光子)的处理行为由orgran中定义的各项细胞参数来决定。
 * Cell的排布由器官来完成，一个器官通常会撒布一群相同类型的细胞。
 * 
 * synapses字段比较特殊，不一定每个细胞会用到，取决于细胞的类型。常用的一种用法是取最活跃的几个光子输入信号的方向，建立几个对应其方向的动态触突，
 * 以后只要有任一个触突有信号，其它的触突就发送信号，这样两个或多个不同方向的信息就被关联起来了，这就是体全息存贮逆向成像的基础，是自然规律为什么会
 * 沉淀在人脑中 的基石，因为任意有相关性的信号，只要反复或同时发生，总会有脑细胞位于这两个信号之间，将它们这个关联关系保存下来。
 * 这个synapse是动态生成的，不是在细胞诞生时生成的，所以更宝贵。
 * 
 * Organ类中也可能让细胞产生一些无状态的触突（保存在Organ类模板中调用)，区别在于后者只能进行一些信号传导、转折、发散、聚焦等比较单调刻板的功能，
 * 不具备智能性。例如要模拟体全息存贮现象，既可以利用Cell中的动态触突，也可以利用Organ类中固定的触突，但后者对信号的输入角度有严格要求，处理信
 * 息量有限，即使细胞位于两个信号的交点（驻点)，也不一定能接收到信号;而利用动态触突，则可以确保360度无死角地建立两个或多个重复信号之间的关联。
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class Cell {// Cell是脑神经元，将来脑里会有上亿个脑细胞，为节约内存，不重要的、与细胞状态无关的参数都存放在Organ类中去了。

	/** energy of cell, energy got from food */
	public float energy; // 每个细胞当前的能量值

	/** tire value of cell */
	public float tire; // 每个细胞的疲劳值，只取决于最近的激活频率

	/** Organ index in egg */
	public Organ organ; // 细胞属于哪个器官

	public Synapse[] synapses; // 动态触突， 详见上面解说,通常是只在最活跃的几个光子输入信号的来源方向上建立动态触突
}
