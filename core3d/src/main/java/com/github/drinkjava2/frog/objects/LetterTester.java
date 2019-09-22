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

/**
 * LetterTester used to test A, B , C, D letter recognition
 *
 * 这是一个临时类，用来测试青蛙的视觉模式识别功能。 在测试的前半段，它在青蛙视觉区激活一个字母的图像并同时激活对应这个字母的区(如A的图像对应A区，
 * B的图像对应B区...)， 然后仅仅激活图像，检测是否对应字母的区能被图像激活, 有就增加青蛙的能量，让它在生存竟争中胜出
 * 
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class LetterTester implements Object {

	@Override
	public void build() { // do nothing
	}

	@Override
	public void destory() {// do nothing
	}

	@Override
	public void active(int screen) {

	}

}
