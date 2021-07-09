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
 * Cell is the smallest unit of brain  
 * Cell是脑的最小单元， cell的行为由它的器官类型决定
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Cell {
    public int x;
    public int y;
    public int z;

    public Cell(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void act() { //cell执行行动
    }

}
