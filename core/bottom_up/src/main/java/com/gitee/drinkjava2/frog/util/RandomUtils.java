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
package com.gitee.drinkjava2.frog.util;

import java.util.Random; 

/**
 * Random Utilities used in this project
 * 
 * 随机数工具，最理想情况下，随机算法和遗传算法是Frog项目中仅有的两个算法。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class RandomUtils {

	private RandomUtils() {
	}

	private static final Random rand = new Random();

	public static int nextInt(int i) {//生成一个整数
		return rand.nextInt(i);
	}

	public static float nextFloat() {//生成一个实数
		return rand.nextFloat();
	}

	public static boolean percent(float percent) {// 有百分之percent的机率为true
		return rand.nextInt(100) < percent;
	}

  
	public static int vary(int v, int percet) {//给定一个整数，有百分之percent的机率变异，变异范围由vary方法决定
		if (percent(percet))
			return vary(v);
		return v;
	}

	public static int vary(int v) {// 随机有大概率小变异，小概率大变异，极小概率极大变异
		if (percent(40))
			v *= .98 + .04 * nextFloat(); // 0.98~1.02
		if (percent(10))
			v *= .95 + .103 * nextFloat(); // 0.95~1.053
		else if (percent(5))
			v *= .08 + 0.45 * nextFloat(); // 0.8~1.25
		else if (percent(1))
			v *= .05 + 1.5 * nextFloat(); // 0.5~2
		return v;
	}

	public static float vary(float v, int percet) {//给定一个实数，有百分之percent的机率变异，变异范围由vary方法决定
		if (percent(percet))
			return vary(v);
		return v;
	}

	public static float vary(float v) {// 随机有大概率小变异，小概率大变异，极小概率极大变异
		if (percent(40))
			v *= .98 + .04 * nextFloat(); // 0.98~1.02
		if (percent(10))
			v *= .95 + .103 * nextFloat(); // 0.95~1.053
		else if (percent(5))
			v *= .08 + 0.45 * nextFloat(); // 0.8~1.25
		else if (percent(1))
			v *= .05 + 1.5 * nextFloat(); // 0.5~2
		return v;
	} 
 

}
