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
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.gitee.drinkjava2.frog.brain.Cell;
import com.gitee.drinkjava2.frog.brain.CellActions;
import com.gitee.drinkjava2.frog.brain.Cuboid;
import com.gitee.drinkjava2.frog.brain.Hole;
import com.gitee.drinkjava2.frog.brain.Organ;
import com.gitee.drinkjava2.frog.brain.Photon;
import com.gitee.drinkjava2.frog.egg.Egg;
import com.gitee.drinkjava2.frog.objects.Material;

/**
 * Frog = cells <br/>
 * cells = actions + photons <br/>
 * 
 * Frog's name is Sam.
 * 
 * 青蛙脑由一个cells三维数组组成，每个cell里可以存在多个行为，行为是由器官决定，同一个细胞可以存在多种行为。光子是信息的载体，永远不停留。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Frog {// 这个程序大量用到public变量而不是getter/setter，主要是为了编程方便和简洁，但缺点是编程者需要小心维护各个变量
	/** brain cells */
	public Cell[][][] cells;// 一开始不要初始化，只在调用getOrCreateCell方法时才初始化相关维以节约内存

	/** organs */
	public List<Organ> organs = new ArrayList<>();

	public int x; // frog在Env中的x坐标
	public int y; // frog在Env中的y坐标
	public long energy = 10000000; // 青蛙的能量为0则死掉
	public boolean alive = true; // 设为false表示青蛙死掉了，将不参与计算和显示，以节省时间
	public int ateFood = 0;

	static Image frogImg;
	static {
		try {
			frogImg = ImageIO.read(new FileInputStream(Application.CLASSPATH + "frog.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Frog(int x, int y, Egg egg) {// x, y 是虑拟环境的坐标
		this.x = x;
		this.y = y;
		for (Organ org : egg.organs)
			organs.add(org);
	}

	public void initFrog() {// 仅在测试之前调用这个方法初始化frog以节约内存，测试完成后要清空units释放内存
		try {
			cells = new Cell[Env.FROG_BRAIN_XSIZE][][]; // 为了节约内存，先只初始化三维数组的x维，另两维用到时再分配
		} catch (OutOfMemoryError e) {
			System.out.println("OutOfMemoryError found for frog, force it die.");
			this.alive = false;
			return;
		}
		for (int orgNo = 0; orgNo < organs.size(); orgNo++) {
			organs.get(orgNo).init(this, orgNo);
		}
	}

	/** Find a organ in frog by organ's name */
	@SuppressWarnings("unchecked")
	public <T extends Organ> T findOrganByName(String organName) {// 根据器官名寻找器官，但不是每个器官都有名字
		for (Organ o : organs)
			if (o.organName != null && organName.equalsIgnoreCase(o.organName))
				return (T) o;
		return null;
	}

	/** Set with given activeValue */
	public void setCuboidVales(Cuboid o, boolean active) {// 激活长方体区域内的所有脑区
		if (!alive)
			return;
		for (int x = o.x; x < o.x + o.xe; x++)
			if (cells[x] != null)
				for (int y = o.y; y < o.y + o.ye; y++)
					if (cells[x][y] != null)
						for (int z = o.z; z < o.z + o.ze; z++)
							if (cells[x][y][z] != null)
								getOrCreateCell(x, y, z).hasInput = active;
	}

	private int activeNo = 0;// 每一帧光子只能走一步，用这个来作标记

	public boolean active(Env v) {// 这个active方法在每一步循环都会被调用，是脑思考的最小帧
		activeNo++;
		// 如果能量小于0、出界、与非食物的点重合则判死
		if (!alive || energy < 0 || Env.outsideEnv(x, y) || Env.bricks[x][y] >= Material.KILLFROG) {
			energy -= 100; // 死掉的青蛙也要消耗能量，确保淘汰出局
			alive = false;
			return false;
		}
		energy -= 20;
		for (Organ o : organs)
			o.active(this); // 调用每个器官的active方法， 通常只用于执行器官的外界信息输入、动作输出，脑细胞的遍历不是在这一步

		// 这里是最关键的脑细胞主循环，脑细胞负责捕获和发送光子，光子则沿它的矢量方向每次自动走一格，如果下一格是真空(即cell未初始化）会继续走下去并衰减直到为0(为减少运算)
		for (int i = 0; i < Env.FROG_BRAIN_XSIZE; i++) {
			Env.checkIfPause(this);
			if (cells[i] != null)
				for (int j = 0; j < Env.FROG_BRAIN_YSIZE; j++)
					if (cells[i][j] != null)
						for (int k = 0; k < Env.FROG_BRAIN_ZSIZE; k++) {
							Cell cell = cells[i][j][k];
							if (cell != null)
								CellActions.act(this, activeNo, cell); // 调用每个细胞的act方法
						}
		}
		return alive;
	}

	public void show(Graphics g) {// 显示青蛙的图象
		if (!alive)
			return;
		g.drawImage(frogImg, x - 8, y - 8, 16, 16, null);
	}

	/** Check if cell exist */
	public Cell getCell(int x, int y, int z) {// 返回指定脑ssf坐标的cell ，如果不存在，返回null
		if (cells[x] == null || cells[x][y] == null)
			return null;
		return cells[x][y][z];
	}

	/** Get a cell in position (x,y,z), if not exist, create a new one */
	public Cell getOrCreateCell(int x, int y, int z) {// 获取指定坐标的Cell，如果为空，则在指定位置新建Cell
		if (outBrainBound(x, y, z))
			return null;
		if (cells[x] == null)
			cells[x] = new Cell[Env.FROG_BRAIN_YSIZE][];
		if (cells[x][y] == null)
			cells[x][y] = new Cell[Env.FROG_BRAIN_ZSIZE];
		Cell cell = cells[x][y][z];
		if (cell == null) {
			cell = new Cell(x, y, z);
			cells[x][y][z] = cell;
		}
		return cell;
	}

	/** Check if x,y,z out of frog's brain bound */
	public static boolean outBrainBound(int x, int y, int z) {// 检查指定坐标是否超出frog脑空间界限
		return x < 0 || x >= Env.FROG_BRAIN_XSIZE || y < 0 || y >= Env.FROG_BRAIN_YSIZE || z < 0
				|| z >= Env.FROG_BRAIN_ZSIZE;
	}

	/** Photon always walk */
	public void addAndWalk(Photon p) { // 添加光子的同时让它沿光子方向自动走一格
		p.x += p.mx;
		p.y += p.my;
		p.z += p.mz;
		int rx = Math.round(p.x);
		int ry = Math.round(p.y);
		int rz = Math.round(p.z);
		if (Frog.outBrainBound(rx, ry, rz))
			return;// 出界直接扔掉
		Cell cell = getCell(rx, ry, rz);
		if (cell != null)
			cell.addPhoton(p);
	}

	/** Photon always walk */
	public void addAndWalkAndDig(Photon p) { // 添加光子的同时让它沿光子方向自动走一格
		p.x += p.mx;
		p.y += p.my;
		p.z += p.mz;
		int rx = Math.round(p.x);
		int ry = Math.round(p.y);
		int rz = Math.round(p.z);
		if (Frog.outBrainBound(rx, ry, rz))
			return;// 出界直接扔掉
		Cell cell = getCell(rx, ry, rz);
		if (cell != null) {
			cell.addPhoton(p);
			cell.digHole(p);
		}
	}

	// for test purpose, reset some values for prepare next training.
	public void prepareNewTraining() {
		for (int i = 0; i < Env.FROG_BRAIN_XSIZE; i++) {
			if (cells[i] != null)
				for (int j = 0; j < Env.FROG_BRAIN_YSIZE; j++)
					if (cells[i][j] != null)
						for (int k = 0; k < Env.FROG_BRAIN_ZSIZE; k++) {
							Cell cell = cells[i][j][k];
							if (cell != null) {
								cell.deleteAllPhotons();
								cell.hasInput = false;
								cell.photonSum = 0;
								if (cell.holes != null)
									for (Hole h : cell.holes) {
										h.age += 100;// 强迫洞的年龄增加,用这个方法来区分开不同批次的训练
									}
							}
						}
		}
	}

}
