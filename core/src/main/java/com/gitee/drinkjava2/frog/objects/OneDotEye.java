package com.gitee.drinkjava2.frog.objects;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.brain.Consts;
import com.gitee.drinkjava2.frog.objects.EnvObject.DefaultEnvObject;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * 
 * DotEye的作用只有一个，就是定期在视网膜细胞上激活一个点，告知食物存在
 * 当前版本中Eye还没用到，目前只测试模式识别最简单的情况，即只有一个感光细胞的情况下，如何形成看到食物时咬下这个条件反射
 *  
 */
public class OneDotEye extends DefaultEnvObject {
    public static boolean seeFood;
    private static int[] foodStep = new int[40];
    static { 
        for (int i = 0; i < 40; i++) {
            foodStep[i] = i * 20 + RandomUtils.nextNegOrPosInt(9); //食物只会出现在20为周期但不固定的时间点上，不用纯随机数是因为不好比较吃食率，不用纯周期是因为要防止细胞进化出周期进入鞍点
        }
    }

    private static boolean foodExist(int step) {  
        for (int i : foodStep)
            if (step == i)
                return true;
        return false;
    }

    @Override
    public void active(int screen, int step) {
        seeFood = false;
        if (foodExist(step)) { //这里模拟食物随机出现，被青蛙看到, 不用纯随机数是因为
            seeFood = true;
            for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++) {
                Frog f = Env.frogs.get(i);
                f.energys[0][0][0] = f.consts[Consts.ADD_EYE];
            }
        }
    }

}
