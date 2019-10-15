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
package com.github.drinkjava2.frog.brain.organ;

import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.BrainPicture;
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Synapse;

/**
 * ConeOrgan is a cone-cylinder shape organ inside of brain, it will arrange
 * cells in brain. ConeOrgan's size, angle, location and cell parameters are
 * randomly created
 * 
 * 锥器官是一种外形为锥圆柱体的脑内器官，它的作用是在它的形状范围内播种脑细胞， 这是第一个也是最重要的脑细胞播种器官，它的数
 * 量、形状、大小、角度、位置、神经元分布方式，以及神经元的内部参数可以随机生成和变异, 并由生存竟争来淘汰筛选。
 * 
 * 锥器官可以组合成复杂的网络结构或曲折的信号传导路径，例如一个圆柱形神经传导锥器官，可以变异为由两个圆柱状传导锥器官首尾相接，然后这两个锥器官各
 * 自变异，就有可能形成弯的传导路径或更复杂的网络结构。看起来复杂，但大自然可以做到比这更复杂的进化逻辑，先假设大自然是万能的，只要是有用的
 * 变异逻辑，就有可能出现。
 * 
 * 锥器官的细胞播种范围是以起始点为顶点，终点为底点的一个锥状圆柱体，drawOnBrainPicture这个方法需要重写,目前暂用一条线加两个圆圈表示
 * 
 * 锥器官描述细胞的形状和行为，其中触突数量和参数是有可能随机变异的。行为则是硬编码不可以变异，模拟单个神经元的逻辑，不同的细胞有不同的
 * 行为，通过type来区分，虽然行为不可以变异，但是可以写出尽可能多种不同的行为（貌似神经元的活动也只有有限的几种)，由生存竟争来筛选。
 * 锥器官是器官的子类，所以它是可串行化的，所有锥器官都会保存在蛋里面。
 * 锥器官是单例，不占内存，每个脑细胞都指向某个锥器官单例，锥器官这个单例参与神经活动时是无状态的，神经元的状态参数是保存在Cell中。
 * 
 * 信息（光子）的逻辑：
 * 能量上限很低的细胞，相当于信息的中转站，来多少光子就转发多少光子;能量上限高的细胞，可以起到记忆作用，它的原理是多个触突、反复累集的光子能量，
 * 可以在收到某个方向光子的信号后短暂释放，以这个细胞中为心形成一个新的波源，波的发散方向就是触突的分布方向，只有位于波的驻点位置的细胞才会吸收和释放更多
 * 能量，光子总是直线传播，不在合适交点(波的驻点)位置的细胞吸收不到能量，所以必须有数量非常多的脑细胞存在。在器官中的触突是无状态的，只能进行简单的信号
 * 传导、发散、会聚等处理,在Cell中的是动态触突，它是根据光子的来源方向动态产生的，所以适用面更广。
 * 
 * 光子自带传播方向，并在每个测试步长中自动走一格，为什么还需要脑细胞的触突？<br/>
 * 因为光子只能直线传播，而触突具有分发、产生、收集、改变光子方向、远距传输光子等作用，能增加脑的复杂，而cell中根据光子的方向动态生成的触突更是智能的基
 * 石，它将有关联关系的 重复光子信号从海量的自然界噪声信号中分离保存下来。
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class ConeOrgan implements Organ {
	private static final long serialVersionUID = 1L;

	public float fat = 0;// 细胞活跃多，则fat值大，在一屏测试完成后，如果fat值很低，则这个器官被丢弃的可能性加大

	// 注意从这行开始，以下所有参数都参与变异和生存竟争，随机有大概率小变异，有小概率大变异，有极小概率极大变异

	// ==============以下这些参数用来定义锥器官的尺寸、位置、神经元分布密度等参数,在播种时要用到================
	public float startX; // 这6个变量定义了cone的中心线起点和终点
	public float startY;
	public float startZ;
	public float endX;
	public float endY;
	public float endZ;

	public float startRadius = 8; // 起点的半径
	public float endRadius = 8; // 终点的半径

	public int startType = 0; // 起点端面类型， 0表示范围一直延长到边界, 1表示是个以startR为半径的球面
	public int endType = 0; // 终点端面类型， 0表示范围一直延长到边界, 1表示是个以endR为半径的球面

	public float startDensity = 1; // 起点附近的脑细胞播种密度，单位是 细胞数/每cube，通常取值0~1之间
	public float endDensity = 1; // 终点附近的脑细胞播种密度

	// ===============以下这些参数用来定义神经元自身的参数===============

	public int type; // 脑细胞类型, 不同类型细胞对同一个参数的解释行为可能不同，或根本不会用到某个参数, 这个字段如果变异，将完全改变器官的行为

	public int synapsesLimit;// 细胞允许创建动态触突的数量上限，详见Cell类的synapses字段

	public float energyLimit;// 细胞能量上限，当能量超过能存贮的上限，多余的光子能量将不被细胞吸收，直接煙灭或以光子的形式转发出去

	public float outputRate;// 细胞激活后，一次脉冲会释放出细胞存贮的能量比率(或释放出一个固定值)， 根据能量守恒，这个值通常小于1

	public float outputDoor;// 输出阀值，细胞当前能量小于这个阀值时，细胞不会产生输出

	public float inputRate;// 能量接收率，细胞存贮的能量占输入能量的比率(或吸收一个固定值)， 根据能量守恒，这个值通常小于1

	public float inputDoor;// 接收阀值，接收的能量小于这个阀值时，细胞不会吸收这份能量，直接煙灭或以光子的形式转发出去

	public float radius;// 细胞即使没有触突，也可以处理光子，这个radius是细胞的管辖半径，但局限于角度的信号处理，如穿透和反射，或6个正方向

	public float dropRate;// 是一个介于0~1的值，反映了细胞存的能量下降速率，在每一步长中细胞能量都以这个速率损失，可以参考遗忘曲线

	// =====注意以下三个字段可以让细胞具备一些无状态的触突，详见与Cell类中动态触突的对比 =====
	public Synapse[] inputs; // 输入触突，位置是相对细胞而言的
	public Synapse[] sides; // 侧面（通常是抑制，是负光子输出)输出触突，从脉冲神经网络学习到有这种侧向抑制
	public Synapse[] outputs; // 输出触突

	@Override
	public boolean allowBorrow() {
		return true; // 锥器官允许当成精子注射到卵子中去
	}

	@Override
	public void init(Frog f) { // 在青蛙生成时调用这个方法，进行初始化，通常是根据参数来撒种脑细胞
	}

	@Override
	public void active(Frog f) { // 每一步测试都会调用器官的active方法 ，缺省啥也不干，对于内部器官来说，缺省啥也不干
	}

	@Override
	public void drawOnBrainPicture(Frog f, BrainPicture pic) { // 把器官的轮廓显示在脑图上
	}

	@Override
	public Organ[] vary() {
		ConeOrgan newOrgan = null;
		try {
			newOrgan = this.getClass().newInstance();
		} catch (Exception e) {
			throw new UnknownError("Can not make new Organ copy for " + this);
		}
		// TODO： 在这里要加入参数的变异，每个器官都有可能参数不同
		return new ConeOrgan[] { newOrgan };
	}

	/**
	 * Each cell's act method will be called once at each loop step
	 * 
	 * act方法是只有锥器官才具备的方法，在每个测试步长中act方法都会被调用一次，这个方法针对不同的细胞类型有不同的行为逻辑，这是硬
	 * 编码，所以要准备多套不同的行为（可以参考动物脑细胞的活动逻辑)，然后抛给电脑去随机筛选，不怕多。
	 * 
	 */
	public void cellAct(Frog f, Cell cell, int x, int y, int z) {
	}
}
