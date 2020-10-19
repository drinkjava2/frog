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

import java.awt.Color;
import java.awt.Graphics;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import com.gitee.drinkjava2.frog.egg.Egg;
import com.gitee.drinkjava2.frog.objects.Material;

/**
 * Snake has similar brain like Frog, snake eat frog <br>
 * 蛇是青蛙的子类，区别是： 蛇只能看到青蛙，并将其当作一个单个象素点的食物， 青蛙能看到真的食物(Food)和蛇
 * 
 * @since 1.0
 */
public class Frog extends Animal {

	public Frog(Egg egg) {
		super(egg);
		try {
			animalImage = ImageIO.read(new FileInputStream(Application.CLASSPATH + "frog.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void kill() {// 杀死蛙
		this.alive = false;
		Env.clearMaterial(x, y, Material.FROG_TAG);
	}

	@Override
	public void show(Graphics g) {// 显示蛙的图象
		if (!alive)
			return;
		g.drawImage(animalImage, x - 8, y - 8, 16, 16, null);// 减去坐标，保证中心显示在当前x,y处
		if(high>0) { //如果跳起来了，画个小黄标记出来
			g.setColor(Color.YELLOW);
			int r=5;
			g.fillArc(x-r+2, y-r, r, r, 0, 360);
		}
		
		if(guagua) { //如果呱呱叫了，画个小红圈标记出来
			g.setColor(Color.red); 
			g.drawArc(x-8, y-8, 16, 16, 0, 360);
		}
	}
}
