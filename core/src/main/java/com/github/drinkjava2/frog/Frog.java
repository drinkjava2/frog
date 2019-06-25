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
package com.github.drinkjava2.frog;

import java.awt.Graphics;
import java.awt.Image;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Input;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.brain.Output;
import com.github.drinkjava2.frog.egg.CellGroup;
import com.github.drinkjava2.frog.egg.Egg;
import com.github.drinkjava2.frog.egg.OrganDesc;
import com.github.drinkjava2.frog.egg.Zone;
import com.github.drinkjava2.frog.env.Application;
import com.github.drinkjava2.frog.env.Env;

/**
 * Frog = brain + organ, but now let's only focus on brain, organs are hard
 * coded in egg
 * 
 * 青蛙由脑细胞和器官组成，目前脑细胞可以变异、进化、遗传，以由电脑自动生成神经网络，但是器官在蛋里硬编码，不许进化，将来可以考虑器官的进化
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Frog {

	public CellGroup[] cellGroups;

	/** brain cells */
	public List<Cell> cells = new ArrayList<>(); 
 
	/** organs */
	public List<Organ> organs = new ArrayList<>();

	public int x; // frog在env中的x坐标
	public int y; // frog在env中的y坐标
	public long energy = 10000; // 能量为0则死掉
	public boolean alive = true; // 设为false表示青蛙死掉了，将不参与任何计算和显示，以节省时间

	static final Random r = new Random();
	static Image frogImg;
	static {
		try {
			frogImg = ImageIO.read(new FileInputStream(Application.CLASSPATH + "frog.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Frog(int x, int y, Egg egg) {
		this.x = x;
		this.y = y;

		// Brain cells
		if (egg.cellGroups != null) {
			cellGroups = new CellGroup[egg.cellGroups.length];
			for (int k = 0; k < egg.cellGroups.length; k++) {
				CellGroup g = egg.cellGroups[k];
				cellGroups[k] = new CellGroup(g);
				for (int i = 0; i < g.cellQty; i++) {// 开始根据蛋来创建脑细胞
					Cell c = new Cell();
					c.group = k;
					int cellQTY = Math.round(g.inputQtyPerCell);
					c.inputs = new Input[cellQTY];
					for (int j = 0; j < cellQTY; j++) {
						c.inputs[j] = new Input();
						c.inputs[j].cell = c;
						Zone.copyXY(randomPosInZone(g.groupInputZone), c.inputs[j]);
						c.inputs[j].radius = g.cellInputRadius;
					}
					cellQTY = Math.round(g.outputQtyPerCell);
					c.outputs = new Output[cellQTY];
					for (int j = 0; j < cellQTY; j++) {
						c.outputs[j] = new Output();
						c.outputs[j].cell = c;
						Zone.copyXY(randomPosInZone(g.groupOutputZone), c.outputs[j]);
						c.outputs[j].radius = g.cellOutputRadius;
					}
					cells.add(c);
				}
			}
		}

		// Create organs from egg, like eye, moves, eat...
		if (egg.organDescs != null)
			for (OrganDesc od : egg.organDescs)
				organs.add(new Organ(od));

	}

	public boolean active(Env v) {
		energy -= 20;
		if (!alive)
			return false; 
		if (energy < 0) {
			alive = false;
			return false;
		}

		for (Organ o : organs)
			o.active(this);
		return alive;
	}

	private static Zone randomPosInZone(Zone z) {
		return new Zone(z.x - z.radius + z.radius * 2 * r.nextFloat(), z.y - z.radius + z.radius * 2 * r.nextFloat(),
				0);
	}

	public void show(Graphics g) {
		if (!alive)
			return;
		g.drawImage(frogImg, x - 8, y - 8, 16, 16, null);
	}

}
