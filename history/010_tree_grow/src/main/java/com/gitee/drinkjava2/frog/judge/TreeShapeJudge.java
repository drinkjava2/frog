package com.gitee.drinkjava2.frog.judge;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;

/**
 * TreeShapeJudge to create a tree  
 */
public class TreeShapeJudge {//NOSONAR

    private static boolean hasCell(long[][][] cells, int x, int y, int z) { //检查指定位置是否有TREE_CELL
        if (Animal.outBrainRange(x, y, z))
            return false;
        return (cells[x][y][z] & 1) > 0;
    }

    //@formatter:off
    private static boolean hasCellAround(long[][][] cells, int x, int y, int z) {//检查四周是否有TREE_CELL
        if (hasCell(cells, x + 1, y, z))return true;
        if (hasCell(cells, x - 1, y, z))return true;
        if (hasCell(cells, x + 1, y+1, z))return true;
        if (hasCell(cells, x - 1, y+1, z))return true;
        if (hasCell(cells, x + 1, y-1, z))return true;
        if (hasCell(cells, x - 1, y-1, z))return true;
        if (hasCell(cells, x, y+1, z))return true;
        if (hasCell(cells, x, y-1, z))return true;
        return false;
    }
    //@formatter:on

    public static void judge(Animal animal) {
        long[][][] cells = animal.cells;
        for (int x = 0; x < Env.BRAIN_CUBE_SIZE; x++)
            for (int y = 0; y < Env.BRAIN_CUBE_SIZE; y++)
                for (int z = 0; z <= Env.BRAIN_CUBE_SIZE - 2; z++) {
                    long cell = cells[x][y][z];
                    if ((cell & 1) > 0) {
                        if ((z == 0 && x == Env.BRAIN_XSIZE / 2 && y == Env.BRAIN_YSIZE / 2) //如果在底部中心
                                || //或
                                (!hasCell(cells, x, y, z - 1) // 正下方没有cell
                                        && !hasCellAround(cells, x, y, z) //且周围没有cell   
                                        && hasCellAround(cells, x, y, z - 1) //且下方周围有cell
                                ))
                            animal.awardAA();
                        else
                            animal.penaltyAA();
                    }
                }

    }

}
