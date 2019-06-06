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
import java.awt.Graphics;

import com.github.drinkjava2.frog.Frog;

/**
 * Organ is a part of frog, organ can be saved in egg
 * 
 * 器官是脑的一部分，多个器官在脑内可以允许重叠出现在同一位置。
 * 
 * @author Yong Zhu
 * @since 1.0.4
 */
public abstract class Organ extends Zone {
	private static final long serialVersionUID = 1L;
	public String name; // 显示在脑图上的器官名称，可选
	public boolean isFixed = true; // 如果是固定的，则不参与变异和进化
	public long fat = 0; // 如果活跃多，fat值高，则保留（及变异）的可能性大，反之则很可能丢弃掉
	public boolean isGroup = false; // 是细胞组还是普通的器官，普通的器官通常无需进化
	public int cellQty = 1; // 这个器官里会生成多少个脑细胞，可以少，但不能多于此数, 有些特殊器官没有脑细胞

	/** If active in this organ's zone? */
	public boolean outputActive(Frog f) {
		for (Cell cell : f.cells) {
			for (Output output : cell.outputs) { //
				if (cell.energy > 10 && this.nearby(output)) {
					f.organs.get(cell.organNo).fat++;
					cell.energy -= 30;
					return true;
				}
			}
		}
		return false;
	}

	/** Set X, Y, Radius of current Organ */
	public Organ setXYR(float x, float y, float radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		return this;
	}

	/** Set X, Y, Radius, name of current Organ */
	public Organ setXYRN(float x, float y, float radius, String name) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.name = name;
		return this;
	}

	/** Set X, Y, Radius, name of current Organ */
	public Organ setFixed(boolean fixed) {
		this.isFixed = fixed;
		return this;
	}

	/** Child class can override this method to drawing picture */
	public void drawOnBrainPicture(BrainPicture pic) {// 把自已这个器官在脑图上显示出来，子类可以重写这个方法
		Graphics g = pic.getGraphics();// border
		if (this.isFixed)
			g.setColor(Color.BLACK); // 如果是固定的，黑色表示
		else
			g.setColor(BrainPicture.color(this.cellQty)); // 如果是继承的，彩虹色表示，颜色数越往后表示数量越多
		pic.drawZone(g, this);
		if (this.name != null)
			pic.drawText(g, this, String.valueOf(this.name));
	}

	/** make a new copy of current organ */
	public Organ newCopy() { // 创建一个当前器官的副本
		Organ newOrgan = null;
		try {
			newOrgan = this.getClass().newInstance();
			copyXYR(this, newOrgan);
			newOrgan.name = this.name;
			newOrgan.fat = this.fat;
			newOrgan.isGroup = this.isGroup;
			newOrgan.cellQty = this.cellQty;
			newOrgan.isFixed = this.isFixed;
			return newOrgan;
		} catch (Exception e) {
			throw new UnknownError("Can not make new Organ copy for " + this);
		}
	}

	/** Only call once when frog created , Child class can override this method */
	public void init(Frog f) { // 仅在Frog生成时这个方法会调用一次，缺省啥也不干，通常用于Group子类的初始化
	}

	/** Only call once when frog created , Child class can override this method */
	public Organ[] vary() { // 在下蛋时每个器官会调用这个方法，缺省返回自已的副本，Group类通常要覆盖这个方法
		return new Organ[] { newCopy() };
	}

	/** Each loop step call active method, Child class can override this method */
	public void active(Frog f) { // 每一步都会调用器官的active方法
		f.energy -= 20; // 每个器官运动都要消耗能量, 死了也要消耗能量
		if (!f.alive)
			return;
		if (f.energy < 0) { // 如果能量小于0则死
			f.alive = false;
			return;
		}
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
