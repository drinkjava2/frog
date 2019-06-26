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


	public static boolean percent(int percent) {
		return rand.nextInt(100) < percent;
	}
	
	
//	/** vary a zone position, size a little bit */
//	public static void varyZone(Zone z) {
//		int i = rand.nextInt(100);
//		if (i < 95) // 有95的机率不变异
//			return;
//		z.x = varyByRate(z.x, 0.01f);
//		z.y = varyByRate(z.y, 0.01f);
//		z.r = varyByRate(z.r, 0.03f);
//	}
 

//	public static float varyByRate(float f, float rate) { // 用指定的机率变异
//		boolean bigger = rand.nextInt(2) > 0;
//		if (bigger)
//			f = f + f * rate * rand.nextFloat() + .001f;
//		else
//			f = f - f * rate * rand.nextFloat() - .001f;
//		if (Float.isNaN(f))
//			f = 0f;
//		if (f > 1000000f)
//			f = 1000000f;
//		else if (f < -1000000f)
//			f = -1000000f;
//		return f;
//	}

//	public static float vary(float f) { // 大部分时候不变，有极小机会变异,有极极小机会大变异，有极极极小机会大大大变异
//		int i = rand.nextInt(100);
//		if (i < 50) // 有50的机率不变异
//			return f;
//		float rate = 0.2f; // 50%机率在0.2倍范围变异
//		if (i > 80)
//			rate = 1f; // 有20%的机率在1倍的范围变异
//		if (i > 90)
//			rate = 10f; // 有10%的机率在10倍的范围变异
//		if (i > 95)
//			rate = 100f; // 有5%的机率在100倍的范围变异
//		if (i > 98)
//			rate = 1000f; // 有1%的机率在1000倍的范围变异
//
//		boolean bigger = rand.nextInt(2) > 0;
//		if (bigger)
//			f = f + f * rate * rand.nextFloat() + .001f;
//		else
//			f = f - f * rate * rand.nextFloat() - .001f;
//		if (Float.isNaN(f))
//			f = 0f;
//		if (f > 1000000f)
//			f = 1000000f;
//		else if (f < -1000000f)
//			f = -1000000f;
//		return f;
//	}

}
