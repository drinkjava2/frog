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
package com.gitee.drinkjava2.frog.brain;

import java.io.Serializable;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Line is line connect 2 cells, may have one or more
 * line/diode/resistance/capacity types
 * 
 * Line是一个随机方式两个脑细胞的连线，连线并不是简单直连，每个连线可以有一或多个属性： <br/>
 * 直连：信号无损双向传输 <br/>
 * 二极管：信号只能单向传输<br/>
 * 电容：信号会累加存贮在线上 <br/>
 * 电阻：信号传输会有衰减 <br/>
 * 溢流阀：仅当信号大于一个阀值时才会传输通过 。。。。。。<br/>
 * 
 * 本版本中Line还是先用传统的随机生成的方式生成，将来再考虑用分裂算法由基因来生成
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Line implements Serializable {
	private static final long serialVersionUID = 1L;

	private static int _index = 1;

	// 从上到下，优先级从高到低，也就是说以下属性是串联生效的，连线没有，下面的二级管之类的属性也就无从谈起
	public static final int LIN = 1; // 直连线, 能量在两个细胞间双向传输，并从高的向低的流动
	public static final int DIO = 1 << _index++; // 二级管，能量只能单向流动,
	public static final int OVR = 1 << _index++; // 溢流阀相当于一个双向PN结, 允许流动的能量必须大于这个阀值
	public static final int RES = 1 << _index++; // 电阻,传递的能量要乘以这个电阻系数
	public static final int CAP = 1 << _index++; // 电容，
	// 三级管？连线没有这种属性，不知道实际生物有没有三级管这个细胞功能。也就是说，目前打算用以上元件组装一台电脑，但是不允许使用三级管。

	public int type = 0; // type中哪个位如果为1表示上面的某个属性

	public int x1, y1, z1, x2, y2, z2; // 连线的头尾两个细胞坐标位置

	/**
	 * value 取值0~1之间 当type有LIN时表示直连, 能量从高处流向低处 当type有RES时表示电阻值, 能量从高处流向低处时乘以电阻率
	 * 当type有CAP表示记忆电容量，信号通过时保存一部分在中点，中点能量加上任一方向新信号，会产生反向传播信号效果 当type有OVR时表示溢流阀值
	 */
	public float value = 0;

	public Line() { // 缺省构造器不能省，从蛋里读出时要调用这个
	}

	public Line(int[] from, int[] to) {
		this.type = 1 << RandomUtils.nextInt(_index);
		x1 = from[0];
		y1 = from[1];
		z1 = from[2];
		x2 = to[0];
		y2 = to[1];
		z2 = to[2];
		value = 0.5f; // 所有类型连线初值都暂定为0.5f， 反正每个line的值都会自己变异并传到蛋里去
	}

	public void active(Animal a) {// line干的活，就是各种花式把能量在细胞间搬来搬去
		if ((type & LIN) == 0) // 如果没有连线，直接退出，
			return;

		float eFrom = a.energys[x1][y1][z1];
		float eTo = a.energys[x1][y1][z1];
		if (eFrom < 0.1f || eTo < 0.1f)
			return;

		if (eFrom > eTo) {

		} else { // eFrom<eTo

		}

		if ((type & DIO) > 0 && eFrom >= 0.1f) { // 如果有二极管，能量只从源向目的流动，与能量大小无关
			a.energys[x1][y1][z1] = (eFrom + eTo) > 1 ? 1 : eFrom + eTo; // 所有细胞能量不允许超过1
		}

	}

	public void drawOnBrainPicture(Animal f, BrainPicture pic) {// 把自已这个器官在脑图上显示出来
		pic.drawCentLine(x1, y1, z1, x2, y2, z2);
		StringBuilder name = new StringBuilder();
		if ((type & LIN) > 0) 
			name.append("L");
		if ((type & DIO) > 0)  
			name.append("D");
		if ((type & OVR) > 0)
			name.append("O");
		if ((type & RES) > 0)
			name.append("R");
		if ((type & CAP) > 0)
			name.append("C");
		float f1 = RandomUtils.nextFloat() * 0.2f-0.1f; // 随机略改变一下名字输出位置，这样可以根据文字有多少知道有多少根线在同一位置
		pic.drawTextCenter(0.5f*(x1+x2), 0.5f*(y1+y2)+f1, 0.5f*(z1+z2)+f1, name.toString(), 0.2f); // pic.drawCircle(x, y, z, 0.2f);
	}

}
