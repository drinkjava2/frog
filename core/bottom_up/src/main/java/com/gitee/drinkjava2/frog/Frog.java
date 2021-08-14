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
package com.gitee.drinkjava2.frog;

import java.awt.Graphics;
import java.awt.Image;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import com.gitee.drinkjava2.frog.brain.Cell;
import com.gitee.drinkjava2.frog.egg.Egg;
import com.gitee.drinkjava2.frog.objects.Material;

/**
 * Frog's name is Sam, Frog is made up of cells, cells are created by gene tree, gene tree is saved in egg.
 * 
 * 青蛙由细胞组成，细胞的生成由基因树决定，基因树保存在蛋里，蛋最早为空，第一个基因树由随机数产生
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Frog {// 这个项目里大量用到全局public变量而不是私有变量+getter/setter，主要是为了编程方便和简洁，大项目不提倡
    /** brain cells */
    public Cell[][][] cells;// 一开始不要初始化，只在调用getOrCreateCell方法时才初始化相关维以节约内存

    public int x; // frog在Env中的x坐标
    public int y; // frog在Env中的y坐标
    public long energy = 10000000; // 青蛙的能量为0则死掉
    public boolean alive = true; // 设为false表示青蛙死掉了，将不参与计算和显示，以节省时间
    public int ateFood = 0;

    static Image frogImg;
    static {
        try {
            frogImg = ImageIO.read(new FileInputStream(Application.CLASSPATH + "frog.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Frog(int x, int y, Egg egg) {// x, y 是虑拟环境的坐标
        this.x = x;
        this.y = y;
    }

    public void initFrog() {// 仅在测试之前调用这个方法初始化frog以节约内存，测试完成后要清空units释放内存
        try {
            cells = new Cell[Env.FROG_BRAIN_XSIZE][][]; // 为了节约内存，先只初始化三维数组的x维，另两维用到时再分配
        } catch (OutOfMemoryError e) {
            System.out.println("OutOfMemoryError found for frog, force it die.");
            this.alive = false;
            return;
        }
    }

    public boolean active(Env v) {// 这个active方法在每一步循环都会被调用，是脑思考的最小帧
        // 如果能量小于0、出界、与非食物的点重合则判死
        if (!alive || energy < 0 || Env.outsideEnv(x, y) || Env.bricks[x][y] >= Material.KILLFROG) {
            energy -= 100; // 死掉的青蛙也要消耗能量，确保淘汰出局
            alive = false;
            return false;
        }
        energy -= 20;

        // 这里是最关键的脑细胞主循环，调用每个细胞的act方法
        for (int i = 0; i < Env.FROG_BRAIN_XSIZE; i++) {
            Env.checkIfPause(this);
            if (cells[i] != null)
                for (int j = 0; j < Env.FROG_BRAIN_YSIZE; j++)
                    if (cells[i][j] != null)
                        for (int k = 0; k < Env.FROG_BRAIN_ZSIZE; k++) {
                            Cell cell = cells[i][j][k];
                            if (cell != null)
                                cell.act();
                        }
        }
        return alive;
    }

    public void show(Graphics g) {// 显示青蛙的图象
        if (!alive)
            return;
        g.drawImage(frogImg, x - 8, y - 8, 16, 16, null);
    }

    /** Check if cell exist */
    public Cell getCell(int x, int y, int z) {// 返回指定脑坐标的cell ，如果不存在，返回null
        if (cells[x] == null || cells[x][y] == null)
            return null;
        return cells[x][y][z];
    }

    /** Get a cell in position (x,y,z), if not exist, create a new one */
    public Cell getOrCreateCell(int x, int y, int z) {// 获取指定坐标的Cell，如果为空，则在指定位置新建Cell
        if (outBrainBound(x, y, z))
            return null;
        if (cells[x] == null)
            cells[x] = new Cell[Env.FROG_BRAIN_YSIZE][];
        if (cells[x][y] == null)
            cells[x][y] = new Cell[Env.FROG_BRAIN_ZSIZE];
        Cell cell = cells[x][y][z];
        if (cell == null) {
            cell = new Cell(x, y, z);
            cells[x][y][z] = cell;
        }
        return cell;
    }
 

    /** Check if x,y,z out of frog's brain bound */
    public static boolean outBrainBound(int x, int y, int z) {// 检查指定坐标是否超出frog脑空间界限
        return x < 0 || x >= Env.FROG_BRAIN_XSIZE || y < 0 || y >= Env.FROG_BRAIN_YSIZE || z < 0 || z >= Env.FROG_BRAIN_ZSIZE;
    }

}
