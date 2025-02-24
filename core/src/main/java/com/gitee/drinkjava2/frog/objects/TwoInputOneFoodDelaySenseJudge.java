package com.gitee.drinkjava2.frog.objects;

import java.awt.Color;
import java.awt.Graphics;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;

/**
 * 
 * TwoInputOneFoodDelaySenseJudge继承于TwoInputOneFoodFullSenseJudge
 * 区别在于甜苦味会延迟咬动作几个时间单位产生，强迫它必须根据之前形成的条件反射来预判决定咬不咬。开发中，未完成
 * 
 */
public class TwoInputOneFoodDelaySenseJudge extends TwoInputOneFoodFullSenseJudge {

    @Override
    public void active(int screen, int step, Graphics g) {// 重写active方法，改成甜苦味会延迟咬动作几个时间单位产生
        Frog f;
        int foodCode = food[step];
        boolean seeFood1 = (foodCode & 1) > 0;
        boolean seeFood2 = (foodCode & 0b10) > 0;
        boolean isSweet = (foodCode == sweetFoodCode); //甜味只能有一种情况
        boolean isBitter = !isSweet && (seeFood1 || seeFood2); //食物存在但又不是甜的，那就是个苦食物

        for (int i = 0; i < Env.FROG_PER_SCREEN; i++) {
            f = Env.frogs.get(Env.current_screen * Env.FROG_PER_SCREEN + i);
            f.see1 = seeFood1;
            f.see2 = seeFood2;
            if (step < Env.STEPS_PER_ROUND - 2) { // 提前看到食物正在靠近
                f.seeFoodComing = ((food[step + 1] > 0) || (food[step + 2] > 0));
            }

            if (f.bite) { // 咬下了不能立刻感到味觉，而要过段时间，所以这里我们代替大自然先把当前味觉暂存到sweet/bitter缓存的后面位置上
                if (isSweet) { //甜食
                    f.sweetNerveDelay[step + f.nerveDelay] = true;
                    f.awardAAA3(); //因为甜食数量少比苦食少，为了鼓励多咬，甜食加分比苦食扣分多
                    f.ateFood++; //ateFood, ateWrong, ateMiss都是统计用的，不参与逻辑
                } else if (isBitter) { // 苦食
                    f.bitterNerveDelay[step + f.nerveDelay] = true;
                    f.penaltyAAA(); //
                    f.ateWrong++;
                } else {//不甜也不苦说明咬到空气
                    f.sweetNerveDelay[step + f.nerveDelay] = false;
                    f.bitterNerveDelay[step + f.nerveDelay] = false;
                    f.penaltyA();//咬空气也要消耗能量，少扣点分
                    f.ateWrong++;
                }
            } else { //如果没有咬，当然味觉也没有，也不用扣分，但是大自然会把躺平的青蛙淘汰，因为躺平的青蛙吃的少
                f.sweetNerveDelay[step + f.nerveDelay] = false;
                f.bitterNerveDelay[step + f.nerveDelay] = false;
                if (isSweet) { //如果没有咬但是食物是甜的，说明错过了一个甜食
                    f.ateMiss++;
                }
            }

            f.sweet = f.sweetNerveDelay[step]; //当前青蛙感到的味觉实际上是前几个时间周期产生的味觉，也就是说食物被咬下之后好久才尝到味道
            f.bitter = f.bitterNerveDelay[step];

            // 开始显示状态线条
            int x;
            int y;
            if (i == 0) {// 虚拟环境只显示第一个青蛙的红绿线
                x = step % (Env.ENV_WIDTH / n);// 用红色标记当前走到哪一步食物位置
                y = step / (Env.ENV_WIDTH / n);
                g.fillRect(x * n, y * n + n / 2, n, 2);
                if (f.bite) { // 如果咬下，显示黑色
                    g.setColor(Color.MAGENTA);
                    g.fillRect(x * n, y * n, n, 3);
                }
            }
        }

    }

}
