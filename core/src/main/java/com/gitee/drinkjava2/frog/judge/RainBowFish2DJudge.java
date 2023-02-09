package com.gitee.drinkjava2.frog.judge;

/**
 * judge method be called in animal's initAnimal method 
 * 
 *  judge方法在动物的初始化后被调用，根据脑细胞群的2维结构和参数来对动物进行奖罚，即加减它的能量值，这是一个临时类，只是用来检验Tree4Util的2维成形功能,这个类以后会删除。
 *  以前有一个类似的RainBowFishJudge是三维的，现在这个是二维的。
 *  
 *  这个类的show方法在绘脑图时调用，在脑图里显示脑细胞群的2维形状和参数，用不同颜色直径的空心圆来表示不同参数，judge方法就像是一个模子，细胞长在这个模子里的有奖，否则扣分
 */
public class RainBowFish2DJudge extends D2Judge {
    public static RainBowFish2DJudge instance = new RainBowFish2DJudge(); //单例实例

    public RainBowFish2DJudge() {
        putPixiel(1, "🐟");
    }

}
