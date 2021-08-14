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
package com.gitee.drinkjava2.frog.objects;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.brain.BrainPicture;
import com.gitee.drinkjava2.frog.brain.organ.Eye;
import com.gitee.drinkjava2.frog.brain.organ.FrontEar;
import com.gitee.drinkjava2.frog.brain.organ.TopEar;
import com.gitee.drinkjava2.frog.util.StringPixelUtils;

/**
 * ChineseTester used to test test recognition
 *
 * 这是一个临时类，用来测试耳朵对汉字的模式识别及汉字的逆向成像
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class LetterTester implements EnvObject {
	public static final String STR = "01234青青园中葵朝露待日晞阳春布德泽万物生光辉";
	//public static final String STR = "青青园中葵朝露待日晞阳春布德泽万物生光辉常恐秋节至焜黄华叶衰百川东到海何时复西归少壮不努力老大徒伤悲";
	public static final int TIME = 120;

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
		TopEar topEar = frog.findOrganByName("TopEar");
		FrontEar frontEar = frog.findOrganByName("FrontEar");

		int index = Env.step / TIME;
		if (Env.step % TIME == 0)
			frog.prepareNewTraining();

		if (index < STR.length()) {
			BrainPicture.setNote("第" + (index + 1) + "个字训练:"+STR.charAt(index));
			topEar.hearSound(frog, index);
			frontEar.hearSound(frog, index);
			eye.seeImageWithOffset(frog, StringPixelUtils.getSanserif12Pixels(STR.substring(index, index + 1)),0,0);
		} else {
			int index2 = index % STR.length();
			BrainPicture.setNote("第" + (index2 + 1) + "个字识别");
			eye.seeImageWithOffset(frog, StringPixelUtils.getSanserif12Pixels(STR.substring(index2, index2 + 1)),0,0);
			if (Env.step % TIME > (TIME - 2)) {
				int c1 = topEar.readcode(frog);
				int c2 = frontEar.readcode(frog);
				int result = c1*5+c2;
				System.out.println("c1="+c1+", c2="+c2+", result=" + result+", 即 '"+STR.substring(result, result+1)+"'");
				frog.prepareNewTraining();
			}
		}
	}

}
