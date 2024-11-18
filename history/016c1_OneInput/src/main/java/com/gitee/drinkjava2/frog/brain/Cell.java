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
 * 从效率的角度出发，Line、Cell这两个对象将来可能都不实际创建，因为Java中对象的操作比数组更占用内存和影响速度，
 * 所以Cell和Line里尽量只放一些文档、静态方法，以便以后采用数组来代替Cell对象或Line对象，尤其是Line可能用计算机模拟根本不需要分配内存，是个虚对象。
 * 
 * 
 * 关于Cell的特性：
 * 1.Cell相当于一个脑神经元细胞的细胞主体部分，能量只保存在主体上，只存贮正值， 在0~1之间，传入的多余的能量将削除
 * 2.Cell如果有能量，能量会随时间消逝，随时间消逝的能量曲线是个属性，不同种类的Cell这个属性是不同的
 * 3.Cell激活后，给每个树突轴突（Line)发送等同自身能量的能量，注意不是均分，而是每个都拷贝一份等量的能量。（此处能量不守恒，由它的上层计算机系统注入能量即熵）。
 * 4.Cell一直给Line发送能量，但是否能量被传送、能量传送后是否扣除影响细胞主体能量？这些都不由Cell考虑，而是由Line的属性和行为来决定，Line是复杂的。
 * 同一个Line接收激活的Cell能量,有一个再次传送给间隔时间, 这是由这个Line的阀值特性、Cell的能量下降曲线、Line的能量扣除值来决定的。
 * 
 * Cell可以看出来非常简单，比树突、轴突要简单的多，就是一个能量暂存、放大(拷贝)、消逝、分发节点。而树突、轴突(统一用Line来表示)有复杂的行为，但原则上不保存能量。Cell和Line分工
 * 协作，一个保存能量，一个传输能量。 Line的种类有很多，今后分裂算法的主要任务就是进行Line种类的空间分配。
 * 
 * ======== Line可以是个虚对象! =========
 * 为什么Line不保存能量？因为如果不保存能量，Line就是无状态的，甚至可以不创建Line实体或数组，不需要给Line分配内存，将Line作为Cell的一个行为即可，能量到了Cell后，根据它有
 * 多少个和多少种Line属性，进行相应的能量分发行为，影响周围或近或远的Cell能量。
 * 打个比方比如
 * "6叉Line属性"会对四周的细胞发送能量，“特长上Line属性”会对上方100个细胞间隔的细胞发送能量，这个行为实际上并不需要创建Line对象或给它分配内存，所以Line可以
 * 是一种虚对象, 作为Cell的一种属性存在(即在表示Cell的长整数中占据一个字节的标志位，而这个标志位又由分裂算法的基因决定)。
 * 世上的道理是相通的，如果一个数据结据在电脑里能简化编程，实际生物细胞也可能采用同样的方案。
 * 
 * 总体来说，脑是简单的，决定它结构的熵并不多（一颗人类受精卵约包含750MB的信息），所以受精卵里并不记录每个细胞、每个连线的位置，而是大致决定有多少种类细胞（每类细胞上有相似的连线），然后交给分裂算法来
 * 生成海量的细胞，但是这个脑的复杂性与细胞数量无关，这个复杂性即熵量仅仅是由这750MB还要去除与脑结构不相关的其它部分的信息来决定的，脑本身的复杂度可能定义为"约50MB信息控制的100亿细胞的分形结构"。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Cell {
}
