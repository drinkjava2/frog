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

import java.awt.Color;
import java.io.Serializable;

import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.util.RandomUtils;

/**
 * Organ is a cone-cylinder shape zone inside of brain,, organ can be saved in
 * egg， organ will create cells in brain. Most organ's size, angle, location and
 * cell parameters are randomly created
 * 
 * 器官是脑的一部分，多个器官在脑内可以允许部分或完全重叠出现在同一脑内位置，器官都是可以变异的，所有器官都会保存在蛋里面。
 * 
 * 器官是一种外形为锥圆柱体或长方体的脑内区，它的作用是在它的形状范围内在脑细胞内登记行为，当然如果脑细胞不存在它首先会新建一个脑细胞。
 * 器官的数量、形状、大小、角度、位置、神经元分布方式，以及神经元的内部参数可以随机生成和变异, 并由生存竟争来淘汰筛选。
 * 
 * 器官可以组合成复杂的网络结构或曲折的信号传导路径，例如一个圆柱形神经传导器官，可以变异为由两个圆柱状传导器官首尾相接，然后这两个器官各
 * 自变异，就有可能形成弯的传导路径或更复杂的网络结构。 
 * 
 * 器官描述细胞的行为，其中洞数量和参数是有可能随机变异的。行为则是硬编码不可以变异，模拟单个神经元的逻辑，不同的细胞可以登记属于多个不同的
 * 器官，虽然器官行为不可以变异，但是可以写出尽可能多种不同的行为（貌似神经元的活动也只有有限的几种)，由生存竟争来筛选。
 * 
 * 器官通常是单例，每个脑细胞都指向一些器官单例，器官这个单例参与神经活动时是无状态的，有状态的神经元的参数是保存在Cell中而不是器官中,这种设计是为了减少
 * Cell的内存占用
 * 
 * 
 * @author Yong Zhu
 * @since 1.0.4
 */
public class Organ implements Serializable, Cloneable {// 因为要保存在蛋文件里，所以必须支持串行化
	private static final long serialVersionUID = 1L;

	// 以下是各种器官类型，每个神经元都属于一个器官，每个器官都有一个type类型参数
	public static final int EMPTY = 0;// 空细胞，不处理光子
	public static final int MOVE = 1;// 会让光子沿直线走一格
	public static final int MOVE_JELLY = 2;// 会让光子沿直线走一格，并在下一个细胞上打洞
	public static final int EYE = 3;// 眼细胞，会根据cell激活度产生发散到各个方向的光子
	public static final int EAR = 4;// 耳细胞,类似眼细胞,不同点是为了简化，脑内听觉区和输入区混用一个区，所以它也可吸收光子，倒过来激活cell
	public static final int JELLY = 5; // 光子在当前细胞上可以打出洞来
	public static final int STATIC = 6; // 只有静态洞的细胞，它忽略动态洞参数
	public static final int MIX = 7; // 同时具有静态和动态洞的细胞

	public static final int TYPE_QTY = 8;// 所有的type都是预先写好在这里的，自动生成的type也只能在写好的type里选一个

	private static int organNoIndex = 1;
	public int organNo = organNoIndex++; // 每个器官都有一个唯一的编号,作用是同一个编号的光子间将不产生绑定
	public int color = 1;// 这个不重要，表示它生成的光子的显示在脑图中的颜色号，见ColorUtils
	public float fat = 0;// 细胞活跃多，则fat值大，如果fat值很低，则这个器官被丢弃的可能性加大，这个值很重要，它使得孤岛器官被淘汰
	public boolean allowVary;// 是否允许变异，有一些器官是手工创建的，在项目初级阶段禁止它们参与变异和生存竟争。
	public boolean allowBorrow;// 是否允许在精子中将这个器官借出，有一些器官是手工创建的，在项目初级阶段禁止它们借出
	public String organName;// 器官的名字，通常只有手工创建的器官才有名字，可以用frog.findOrganByName来查找到这个器官

	// ======= 本行以下是一些假设的参数，受变异和生存竟争影响 ，并通过大样本数、随机变异、生存竟争来进行多参数的优化选择 ========

	public int type; // 器官类型，见上面的常量定义，这个字段通常很稳定。一旦变异，将从根本上改变器官的播种行为和神经元的行为

	public Shape shape; // 器官的形状，不同的形状要写出不同的播种行为
 

	// =====注意以下三个字段可以让细胞具备一些固定角度的洞，这个不占内存，但缺点是不灵活，不智能，详见与Cell类中动态洞的对比 =====
	public Hole[] holes; // 输出洞

	public Organ() {// 缺省构造器，生成具有缺省参数但没有形状的器官
		allowVary = true;
		allowBorrow = true;
		type = 0;
		holes = null;
	}

	/** Only call once after organ be created */
	public Organ[] vary(Frog f) { // 器官变异，仅会在青蛙下蛋时即new Egg(frog)中被调用一次，返回本身或变异后的一个或一组类似器官返回
		if (!allowVary)
			return new Organ[] { this };// 如果不允许变异，器官就把自身返回，存放在蛋里
		// 各参数 随机有大概率小变异，小概率大变异，极小概率极大变异
		type = RandomUtils.vary(type, 10);// 这个type通常不允许变，所以只给它10%的机率去变, 也就是说在正常变异概率上再乘以10%的变异可能性
		shape = RandomUtils.vary(shape);
		holes = RandomUtils.vary(holes);
		return new Organ[] { this };
	}

	/** Only call once when frog created , Child class can override this method */
	public void init(Frog f, int orgNo) { // 在青蛙生成时会调用这个方法，进行一些初始化，通常是根据参数来播种脑细胞
		// 里是器官播种脑细胞的具体代码,对于手工生成的器官，也可以重写这个方法，对于自动生成的器官，必须根据type和shape等来播种，要写死在这里
		if (shape != null)
			shape.createCellsRegOrgan(f, orgNo); // 先均匀播种脑细胞试试
	}

	/** each step will call Organ's active methodd */
	public void active(Frog f) { // 每一步测试都会调用active方法，通常用于手动生成的器官，自动生成的器官其行为仅由脑细胞来决定
		// do nothing
	}

	/** each step will call Organ's active methodd */
	public void cellAct(Frog frog, Cell c, int activeNo ) { // 每个细胞都会调用cellAct方法,这是针对细胞级别的方法，子类要覆盖它
	  
	}

	/** Child class can override this method to drawing picture */
	public void drawOnBrainPicture(Frog f, BrainPicture pic) { // 把器官的轮廓显示在脑图上
		if (shape == null)
			return;// 如果没有形状，就不画
		if (!Env.SHOW_FIRST_FROG_BRAIN || !f.alive) // 如果不允许画或青蛙死了，就直接返回
			return;
		pic.setPicColor(Color.LIGHT_GRAY); // 缺省是灰色
		shape.drawOnBrainPicture(pic);
	}

	public static Organ randomCuboidOrgan() {
		Organ o = new Organ();
		o.shape = RandomUtils.randomCuboid();
		return o;
	}

	public static Organ randomConeOrgan() {
		Organ o = new Organ();
		o.shape = RandomUtils.randomCone();
		return o;
	}

}
