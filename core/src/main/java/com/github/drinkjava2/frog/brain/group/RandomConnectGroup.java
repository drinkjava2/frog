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
package com.github.drinkjava2.frog.brain.group;

import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.Organ;

/**
 * RandomConnectGroup
 * 
 * 这是一个随机方式连接两端的Group，它是从旧版的CellGroup改造过来，这是一种最简单的神经元排列方式，只有一组细胞，触突输入区和输出区分别位于Zone内的任意随机两点。
 * 至于是否合理则由frog的遗传进化来决定，不合理的RandomConnectGroup会被淘汰掉。
 * 
 * (还没改造完成，在不破坏原有外在表现的基础上，要平滑将它改造成一个标准Group的子类，也是第一个子类 )
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class RandomConnectGroup extends Group {
	private static final long serialVersionUID = 1L;
	public boolean isFixed = true; // 如果是固定的，则不参与变异和进化

	@Override
	public void init(Frog f) {
		// Brain cells
		// if (egg.cellGroups != null) {
		// cellGroups = new RandomConnectGroup[egg.cellGroups.length];
		// for (int k = 0; k < egg.cellGroups.length; k++) {
		// RandomConnectGroup g = egg.cellGroups[k];
		// cellGroups[k] = new RandomConnectGroup(g);
		// for (int i = 0; i < g.cellQty; i++) {// 开始根据蛋来创建脑细胞
		// Cell c = new Cell();
		// c.group = k;
		// int cellQTY = Math.round(g.inputQtyPerCell);
		// c.inputs = new Input[cellQTY];
		// for (int j = 0; j < cellQTY; j++) {
		// c.inputs[j] = new Input();
		// c.inputs[j].cell = c;
		// Zone.copyXY(randomPosInZone(g.groupInputZone), c.inputs[j]);
		// c.inputs[j].radius = g.cellInputRadius;
		// }
		// cellQTY = Math.round(g.outputQtyPerCell);
		// c.outputs = new Output[cellQTY];
		// for (int j = 0; j < cellQTY; j++) {
		// c.outputs[j] = new Output();
		// c.outputs[j].cell = c;
		// Zone.copyXY(randomPosInZone(g.groupOutputZone), c.outputs[j]);
		// c.outputs[j].radius = g.cellOutputRadius;
		// }
		// cells.add(c);
		// }
		// }
		// }

	}

	@Override
	public Organ[] vary() {
		return null;
	}

	// for (RandomConnectGroup group : frog.cellGroups) {
	// drawLine(g, group.groupInputZone, group.groupOutputZone);
	// drawZone(g, group.groupInputZone);
	// fillZone(g, group.groupOutputZone);
	// if (group.fat > 0) {
	// g.setColor(Color.BLACK);
	// drawCircle(g, group.groupOutputZone); // 如果胖了，表示激活过了，下次下蛋少不了这一组
	// }
	// }

}
