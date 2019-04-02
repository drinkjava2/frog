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
import com.github.drinkjava2.frog.brain.Output;
import com.github.drinkjava2.frog.egg.CellGroup;
import com.github.drinkjava2.frog.egg.Egg;
import com.github.drinkjava2.frog.egg.Zone;
import com.github.drinkjava2.frog.env.Application;
import com.github.drinkjava2.frog.env.Env;

/**
 * Frog = brain + body, but now let's only focus on brain, ignore body
 * 
 * 为了简化模型，这个类里出现多个固定数值的编码，以后要改进成可以可以放在蛋里遗传进化的动态数值，先让生命延生是第一步，优化是以后的事
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
public class Frog {

	public CellGroup[] cellGroups;

	/** brain cells */
	public List<Cell> cells = new ArrayList<Cell>();

	/** 视觉细胞的输入区在脑中的坐标，随便取一个区就可以了，以后再考虑进化成两个眼睛 */
	public Zone eye = new Zone(50, 250, 50);

	/** 饥饿的感收区在脑中的坐标，先随便取就可以了，以后再考虑放到蛋里去进化 */
	public Zone hungry = new Zone(300, 100, 100);

	/** 进食奖励的感收区在脑中的坐标，先随便取就可以了，以后再考虑放到蛋里去进化 */
	public Zone happy = new Zone(300, 600, 100);

	/** 运动细胞的输入区在脑中的坐标，先随便取就可以了，以后再考虑放到蛋里去进化 */
	public Zone moveDown = new Zone(700, 100, 40); // 屏幕y坐标是向下的
	public Zone moveUp = new Zone(700, 400, 40);
	public Zone moveLeft = new Zone(650, 250, 40);
	public Zone moveRight = new Zone(750, 250, 40);

	public int x;
	public int y;
	public long energy = 10000; 
	public boolean alive = true; // 设为false表示青蛙死掉了，将不参与任何计算，以节省时间

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
		if (egg.cellGroups == null)
			throw new IllegalArgumentException("Illegal egg cellgroups argument:" + egg.cellGroups);

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

	private int goUp = 0;
	private int goDown = 0;
	private int goLeft = 0;
	private int goRight = 0;

	/** Active a frog, if frog is dead return false */
	public boolean active(Env env) {
		if (!alive)
			return false;
		energy -= 20;
		if (energy < 0) {
			alive = false;
			return false;
		}

		for (Cell cell : cells) {
			if (energy < 10000) // in hungry
				for (Input input : cell.inputs) {
					if (input.nearby(hungry)) { 
						if (cell.energy < 100)
							cell.energy++;
					}
				}

			for (Output output : cell.outputs) { // hungry drive moves
				if (goUp < 1 && cell.energy > 10 && moveUp.nearby(output)) {
					cellGroups[cell.group].fat++;
					goUp++;
					if (cell.energy > 0)
						cell.energy--;
				}
				if (goDown < 1 && cell.energy > 10 && moveDown.nearby(output)) {
					cellGroups[cell.group].fat++;
					goDown++;
					if (cell.energy > 0)
						cell.energy--;
				}
				if (goLeft < 1 && cell.energy > 10 && moveLeft.nearby(output)) {
					cellGroups[cell.group].fat++;
					goLeft++;
					if (cell.energy > 0)
						cell.energy--;
				}
				if (goRight < 1 && cell.energy > 10 && moveRight.nearby(output)) {
					cellGroups[cell.group].fat++;
					goRight++;
					if (cell.energy > 0)
						cell.energy--;
				}
			}
		}
		moveAndEat(env);
		return alive;
	}

	/** 如果青蛙位置与food重合，吃掉它 */
	private void moveAndEat(Env env) {
		if (!alive)
			return;
		x = x + Math.round(goRight - goLeft);
		y = y + Math.round(goUp - goDown);

		goUp = 0;// 方向重置
		goDown = 0;
		goLeft = 0;
		goRight = 0;
		if (x < 0 || x >= env.ENV_XSIZE || y < 0 || y >= env.ENV_YSIZE) {// 越界者死！
			alive = false;
			return;
		}

		boolean eatedFood = false;
		if (env.foods[x][y] > 0) {
			env.foods[x][y] = 0;
			energy = energy + 1000;// 吃掉food，能量境加
			eatedFood = true;
		}
		// 奖励
		if (eatedFood) {

		}
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
