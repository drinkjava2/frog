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

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;

/**
 * Cells3D is 3D array of cells  
 * 
 * Cells3D 是一个三维动态数组，把animal中的cells list用三维数组的方式动态存放，以方便快速定位三维空间中的细胞
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Cells3D {
    public int[][][] cells = new int[Env.BRAIN_XSIZE][][]; // 为了节约内存，先只初始化三维数组的x维，另两维用到时再分配

    public Cells3D() { 
    } 

    /** check if cell exist at position (x,y,z) */
    public boolean ifExistCell(Animal animal, int x, int y, int z) {// 返回指定脑坐标的cell ，如果不存在，返回null
        if (cells[x] == null || cells[x][y] == null)
            return false;
        return cells[x][y][z]>0; //arrayIndex为0时是空，为1时表示animal.cells[0];
    }
    
    /** Get a cell at position (x,y,z), if not exist, return null */
    public Cell getCell(Animal animal, int x, int y, int z) {// 返回指定脑坐标的cell ，如果不存在，返回null
        if (cells[x] == null || cells[x][y] == null)
            return null;
        int arrayIndex=cells[x][y][z]; //arrayIndex为0时是空，为1时表示animal.cells[0];
        if(arrayIndex<=0)
            return null;
        return animal.cells.get(arrayIndex-1);
    }
    
    /** put a cell at position (x,y,z) */
    public void putCell(Cell cell, int arrayIndex) {// 获取指定坐标的Cell，如果为空，则在指定位置新建Cell 
        if (Animal.outBrainRange(cell.x, cell.y, cell.z))
            return;
        if (cells[cell.x] == null)
            cells[cell.x] = new int[Env.BRAIN_YSIZE][];
        if (cells[cell.x][cell.y] == null)
            cells[cell.x][cell.y] = new int[Env.BRAIN_ZSIZE]; 
        cells[cell.x][cell.y][cell.z] = arrayIndex; 
    } 
    
}
