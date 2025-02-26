package com.gitee.drinkjava2.frog.objects;

import java.awt.Color;
import java.awt.Graphics;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * 
 * 用来模拟：当有两个视细胞、一个甜细胞、一个咬细胞这种情况下， 判断青蛙能否根据甜味细胞的训练，在下次相同视信号出现时决定咬还是不咬。
 * 有两个视细胞输入，有4种排列组合，去除全为0的，即青蛙要实现与、或、异或这三种模式识别, 每轮测试随机定一种或两种模式对应可食甜食，其它情况对应不可食苦食，详见下面组合
 * 
 * 
 * TwoInputJudge（0）: 两点输入，4种排列组合中有一种是食物，青蛙全过程能感到甜苦味，经实测约2000轮后青蛙会进化出先尝一尝再决定咬还是不咬，利用味觉绕过了图像的模式识别，所以要改进测试条件，让味觉延迟于视觉，让它只能根据图像和记忆来预判咬不咬
 * TwoInputJudge（n）: 甜苦味会延迟咬动作n个时间单位产生，强迫它不能根据味觉，而必须根据视觉和之前的条件反射来预判咬不咬。 
 *  
 */
public class TwoInputJudge implements EnvObject {
    int n = 20; //n是表示食物的小方块边长，食物code由多个位组成时，小方块显示它的二进制条形码 
    int group = 15; //以group为一组，随机安排一半为食物
    int groupspace = 15; //TODO:group之间有一段空白间隔, 以免干扰 
    int[] food = new int[Env.STEPS_PER_ROUND + group];
    int sweetFoodCode; //甜食code，食物code有三种，但只有一种与甜食code相同的食物可食。
    int totalSweetFood = 0;
    int nerveDelay = 0;

    public TwoInputJudge(int nerveDelay) { // nerveDelay表示从咬下到感到甜苦味之间的延迟
        this.nerveDelay = nerveDelay;
    }

    public void resetFood() {
        sweetFoodCode = 1 + RandomUtils.nextInt(3); // 甜食code每一轮测试都不一样，强迫青蛙每一轮都要根据苦和甜味快速适应，从三种食物中找出正确的那一种食物
        int step = 0;
        int x = 2;
        while (step < (Env.STEPS_PER_ROUND)) {
            int firstFood = RandomUtils.nextInt(group / 2); //以group为一组，随机安排一半为食物
            int foodCode = 1 + RandomUtils.nextInt(3); //食物有0,1,2,3四种图案，分别对应两个细胞的00,01,10,11四种情况
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
            if (f == sweetFoodCode)
                totalSweetFood++;
    }

    @Override
    public void build() { //build在每屏测试前调用一次，这里用随机数准备好食物出现和消失的顺序为测试作准备
        Graphics g = Env.graph;
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
                g.setColor(Color.DARK_GRAY); //食物是甜的时，用蓝色表示，蓝莓?
            else
                g.setColor(Color.LIGHT_GRAY);
            if ((foodCode & 1) > 0)
                g.fillRect(x * n, y * n, n, n / 2);
            if ((foodCode & 2) > 0)
                g.fillRect(x * n, y * n + n / 2, n, n / 2);
            x++;
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
                    f.sweetNerveDelay[step + nerveDelay] = true;
                    f.awardAAA3(); //因为甜食数量少比苦食少，为了鼓励多咬，甜食加分比苦食扣分多
                    f.ateFood++; //ateFood, ateWrong, ateMiss以及各种色条都是统计和调试用的，不参与逻辑
                    g.setColor(Color.GREEN); //绿=====咬到食物
                } else if (isBitter) { // 苦食
                    f.bitterNerveDelay[step + nerveDelay] = true;
                    f.penaltyAAA(); //
                    f.ateWrong++;
                    g.setColor(Color.RED);//红=====咬到毒物
                } else {//不甜也不苦说明咬到空气
                    f.sweetNerveDelay[step + nerveDelay] = false;
                    f.bitterNerveDelay[step + nerveDelay] = false;
                    f.penaltyAAA();//咬空气也要消耗能量，扣点分
                    f.ateWrong++;
                    g.setColor(Color.MAGENTA); //紫=====咬到空气
                }
            } else { //如果没有咬，当然味觉也没有，也不用扣分，但是大自然会把躺平的青蛙淘汰，因为躺平的青蛙吃的少
                f.sweetNerveDelay[step + nerveDelay] = false;
                f.bitterNerveDelay[step + nerveDelay] = false;
                if (isSweet) { //如果没有咬但是食物是甜的，说明错过了一个甜食
                    f.ateMiss++;
                    g.setColor(Color.MAGENTA); //青=====漏了食物
                } else
                    g.setColor(Color.LIGHT_GRAY); //灰=====漏对了，毒物或空气
            }

            f.sweet = f.sweetNerveDelay[step]; //当前青蛙感到的味觉实际上是前几个时间周期咬下时产生的味觉
            f.bitter = f.bitterNerveDelay[step];

            // 开始显示状态色条供调试用，
            if (i == 0) {// 虚拟环境只显示第一个青蛙的色条
                int x = step % (Env.ENV_WIDTH / n);
                int y = step / (Env.ENV_WIDTH / n);
                g.fillRect(x * n, y * n + n / 2, n, 2);
                if (f.sweet) {
                    g.setColor(Color.CYAN);
                    g.fillRect(x * n, y * n + n / 2 - 2, n, 2);
                } else if (f.bitter) {
                    g.setColor(Color.BLUE);
                    g.fillRect(x * n, y * n + n / 2 - 2, n, 2);
                }

            }
        }
    }

}
