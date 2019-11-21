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
import com.github.drinkjava2.frog.brain.BrainPicture;
import com.github.drinkjava2.frog.brain.organ.Ear;
import com.github.drinkjava2.frog.brain.organ.Eye;
import com.github.drinkjava2.frog.util.RandomUtils;
import com.github.drinkjava2.frog.util.StringPixelUtils;

/**
 * ChineseTester used to test test recognition
 *
 * 这是一个临时类，用来测试大耳朵对编码后的汉字的模式识别及汉字的逆向成像
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class BigEarTester implements EnvObject {
	private static final String STR = "我们要让小青蛙学习床前明月光疑是地上霜";
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
		}
		Frog frog = Env.frogs.get(screen * Env.FROG_PER_SCREEN); // 这个测试只针对每屏的第一只青蛙，因为脑图固定只显示第一只青蛙
		Eye eye = frog.findOrganByName("eye");

		Ear ear = frog.findOrganByName("ear");

		if (next50(1)) {
			BrainPicture.setNote("第1个字训练");
			ear.hearSound(frog, "A");
			eye.seeImage(frog, StringPixelUtils.getSanserif12Pixels("一"));
		} else if (next50(50)) {
			BrainPicture.setNote(" 第2个字训练");
			ear.hearSound(frog, "C");
			eye.seeImage(frog, StringPixelUtils.getSanserif12Pixels("二"));
		} else if (next50(100)) {
			BrainPicture.setNote("只看到第3个字");
			eye.seeImage(frog, StringPixelUtils.getSanserif12Pixels("一"));
		} else if (next50(150)) {
			BrainPicture.setNote("只看到第4个字");
			eye.seeImage(frog, StringPixelUtils.getSanserif12Pixels("二"));
		} else if (next50(200)) {
			BrainPicture.setNote("看到第5个字");
			byte[][] pic = StringPixelUtils.getSanserif12Pixels("三");
			eye.seeImage(frog, pic);
		} else {
			BrainPicture.setNote(null);
			ear.hearNothing(frog);
			eye.seeNothing(frog);
		}

	}

	private static boolean next50(int i) {
		return Env.step > i && Env.step < i + 25;
	}

}
