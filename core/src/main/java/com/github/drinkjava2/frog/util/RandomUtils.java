/* Copyright 2018-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.github.drinkjava2.frog.util;

import static com.github.drinkjava2.frog.Env.FROG_BRAIN_XSIZE;
import static com.github.drinkjava2.frog.Env.FROG_BRAIN_YSIZE;
import static com.github.drinkjava2.frog.Env.FROG_BRAIN_ZSIZE;

import java.util.Random;

import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Cone;
import com.github.drinkjava2.frog.brain.Cuboid;
import com.github.drinkjava2.frog.brain.Hole;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.brain.Shape;

/**
 * Random Utilities used in this project
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class RandomUtils {

	private RandomUtils() {
	}

	private static final Random rand = new Random();

	public static int nextInt(int i) {
		return rand.nextInt(i);
	}

	public static float nextFloat() {
		return rand.nextFloat();
	}

	public static boolean percent(float percent) {// 有百分这percent的机率为true
		return rand.nextFloat() * 100 < percent;
	}

	public static Cell getRandomCell(Frog f) {// 在随机一个器官里取一个随机Cell
		int r = rand.nextInt(f.organs.size());
		Organ o = f.organs.get(r);
		if (o.shape == null)
			return null;
		if (o.shape instanceof Cuboid) {
			Cuboid c = (Cuboid) o.shape;
			int x = c.x + rand.nextInt(c.xe);
			int y = c.y + rand.nextInt(c.ye);
			int z = c.z + rand.nextInt(c.ze);
			return f.getCell(x, y, z);
		}
		return null;
	}

	/** Randomly create a Cuboid inside of brain space */
	public static Cuboid randomCuboid() {// 随机生成一个位于脑空间内的立方体
		Cuboid c = new Cuboid();
		c.x = nextInt(FROG_BRAIN_XSIZE) - FROG_BRAIN_XSIZE / 4;// 为了多产生贴边的立方体，超出边界的被裁切
		if (c.x < 0)
			c.x = 0;
		c.y = nextInt(FROG_BRAIN_YSIZE) - FROG_BRAIN_YSIZE / 4;
		if (c.y < 0)
			c.y = 0;
		c.z = nextInt(FROG_BRAIN_ZSIZE) - FROG_BRAIN_ZSIZE / 4;
		if (c.z < 0)
			c.z = 0;
		c.xe = 1 + nextInt(FROG_BRAIN_XSIZE); // 立方体任一个边长至少是1
		if (c.xe > (FROG_BRAIN_XSIZE - c.x))// 超出边界的被裁切
			c.xe = FROG_BRAIN_XSIZE - c.x;
		c.ye = 1 + nextInt(FROG_BRAIN_YSIZE);
		if (c.ye > (FROG_BRAIN_YSIZE - c.y))
			c.ye = FROG_BRAIN_YSIZE - c.y;
		c.ze = 1 + nextInt(FROG_BRAIN_ZSIZE);
		if (c.ze > (FROG_BRAIN_ZSIZE - c.z))
			c.ze = FROG_BRAIN_ZSIZE - c.z;
		return c;
	}

	/** Randomly create a Cone inside of brain space */
	public static Cone randomCone() {// 随机生成一个位于脑空间内的锥体
		Cone c = new Cone();
		c.x1 = nextInt(Env.FROG_BRAIN_XSIZE);
		c.y1 = nextInt(Env.FROG_BRAIN_YSIZE);
		c.z1 = nextInt(Env.FROG_BRAIN_ZSIZE);
		c.x2 = nextInt(Env.FROG_BRAIN_XSIZE);
		c.y2 = nextInt(Env.FROG_BRAIN_YSIZE);
		c.z2 = nextInt(Env.FROG_BRAIN_ZSIZE);
		c.r1 = nextInt(Env.FROG_BRAIN_ZSIZE / 2);// 暂时以z边长的一半取随机数
		c.r2 = nextInt(Env.FROG_BRAIN_ZSIZE / 2);
		return c;
	}

	public static int vary(int v, int percet) {
		if (percent(percet))
			return vary(v);
		return v;
	}

//	public static int vary(int v) {// 随机有大概率小变异，小概率大变异，极小概率极大变异
//		if (percent(40))
//			v *= .98 + .04 * nextFloat(); // 0.98~1.02
//		if (percent(10))
//			v *= .95 + .103 * nextFloat(); // 0.95~1.053
//		else if (percent(5))
//			v *= .08 + 0.45 * nextFloat(); // 0.8~1.25
//		else if (percent(1))
//			v *= .05 + 1.5 * nextFloat(); // 0.5~2
//		return v;
//	}

	public static int vary(int v) {// 随机有大概率小变异，小概率大变异，极小概率极大变异
		if (percent(40))
			v += v * .04 * (nextFloat() - 0.5); // v=v+-.04
		if (percent(10))
			v += v * .103 * (nextFloat() - 0.5); // v=v+-0.1
		else if (percent(5))
			v += v * 1 * (nextFloat() - 0.5); // v=v+-0.4
		else if (percent(2))
			v += v * 4 * (nextFloat() - 0.5); // v=v+-2
		else if (percent(1f))
			v += v * 8 * (nextFloat() - 0.5); // v=v+-6
		return v;
	}

	public static int varyInLimit(int v, int from, int to) {// 让返回值在from和to之间随机变异
		int i = vary(v);
		if (i < from)
			i = from;
		if (i > to)
			i = to;
		return i;
	}

	public static float vary(float v, int percet) {
		if (percent(percet))
			return vary(v);
		return v;
	}

//	public static float vary(float v) {// 随机有大概率小变异，小概率大变异，极小概率极大变异
//		if (percent(40))
//			v *= .98 + .04 * nextFloat(); // 0.98~1.02
//		if (percent(10))
//			v *= .95 + .103 * nextFloat(); // 0.95~1.053
//		else if (percent(5))
//			v *= .08 + 0.45 * nextFloat(); // 0.8~1.25
//		else if (percent(1))
//			v *= .05 + 1.5 * nextFloat(); // 0.5~2
//		return v;
//	}

	public static float vary(float v) {// 随机有大概率小变异，小概率大变异，极小概率极大变异
		if (percent(40))
			v += v * .04 * (nextFloat() - 0.5); // v=v+-.04
		if (percent(10))
			v += v * .103 * (nextFloat() - 0.5); // v=v+-0.1
		else if (percent(5))
			v += v * 1 * (nextFloat() - 0.5); // v=v+-0.4
		else if (percent(2))
			v += v * 4 * (nextFloat() - 0.5); // v=v+-2
		else if (percent(1f))
			v += v * 8 * (nextFloat() - 0.5); // v=v+-6
		return v;
	}

	public static Shape vary(Shape shape) {// 随机有大概率小变异，小概率大变异，极小概率极大变异
		return shape; // TODO shape的变异
	}

	public static Hole[] vary(Hole[] holes) {// 随机有大概率小变异，小概率大变异，极小概率极大变异
		return holes; // TODO holes的变异
	}

}
