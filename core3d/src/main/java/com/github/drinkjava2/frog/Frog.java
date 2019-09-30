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

import javax.imageio.ImageIO;

import com.github.drinkjava2.frog.brain.Cube;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.egg.Egg;
import com.github.drinkjava2.frog.objects.Material;

/**
 * Frog = organs + cubes <br/>
 * cubes = brain cells + photon
 * 
 * 青蛙由器官组成，器官中的Group类会播种出各种脑细胞填充在一个cubes三维数组代表的空间中，每个cube里可以存在多个脑细胞和光子，光子是信息的载体，永远不停留。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Frog {

	/** brain cells */
	public Cube[][][] cubes;

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

	public Frog(int x, int y, Egg egg) {
		initCubes();
		this.x = x; // x, y 是虑拟环境的坐标
		this.y = y;
		for (Organ org : egg.organs)
			organs.add(org);
	}

	public void initCubes() {
		cubes = new Cube[Env.FROG_BRAIN_XSIZE][Env.FROG_BRAIN_YSIZE][Env.FROG_BRAIN_ZSIZE];
		for (int a = 0; a < Env.FROG_BRAIN_XSIZE; a++)
			for (int b = 0; b < Env.FROG_BRAIN_YSIZE; b++)
				for (int c = 0; c < Env.FROG_BRAIN_ZSIZE; c++)
					cubes[a][b][c] = new Cube();
	}

	public void initOrgans() {// 调用每个器官的init方法，通常用于脑细胞的播种
		for (Organ org : organs)
			org.init(this);
	}

	/** Find a organ in frog by organ's name */
	public Organ findOrganByName(String organName) {// 根据器官名寻找器官
		for (Organ o : organs)
			if (organName.equalsIgnoreCase(o.getClass().getSimpleName()))
				return o;
		return null;
	}

	/** Active all cubes in organ with given activeValue */
	public void activeOrgan(Organ o, float activeValue) {// 激活与器官重合的所有脑区
		for (int x = o.x; x < o.x + o.xe; x++)
			for (int y = o.y; y < o.y + o.ye; y++)
				for (int z = o.z; z < o.z + o.ze; z++)
					cubes[x][y][z].active = activeValue;
	}

	/** Deactivate all cubes in organ with given activeValue */
	public void deactivateOrgan(Organ o) {// 激活与器官重合的所有脑区
		for (int x = o.x; x < o.x + o.xe; x++)
			for (int y = o.y; y < o.y + o.ye; y++)
				for (int z = o.z; z < o.z + o.ze; z++)
					cubes[x][y][z].active = 0;
	}

	/** Calculate organ activity by add all organ cubes' active value together */
	public float getOrganActivity(Organ o) {// 遍历所有器官所在cube，将它们的激活值汇总返回
		float activity = 0;
		for (int x = o.x; x < o.x + o.xe; x++)
			for (int y = o.y; y < o.y + o.ye; y++)
				for (int z = o.z; z < o.z + o.ze; z++)
					activity += this.cubes[x][y][z].active;
		return activity;
	}

	public boolean active(Env v) {// 这个active方法在每一步循环都会被调用，是脑思考的最小帧
		// 如果能量小于0、出界、与非食物的点重合则判死
		if (!alive || energy < 0 || Env.outsideEnv(x, y) || Env.bricks[x][y] >= Material.KILLFROG) {
			energy -= 100; // 死掉的青蛙也要消耗能量，确保淘汰出局
			alive = false;
			return false;
		}
		energy -= 20;
		for (Organ o : organs) {
			o.active(this); // 调用每个器官的active方法，如果重写了的话
		}
		return alive;
	}

	public void show(Graphics g) {// 显示青蛙的图象
		if (!alive)
			return;
		g.drawImage(frogImg, x - 8, y - 8, 16, 16, null);
	}

}
