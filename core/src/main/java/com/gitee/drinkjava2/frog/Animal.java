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
package com.gitee.drinkjava2.frog;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import com.gitee.drinkjava2.frog.brain.Cell;
import com.gitee.drinkjava2.frog.brain.Organ;
import com.gitee.drinkjava2.frog.egg.Egg;
import com.gitee.drinkjava2.frog.objects.Material;
import com.gitee.drinkjava2.frog.organ.Line;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Animal = cells <br/>
 * cells = actions + photons <br/>
 * 
 * Animal's name is Sam.
 * 
 * 脑由一个cells三维数组组成，每个cell里可以存在多个行为，行为是由器官决定，同一个细胞可以存在多种行为。光子是信息的载体，永远不停留。
 * 
 * @author Yong Zhu
 * 
 * @since 1.0
 */
public abstract class Animal {// 这个程序大量用到public变量而不是getter/setter，主要是为了编程方便和简洁，但缺点是编程者需要小心维护各个变量
	/** brain cells */
	public List<Cell> cells = new ArrayList<>();

	/** organs */
	public List<Organ> organs = new ArrayList<>();

	public int x; // animal在Env中的x坐标
	public int y; // animal在Env中的y坐标
	public long energy = 10000000; // 青蛙的能量为0则死掉
	public boolean alive = true; // 设为false表示青蛙死掉了，将不参与计算和显示，以节省时间
	public int ateFood = 0; // 青蛙曾吃过的食物总数，下蛋时如果两个青蛙能量相等，可以比数量
	public int no; // 青蛙在Env.animals中的序号，从1开始， 会在运行期写到当前brick的最低位，可利用Env.animals.get(no-1)快速定位青蛙
	public int high=0; //青蛙跳在空中的高度, 用于听力测试
	public int crockRadius = 40; // 呱呱叫或哇哇叫的传播半径
	public boolean guagua = false; // 呱呱叫，不是那个呱呱叫的意思，就是呱呱叫的意思
    public boolean wawa = false; // 哇哇叫，哇哇叫和呱呱叫是两种不同的声音	

	public Image animalImage;

	public Animal(Egg egg) {// x, y 是虑拟环境的坐标
		if (Env.BORN_AT_RANDOM_PLACE) {
			x = RandomUtils.nextInt(Env.ENV_WIDTH);
			y = RandomUtils.nextInt(Env.ENV_HEIGHT);
		} else {
			this.x = egg.x + RandomUtils.nextInt(80) - 40;
			this.y = egg.y + RandomUtils.nextInt(80) - 40;
			if (this.x < 0)
				this.x = 0;
			if (this.y < 0)
				this.y = 0;
			if (this.x >= (Env.ENV_WIDTH - 1))
				this.x = Env.ENV_WIDTH - 1;
			if (this.y >= (Env.ENV_HEIGHT - 1))
				this.y = Env.ENV_HEIGHT - 1;
		}
		for (Organ org : egg.organs)
			organs.add(org);
	}

	public void initAnimal() { // 初始化animal,通常只是调用每个organ的init方法
		for (Organ org : organs)
			org.initOrgan(this);// 每个新器官初始化，如果是Group类，它们会生成许多脑细胞
		for (int i = 0; i <200; i++) {
			addRandomLines();			
		}
	}

	public void addRandomLines() {// 有一定机率在器官间生成随机的神经连线
		if (alive && RandomUtils.percent(5f)) {// 有很小的机率在青蛙活着时就创建新的器官
			Line line = new Line();
			line.initilized = false;
			line.initOrgan(this);
			organs.add(line);
		}
	}

	public boolean active() {// 这个active方法在每一步循环都会被调用，是脑思考的最小帧
		// 如果能量小于0、出界、与非食物的点重合则判死
		if (!alive) {
			energy -= 1000; // 死掉的青蛙也要消耗能量，确保淘汰出局
			return false;
		}
		if (energy < 0 || Env.outsideEnv(x, y) || Env.bricks[x][y] >= Material.KILL_ANIMAL) {
			energy -= 1000;
			kill();
			return false;
		}
		energy -= 20;
		// 依次调用每个organ的active方法
		for (Organ organ : organs)
			organ.active(this);
		// 依次调用每个cell的active方法，这是写在organ类里的方法，因为同一个器官的cell具有相同的行为
		for (Cell cell : cells)
			cell.organ.active(this, cell);
		//addRandomLines(); // 随机添加神经连线, 这是一个硬编码, 目前一个连线对应一个器官，待改进
		return alive;
	}

	public abstract void show(Graphics g);// 显示青蛙或蛇，子类要重写这个方法

	public abstract void kill();// 杀死青蛙或蛇，子类要重写这个方法

	@SuppressWarnings("unchecked")
	public <T extends Organ> T findOrganByClass(Class<?> claz) {// 根据器官名寻找器官，但不是每个器官都有名字
		for (Organ o : organs)
			if (o != null && o.getClass() == claz)
				return (T) o;
		return null;
	}

	/** Check if x,y,z out of animal's brain range */
	public static boolean outBrainRange(int x, int y, int z) {// 检查指定坐标是否超出animal脑空间界限
		return x < 0 || x >= Env.FROG_BRAIN_XSIZE || y < 0 || y >= Env.FROG_BRAIN_YSIZE || z < 0
				|| z >= Env.FROG_BRAIN_ZSIZE;
	}
	
	/** Check if animal close to a position */
	public boolean isClosePosition(int x, int y, int r) {// 检查是否接近x,y点的r距形范转
		return  Math.abs(this.x-x)<r && Math.abs(this.y-y)<r;
	}
	

	/** Print debug info */
	public String debugInfo() {// 输出Animal调试内容
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < organs.size(); i++) {
			Organ o = organs.get(i);
			if (o != null) {
				sb.append("organ(" + i + ")=" + o.getClass()).append("\r");
				if (Line.class.equals(o.getClass())) {
					Line l = (Line) o;
					sb.append(l.inputZone.debugInfo()).append("\r");
					sb.append(l.outputZone.debugInfo()).append("\r");
				}
			}
		}
		return sb.toString();
	}

}
