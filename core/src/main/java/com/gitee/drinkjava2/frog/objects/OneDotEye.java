package com.gitee.drinkjava2.frog.objects;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.objects.EnvObject.DefaultEnvObject;

/**
 * DotEye的作用只有一个，就是定期在视网膜细胞上激活一个点，告知食物存在
 */
public class OneDotEye extends DefaultEnvObject {
    public static int code = 0;

    @Override
    public void active(int screen, int step) {
        code++;
        if (code % 20 == 0) { //每隔20步在所有青蛙的视网膜上画一个图案 ，单个点调试时设为每20步激活时就是食物 
            for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++)
                Env.frogs.get(i).energys[0][0][0] = 5f;
        }
    }

}
