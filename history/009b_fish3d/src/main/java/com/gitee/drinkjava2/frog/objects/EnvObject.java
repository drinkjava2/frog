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

/**
 * EnvObject means some virtual object in Env
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public interface EnvObject {

	public void build(); // 在Env中创建本身物体，指改变Env.bricks数组元素为本身物体的组成材料。只在每屏测试前调用一次

	public void destory();// 从Env中清除本身物体，只在每屏测试完成后调用一次

	public void active(); // 每个步长都会调用一次这个方法
}
