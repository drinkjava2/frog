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
 * 用来模拟：当有两个视细胞、一个甜细胞、一个咬细胞这种情况下， 判断青蛙能否根据甜味细胞的训练，在下次相同视信号出现时决定咬还是不咬。
 * 有两个视细胞输入，有4种排列组合，去除全为0的，即青蛙要实现与、或、异或这三种模式识别, 每轮测试随机定一种或两种模式对应可食甜食，其它情况对应不可食苦食，详见下面组合
 * 
 * 
 * TwoInputOneFoodFullSenseJudge: 两点输入，4种排列组合中有一种是食物，青蛙全过程能感到甜苦味，经实测约2000轮后青蛙会进化出先尝一尝再决定咬还是不咬，利用味觉绕过了图像的模式识别，所以要改进测试条件，取消它后半段的味觉，让它只根据图像和记忆来决定咬不咬
 * TwoInputOneFoodHalfSenseJudge: 两点输入，4种排列组合中有一种是食物，青蛙前半程能感到甜苦味，后半程屏蔽青蛙的味觉，强迫它根据惯性来决定咬不咬。  （开发中，未完成)
 * TwoInputTwoFoodFullSenseJudge: 两点输入，4种排列组合中有两种是食物，青蛙全过程能感到甜苦味，(开发中，未完成)
 * TwoInputTwoFoodHalfSenseJudge: 两点输入，4种排列组合中有两种是食物，青蛙前半程能感到甜苦味，后半程屏蔽青蛙的味觉，强迫它根据惯性来决定咬不咬。 （开发中，未完成)
 *  
 */
public class TwoInputTwoFoodHalfSenseJudge extends DefaultEnvObject { //注：开发中，未完成
    private int n = 10; //n是表示食物的小方块边长，食物code由多个位组成时，小方块显示它的二进制条形码 
    private static int group = 15; //以group为一组，随机安排一半为食物
    private static int[] food = new int[Env.STEPS_PER_ROUND + group];
    private static int bitterFoodCode; //苦食code，食物code有三种，但有一种食物是苦的不可食，另两种是甜的可食
    private static int totalSweetFood = 0;

    private static void resetFood() {
        bitterFoodCode = 1 + RandomUtils.nextInt(3); // 甜食code每一轮测试都不一样，强迫青蛙每一轮都要根据苦和甜味快速适应，从三种食物中找出正确的那一种食物,
                                                     // 下一步是每一轮测试中途都有可能改变甜食code，让青蛙活着时就就能找出食物(即记忆功能) 
        int step = 0;
        int x = 2;
        while (step < (Env.STEPS_PER_ROUND)) {
            int firstFood = RandomUtils.nextInt(group / 2); //以group为一组，随机安排一半为食物
            int foodCode = 1 + RandomUtils.nextInt(3); //食物有1,2,3四种图案，分别对应两个细胞的01,10,11四种情况
            for (int i = 0; i < group; i++)
                if (i < firstFood || i > firstFood + x)
                    food[step + i] = 0;
                else
                    food[step + i] = foodCode;
            step += group;
            if (x == 2)
                x = group / 2 + 1;
            else
                x = 2;
        }
        for (int f : food)
            if (f > 0 && f != bitterFoodCode)
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
            boolean isSweet = (foodCode > 0 && foodCode != bitterFoodCode);
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
    public void active(int screen, int step, Graphics g) {//这个方法不和脑细胞打交道，只和输入输出细胞（即Animal中的简单变量)打交道，不要和胞细胞搞混了
        Frog f;
        int x, y;
        int foodCode = food[step];
        boolean seeFood1 = (foodCode & 1) > 0;
        boolean seeFood2 = (foodCode & 0b10) > 0;
        boolean isSweet = (foodCode > 0 && foodCode != bitterFoodCode);
        for (int i = 0; i < Env.FROG_PER_SCREEN; i++) {
            f = Env.frogs.get(Env.current_screen * Env.FROG_PER_SCREEN + i);
            f.see1 = seeFood1;
            f.see2 = seeFood2;

            if (step < Env.STEPS_PER_ROUND - 2) { //提前看到食物正在靠近
                f.seeFoodComing = ((food[step + 1] > 0) || (food[step + 2] > 0));
            }

            if (f.bite) {
                if (isSweet) {
                    f.awardAAA(); //咬到了有奖
                    f.ateFood++;
                    if (step < Env.HALF_STEPS_PER_ROUND) {//前半段能尝到甜苦味，后半段不能，要凭借已经形成的条件反射来判断是不是可以咬
                        f.sweet = true; //咬对了，能感觉到甜味，这是大自然进化出来的功能，给青蛙一个知道自己咬对的信号
                        f.bitter = false;
                    }
                    g.setColor(Color.GREEN);
                    Genes.sweetEvent(f);//sweet事件发生，相当于脑内产生激素，导致脑内部最近活跃的细胞正权重增加
                } else { //咬到苦的或咬空了
                    f.ateWrong++;
                    g.setColor(Color.RED);
                    if (foodCode == 0) {//咬空了也要少扣一点分(因为消耗能量了) 
                        f.penaltyA();
                    } else { //咬到苦的了
                        if (step < Env.HALF_STEPS_PER_ROUND) {//前半段能尝到甜苦味，后半段不能，要凭借已经形成的条件反射来判断是不是可以咬
                            f.sweet = false;//关闭甜和苦味感觉
                            f.bitter = true; //咬错了，能感觉到苦味，这是大自然随机进化出来的感官功能，给青蛙一个知道自己咬错的信号
                        }
                        f.penaltyAAA2(); //咬到苦的扣分（因为苦的食物吃多了会毒死青蛙）, 为了防止青蛙进化成始终保持咬状态，或躺平一口也不咬，这里设计成苦味食物扣分为甜食奖励两倍
                                         //这里扣分只影响青蛙的生存率，不直接影响苦激素对权重的调节量，但是长期淘汰下来，自然调节大的被生存下来，通过这种方式用进化来找到成苦激素对对权重的合适调节量大小
                                         //目前青蛙会进化成每次遇到食物先尝一下来绕过模式识别的条件反射形成，这个问题要解决   
                        Genes.bitterEvent(f);//bitter事件发生，相当于脑内产生激素，导致脑内部最近活跃的细胞负权重变化
                    }
                }
            } else { //如果没有咬，就不扣分，因为不消耗能量
                f.sweet = false;//关闭甜和苦味感觉
                f.bitter = false;
                if (isSweet) { //如果没有咬但是食物是甜的，说明漏咬了一个甜食，但这里也不扣分，因为没有扣分的理由，但是因为漏咬了躺平了，所以它比其它青蛙吃的少，大自然会淘汰这个青蛙
                    g.setColor(Color.RED);
                    f.ateMiss++;
                } else
                    g.setColor(Color.GREEN); //如果没有咬且食物不甜，miss对了
            }
            if (i == 0) {//虚拟环境只显示第一个青蛙的红绿线
                x = step % (Env.ENV_WIDTH / n);//用红色标记当前走到哪一步食物位置
                y = step / (Env.ENV_WIDTH / n);
                g.fillRect(x * n, y * n + n / 2, n, 2);
            }
        }

    }

}
