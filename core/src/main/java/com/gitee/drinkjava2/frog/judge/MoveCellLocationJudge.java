package com.gitee.drinkjava2.frog.judge;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.brain.Cells;

/**
 * MoveCellLocationJudge determine move cells can only be on bottom layer of brain 
 * 
 * 运动细胞只允许出现在脑的最底层，否则扣分 
 */
public class MoveCellLocationJudge {//NOSONAR

    public static void judge(Animal animal) {////检查animal的脑细胞分布和参数是否符合要求并加减分
        for (int x = 0; x < Env.BRAIN_CUBE_SIZE; x++)
            for (int y = 0; y < Env.BRAIN_CUBE_SIZE; y++)
                for (int z = 0; z < Env.BRAIN_CUBE_SIZE; z++) {
                    if (z > 0) {
                        if (

                        ((animal.cells[x][y][z] & Cells.MOVE_UP) != 0)

                                || ((animal.cells[x][y][z] & Cells.MOVE_DOWN) != 0)

                                || ((animal.cells[x][y][z] & Cells.MOVE_LEFT) != 0)

                        ) {
                            animal.penaltyAAAA();
                        } else {
                            animal.awardAAAA();
                        }
                    }
                }
    }

}
