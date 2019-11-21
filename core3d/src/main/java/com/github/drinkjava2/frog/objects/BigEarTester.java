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
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.organ.BigEar;
import com.github.drinkjava2.frog.brain.organ.Eye;
import com.github.drinkjava2.frog.util.StringPixelUtils;

/**
 * ChineseTester used to test test recognition
 *
 * 这是一个临时类，用来测试大耳朵对编码后的汉字的模式识别及汉字的逆向成像，大耳朵相对于小耳朵来说，分辨率更高，所有声音都分为声母和韵母两个音编码
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class BigEarTester implements EnvObject {
	private static final String STR = "床前明月光疑是地上霜";

	@Override
	public void build() { // do nothing
	}

	@Override
	public void destory() {// do nothing
	}

	@Override
	public void active(int screen) {
		Frog frog = Env.frogs.get(screen * Env.FROG_PER_SCREEN); // 这个测试只针对每屏的第一只青蛙，因为脑图固定只显示第一只青蛙
		Eye eye = frog.findOrganByName("Eye");
		BigEar ear = frog.findOrganByName("BigEar");
		int index = Env.step / 30;
		if (Env.step % 30 == 0)
			frog.deleteAllPhotons();

		if (index < STR.length()) {
			Cell.blockBackEarPhoton = true;
			Cell.blockBackEyePhoton = true;
			BrainPicture.setNote("第" + (index + 1) + "个字训练");
			int firstCode = index / 5;
			int secondCode = index % 5;
			ear.hearSound(frog, firstCode, secondCode);
			eye.seeImage(frog, StringPixelUtils.getSanserif12Pixels(STR.substring(index, index + 1)));
		} else {
			Cell.blockBackEarPhoton = false;
			int index2 = index % 10;
			BrainPicture.setNote("第" + (index2 + 1) + "个字识别");
			eye.seeImage(frog, StringPixelUtils.getSanserif12Pixels(STR.substring(index2, index2 + 1)));
			if (Env.step % 30 > 28) {
				int[] result = ear.readcode(frog);
				System.out.println(result[0] + "," + result[1]);
				frog.deleteAllPhotons();
			}
		}

	}

	private static boolean next50(int i) {
		return Env.step > i && Env.step < i + 25;
	}

}
