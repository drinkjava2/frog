package com.gitee.drinkjava2.frog.judge;

/**
 * Fower2DJudge judge flower shape on one x layer
 * 
 * Fower2DJudge与RainBowFish2DJudge类似，只是指定层和图像不同
 */
public class Flower2DJudge extends D2Judge {
    public static Flower2DJudge instance = new Flower2DJudge(); //单例实例

    public Flower2DJudge() {
        putPixiel(6, "🌷");
    }

}
