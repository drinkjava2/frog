package com.gitee.drinkjava2.frog.AwardPenalty;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;

/**
 * execute method be called after animal's initAnimal method 
 * 
 *  这个类的awardPenalty方法在动物的初始化后被调用，根据脑细胞的三维结构形状来对动物进行奖罚，即加减它的能量值，这是一个临时类，只是用来检验细胞三维成形功能，以后可能改名或删除
 */
public class BrainShapeAwardPenalty {
    private static boolean[][][] brainShape = new boolean[Env.BRAIN_XSIZE][Env.BRAIN_YSIZE][Env.BRAIN_ZSIZE];

    public static void execute(Animal animal) {//检查animal的脑细胞是否位于brainShape的范围内
        //TODO 检查animal的脑细胞是否位于brainShape的范围内
    }

}
