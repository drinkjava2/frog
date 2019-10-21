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
import com.github.drinkjava2.frog.brain.organ.Ear;
import com.github.drinkjava2.frog.brain.organ.Eye;
import com.github.drinkjava2.frog.util.RandomUtils;

/**
 * Egg is the static structure description of frog, can save as file, to build a
 * frog, first need build a egg.<br/>
 * 
 * 蛋存在的目的是为了以最小的字节数串行化存储Frog,它是Frog的生成算法描述，而不是Frog本身，这样一来Frog就不能"永生"了，因为每一个egg都不等同于
 * 它的母体， 而且每一次测试，大部分条件反射的建立都必须从头开始训练，类似于人类，无论人类社会有多聪明， 婴儿始终是一张白纸，需要花大量的时间从头学习。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Egg implements Serializable {
	private static final long serialVersionUID = 1L;

	public List<Organ> organs = new ArrayList<>();// NOSONAR

	public Egg() {// 无中生有，创建一个蛋，先有蛋，后有蛙
		organs.add(new Eye()); // 眼是手工创建的，必有
		organs.add(new Ear()); // 耳是手工创建的，这个是用来测试ABCD字母识别的
		organs.add(Organ.randomCuboidOrgan());
	}

	/** Create egg from frog */
	public Egg(Frog frog) { // 青蛙下蛋，每个青蛙的器官会创建自已的副本或变异，可以是0或多个
		for (Organ organ : frog.organs)
			for (Organ newOrgan : organ.vary(frog))
				organs.add(newOrgan);
	}

	/**
	 * Create a egg by join 2 eggs, x+y=zygote 模拟X、Y 染色体合并，两个蛋生成一个新的蛋，
	 * X有可能从Y里的相同位置借一个器官
	 */
	public Egg(Egg x, Egg y) {
		for (Organ organ : x.organs) {
			if (!organ.allowVary || organ.fat != 0 || RandomUtils.percent(70)) // 如果器官没用到,fat为0，增加丢弃它的机率
				organs.add(organ);
		}
		// x里原来的organ
		if (RandomUtils.percent(70)) // 70%的情况下不作为, x就是受精卵
			return;
		// 从y里借一个organ，替换掉原来位置的organ，相当于DNA级别的片段切换，它要求一个随机位置的Organ都允许替换allowBorrow
		int yOrganSize = y.organs.size();
		if (yOrganSize > 0) {
			int i = RandomUtils.nextInt(yOrganSize);
			Organ o = y.organs.get(i);
			if (o.allowBorrow) {
				if (organs.size() > i && organs.get(i).allowBorrow)
					organs.set(i, o);// 用y里的organ替换掉x里的organ,模拟受精
			}
		}
		if (RandomUtils.percent(50))// 有50%的机率随机会产生新的器官
			organs.add(Organ.randomCuboidOrgan());
		if (RandomUtils.percent(organs.size())) {// 器官会随机丢失，并且机率与器官数量成正比,防止器官无限增长
			int i = RandomUtils.nextInt(organs.size());
			if (organs.get(i).allowBorrow)
				organs.remove(i);
		}
	}

}
