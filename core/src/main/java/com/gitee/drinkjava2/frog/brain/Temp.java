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

import static com.gitee.drinkjava2.frog.util.RandomUtils.percent;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.util.Logger;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/** 
 * 
 * 临时类，待删，测试一个细胞有三个输入，并且每个输入是双通道（奖罚）情况下是否可以实现所有的模式识别，（三个通道有8种排列组合，如一组参数能实现任意8种组合，则有2的8次方=256种排列组合）
 * 
 * 处理逻辑为  (信号x正权重)取1为饱和值 - 信号x负权重，当结果大于0.5时输出1。 这个逻辑是模拟“大脑只需单个神经元就可进行XOR异或运算”一文原理。只不过这里扩展到三个（或以上)输入的情况。
 * 
 */
@SuppressWarnings("all")
public class Temp {
    public static void main(String[] args) {
        System.out.println("a");
        long cont = 0;
        for (int i = 1; i < 256; i++)
            for (int a = 0; a < 10; a++) {
                for (int b = 0; b < 10; b++) {
                    for (int c = 0; c < 10; c++) {

                        for (int d = 0; d < 10; d++) {
                            for (int e = 0; e < 10; e++) {
                                for (int f = 0; f < 10; f++) {

                                    int bt = 1;
                                    for (int n = 1; n < 7; n++) {
                                        int x, y, z;
                                        if ((n & 1) > 0)
                                            x = 1;
                                        if ((n & 2) > 0)
                                            y = 1;
                                        if ((n & 4) > 0)
                                            z = 1;
                                        int result = i & bt;
                                        bt = bt << 1;
                                    }

                                }
                            }
                        }

                    }
                }
            }
        System.out.println(cont);
    }

}
