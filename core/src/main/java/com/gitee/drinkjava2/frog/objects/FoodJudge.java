package com.gitee.drinkjava2.frog.objects;

import java.awt.Color;
import java.awt.Graphics;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * 
 * XinputJudge用来代替以前的oneInputJudge/twoInputJudge/ThreeInputJudge..., 区别只是视觉像素点的多少，逻辑都相似所以可以合并
 *  
 */
public class FoodJudge implements EnvObject {
    int n = 20; //n是表示食物的小方块边长，食物code由多个位组成时，小方块显示它的二进制条形码 
    final int p = 2; //p表示食物由有几个视觉像素点
    final int bits = (int) Math.pow(2, p); //bits = 2 ^ p; 
    final int tasteDelay = 4; //tasteDelay表示从咬下到感到甜苦味之间的延迟 
    int groupSize = 15; //groupSize表示连续多少个食物
    int[] food = new int[Env.STEPS_PER_ROUND + groupSize]; //食物在时间上的分布用一个数组表示
    int sweetFoodCode; //p个像素点的所有组合中，只有一个组合表示可食，先不考虑多种食物可食， sweetFoodCode可为零，表示所有食物都是有毒的苦食
    int[] foodOrders = new int[] { 1, 10, 11 }; //视觉信号固定顺序

    public FoodJudge() {
    }

    public void resetFood() {
        int pos = 0;
        for (int i = 0; i < foodOrders.length; i++) {
            for (int j = 0; j < 50; j++) {
                if (pos >= Env.STEPS_PER_ROUND)
                    return;
                food[pos++] = foodOrders[i];
            }
            for (int j = 0; j < 2; j++) { //间隔
                if (pos >= Env.STEPS_PER_ROUND)
                    return;
                food[pos++] = 0;
            }
        }
    }

    @Override
    public void build() { //build在每屏测试前调用一次，这里用随机数准备好食物出现和消失的顺序为测试作准备
        Graphics g = Env.graph;
        resetFood();

        for (int i = 0; i < Env.STEPS_PER_ROUND; i++) { //画出当前食物分布图
            int x = i % (Env.ENV_WIDTH / n);
            int y = i / (Env.ENV_WIDTH / n);
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(x * n, y * n, n, n);
            int foodCode = food[i];
            boolean isSweet = (foodCode == sweetFoodCode);
            if (isSweet)
                g.setColor(Color.DARK_GRAY); //食物是甜的时，用深灰表示
            else
                g.setColor(Color.LIGHT_GRAY); //食物是苦时，用浅灰表示

            for (int j = 0; j < p; j++) {
                if ((foodCode & (1 << j)) > 0)
                    g.fillRect(x * n, y * n + n / p * j, n, n / p);
            }
        }
    }

    @Override
    public void destory() {
    }

    @Override
    public void active() { //改成甜苦味会延迟咬nerveDelay个时间单位产生, 如果设为0表示没有延迟,
        Graphics g = Env.graph;
        int step = Env.step;
        Frog f;
        int foodCode = food[step];
        boolean seeFood1 = (foodCode & 1) > 0;
        boolean seeFood2 = (foodCode & 0b10) > 0;
        boolean isSweet = sweetFoodCode > 0 && foodCode == sweetFoodCode; //甜味只能有一种情况
        boolean isBitter = !isSweet && (foodCode > 0); //食物存在但又不是甜的，那就是个苦食物

        for (int i = 0; i < Env.FROG_PER_SCREEN; i++) {
            f = Env.frogs.get(Env.current_screen * Env.FROG_PER_SCREEN + i);
            f.see1 = seeFood1;
            f.see2 = seeFood2;
            if (step < Env.STEPS_PER_ROUND - 2) { // 提前看到食物正在靠近
                f.seeFoodComing = ((food[step + 1] > 0) || (food[step + 2] > 0));
            }

            //下面的ateFood, ateWrong, ateMiss以及各种色条都是统计和调试用的，不参与逻辑
            //主线条中的绿表示作出正确咬动作， 红表示咬错或漏咬
            //主线条上方的副线条绿色表示尝到甜味，红色表示尝到苦味

            if (f.bite) {//如果咬下 
                if (isSweet) { //甜食
                    f.sweetNerveDelay[step + tasteDelay] = true; // 咬下了不能立刻感到味觉，而要过段时间，所以这里我们代替大自然先把当前味觉暂存到sweet/bitter缓存的后面位置上
                    f.bitterNerveDelay[step + tasteDelay] = false;
                    f.awardAAAA(); //因为甜食数量少比苦食少，为了鼓励多咬，甜食加分比苦食扣分多
                    f.ateFood++;
                    g.setColor(Color.GREEN); //咬到食物
                } else if (isBitter) { //苦食
                    f.sweetNerveDelay[step + tasteDelay] = false;
                    f.bitterNerveDelay[step + tasteDelay] = true; //同理，苦味缓存
                    f.penaltyAAA(); //
                    f.ateWrong++;
                    g.setColor(Color.RED);//咬到毒物
                } else {//不甜也不苦说明咬到空气
                    f.sweetNerveDelay[step + tasteDelay] = false;
                    f.bitterNerveDelay[step + tasteDelay] = false;
                    f.penaltyAAA();//咬空气也要消耗能量，扣点分
                    f.ateWrong++;
                    g.setColor(Color.RED); //咬到空气
                }
            } else { //如果没有咬，当然味觉也没有，也不用扣分，但是大自然会把躺平的青蛙淘汰，因为躺平的青蛙吃的少
                f.sweetNerveDelay[step + tasteDelay] = false;
                f.bitterNerveDelay[step + tasteDelay] = false;
                if (isSweet) { //如果没有咬但是食物是甜的，说明错过了一个甜食
                    f.ateMiss++;
                    g.setColor(Color.RED); //漏了食物
                } else
                    g.setColor(Color.DARK_GRAY); //漏对了，毒物或空气
            }

            f.sweet = f.sweetNerveDelay[step]; //当前青蛙感到的味觉实际上是前几个时间周期咬下时产生的味觉
            f.bitter = f.bitterNerveDelay[step];

            // 开始画出状态色条
            if (i == 0) {// 虚拟环境只显示第一个青蛙的色条
                int x = step % (Env.ENV_WIDTH / n);
                int y = step / (Env.ENV_WIDTH / n);
                g.fillRect(x * n, y * n + n / 2, n, 3);
                if (f.sweet) { //副线条显示味觉
                    g.setColor(Color.GREEN); //绿表示甜
                    g.fillRect(x * n, y * n + n / 2 - 4, n, 3);
                } else if (f.bitter) {
                    g.setColor(Color.RED); //苦用红表示
                    g.fillRect(x * n, y * n + n / 2 - 4, n, 3);
                }

            }
        }
    }

}
