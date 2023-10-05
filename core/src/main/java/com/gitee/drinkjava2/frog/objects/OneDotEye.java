package com.gitee.drinkjava2.frog.objects;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.brain.Genes;
import com.gitee.drinkjava2.frog.objects.EnvObject.DefaultEnvObject;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * 
 * OneDotEye的作用只有一个，就是定期在视网膜细胞上激活一个点，告知食物存在
 * 当前版本中Eye还没用到，目前只测试模式识别最简单的情况，即只有一个感光细胞的情况下，如何形成看到食物时咬下这个条件反射
 *  
 */
public class OneDotEye extends DefaultEnvObject {
    private static int[] food = new int[Env.STEPS_PER_ROUND];
    static {
        //食物只会出现在15为周期但不固定的时间点上，以防止细胞进化出周期进入鞍点, 食物用数字表示，0为不存在 
        for (int i = 15; i < Env.STEPS_PER_ROUND - 15; i += 15)
            food[i + RandomUtils.nextNegOrPosInt(5)] = 1;
    }

    public static boolean seeFood(int step) {
        return food[step] > 0;
    }

    public static boolean foodSweet(int step) {//食物是甜还是苦？ 苦甜不给视觉信号
        //将step分为4段，1、3段为甜，2、4段为苦，这样看到食物就咬下是行不通的，要想满分必须根据苦甜动态调整权重
        if (food[step] == 0)
            return false;
        int q = Env.STEPS_PER_ROUND / 2;
        return step<q;
//        if (step < q || (step > 2 * q && step < 3 * q))
//            return true;
//        return false;
    }

    @Override
    public void active(int screen, int step) {
        if (seeFood(step)) { //如看到食物, 激活所有青蛙的视网膜
            for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++) {
                Frog f = Env.frogs.get(i);
                f.setEng1(Genes.EYE_POS);
            }
        }
    }
}
