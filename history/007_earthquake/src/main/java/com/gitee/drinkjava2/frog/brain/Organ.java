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

import java.awt.Color;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Animal;

/**
 * Organ is a part of frog, organ can be saved in egg
 * 
 * 器官是脑的一部分，多个器官在脑内可以允许重叠出现在同一位置。
 * 
 * @author Yong Zhu
 * @since 1.0.4
 */
public class Organ extends Zone {
	private static final long serialVersionUID = 1L;
	public String name; // 显示在脑图上的器官名称，可选
	public long fat = 0; // 如果活跃多，fat值高，则保留（及变异）的可能性大，反之则很可能丢弃掉
	// public float organWasteEnergy = 0.05f; //
	// 器官在每个测试循环中需要消耗青蛙多少能量，可以通过调节这个参数抑制器官数量无限增长
	public float organActiveEnergy = 1; // 输出器官激活需要消耗每个脑细胞多少能量
	public float organOutputEnergy = 2; // 感觉器官激活会给每个脑细胞增加多少能量
	public boolean initilized; // 通过这个标记判断是否需要手工给定它的参数初值
	
    public static boolean[] sound=new boolean[Env.FROG_BRAIN_XSIZE];

	public boolean allowBorrow() { // 是否允许在精子中将这个器官借出
		return false;
	}

	/** Only call once when frog created , Child class can override this method */
	public void initOrgan(Animal a) { // 仅在Animal生成时会调用一次
	}

	/** Each loop step call active method, Child class can override this method */
	public void active(Animal a) { // 这是器官级别的方法，每个步长调用一次
	}

	/** Each loop step call active method, Child class can override this method */
	public void active(Animal a, Cell c) { // 这是Cell级别的方法，每个步长、每个细胞都要调用一次
	}

	/** check if organ be actived by other cess*/
	public boolean beActivedByCells(Animal a) { // 判断是否这个器官被某个细胞激活
		for (Cell cell : a.cells)
			if (cell.energy > organActiveEnergy && this.nearby(cell.output)) {
				cell.organ.fat++;
				cell.energy -= 30;//
				return true;
			}
		return false;
	}

	/** to active all cells nearby this orgain */
	public void activeCells(Animal a, float energy) { // 某器官激活触突位于它这个区的所有细胞
		for (Cell cell : a.cells)
			if (cell.energy < 100)
				if (this.nearby(cell.input)) {
					cell.energy += energy;
					return;
				}
	}

	/** Set X, Y, Radius, name of current Organ */
	public Organ setXYZRHN(float x, float y, float z, float r, float h, String name) {
		this.setXYZRH(x, y, z, r, h);
		this.name = name;
		return this;
	}

	/** Child class can override this method to drawing picture */
	public void drawOnBrainPicture(Animal a, BrainPicture pic) {// 把自已这个器官在脑图上显示出来，子类可以重写这个方法
		if (!Env.SHOW_FIRST_ANIMAL_BRAIN)
			return;
		pic.setPicColor(Color.BLACK); // 缺省是黑色
		pic.drawZone(this);
		if (this.name != null)
			pic.drawText(x, y, z, String.valueOf(this.name));
	}

	/** Only call once after organ be created by new() method */
	public Organ[] vary() { // 在下蛋时每个器官会调用这个方法，缺省返回一个类似自已的副本，子类通常要覆盖这个方法
		Organ newOrgan = null;
		try {
			newOrgan = this.getClass().newInstance();
		} catch (Exception e) {
			throw new UnknownError("Can not make new organ copy for " + this);
		}
		copyXYZRH(this, newOrgan);
		newOrgan.name = this.name;
		newOrgan.fat = this.fat;
		return new Organ[] { newOrgan };
	}

}
