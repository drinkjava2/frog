package com.gitee.drinkjava2.frog.judge;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.brain.Cells;

/**
 * judge method be called after animal's initAnimal method 
 * 
 *  这个类的judge方法在动物的初始化后被调用，根据脑细胞群的颜色参数对动物进行奖罚，即加减它的能量值，这是一个临时类，只是用来检验细胞多参数分裂或扩张成形功能，以后可能改名或删除
 */
public class BrainRainbowColorJudge {

    public static void judge(Animal animal) {//检查颜色与小鱼图像重合，且呈斑马纹色彩分布，是就加分，否则扣分
        int colorWidth = Env.BRAIN_CUBE_SIZE / Cells.GENE_NUMBERS; //每条彩带宽度
        for (int x = 0; x < Env.BRAIN_CUBE_SIZE; x++)
            for (int y = 0; y < Env.BRAIN_CUBE_SIZE; y++)
                for (int z = 0; z < Env.BRAIN_CUBE_SIZE; z++)
                    for (int i = 1; i <= Cells.GENE_NUMBERS; i++)
                        if ((animal.cells[x][y][z] & (1 << i)) != 0) //如果色彩存
                            if ((animal.cells[x][y][z] & 1) != 0 && x >= (i - 1) * colorWidth && x < (i * colorWidth))
                                animal.awardAA();
                            else
                                animal.penaltyA();
    }

}
