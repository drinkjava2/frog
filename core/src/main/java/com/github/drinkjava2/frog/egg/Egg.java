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
import com.github.drinkjava2.frog.brain.group.Group;
import com.github.drinkjava2.frog.brain.group.RandomConnectGroup;
import com.github.drinkjava2.frog.brain.organ.Eat;
import com.github.drinkjava2.frog.brain.organ.Eye;
import com.github.drinkjava2.frog.brain.organ.Happy;
import com.github.drinkjava2.frog.brain.organ.Hungry;
import com.github.drinkjava2.frog.brain.organ.MoveDown;
import com.github.drinkjava2.frog.brain.organ.MoveLeft;
import com.github.drinkjava2.frog.brain.organ.MoveRight;
import com.github.drinkjava2.frog.brain.organ.MoveUp;
import com.github.drinkjava2.frog.brain.organ.Pain;
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
	private static final long serialVersionUID = 1L;

	public List<Organ> organs = new ArrayList<>();

	public List<Group> groups = new ArrayList<>();

	public Egg() {// 无中生有，创建一个蛋，先有蛋，后有鸡
		organs.add(new Happy().setXYRN(300, 700, 100, "Happy"));
		organs.add(new Pain().setXYRN(600, 700, 100, "Pain"));
		organs.add(new Hungry().setXYRN(300, 100, 100, "Hungry"));
		organs.add(new MoveUp().setXYRN(800, 100, 60, "Up"));
		organs.add(new MoveDown().setXYRN(800, 400, 60, "Down"));
		organs.add(new MoveLeft().setXYRN(700, 250, 60, "Left"));
		organs.add(new MoveRight().setXYRN(900, 250, 60, "Right"));
		organs.add(new Eat().setXYRN(0, 0, 0, "Eat"));
		organs.add(new Eye().setXYRN(100, 400, 100, "Eye"));
		addRandomConnectionGroups();

	}

	/** Create egg from frog */
	public Egg(Frog frog) { // 青蛙下蛋，每个青蛙的器官会创建自已的副本或变异，可以是0或多个
		for (Organ organ : frog.organs)
			for (Organ newOrgan : organ.vary())
				organs.add(newOrgan);
		addRandomConnectionGroups();
	}

	private void addRandomConnectionGroups() {
		for (int i = 0; i < 20; i++) {
			organs.add(new RandomConnectGroup(500, 500, 500));
		}
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
			for (Organ newOrgan : organ.vary())
				organs.add(newOrgan);

		// 从y里借一个organ
		int yOrganSize = y.organs.size();
		if (yOrganSize > 0) {
			Organ o = y.organs.get(RandomUtils.nextInt(yOrganSize));
			if (o.allowBorrow())
				organs.add(o);
		}
		addRandomConnectionGroups();
	}

}
