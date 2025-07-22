package com.gitee.drinkjava2.frog.objects;

import java.awt.Color;
import java.awt.Graphics;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;

/**
 * 
 * FoodJudge用来代替以前的oneInputJudge/twoInputJudge/ThreeInputJudge..., 区别只是视觉像素点的多少，逻辑都相似所以可以合并
 *  
 */
public class FoodJudge implements EnvObject {
    public static boolean foodBit1;
    public static boolean foodBit2;

    int n = 20; //n是表示食物的小方块边长，食物code由多个位组成时，小方块显示它的二进制条形码 
    final int p = 2; //p表示食物由有几个视觉像素点
    final int bits = (int) Math.pow(2, p); //bits = 2 ^ p; 
    final int tasteDelay = 4; //tasteDelay表示从咬下到感到甜苦味之间的延迟 
    public static int groupSize = Env.STEPS_PER_ROUND / 6 - 2; //groupSize表示连续多少个食物
    public static int[] food = new int[Env.STEPS_PER_ROUND + groupSize]; //食物在时间上的分布用一个数组表示
    public static boolean[] sweet = new boolean[Env.STEPS_PER_ROUND + groupSize]; //甜味训练信号在时间上的分布用一个数组表示，训练信号允许延迟几个时钟

    final int sweetFoodCode = 2; //p个像素点的所有组合中，只有一个组合表示可食，先不考虑多种食物可食， sweetFoodCode可为零，表示所有食物都是有毒的苦食
    int[] foodOrders = new int[] { 1, 2, 3 }; //视觉信号固定顺序
    int midPos = 0;

    public FoodJudge() {
    }

    public void resetFood() {
        int pos = 0;
        for (int k = 0; k < 2; k++) {
            for (int i = 0; i < foodOrders.length; i++) {//
                for (int j = 0; j < groupSize; j++) {
                    if (pos >= Env.STEPS_PER_ROUND)
                        return;
                    food[pos] = foodOrders[i];
                    if (pos < Env.STEPS_PER_ROUND / 2) //在前半段给出甜味训练信号 
                        sweet[pos] = foodOrders[i] == sweetFoodCode;
                    else
                        sweet[pos] = false;
                    pos++;
                }
                for (int j = 0; j < 2; j++) { //加点间隔分开每个信号，不影响逻辑
                    if (pos >= Env.STEPS_PER_ROUND)
                        return;
                    food[pos++] = 0;
                }
            }
            if (k == 0)
                midPos = pos;
        }

    }

    @Override
    public void build() { //build在每屏测试前调用一次，这里用随机数准备好食物出现和消失的顺序为测试作准备
        Graphics g = Env.graph;
        resetFood();

        for (int pos = 0; pos < Env.STEPS_PER_ROUND; pos++) { //画出当前食物分布图
            int x = pos % (Env.ENV_WIDTH / n);
            int y = pos / (Env.ENV_WIDTH / n);
            int foodCode = food[pos];
            boolean isSweet = (foodCode == sweetFoodCode);
            if (isSweet)
                g.setColor(Color.DARK_GRAY); //食物是甜的时，用深灰表示
            else
                g.setColor(Color.LIGHT_GRAY); //食物是苦时，用浅灰表示

            for (int j = 0; j < p; j++) { //画出用条形二进制码表示的食物code
                if ((foodCode & (1 << j)) > 0)
                    g.fillRect(x * n, y * n + n / p * j, n, n / p);
            }

            if (pos == midPos) { //画出中间点，中间点之后就没有训练信号了
                g.setColor(Color.RED);
                g.fillRect(x * n, y * n, 6, n);
            }

            if (sweet[pos]) { //画出甜味训练信号
                g.setColor(Color.GREEN);
                g.fillRect(x * n, y * n + n / 2, n, 2);
            }

            g.setColor(Color.BLACK);
            g.drawRect(x * n, y * n, n, n);

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
        foodBit1 = (foodCode & 1) > 0;
        foodBit2 = (foodCode & 0b10) > 0;
        boolean isFood = foodCode > 0; //食物
        boolean isSweet = foodCode == sweetFoodCode; //甜食
        boolean isBitter = isFood && !isSweet; //苦食

        for (int i = 0; i < Env.FROG_PER_SCREEN; i++) {
            f = Env.frogs.get(Env.current_screen * Env.FROG_PER_SCREEN + i);

            // 开始画出状态色条
            if (i == 0) {// 只显示第一个青蛙的色条，其它的跳过
                int x = step % (Env.ENV_WIDTH / n);
                int y = step / (Env.ENV_WIDTH / n);
                g.setColor(Color.BLUE);
                g.fillRect(x * n, y * n + n / 3, n, 3);
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
