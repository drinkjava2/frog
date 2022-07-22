package com.gitee.drinkjava2.frog.judge;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;

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
                    long cell = animal.cells[x][y][z];
                    if (z >= 1) {
                        if ((cell & 1) > 0) //注意四个条件要分别判断和扣分，不能合并放在同一个if条件里，否则互相干扰，进化不出结果
                            animal.penaltyAAAA();
                        if ((cell & 2) > 0)
                            animal.penaltyAAAA();
                        if ((cell & 4) > 0)
                            animal.penaltyAAAA();
                        if ((cell & 8) > 0)
                            animal.penaltyAAAA();
                    }
                    if (z == 0) {
                        if ((cell & 1) > 0) //注意四个条件要分别判断和扣分，不能合并放在同一个if条件里，否则互相干扰，进化不出结果
                            animal.awardAAAA();
                        if ((cell & 2) > 0)
                            animal.awardAAAA();
                        if ((cell & 4) > 0)
                            animal.awardAAAA();
                        if ((cell & 8) > 0)
                            animal.awardAAAA();
                    }
                }

    }

}
