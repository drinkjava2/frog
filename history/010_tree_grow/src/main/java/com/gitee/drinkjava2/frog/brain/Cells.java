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
 * Cells代表不同的脑细胞参数，对应每个参数，细胞有不同的处理光子的行为
 * 
 * @author Yong Zhu
 * @since 10.0
 */
@SuppressWarnings("all")
public class Cells {
    public static long[] GENES = new long[64];

    public static int GENE_NUMBERS = 0; //目前有多少条基因，每个脑细胞用是一个long来存储，所以最多允许64条基因，每个基因控制一个细胞的参数
    private static long gene = 1L;

    public static final long TREE_CELL = nextMask(); //细胞如激活，青蛙向上运动
    
    //public static final long EYE = nextMask(); //光线感觉细胞，即视网膜
//    public static final long FELL_HAPPY = nextMask(); //快乐感觉细胞，通常吃食后激活
//    public static final long FELL_PAIN = nextMask(); //痛苦感觉细胞，受伤害后激活
//
//    public static final long MOVE_UP = nextMask(); //细胞如激活，青蛙向上运动
//    public static final long MOVE_DOWN = nextMask();//细胞如激活，青蛙向下运动
//    public static final long MOVE_LEFT = nextMask(); //细胞如激活，青蛙向左运动
//    public static final long MOVE_RIGHT = nextMask(); //细胞如激活，青蛙向右运动
//    public static final long MOVE_ANY = MOVE_UP | MOVE_DOWN | MOVE_LEFT | MOVE_RIGHT; //任意移动，是上面四个bit位的合并
   
//
//    public static final long PHOTON_DELETE = nextMask(); // 删除光子
//    public static final long PHOTON_ABSORB = nextMask(); // 删除并吸收光子能量
//    public static final long PHOTON_FIX = nextMask(); //固定光子，使光子不能移动
//    public static final long PHOTON_ENHENCE = nextMask(); // 提高光子能量
//    public static final long PHOTON_WEAKEN = nextMask(); //减弱光子能量
//    public static final long PHOTON_SEND = nextMask(); //如细胞有能量，发送光子
//    public static final long PHOTON_SEND_NEG = nextMask(); //如细胞有能量，发送负能量光子
//
//    public static final long PHOTON_LIMIT_UP = nextMask(); //光子只能向上扇面发送
//    public static final long PHOTON_LIMIT_DOWN = nextMask(); //光子只能向下扇面发送
//    public static final long PHOTON_LIMIT_LEFT = nextMask(); //光子只能向左扇面发送
//    public static final long PHOTON_LIMIT_RIGHT = nextMask(); //光子只能向右扇面发送
//    public static final long PHOTON_LIMIT_FRONT = nextMask(); //光子只能向前扇面发送
//    public static final long PHOTON_LIMIT_BACK = nextMask(); //光子只能向后扇面发送

    private static long nextMask() {// 每次将Code左移1位
        long result = gene;
        if (result < 0)
            throw new IllegalArgumentException("Mask out of maximum long integer range");
        GENES[GENE_NUMBERS++] = gene;
        gene = gene << 1; //这个gene占用long的一位，将来判断一个细胞是否包含此基因只要与它做“与”运算
        return result;
    }
     
}
