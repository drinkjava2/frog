package com.gitee.drinkjava2.frog.objects;

import java.awt.Color;
import java.awt.Graphics;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.brain.Genes;
import com.gitee.drinkjava2.frog.objects.EnvObject.DefaultEnvObject;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * 
 * TwoInputJudge 用来模拟：当有两个视细胞、一个甜细胞、一个咬细胞这种情况下， 判断青蛙能否根据甜味细胞的训练，在下次相同视信号出现时提前咬下去。
 * 因为只有两个视细胞，即青蛙要实现与、或、异或这三种模式识别, 目前三种情况只随机每轮设一个甜值，青蛙咬下三种情况中有甜值的code时才有奖励
 *  
 */
public class TwoInputJudge extends DefaultEnvObject {
    private int n = 16; //n是表示食物的小方块边长，食物code由多个位组成时，小方块显示它的二进制条形码 
    private static int group=15; //以group为一组，随机安排一半为食物
    private static int[] food = new int[Env.STEPS_PER_ROUND + group];
    private static int sweetFoodCode; //甜食code，食物code有三种，但只有一种与甜食code相同的食物可食。
    private static int totalSweetFood=0; 

    private static void resetFood() {
        sweetFoodCode = 1 + RandomUtils.nextInt(3); // 甜食code每一轮测试都不一样，强迫青蛙每一轮都要根据苦和甜味快速适应，从三种食物中找出正确的那一种食物,
                                                    // 下一步是每一轮测试中途都有可能改变甜食code，让青蛙活着时就就能找出食物(即记忆功能)
        System.out.println("sweetFoodCode="+sweetFoodCode); //debug
        int step = 0;
        int x = 2; 
        while (step < (Env.STEPS_PER_ROUND)) {
            int firstFood = RandomUtils.nextInt(group/2); //以group为一组，随机安排一半为食物
            int foodCode = 1+RandomUtils.nextInt(3); //食物有1,2,3四种图案，分别对应两个细胞的01,10,11四种情况
            for (int i = 0; i < group; i++)
                if (i < firstFood || i > firstFood + x)
                    food[step + i] = 0;
                else
                    food[step + i] = foodCode;
            step += group;
            if (x == 2)
                x = group/2+1;
            else
                x = 2;
        }
        for (int f : food)
            if (f==sweetFoodCode)
                totalSweetFood++;
    }

    @Override
    public void build(Graphics g) { //build在每屏测试前调用一次，这里用随机数准备好食物出现和消失的顺序为测试作准备
        if (totalSweetFood == 0) {
            resetFood();
            System.out.println("totalSweetFood=" + totalSweetFood);
        }
        resetFood();

        for (int i = 0; i < Env.STEPS_PER_ROUND; i++) { //画出当前食物分布图
            int x = i % (Env.ENV_WIDTH / n);
            int y = i / (Env.ENV_WIDTH / n);
            g.drawRect(x * n, y * n, n, n);
            int foodCode = food[i];
            boolean isSweet = (foodCode == sweetFoodCode);
            if (isSweet)
                g.setColor(Color.BLUE); //食物是甜的时，用蓝色表示，蓝莓?
            else
                g.setColor(Color.GRAY);
            if ((foodCode & 1) > 0)
                g.fillRect(x * n, y * n, n, n / 2);
            if ((foodCode & 2) > 0)
                g.fillRect(x * n, y * n + n / 2, n, n / 2);
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
        int foodCode = food[step];
        boolean seeFood1 = (foodCode & 1) > 0;
        boolean seeFood2 = (foodCode & 0b10) > 0;
        boolean isSweet = (foodCode == sweetFoodCode);
        for (int i = 0; i < Env.FROG_PER_SCREEN; i++) {
            f = Env.frogs.get(Env.current_screen * Env.FROG_PER_SCREEN + i);
            if (seeFood1)
                f.setEng(Genes.EYE1_POS, 1f);
            else
                f.setEng(Genes.EYE1_POS, 0f);

            if (seeFood2)
                f.setEng(Genes.EYE2_POS, 1f);
            else
                f.setEng(Genes.EYE2_POS, 0f);

            if (f.getEng(Genes.BITE_POS) > 0.8) {
                if (isSweet) {
                    f.awardA(); //咬到了有奖
                    f.ateFood++;
                    f.setEng(Genes.SWEET_POS, 1); //咬到能感觉到甜，这是大自然进化出来的功能，给青蛙一个知道自己咬对的信号
                    f.setEng(Genes.BITTER_POS, 0);
                    g.setColor(Color.GREEN);
                } else { //咬错了扣分
                    f.ateWrong++;
                    f.penaltyA();
                    f.setEng(Genes.SWEET_POS, 0);
                    f.setEng(Genes.BITTER_POS, 1); //咬错了，能感觉到苦味，这是大自然进化出来的功能，给青蛙一个知道自己咬错的信号
                    g.setColor(Color.RED);
                }
            } else { //如果没有咬
                f.setEng(Genes.SWEET_POS, 0); //关闭甜和苦味感觉
                f.setEng(Genes.BITTER_POS, 0);
                if (isSweet) { //如果没有咬但是食物是甜的，说明miss了一个甜食
                    g.setColor(Color.RED);
                    f.ateMiss++;
                } else
                    g.setColor(Color.GREEN); //如果没有咬且食物不甜，miss对了
            }
            if (i == 0) {
                x = step % (Env.ENV_WIDTH / n);//用红色标记当前走到哪一步食物位置
                y = step / (Env.ENV_WIDTH / n);
                g.fillRect(x * n, y * n + n / 2, n, 2);
            }
        }

    }

}
