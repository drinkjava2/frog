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
    public static int GENE_NUMBERS = 4; //目前有多少条基因，每个脑细胞用是一个long来存储，所以最多允许64条基因，每个基因控制一个细胞的参数
   
    //1 to 8: 轴突方向在x方向的角度，1表示0度, 2表示45度, 3为90 度, ......, 7为270度，以下类似
    //9 to 16: 轴突方向在y方向的角度
    //17 to 24:轴突在z方向的角度
    
    //25~28: 轴突长度为1,2,4,8，可以重复，即同时存在多个长度
    //29~32: 树突范围为1,2,4,可以重复
    //33: 1为正信号 1为抵制信号
    
    
}
