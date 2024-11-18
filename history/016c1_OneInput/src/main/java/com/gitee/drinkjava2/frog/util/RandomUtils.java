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
package com.gitee.drinkjava2.frog.util;

import java.util.Random;

/**
 * Random Utilities used in this project
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class RandomUtils {

    private RandomUtils() {
    }

    private static final Random rand = new Random();

    public static int nextInt(int i) {//返回随机整数，最小为0，最大为n-1
        if (i == 0)
            return 0;
        return rand.nextInt(i);
    }

    public static int nextNegOrPosInt(int n) {//返回随机整数，最小为-(n-1)，最大为n-1
        int x = nextInt(n);
        if (percent(50))
            return x;
        return -x;
    }

    public static float nextFloat() {
        return rand.nextFloat();
    }
    
    public static float nextNegOrPosFloat() {
        if (percent(50))
            return rand.nextFloat();
        return -rand.nextFloat();
    }

    public static boolean percent(float percent) {// 有百分这percent的机率为true
        return rand.nextFloat() * 100 < percent;
    }

    public static int vary(int v, int percet) {
        if (percent(percet))
            return vary(v);
        return v;
    }

    //    public static int vary(int v) {// 随机有大概率小变异，小概率大变异，极小概率极大变异 
    //        if (percent(50))
    //            v += (nextInt(3) - 0.5);
    //        else if (percent(50))
    //            v += 2 * (nextInt(2) - 0.5);
    //        else if (percent(50))
    //            v += 4 * (nextInt(2) - 0.5);
    //        else if (percent(50))
    //            v += 8 * (nextInt(2) - 0.5);
    //        else if (percent(50))
    //            v += 16 * (nextInt(2) - 0.5);
    //        else if (percent(50))
    //            v += 32 * (nextInt(2) - 0.5);
    //        else if (percent(50))
    //            v += 64 * (nextInt(2) - 0.5);
    //        else if (percent(50))
    //            v += 100 * (nextInt(2) - 0.5);
    //        return v;
    //    }

    public static int vary(int v) {// 随机有大概率小变异，小概率大变异，极小概率极大变异 ，这里改进算法，用正切函数曲线来实现这个概率
        int n = nextNegOrPosInt(900);
        return (int) (v + Math.round(2 * Math.tan(0.1f * n * 3.14159 / 180)));
    }

    //    public static void main(String[] args) {
    //        int n = 0;
    //        for (int i = 0; i < 100; i++) {
    //            n = vary(n); 
    //            System.out.println(n);
    //        }
    //    }

    public static float vary(float v) {// 随机有大概率小变异，小概率大变异，极小概率极大变异
        if (percent(40))
            v += v * .04 * (nextFloat() - 0.5); // v=v+-.04
        if (percent(10))
            v += v * .103 * (nextFloat() - 0.5); // v=v+-0.1
        else if (percent(5))
            v += v * 1 * (nextFloat() - 0.5); // v=v+-0.4
        else if (percent(2))
            v += v * 4 * (nextFloat() - 0.5); // v=v+-2
        else if (percent(1f))
            v += v * 8 * (nextFloat() - 0.5); // v=v+-6
        return v;
    }

    public static int varyInLimit(int v, int from, int to) {// 让返回值在from和to之间随机变异
        int i = vary(v);
        if (i < from)
            i = from;
        if (i > to)
            i = to;
        return i;
    }

    public static float varyInLimit(float v, float from, float to) {// 让返回值在from和to之间随机变异
        float i = vary(v);
        if (i < from)
            i = from;
        if (i > to)
            i = to;
        return i;
    }

}
