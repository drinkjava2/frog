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

/**
 * Cells代表不同的脑细胞参数，对应每个参数，用8叉树算法生成不同的细胞。
 * 每个脑细胞用一个long来存储，所以最多允许64个基因位, 有时一个参数由多个基因位决定。
 * 基因+分裂算法=结构。这个类里定义每个基因位的掩码和含义
 * 
 * @author Yong Zhu
 * @since 10.0
 */
@SuppressWarnings("all")
public class Cells {
    public static long EXIST = /*                     */0b1L; //细胞存在否,1为存在,0为不存在
    public static long POSITIVE = /*                */ 0b10L; //细胞信号,1为正信号,0为负信号
    public static long ZTX = /*                   */0b11100L; //轴突x方向 (2个0)
    public static long ZTY = /*                */0b11100000L; //轴突y方向 (5个0)
    public static long ZTZ = /*             */0b11100000000L; //轴突z方向 (8个0)
    public static long ZT_LONG = /*      */0b11100000000000L; //轴突长度 (11个0)
    public static long ST_LONG = /*    */0b1100000000000000L; //树突长度 (14个0)

    public static int GENE_NUMBERS = 16; //目前有多少条基因    
}
