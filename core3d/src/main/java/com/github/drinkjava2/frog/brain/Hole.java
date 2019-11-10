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

import java.io.Serializable;

/**
 * Hole can be input, output, side synapse
 * 
 * 以前叫突触，现在改名叫洞，更形象一点，每个光子传来就好象在果冻上砸出个洞。管它符不符合生物脑突触这个形象，张牙舞爪的神经元变成了千创百孔的果冻，先乱试一通再说。
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class Hole implements Serializable {
	private static final long serialVersionUID = 1L;
	public float x; // x,y,z分别是 这个洞角度在三个轴上的投影
	public float y;
	public float z;
	public float size;// 洞的大小，同一个方向砸来的光子越多，洞就越大
}
