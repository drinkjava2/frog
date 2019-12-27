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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.egg.Egg;
import com.github.drinkjava2.frog.objects.Material;

/**
 * Frog = organs + brain cells
 * 
 * 青蛙由器官组成，器官中的Group类会生成各种脑细胞
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Frog {

	/** brain cells */
	public List<Cell> cells = new ArrayList<>();

	/** organs */
	public List<Organ> organs = new ArrayList<>();

	public int x; // frog在Env中的x坐标
	public int y; // frog在Env中的y坐标
	public long energy = 100000; // 青蛙的能量为0则死掉
	public boolean alive = true; // 设为false表示青蛙死掉了，将不参与计算和显示，以节省时间
	public int ateFood = 0;

	// 青蛙有两只脚，不是自动进化而来的，而是直接赋予它（以后也可以考虑用个数随机生成、可感、可控、互锁的积木块来模拟运动器官本身的自动生成)
	public int bFootPos = 0; // 底部脚的水平位置, -5 to 5
	public String bFootStatus = "UP"; // 底部脚的状态，只有UP和DOWN两种状态
	public int rFootPos = 0;// 右侧脚的水平位置, -5 to 5
	public String rFootStatus = "UP";// 右侧脚的状态，只有UP和DOWN两种状态

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
		for (Organ org : egg.organs)
			organs.add(org);
	}

	public void initOrgans() {
		for (Organ org : organs)
			org.initFrog(this);// 每个新器官初始化，如果是Group类，它们会生成许多脑细胞
	}

	/** Find a organ in frog by organ's name */
	@SuppressWarnings("unchecked")
	public <T extends Organ> T findOrganByName(String organName) {// 根据器官名寻找器官，但不是每个器官都有名字
		for (Organ o : organs)
			if (organName.equalsIgnoreCase(o.name))
				return (T) o;
		return null;
	}

	public boolean active(Env v) {
		// 如果能量小于0则死、出界、与非食物的点重合则判死
		if (!alive || energy < 0 || Env.outsideEnv(x, y) || Env.bricks[x][y] >= Material.KILLFROG) {
			energy -= 100; // 死掉的青蛙也要消耗能量，确保淘汰出局
			alive = false;
			return false;
		}
		energy -= 20;
		for (Organ o : organs) {
			o.active(this);
		}
		return alive;
	}

	public void show(Graphics g) {// 显示青蛙的图象
		if (!alive)
			return;
		g.drawImage(frogImg, x - 8, y - 8, 16, 16, null);
		if (bFootStatus == "UP")
			g.setColor(Color.BLACK);
		else
			g.setColor(Color.RED);
		g.drawLine(x, y, x + bFootPos, y + 10);// 画底脚
		if (rFootStatus == "UP")
			g.setColor(Color.BLACK);
		else
			g.setColor(Color.RED);
		g.drawLine(x, y, x + 10, y + rFootPos); // 画右脚
	}

}
