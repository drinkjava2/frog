package com.gitee.drinkjava2.frog.judge;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;

/**
 * judge method be called after animal's initAnimal method 
 * 
 *  这个类的judge方法在动物的初始化后被调用，根据脑细胞群的三维结构形状来对动物进行奖罚，即加减它的能量值，这是一个临时类，只是用来检验细胞三维成形功能，以后可能改名或删除
 */
public class BrainColorJudge {

    public static void judge(Animal animal) {//检查animal的脑细胞是否位于brainShape的范围内 
        for (int x = 0; x < Env.BRAIN_CUBE_SIZE; x++) {
            for (int y = 0; y < Env.BRAIN_CUBE_SIZE; y++) {
                for (int z = 0; z < Env.BRAIN_CUBE_SIZE; z++) {
                    if ((animal.cells[x][y][z] & 2) != 0)
                        if (x >= 1 && x <= 3) {
                            animal.award500();
                        } else {
                            animal.penalty1();
                        }
                    if ((animal.cells[x][y][z] & 4) != 0)
                        if (x >= 6 && x <= 8) {
                            animal.award50();
                        } else {
                            animal.penalty1();
                        }
                    if ((animal.cells[x][y][z] & 8) != 0)
                        if (x >= 11 && x <= 13) {
                            animal.award50();
                        } else {
                            animal.penalty1();
                        }
                }
            }
        }
    }

}
