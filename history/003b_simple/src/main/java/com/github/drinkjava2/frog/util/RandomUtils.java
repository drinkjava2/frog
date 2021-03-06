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

import java.util.Random;

import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.Zone;
import com.github.drinkjava2.frog.egg.Egg;

/**
 * Random Utilities used in this project
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class RandomUtils {
	private static final Random rand = new Random();

	public static int nextInt(int i) {
		return rand.nextInt(i);
	}

	public static float nextFloat() {
		return rand.nextFloat();
	}

	/** Return a random zone inside of a zone */
	public static Zone randomZoneInZone(Zone z) { // 在一个区内随机取一个小小区
		return new Zone(z.x - z.r + z.r * 2 * rand.nextFloat(), z.y - z.r + z.r * 2 * rand.nextFloat(),
				z.r * rand.nextFloat() * .04f);
	}

	/** Return a random zone inside of frog's random organ */
	public static Zone randomPosInAnyFrogOrgan(Frog f) {
		if (f.organs == null || f.organs.size() == 0)
			throw new IllegalArgumentException("Can not call randomPosInRandomOrgan method when frog has no organ");
		return randomZoneInZone(f.organs.get(RandomUtils.nextInt(Egg.FIXED_ORGAN_QTY)));
	}

	/** Return a random zone inside of frog's random organ */
	public static Zone randomPosMostInNewEye(Frog f) {
		if (f.organs == null || f.organs.size() == 0)
			throw new IllegalArgumentException("Can not call randomPosInRandomOrgan method when frog has no organ");
		if (RandomUtils.percent(95))
			return randomZoneInZone(f.organs.get(7));// 这是一个硬编码，大部分新联接建立在newEye中
		return randomZoneInZone(f.organs.get(RandomUtils.nextInt(Egg.FIXED_ORGAN_QTY)));
	}

	public static boolean percent(float percent) {
		return rand.nextFloat() * 100 < percent;
	}

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

	public static int varyInLimit(int v, int from, int to) {// 让返回值在from和to之间随机变异
		int i = vary(v);
		if (i < from)
			i = from;
		if (i > to)
			i = to;
		return i;
	}

	public static float varyInLimit(float v, float from, float to) {// 让返回值在from和to之间随机变异
		float i = vary(v);
		if (i < from)
			i = from;
		if (i > to)
			i = to;
		return i;
	}

}
