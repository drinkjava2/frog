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
package com.github.drinkjava2.frog.egg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.brain.organ.Active;
import com.github.drinkjava2.frog.brain.organ.Eat;
import com.github.drinkjava2.frog.brain.organ.Eyes.SeeDown;
import com.github.drinkjava2.frog.brain.organ.Eyes.SeeLeft;
import com.github.drinkjava2.frog.brain.organ.Eyes.SeeRight;
import com.github.drinkjava2.frog.brain.organ.Eyes.SeeUp;
import com.github.drinkjava2.frog.brain.organ.MoveDown;
import com.github.drinkjava2.frog.brain.organ.MoveLeft;
import com.github.drinkjava2.frog.brain.organ.MoveRight;
import com.github.drinkjava2.frog.brain.organ.MoveUp;
import com.github.drinkjava2.frog.util.RandomUtils;

/**
 * Egg is the static structure description of frog, can save as text file, to
 * build a frog, first need build a egg.<br/>
 * 
 * 蛋存在的目的是为了以最小的字节数串行化存储Frog,它是Frog的生成算法描述，而不是Frog本身，这样一来Frog就不能"永生"了，因为每一个egg都不等同于
 * 它的母体， 而且每一次测试，大部分条件反射的建立都必须从头开始训练，类似于人类，无论人类社会有多聪明， 婴儿始终是一张白纸，需要花大量的时间从头学习。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Egg implements Serializable {
	// 为了缩短时间，这个程序随机生成的联结将只落在固定的器官上而不是漫天撒网(见4.12提交)，这是程序的优化，实现的逻辑和随机漫天撒网定是相同的。
	// 但是这个优化带来的问题是这是一个硬编码逻辑，不利于器官的优胜劣汰， 而且下面这个 FIXED_ORGAN_QTY必须每次手工设定，以后需要重构这块的代码
	public static int FIXED_ORGAN_QTY = 9;

	private static final long serialVersionUID = 1L;

	public List<Organ> organs = new ArrayList<>();

	public Egg() {// 无中生有，创建一个蛋，先有蛋，后有蛙
		organs.add(new MoveUp().setXYRN(800, 100, 60, "Up"));
		organs.add(new MoveDown().setXYRN(800, 400, 60, "Down"));
		organs.add(new MoveLeft().setXYRN(700, 250, 60, "Left"));
		organs.add(new MoveRight().setXYRN(900, 250, 60, "Right"));
		organs.add(new SeeUp().setXYRN(200, 300 + 90, 40, "SeeUp"));
		organs.add(new SeeDown().setXYRN(200, 300 - 90, 40, "SeeDown"));
		organs.add(new SeeLeft().setXYRN(200 - 90, 300, 40, "SeeLeft"));
		organs.add(new SeeRight().setXYRN(200 + 90, 300, 40, "SeeRight"));
//		organs.add(new Eye().setXYRN(100, 300, 100, "Eye"));
		organs.add(new Active().setXYRN(500, 100, 60, "Active")); // 永远激活
		// 以上数量就是FIXED_ORGAN_QTY值

		organs.add(new Eat().setXYRN(0, 0, 0, "Eat")); // EAT不是感觉或输出器官，没有位置和大小

	}

	/** Create egg from frog */
	public Egg(Frog frog) { // 青蛙下蛋，每个青蛙的器官会创建自已的副本或变异，可以是0或多个
		for (Organ organ : frog.organs)
			for (Organ newOrgan : organ.vary())
				organs.add(newOrgan);
	}

	/**
	 * Create a egg by join 2 eggs, x+y=zygote 模拟X、Y 染色体合并，两个蛋生成一个新的蛋， X从Y里借一个器官,
	 * 不用担心器官会越来越多，因为通过用进废退原则来筛选,没用到的器官会在几代之后被自然淘汰掉
	 * 器官不是越多越好，因为器官需要消耗能量，器官数量多了，在生存竞争中也是劣势
	 * 
	 */
	public Egg(Egg x, Egg y) {
		// x里原来的organ
		for (Organ organ : x.organs)
			organs.add(organ);

		// 从y里借一个organ
		int yOrganSize = y.organs.size();
		if (yOrganSize > 0) {
			Organ o = y.organs.get(RandomUtils.nextInt(yOrganSize));
			if (o.allowBorrow())
				organs.add(o);
		}
	}

}
