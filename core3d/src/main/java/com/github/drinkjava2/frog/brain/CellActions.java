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
package com.github.drinkjava2.frog.brain;

import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.util.RandomUtils;

/**
 * CellActions have many cell action stored
 * 
 * CellActions写死了许多硬编码的脑细胞活动，用act方法来调用这些活动，type参数来区分调用哪个单例的方法
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class CellActions {

	/*-
	 * Each cell's act method will be called once at each loop step
	 * 
	 * 在每个测试步长中act方法都会被调用一次，这个方法针对不同的细胞类型有不同的行为逻辑，这是硬
	 * 编码，所以要准备多套不同的行为（可以参考动物脑细胞的活动逻辑)，然后抛给电脑去随机筛选，不怕多。
	 * 
	 * 举例来说，以下是一些假想中的脑细胞行为：
	 * 一对一，穿透，光子会穿过细胞，细胞起到中继站的作用，如果没有细胞中继，光子在真空中传播(即三维数组的当前坐标没有初始化)会迅速衰减
	 * 一对一，转向，光子传播角度被改变成另一个绝对角度发出
	 * 一对一，转向，光子传播角度被改变成与器官有关的角度发出，可以模拟光线的发散(如视网膜细胞)和聚焦(如脑内成像，即沿光线发散的逆路径)
	 * 一对多，拆分，入射光子被拆分成多个光子，以一定的发散角发出，通常发散光子的总能量小于入射+细胞输出能量之和
	 * 一对多，拆分，入射光子被拆分成多个光子，发散角与器官相关 
	 * 多对一，聚合，入射光子被触突捕获
	 */
	public static void act(Frog frog, int activeNo, Cell c) {
		if (c.holes != null)
			for (Hole h : c.holes) {// 洞的年龄增加，目的是让年龄越接近的洞之间，绑定的概率和强度越大
				h.age++;
			}
		if (c.organs != null)
			for (int orgNo : c.organs) {
				Organ o = frog.organs.get(orgNo);
				switch (o.type) { // 添加细胞的行为，这是硬编码
				case Organ.MOVE: // 如果是MOVE细胞，它的行为是让每个光子穿过这个细胞走到下一格，保持光子沿直线运动
					if (c.x == 0 || c.z == Env.FROG_BRAIN_ZSIZE - 1) {// 但是对于输入区，将删除光子，并合计一共收到多少
						if (c.photonQty > 0) {
							c.photonSum += c.photonQty;
							c.photons = null;
						}
						break;
					}
					if (c.photons != null) {
						for (int ii = 0; ii < c.photons.length; ii++) {
							Photon p = c.photons[ii];
							if (p == null || p.activeNo == activeNo)// 同一轮新产生的光子或处理过的光子不再走了
								continue;
							p.activeNo = activeNo;
							c.removePhoton(ii);
							frog.addAndWalk(p); // 让光子自已往下走，并且还挖洞
						}
					}
					break;
				case Organ.MOVE_JELLY: // 如果是MOVE_JELLY细胞，它让每个光子穿过这个细胞走到下一格，并在下一个细胞上打出洞来
					if (c.x == 0 || c.z == Env.FROG_BRAIN_ZSIZE - 1) {// 但是对于输入区，将删除光子，并合计一共收到多少
						if (c.photonQty > 0) {
							c.photonSum += c.photonQty;
							c.photons = null;
						}
						break;
					}
					if (c.photons != null) {
						for (int ii = 0; ii < c.photons.length; ii++) {
							Photon p = c.photons[ii];
							if (p == null || p.activeNo == activeNo)// 同一轮新产生的光子或处理过的光子不再走了
								continue;
							p.activeNo = activeNo;
							c.removePhoton(ii);
							frog.addAndWalkAndDig(p); // 让光子自已往下走，并且还挖洞
						}
					}
					break;
				case Organ.EYE: // 如果是视网膜细胞，它的行为是将只要Cell有输入信号，就产生向右的多个光子发散出去，模拟波源
					if (c.hasInput && RandomUtils.percent(40)) {// 随机数的作用是减少光子数，加快速度
						for (float yy = -0.1f; yy <= 0.1f; yy += 0.03) {// 形成一个扇面向右发送
							for (float zz = -0.1f; zz <= 0.1f; zz += 0.03) {
								Photon p = new Photon(orgNo, o.color, c.x, c.y, c.z, 1.0f, yy, zz, 100f);
								p.activeNo = activeNo; // 用这个activeNo防止一直被赶着走
								frog.addAndWalk(p);// 光子不是直接添加，而是走一格后添加在相邻的细胞上
							}
						}
					}
					break;
				case Organ.EAR: // 如果是听力细胞，它的行为是将只要Cell有输入信号，就产生向下的多个光子发散出去，模拟波源
					if (c.hasInput && RandomUtils.percent(40)) {// 随机数的作用是减少光子数，加快速度
						for (float xx = -0.3f; xx <= 0.3f; xx += 0.15) {// 形成一个扇面向下发送
							for (float yy = -1f; yy <= 1f; yy += 0.06) {
								Photon p = new Photon(o.organNo, o.color, c.x, c.y, c.z, xx, yy, -1, 100f);
								p.activeNo = activeNo;
								frog.addAndWalk(p);// 光子不是直接添加，而是走一格后添加在相邻的细胞上
							}
						}
					}
					break;
				default:
					break;
				}
			}
	}
}
