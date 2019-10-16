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
package com.github.drinkjava2.frog.objects;

import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.organ.Ear;
import com.github.drinkjava2.frog.brain.organ.Eye;
import com.github.drinkjava2.frog.util.RandomUtils;
import com.github.drinkjava2.frog.util.StringPixelUtils;

/**
 * LetterTester used to test A, B , C, D letter recognition
 *
 * 这是一个临时类，用来测试青蛙的视觉模式识别功能。 在测试的前半段，它在青蛙视觉区激活一个字母的图像并同时激活对应这个字母的区, 如A的图像对应A区，
 * B的图像对应B区(这相当于同步给它一个单音节听觉信号，所以用到了Ear类，等第一步测试目标完成，将来复合音节会用声母+韵母或内码的编码来表示,而不是一个单音节区)，
 * 然后在下半段仅仅激活图像，检测是否对应字母的听觉区能被图像激活（图像的输入会让青蛙产生幻听，脑内成像区简化成与听力输入区重合)，有就增加青蛙的能量，让它在生存竟争中胜出。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class LetterTester implements EnvObject {
	private static final String STR = "ABCD";
	byte[][] pixels;
	String letter;

	@Override
	public void build() { // do nothing
	}

	@Override
	public void destory() {// do nothing
	}

	@Override
	public void active(int screen) {
		if (Env.step == 0) { // 每当开始新一屏测试时，重选一个随机字符
			letter = String.valueOf(STR.charAt(RandomUtils.nextInt(4)));
			pixels = StringPixelUtils.getSanserif12Pixels(letter);
		}
		Frog firstFrog = Env.frogs.get(screen * Env.FROG_PER_SCREEN);
		Eye eye = firstFrog.findOrganByName("eye");
		eye.seeImage(firstFrog, pixels);

		Ear ear = firstFrog.findOrganByName("ear");

		if (Env.step < Env.STEPS_PER_ROUND / 2) {// 前半段同时还要激活与这个字母对应脑区(听觉输入区)
			ear.hearSound(firstFrog, letter);
		} else if (Env.step == Env.STEPS_PER_ROUND / 2) {// 在中段取消字母对应脑区的激活
			ear.hearNothing(firstFrog);
		} else if (Env.step > Env.STEPS_PER_ROUND / 2) {// 后半段要检测这个字母区是否能收到光子信号
			if (firstFrog.getCuboidTotalValues(ear.getCuboidByStr(letter)) > 0)
				firstFrog.energy += 100;
			//TODO： 然后还要检测其它的区必须没有这个字母的区活跃
		}
	}

}
