package com.gitee.drinkjava2.frog.objects;

import java.awt.Color;
import java.awt.Graphics;

import com.gitee.drinkjava2.frog.Application;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.objects.EnvObject.DefaultEnvObject;

/**
 * 
 * FoodJudge用来代替以前的oneInputJudge/twoInputJudge/ThreeInputJudge..., 区别只是视觉像素点的多少，逻辑都相似所以可以合并
 *  
 */
public class FoodJudge extends DefaultEnvObject {
    int n = 40; //n是表示食物的小方块边长，食物code由多个位组成时，小方块显示它的二进制条形码 

    public static boolean foodBit0;
    public static boolean foodBit1;

    public static final int p = 2; //p表示食物由有几个像素点
    //ep由视细胞像素点多少决定，视细胞像素是食物像素点变焦得到，因为暂时还没有引入变焦和动眼参数，所以这个版本视细胞和食物细胞像素点数量相等
    //    public static final float ep = 1f / p * 2; //每个视细胞收到的能量，视细胞越多，每个视细胞上分到的能量就越少，所有视细胞上能量总和始终为1。也就是说眼睛在看复杂图像和简单图像时花的总能量一样多
    public static final int bits = (int) Math.pow(2, p); //bits = 2 ^ p; 

    //    public final int tasteDelay = 4; //tasteDelay表示从咬下到感到甜苦味之间的延迟
    public static int groupSpace = 6; //每组食物之间的空白间隔
    public static int groupSize = 10; //groupSize表示食物在时间上连续出现多少个时间步长    

    public static int[] food = new int[Env.STEPS_PER_ROUND]; //食物在时间上的分布用一个数组表示，先从固定顺序开始测试以方便调试，以后将改成随机出现

    int[][] foodOrders = {{1, 2, 3}, {1, 3, 2}, {2, 1, 3}, {2, 3, 1}, {3, 1, 2}, {3, 2, 1}};
    int foodGroup = 0;
    int[] foodOrder; //视觉信号固定顺序，每轮选用一组不同的视觉信号 

    public static int sweetFoodCode; //甜食 

    public FoodJudge() {
    }

    public void resetFood() {
        foodOrder = foodOrders[foodGroup];
        foodGroup = foodGroup == 5 ? 0 : ++foodGroup; //foodGroup在foodOrders中顺序选一组
        sweetFoodCode = sweetFoodCode == 3 ? 1 : ++sweetFoodCode; //甜食在1,2,3中顺序选一个
        int pos = 0;
        do {
            for (int i = 0; i < foodOrder.length; i++) {
                for (int j = 0; j < groupSize; j++) {
                    if (pos >= Env.STEPS_PER_ROUND)
                        return;
                    food[pos] = foodOrder[i];
                    pos++;
                }
                for (int j = 0; j < groupSpace; j++) { //加点间隔分开每个信号，不影响逻辑 
                    if (pos >= Env.STEPS_PER_ROUND)
                        return;
                    food[pos++] = 0;
                }
            }
        } while (true);
    }

    @Override
    public void build() { //build在每屏测试前调用一次，这里用随机数准备好食物出现和消失的顺序为测试作准备
        Graphics g = Env.graph;
        resetFood();

        for (int pos = 0; pos < Env.STEPS_PER_ROUND; pos++) { //画出当前食物分布图
            int x = pos % (Env.ENV_WIDTH / n);
            int y = pos / (Env.ENV_WIDTH / n);
            int foodCode = food[pos];
            if (foodCode == sweetFoodCode)
                g.setColor(Color.DARK_GRAY); //食物是甜的时，用深灰表示
            else
                g.setColor(Color.LIGHT_GRAY); //食物是苦时，用浅灰表示

            for (int j = 0; j < p; j++) { //画出用条形二进制码表示的食物code
                if ((foodCode & (1 << j)) > 0)
                    g.fillRect(x * n, y * n + n / p * j, n, n / p);
            }

            g.setColor(Color.BLACK);
            g.drawRect(x * n, y * n, n, n);

        }
    }

    public static boolean isSweetFood() {
        return food[Env.step] == sweetFoodCode;
    }

    public static boolean isBitter() {
        return food[Env.step] > 0 && food[Env.step] != sweetFoodCode;
    }

    @Override
    public void active() {
        Graphics g = Env.graph;
        int step = Env.step;
        Frog f;
        int foodCode = food[step];
        foodBit0 = (foodCode & 1) > 0; //foodBit0和foodBit1是代表食物的两个像素点
        foodBit1 = (foodCode & 0b10) > 0;

        for (int i = 0; i < Env.FROG_PER_SCREEN; i++) {
            f = Env.frogs.get(Env.current_screen * Env.FROG_PER_SCREEN + i);
            if (f.inBiting) {//如果咬下 
                if (isSweetFood()) { //甜食
                    f.awardAAA(); //因为甜食数量少比苦食少，为了鼓励多咬，甜食加分比苦食扣分多
                    f.ateFood++;
                } else if (isBitter()) { //苦食 
                    if (step > 8) //前几个步长不惩罚, 以鼓励咬动作
                        f.penaltyAAA();
                    f.ateWrong++;
                } else {//不甜也不苦说明咬到空气 
                    if (step > 8) //前几个步长不惩罚, 以鼓励咬动作
                        f.penaltyAAA();//咬空气也要消耗能量，扣点分
                    f.ateWrong++;
                }
            }
        }

        //因为只是显示，不影响逻辑，这里不需要遍历所有青蛙，只需要显示第一个青蛙的状态就行了
        f = Env.frogs.get(Env.current_screen * Env.FROG_PER_SCREEN + 0);
        // 开始画出状态色条
        int x = step % (Env.ENV_WIDTH / n);
        int y = step / (Env.ENV_WIDTH / n);

        int ystart = y * n;
        int xstart = x * n;
        int h = 4; // 线条显示宽度变量

        g.setColor(Color.BLUE); //主进度条用蓝色
        g.fillRect(xstart, ystart, n, h);

        g.setColor(Color.GREEN);
        g.setColor(f.feelSweet ? Color.GREEN : Color.GRAY); //副线条, 绿表示甜味
        g.fillRect(xstart, ystart + h, n, h - 1);

        g.setColor(f.feelBitter ? Color.RED : Color.GRAY); //副线条, 红表示苦味
        g.fillRect(xstart, ystart + h * 2, n, h - 1);

        if (Application.debug) {            
            Application.debug = !Application.debug;
            System.out.println(f.inBiting); 
        }

        g.setColor(f.inBiting ? Color.YELLOW : Color.GRAY); //黄表示咬下
        g.fillRect(xstart, ystart + h * 3, n, h - 1);
    }

}
