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
package com.gitee.drinkjava2.frog.temp;

/** 
 * 
 * 临时类，待删，测试一个细胞有三个输入，并且每个输入是双通道（奖罚）情况下是否可以实现所有的模式识别
 * 处理逻辑为  (信号x正权重)取1为饱和值 - 信号x负权重，当结果大于0.5时输出1。 这个逻辑是模拟网上找到的“大脑只需单个神经元就可进行XOR异或运算”一文
 * 原理。只不过这里扩展到三个（或以上)输入的情况。
 * 
 *  三个输入有8种排列组合，如一组参数能实现任意8种组合，则有2的8次方=256种排列组合，去除全为0的输入必须输出0，只计算有1信号的输入，则有2的7次方=128种组合）
 * （经实测，有31种组合条件不，不能找到符合要求的正负权重） 
 * 
 * （4个通道有16种排列组合，如一组参数能实现任意16种组合，则有2的16次方=65536种排列组合，去除全为0的输入，则有2的15次方32768种组合）
 * （经实测，32768组合中有29987种不能找到符合要求的正负权重，如只测前1024个组合，则有603种情况不能找到解，如只测前128组合，则有31种情况下找不到解）
 * 
 * 
 * 
 */
@SuppressWarnings("all")
public class TestInput3 {

    public static void main(String[] args) {
        testInput3();
        //testInput4();
    }

    public static void testInput3() { //这里测试一个细胞有三个树突输入，每个输入有正负两种信号，且信号以1为饱和，测试结果发现有31种情况无法找到解
        long notFoundCont = 0;
        boolean pass = false;
        float p = 0.1f;
        for (int i = 0; i < 128; i++) {
            found: //
            for (float a = 0; a < 1.001; a += p) {
                for (float b = 0; b < 1.001; b += p) {
                    for (float c = 0; c < 1.001; c += p) {

                        for (float d = 0; d < 1.001; d += p) {
                            for (float e = 0; e < 1.001; e += p) {
                                for (float f = 0; f < 1.001; f += p) {

                                    pass = true;
                                    int bt = 1;
                                    int x, y, z;
                                    for (int n = 1; n <= 7; n++) {
                                        x = ((n & 4) > 0) ? 1 : 0;
                                        y = ((n & 2) > 0) ? 1 : 0;
                                        z = ((n & 1) > 0) ? 1 : 0;
                                        int shouldbe = (i & bt) > 0 ? 1 : 0;

                                        float v = x * a + y * b + z * c; //正信号累加
                                        if (v > 1) //如饱和取1
                                            v = 1f;
                                        float v1 = x * d + y * e + z * f; //负信号累加
                                        if (v1 > 1)
                                            v1 = 1f;
                                        v = v - v1;

                                        int result = v >= 0.5 ? 1 : 0;

                                        if (result != shouldbe) {
                                            pass = false;
                                            break;
                                        }
                                        bt = bt << 1;
                                    }

                                    if (pass) {
                                        System.out.print("i=" + i + " found, i=" + bin(i));
                                        System.out.println("   " + r(a) + ", " + r(b) + ", " + r(c) + "    " + r(d) + ", " + r(e) + ", " + r(f));

                                        bt = 1;
                                        for (int n = 1; n <= 7 && false; n++) {
                                            x = ((n & 4) > 0) ? 1 : 0;
                                            y = ((n & 2) > 0) ? 1 : 0;
                                            z = ((n & 1) > 0) ? 1 : 0;
                                            int shouldbe = (i & bt) > 0 ? 1 : 0;
                                            System.out.println("   " + x + "*" + r(a) + " + " + y + "*" + r(b) + " + " + z + "*" + r(c) + " - " + x + "*" + r(d) + " - " + y + "*" + r(e) + " - " + z
                                                    + "*" + r(f) + " = " + shouldbe);

                                            bt = bt << 1;
                                        }

                                        break found;
                                    }

                                }
                            }
                        }

                    }
                }
            }
            if (!pass) {
                System.out.println("i=" + i + " not found, i=" + bin(i));
                notFoundCont++;
            }
        }
        System.out.println("notFoundCont=" + notFoundCont);
    }

    public static void testInput4() {//这里测试一个细胞有4个树突输入，每个输入有正负两种信号，且信号以1为饱和，测试结果发现有603种情况无法找到解
        long notFoundCont = 0;
        boolean pass = false;
        float p = 0.2f;
        for (int i = 0; i < 1024; i++) {
            found: //
            for (float a = 0; a < 1.001; a += p) {
                for (float b = 0; b < 1.001; b += p) {
                    for (float c = 0; c < 1.001; c += p) {
                        for (float d = 0; d < 1.001; d += p) {

                            for (float e = 0; e < 1.001; e += p) {
                                for (float f = 0; f < 1.001; f += p) {
                                    for (float g = 0; g < 1.001; g += p) {
                                        for (float h = 0; h < 1.001; h += p) {

                                            pass = true;
                                            int bt = 1;
                                            int x, y, z, m;
                                            for (int n = 1; n <= 15; n++) {
                                                x = ((n & 8) > 0) ? 1 : 0;
                                                y = ((n & 4) > 0) ? 1 : 0;
                                                z = ((n & 2) > 0) ? 1 : 0;
                                                m = ((n & 1) > 0) ? 1 : 0;
                                                int shouldbe = (i & bt) > 0 ? 1 : 0;

                                                float v = x * a + y * b + z * c + m * d; //正信号累加
                                                if (v > 1) //如饱和取1
                                                    v = 1f;
                                                float v1 = x * e + y * f + z * g + m * h; //负信号累加
                                                if (v1 > 1)
                                                    v1 = 1f;
                                                v = v - v1;

                                                int result = v >= 0.5 ? 1 : 0;

                                                if (result != shouldbe) {
                                                    pass = false;
                                                    break;
                                                }
                                                bt = bt << 1;
                                            }

                                            if (pass) {
                                                System.out.print("i=" + i + " found, i=" + bin(i));
                                                System.out.println("   " + r(a) + ", " + r(b) + ", " + r(c) + ", " + r(d) + "    " + r(e) + ", " + r(f) + ", " + r(g) + ", " + r(h));

                                                bt = 1;
                                                for (int n = 1; n <= 15; n++) {
                                                    x = ((n & 8) > 0) ? 1 : 0;
                                                    y = ((n & 4) > 0) ? 1 : 0;
                                                    z = ((n & 2) > 0) ? 1 : 0;
                                                    m = ((n & 1) > 0) ? 1 : 0;
                                                    int shouldbe = (i & bt) > 0 ? 1 : 0;
                                                    System.out.println("   " + x + "*" + r(a) + " + " + y + "*" + r(b) + " + " + z + "*" + r(c) + " + " + m + "*" + r(d) //
                                                            + " - " + x + "*" + r(e) + " - " + y + "*" + r(f) + " - " + z + "*" + r(g) + " - " + m + "*" + r(h) + " = " + shouldbe);

                                                    bt = bt << 1;
                                                }

                                                break found;
                                            }

                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
            if (!pass) {
                System.out.println("i=" + i + " not found, i=" + bin(i));
                notFoundCont++;
            }
        }
        System.out.println("notFoundCont=" + notFoundCont);
    }

    static float r(float f) { //取小数后2位
        return Math.round(f * 100) * 1.0f / 100;
    }

    static String bin(int i) { //转二进制
        String ibin = Integer.toBinaryString(i);
        while (ibin.length() < 7)
            ibin = "0" + ibin;
        return ibin;
    }

}
