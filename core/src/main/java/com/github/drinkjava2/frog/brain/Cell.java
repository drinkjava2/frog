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

import java.util.Arrays;

/**
 * Cell is the smallest unit of brain space, a Cell can have many actions and
 * photons and holes and relations
 * 
 * Cell是脑的最小空间单元，可以存在多个行为(由organs中的器官代号来表示)和光子(Photon)和洞（Hole)和关联(Relation)，脑是由frog中的cells三维数组组
 * 成，但不是每一维都初始化过。
 * 
 * Cell原则上只有数据，应该把所有方法移出去，放到具体的器官行为中，见CellActions类，但暂时图省事，在Cell里放一些常用方法
 * 
 * Jelly或MoveJelly器官有一种特殊行为，它可以产生动态触突效果，可以把具备动态触突的神经元比作一个果冻，光子来了,在上面撞了一个坑（hole)并损失能量，
 * 如果来的多，或者速度快(能量大)，一部分的光子就被从果冻的另一头撞出去了(光直线传播，寻找下一个神经元，增加信息存储单元，实现体全息存贮)。如果在另一个角度又
 * 来了新的光子，同样的过程在发生，只不过在撞击的过程中，以前被撞出的坑里有可能被撞出光子来，沿着撞击坑的路径直线逆向返回（即波的逆向成像，两个撞击事件,如果
 * 在短期内同时发生，或长期反复发生，就形成比较稳定的关联，这个与神经网络的hebb规则相符合，以后在这个基础上，看看能不能将深度学习的分层结构借签进来，用图形化
 * 表示，并有可能是无级分层的，层与层之前没有明显界限。
 * 
 * 光子是信息的载体，旧版本中光子是实际的对象，但实际上只要能表达带方向的信息传递，光子本身可以省略掉，从2020-2-28开始，为了精简架构，光子被删除，直接用A细胞
 * 在B细胞上挖洞的形式来表达这个信息传递过程，洞是有方向的，洞的方向表示它的信息来源，洞的大小表示信量重复度，洞的年龄表示信息新旧度
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Cell {
	public int x;
	public int y;
	public int z;
	public float energy;

	public Cell(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int[] organs = null; // 每个Cell可以被多个Organ登记，这里保存organ在蛋里的序号

	public Hole[] holes = null;// 洞（即动态突触），洞由光子产生，洞由时间抹平，洞的角度本身就是关联关系，角度越大，关联关系越大

	/** Active this cell, increase its energy value */
	public void active() {// 激活这个细胞，也就是说，增加它的能量值，最大到10000饱和
		energy += 100;
		if (energy > 10000)
			energy = 10000;
	}

	public void deActive() {// 抑制这个细胞，也就是说，减小它的能量值，最小到0
		energy -= 300;
		if (energy < 0)
			energy = 0;
	}

	public void regOrgan(int orgNo) {// 每个Cell可以被多个Organ登记，通常只在青蛙初始化器官时调用这个方法
		if (organs == null)
			organs = new int[] {};
		organs = Arrays.copyOf(organs, organs.length + 1);
		organs[organs.length - 1] = orgNo;
	}

}
