package com.gitee.drinkjava2.frog.objects;

import java.awt.Color;
import java.awt.Graphics;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.brain.Genes;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * 
 * OneInputJudge 用来模拟只有一个信号输入时的情况下，青蛙是否随信号点亮咬下去, 随信号消失不咬
 * 这是最简单的模式识别，即1时输出1， 0时输出0
 *  
 */
public class OneInputJudge implements EnvObject {
    private int n = 16; //n是小方块边长
    private static int[] food = new int[Env.STEPS_PER_ROUND];
    private static int totalFood;

    private static void resetFood() {     
        int step = 0;
        int x=2;
        while (step <= (Env.STEPS_PER_ROUND - 10)) {
            int firstFood = RandomUtils.nextInt(5); //以10为一组，随机安排5个食物
            for (int i = 0; i < 10; i++)
                if (i < firstFood || i > firstFood + x)
                    food[step + i] = 0;
                else
                    food[step + i] = 1;
            step += 10;
            if(x==2)
                x=6;
            else x=2;
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
        for (int i = 0; i < Env.FROG_PER_SCREEN; i++) {
            Frog f = Env.frogs.get(Env.current_screen * Env.FROG_PER_SCREEN + i);
            if (food[step] == 1)
                f.setEng(Genes.EYE1_POS, 1f);
            else
                f.setEng(Genes.EYE1_POS, 0f);

            if (f.getEng(Genes.BITE_POS) > 0.8) {
                if (f.getEng(Genes.EYE1_POS) > 0.8) {
                    f.awardA(); //咬到了有奖
                    f.ateFood++;
                    f.setEng(Genes.SWEET_POS, 1); //咬到能感觉到甜，这是大自然进化出来的功能，给青蛙一个知道自己咬对的信号
                    f.setEng(Genes.BITTER_POS, 0);
                } else {
                    f.ateWrong++;
                    f.penaltyA(); //咬空了扣分
                    f.setEng(Genes.SWEET_POS, 0);
                    f.setEng(Genes.BITTER_POS, 1); //咬错了，能感觉到苦味，这是大自然进化出来的功能，给青蛙一个知道自己咬错的信号
                }
            } else { //如果都没有咬，关闭甜和苦味感觉
                f.setEng(Genes.SWEET_POS, 0);
                f.setEng(Genes.BITTER_POS, 0);
            }
        }

        int x = step % (Env.ENV_WIDTH / n);//用红色标记当前走到哪一步食物位置
        int y = step / (Env.ENV_WIDTH / n);
        g.setColor(Color.RED);
        g.fillRect(x * n, y * n + n / 2, n, 2);
    }

}
