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

import java.io.Serializable;

import com.github.drinkjava2.frog.Frog;

/**
 * CellTemplate is the cell template, a group of CellTemplate be saved in egg
 * 
 * CellTemplate描述细胞的形状和行为，其中触突数量和参数是有可能随机变异的。行为则是硬编码不可以变异，模拟单个神经元的逻辑，不同的细胞有不同的
 * 行为，通过cellType来区分，虽然行为不可以变异，但是可以写出尽可能多种不同的行为，由生存竟争来筛选。
 * CellTemplate是可串行化的，一组CellTemplate实例会保存在蛋里面。
 * 每个脑细胞都指向某个type类型，CellTemplate本身都是单例，不占内存，从命名上可以看出它只是个模板，它是无状态的，只影响 cell处理
 * 信息（光子）的逻辑
 * 
 * 关于细胞 能量: <br/>
 * 能量上限很低的细胞，相当于信息的中转站，来多少光子就转发多少光子;能量上限高的细胞，可以起到记忆作用，它的原理是多个触突、反复累集的光子能量，
 * 可以在收到某个方向光子的信号后短暂释放，以这个细胞中为心形成一个新的波源，只有位于波的驻点位置的细胞才会吸收和释放更多能量，所以细胞的
 * 数量和分布位置很重要。
 * 
 * 已经有了光子，光子自带传播方向，并在每步长中自动走一格，为什么还需要细胞的触突？<br/>
 * 因为触突具有分发、产生、收集、改变光子方向、远距传输光子等作用，有助于增加脑的复杂性，一个个触突就相当于一个个空间棱镜。   
 *  
 *  
 * @author Yong Zhu
 * @since 2.0.2
 */
public class CellTemplate implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public int type; // 当创建CellType实例时，会指定一个type

	//注意所有以下参数都参与蛋的变异，有大概率随机小变异，有小概率随机大变异，而且不同type的细胞对同一个参数的解释行为可能不同，或根本不用某个参数
	
	public float energyLimit;//细胞能量上限，当能量超过能存贮的上限，多余的光子能量将不被细胞吸收，直接煙灭或以光子的形式转发出去

	public float outputRate;//表示细胞激活后，一次脉冲会释放出细胞存贮的能量比率， 通常这个值小于1
	
	public float activeDoor;// 激活阀值，细胞当前能量小于这个阀值时，细胞不会激活

	public float radius;// 细胞即使没有触突，也可以处理光子，这个radius是细胞的管辖半径，但局限于无方向的信号处理，如穿透和反射

	public float dropRate;//是一个介于0~1的值，反映了细胞固有的能量下降速率，在每一步长中细胞能量都以这个速率损失
		
	public Synapse[] inputs; // 输入触突，位置是相对细胞而言的
	public Synapse[] sides; // 侧面（通常是抑制，是负光子输出)输出触突们，才从脉冲神经网络了解到有这种侧向抑制
	public Synapse[] outputs; // 输出触突

	
	/**
	 * Each cell's act method will be called once at each loop step
	 * 
	 * 在轮循时，每个细胞的act方法都会被调用一次，这个方法针对不同的细胞类型有不同的行为逻辑，这是硬编码，要多尝试各种不同的行为，然后抛给电脑去筛选。
	 * 
	 */
	public void act(Frog f, Cell cell, int x, int y, int z) {
		switch (type) { // TODO 待添加细胞的行为，这是硬编码
		case 0:
			// 一对一，穿透，光子会穿过细胞，细胞起到中继站的作用，如果没有细胞中继，光子在真空中传播(即三维数组的当前坐标没有初始化，为空值)会迅速衰减
			break;
		case 1:
			// 一对一，转向，光子传播角度被改变成另一个绝对角度发出
			break;
		case 2:
			// 一对一，转向，光子传播角度被改变成与器官有关的角度发出，可以模拟光线的发散(如视网膜细胞)和聚焦(如脑内成像，即沿光线发散的逆路径)
			break;
		case 3:
			// 一对多，拆分，入射光子被拆分成多个光子，以一定的发散角发出，通常发散光子的总能量小于入射+细胞输出能量之和
			break;
		case 4:
			// 一对多，拆分，入射光子被拆分成多个光子，发散角与器官相关
		case 5:
			// 多对一，聚合，入射光子被触突捕
			break;
		default:
			break;
		}
	}
}
