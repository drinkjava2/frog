package com.gitee.drinkjava2.frog.objects;

import java.awt.Color;
import java.awt.Graphics;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * 
 * OneInputJudge 用来模拟只有一个信号输入时的情况下，青蛙是否随信号点亮咬下去, 随信号消失不咬
 * 这是最简单的模式识别，即1时输出1， 0时输出0
 *  
 */
public class OneInputJudge implements EnvObject {
    private int n = 16; //n是小方块边长
    private static int[] food = new int[Env.STEPS_PER_ROUND + 10];
    private static int totalFood;

    private static void resetFood() {
        int step = 0;
        int x = 2;
        while (step < (Env.STEPS_PER_ROUND)) {
            int firstFood = RandomUtils.nextInt(5); //以10为一组，随机安排5个食物
            for (int i = 0; i < 10; i++)
                if (i < firstFood || i > firstFood + x)
                    food[step + i] = 0;
                else
                    food[step + i] = 1;
            step += 10;
            if (x == 2)
                x = 6;
            else
                x = 2;
        }
        for (int i : food)
            if (i == 1)
                totalFood++;
    }

    @Override
    public void build(Graphics g) { //build在每屏测试前调用一次，这里用随机数准备好食物出现和消失的顺序为测试作准备
        if (totalFood == 0) {
            resetFood();
            System.out.println("totalFood=" + totalFood);
        }
        resetFood();

        for (int i = 0; i < Env.STEPS_PER_ROUND; i++) { //画出当前食物分布图
            int x = i % (Env.ENV_WIDTH / n);
            int y = i / (Env.ENV_WIDTH / n);
            if (food[i] == 1)
                g.fillRect(x * n, y * n, n, n);
            else
                g.drawRect(x * n, y * n, n, n);
            x++;
        }
    }

    @Override
    public void destory(Graphics g) {
    }

    /*
     * active方法中做以下事情：
     * 1.如果有食物就点亮视细胞
     * 2.如果视细胞点亮且咬下就奖，如果视细胞没点亮且咬下就罚
     * 3.在左边Env显示区画出当前food的进度条
     */
    @Override
    public void active(int screen, int step, Graphics g) {
        Frog f;
        int x, y;
        boolean hasFood = food[step] == 1;
        for (int i = 0; i < Env.FROG_PER_SCREEN; i++) {
            f = Env.frogs.get(Env.current_screen * Env.FROG_PER_SCREEN + i);
            f.see1=hasFood;  
            if (f.bite) { //如果咬下
                if (f.see1) {
                    f.awardAAA(); //咬到了有奖
                    f.ateFood++;
                    f.sweet=true;
                    f.bitter=false;
                    g.setColor(Color.GREEN);
                } else {
                    f.ateWrong++;
                    f.penaltyAAA(); //咬空了扣分
                    f.sweet=false;
                    f.bitter=true;//咬错了，能感觉到苦味，这是大自然进化出来的功能，给青蛙一个知道自己咬错的信号
                    g.setColor(Color.RED);
                }
            } else { //如果都没有咬，关闭甜和苦味感觉
                f.sweet=false;
                f.bitter=false;
                if (hasFood) {
                    g.setColor(Color.RED);
                    f.ateMiss++;
                }
                else
                    g.setColor(Color.GREEN);
            }
            if (i == 0) {
                x = step % (Env.ENV_WIDTH / n);//用红色标记当前走到哪一步食物位置
                y = step / (Env.ENV_WIDTH / n);
                g.fillRect(x * n, y * n + n / 2, n, 2);
            }
        }

    }

}
