package com.gitee.drinkjava2.frog.objects;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.brain.Consts;
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
        //食物只会出现在15为周期但不固定的时间点上，以防止细胞进化出周期进入鞍点, 食物用数字表示，0为不存在，1为甜，2为苦
        for (int i = 15; i < Env.STEPS_PER_ROUND - 15; i += 15)
            food[i + RandomUtils.nextNegOrPosInt(5)] = 1 + RandomUtils.nextInt(2);
    }

    public static boolean foodExist(int step) {
        return food[step] > 0;
    }

    public static boolean foodSweet(int step) {
        return food[step] == 1;
    }

    @Override
    public void active(int screen, int step) {
        if (foodExist(step)) { //如食物存在, 激活所有青蛙的视网膜
            for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++) {
                Frog f = Env.frogs.get(i);
                f.energys[0][0][0] = f.consts[Consts.ADD_EYE];
            }
        }
    }
}
