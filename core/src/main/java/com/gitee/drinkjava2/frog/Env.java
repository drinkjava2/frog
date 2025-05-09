package com.gitee.drinkjava2.frog;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.gitee.drinkjava2.frog.brain.Genes;
import com.gitee.drinkjava2.frog.egg.Egg;
import com.gitee.drinkjava2.frog.egg.FrogEggTool;
import com.gitee.drinkjava2.frog.objects.EnvObject;
import com.gitee.drinkjava2.frog.objects.Material;
import com.gitee.drinkjava2.frog.objects.FoodJudge;
import com.gitee.drinkjava2.frog.util.Logger;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Env is the living space of frog. draw it on JPanel
 * 
 * @author Yong Zhu
 * @since 1.0
 */
@SuppressWarnings("all")
public class Env extends JPanel {
    private static final long serialVersionUID = 1L;

    /** Speed of test */
    public static int SHOW_SPEED = 1000; // 测试速度，-1000~1000,可调, 数值越小，速度越慢

    public static final int FROG_EGG_QTY = 200; // 每轮下n个青蛙蛋，可调，只有最优秀的前n个青蛙们才允许下蛋

    public static final int FROG_PER_EGG = 4; // 每个青蛙蛋可以孵出几个青蛙

    public static final int SCREEN = 1; // 分几屏测完

    /** Delete eggs at beginning of each run */
    public static final boolean DELETE_FROG_EGGS = false;// 每次运行是否先删除以前保存的青蛙蛋文件，如果为false将加载旧蛋文件继续运行

    public static boolean SAVE_EGGS_FILE = false; //从2021-11-23起，添加这个选项，允许不输出蛋文件到磁盘上

    public static final boolean BORN_AT_RANDOM_PLACE = true;// 孵出青蛙落在地图上随机位置，而不是在蛋所在地

    /** Frog's brain size */ // 脑细胞位于脑范围内，是个三维结构，在animal中用三维数组来表示
    public static final int BRAIN_SIZE = 4; //脑立方边长大小，必须是2的幂数如4,8,16...，原因参见8叉树算法 

    /** SHOW first animal's brain structure */
    public static boolean SHOW_FIRST_ANIMAL_BRAIN = true; // 是否显示脑图在Env区的右侧

    /** Draw first frog's brain after some steps */
    public static int DRAW_BRAIN_AFTER_STEPS = 0; // 以此值为间隔动态画出脑图，设为0则关闭这个动态脑图功能，只显示一个静态、不闪烁的脑图

    /** Environment x width, unit: pixels */
    public static final int ENV_WIDTH = 400; // 虚拟环境的宽度, 可调

    /** Environment y height, unit: pixels */
    public static final int ENV_HEIGHT = ENV_WIDTH; // 虚拟环境高度, 可调，通常取正方形

    /** Frog's brain display width on screen, not important */
    public static final int FROG_BRAIN_DISP_WIDTH = 400; // Frog的脑图在屏幕上的显示大小,可调

    /** Steps of one test round */
    public static final int STEPS_PER_ROUND = 400;// 每屏测试步数,可调

    public static final int HALF_STEPS_PER_ROUND = STEPS_PER_ROUND / 2; // 每屏测试步数一半    

    public static final int FOOD_QTY = 3000; // 食物数量, 可调

    // 以下是程序内部变量，不要手工修改它们
    public static final int TOTAL_FROG_QTY = FROG_EGG_QTY * FROG_PER_EGG; // 青蛙总数

    public static final int FROG_PER_SCREEN = TOTAL_FROG_QTY / SCREEN; // 每屏显示几个青蛙，这个数值由其它常量计算得来

    public static int current_screen = 0; //当前测试屏

    public static int step;// 当前测试步数, 也可以理解为虚拟世界的当前时间

    private static Image buffImg; //当前虚拟作图区

    public static Graphics graph;//当前虚拟环境画笔，常用

    public static boolean pause = false; // 暂停按钮按下将暂停测试

    public static int[][] bricks = new int[ENV_WIDTH][ENV_HEIGHT];// 组成环境的材料，见Material.java

    public static ArrayList<Frog> frogs = new ArrayList<>(); // 这里存放所有待测的青蛙，可能分几次测完，由FROG_PER_SCREEN大小来决定

    public static ArrayList<Egg> frog_eggs = new ArrayList<>(); // 这里存放新建或从磁盘载入上轮下的蛋，每个蛋可能生成几个青蛙，

    public static EnvObject[] things = new EnvObject[] { new FoodJudge(2, 2) };// 所有外界物体，如食物、测试工具都放在这个things里面

    public static boolean show_split_detail = false; //是否显示脑分裂的细节过程，即从一个细胞开始分裂分裂，而不是只显示分裂的最终结果

    static {
        Logger.info("唵缚悉波罗摩尼莎诃!"); // 杀生前先打印往生咒，因为遗传算法建立在杀生选优的基础上，用这个方式表示对生命的尊重。智能研究不光是技术，还涉及到伦理，对虚拟生命的尊重也是对人类自身的尊重。
                                   // （意识不是一种实体存在，只是一种表象，但正因为此，我们才要尊重所有表现出或低级或高级的意识现象的虚拟智能系统，包括避免制造不必要的虚拟生命的痛苦感觉现象，己所不欲勿施于人。）
        Logger.info("脑图快捷键： T:顶视  F：前视  L:左视  R:右视  X:斜视  方向键：剖视  空格:暂停  鼠标：缩放旋转平移");
        if (DELETE_FROG_EGGS)
            FrogEggTool.deleteEggs();
    }

    public Env() {
        super();
        this.setLayout(null);// 空布局
        this.setBounds(1, 1, ENV_WIDTH, ENV_HEIGHT);
    }

    public static boolean insideBrain(int x, int y) {// 如果指定点在边界内
        return !(x < 0 || y < 0 || x >= BRAIN_SIZE || y >= BRAIN_SIZE);
    }

    public static boolean insideBrain(int x, int y, int z) {// 如果指定点在边界内
        return !(x < 0 || y < 0 || z < 0 || x >= BRAIN_SIZE || y >= BRAIN_SIZE || z >= BRAIN_SIZE);
    }

    public static boolean insideBrain(float x, float y, float z) {// 如果指定点在边界内
        return !(x < 0 || y < 0 || z < 0 || x >= BRAIN_SIZE || y >= BRAIN_SIZE || z >= BRAIN_SIZE);
    }

    public static boolean insideEnv(int x, int y) {// 如果指定点在边界内
        return !(x < 0 || y < 0 || x >= ENV_WIDTH || y >= ENV_HEIGHT);
    }

    public static boolean outsideEnv(int x, int y) {// 如果指定点超出边界
        return x < 0 || y < 0 || x >= ENV_WIDTH || y >= ENV_HEIGHT;
    }

    public static boolean closeToEdge(Animal a) {// 靠近边界? 离死不远了
        return a.xPos < 20 || a.yPos < 20 || a.xPos > (Env.ENV_WIDTH - 20) || a.yPos > (Env.ENV_HEIGHT - 20);
    }

    public static boolean foundAnyThingOrOutEdge(int x, int y) {// 如果指定点看到任意东西或超出边界，返回true
        return x < 0 || y < 0 || x >= ENV_WIDTH || y >= ENV_HEIGHT || Env.bricks[x][y] != 0;
    }

    public static boolean foundFrogOrOutEdge(int x, int y) {// 如果指定点看到青蛙或超出边界，返回true
        if (x < 0 || y < 0 || x >= ENV_WIDTH || y >= ENV_HEIGHT)
            return true;// 如果出界返回true
        if ((Env.bricks[x][y] & Material.FROG_TAG) > 0)
            return true;
        else
            return false;
    }

    public static void setMaterial(int x, int y, int material) {
        if (Env.insideEnv(x, y))
            Env.bricks[x][y] = Env.bricks[x][y] | material;
    }

    public static boolean hasMaterial(int x, int y, int material) {
        if (!Env.insideEnv(x, y))
            return false;
        return (Env.bricks[x][y] & material) > 0;
    }

    public static void clearMaterial(int x, int y, int material) {
        if (Env.insideEnv(x, y))
            Env.bricks[x][y] = Env.bricks[x][y] & ~material;
    }

    public static void rebuildFrogs() {// 根据蛙蛋重新孵化出蛙
        frogs.clear();
        for (int i = 0; i < frog_eggs.size(); i++) {// 创建青蛙，每个蛋生成n个蛙，并随机取一个别的蛋作为精子
            int loop = FROG_PER_EGG;
            if (frog_eggs.size() > 20) { // 如果数量多，进行一些优化，让排名靠前的Egg多孵出青蛙
                if (i < FROG_PER_EGG)// 0,1,2,3
                    loop = FROG_PER_EGG + 1;
                if (i >= (frog_eggs.size() - FROG_PER_EGG))
                    loop = FROG_PER_EGG - 1;
            }
            for (int j = 0; j < loop; j++) {
                Egg zygote = new Egg(frog_eggs.get(i), frog_eggs.get(RandomUtils.nextInt(frog_eggs.size())));
                Frog f = new Frog(zygote);
                frogs.add(f);
                f.no = frogs.size();
            }
        }
    }

    private void drawWorld(Graphics g) {
        int brick;
        for (int x = 0; x < ENV_WIDTH; x++)
            for (int y = 0; y < ENV_HEIGHT; y++) {
                brick = bricks[x][y];
                if (brick != 0) {
                    g.setColor(Material.color(brick));
                    if ((brick & Material.FOOD) > 0) {
                        g.fillRoundRect(x, y, 4, 4, 2, 2); //食物只有一个点太小，画大一点
                    } else
                        g.drawLine(x, y, x, y); // only 1 point
                }
            }
        g.setColor(Color.BLACK);
    }

    static final NumberFormat format100 = NumberFormat.getPercentInstance();
    static {
        format100.setMaximumFractionDigits(2);
    }

 
    public static void checkIfPause(int step) {
        if (pause) {
            do {
                Application.brainPic.drawBrainPicture(step);
                Application.brainPic.requestFocus();
                sleep(100);
            } while (pause);
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Animal getShowAnimal() { //取当前屏第一个青蛙
        return frogs.get(current_screen * FROG_PER_SCREEN);
    }

    public static int round = 1;

    public void run() {
        FrogEggTool.loadFrogEggs(); // 首次运行时，从磁盘加载蛙egg，如加载失败就新建一批egg
        buffImg = createImage(this.getWidth(), this.getHeight());
        graph = buffImg.getGraphics();
        long timerScreen;// 一屏要花多少时间的计时器
        long timerRound;//一轮要花多少时间的计时器，一轮可以由几屏组成
        long timeRound = 0;//一轮要花多少时间
        StringBuilder sb = new StringBuilder();
        round = 1;
        do {
            timerRound = System.currentTimeMillis();
            rebuildFrogs(); // 根据蛙蛋重新孵化出蛙，注意基因变异有可能在孵化过程中发生。初始化数组时间算在一轮时间里
            for (current_screen = 0; current_screen < SCREEN; current_screen++) {// 分屏测试，每屏FROG_PER_SCREEN个蛙
                timerScreen = System.currentTimeMillis();

                graph.setColor(Color.white);
                graph.fillRect(0, 0, this.getWidth(), this.getHeight()); // 先清空虚拟环境
                graph.setColor(Color.BLACK);
                for (EnvObject thing : things) // 创建食物、陷阱等物体
                    thing.build();

                boolean allDead = false;
                for (int j = 0; j < FROG_PER_SCREEN; j++) {
                    Frog f = frogs.get(current_screen * FROG_PER_SCREEN + j);
                    f.initAnimal(); // 初始化器官延迟到这一步，是因为脑细胞太占内存，而且当前屏测完后会清空
                }

                for (step = 0; step < STEPS_PER_ROUND; step++) {
                    if (allDead)
                        break; // 青蛙全死光了就直接跳到下一轮,以节省时间
                    for (EnvObject thing : things)// 调用食物、陷阱等物体的动作
                        thing.active();
                    allDead = true;
                    for (int j = 0; j < FROG_PER_SCREEN; j++) {
                        Frog f = frogs.get(current_screen * FROG_PER_SCREEN + j);
                        if (f.active())// 调用青蛙的Active方法，并返回是否还活着
                            allDead = false;
                    }

                    if (SHOW_SPEED == 1) // 如果speed为1，人为加入延迟
                        sleep(400);
                    else if (step % SHOW_SPEED != 0)// 用是否跳帧画图的方式来控制速度
                        continue;

                    // 开始画things和青蛙 
                    drawWorld(graph);// 画整个虚拟环境中的material 

                    for (int j = 0; j < FROG_PER_SCREEN; j++) { // 显示青蛙
                        Frog f = frogs.get(current_screen * FROG_PER_SCREEN + j);
                        f.showInEnv(graph);
                    }

                    if (DRAW_BRAIN_AFTER_STEPS > 0 && step % DRAW_BRAIN_AFTER_STEPS == 0) //显示脑图是耗时操作，这个开关可以跳过一些脑图显示
                        Application.brainPic.drawBrainPicture(step);
                    if (SHOW_SPEED == 1 && SHOW_FIRST_ANIMAL_BRAIN) //如果速度为1，强制每步都显示脑图
                        Application.brainPic.drawBrainPicture(step);
                    Graphics g2 = this.getGraphics();
                    g2.drawImage(buffImg, 0, 0, this);
                }
                if (SHOW_FIRST_ANIMAL_BRAIN) //一轮结束后再强制再显示脑图一次
                    Application.brainPic.drawBrainPicture(step);
                checkIfPause(step);

                sb.delete(0, sb.length()).append("轮:").append(round).append(", 屏:").append(current_screen).append(", 速:").append(Env.SHOW_SPEED);
                sb.append(", ").append("屏费时:").append(System.currentTimeMillis() - timerScreen).append("ms");
                sb.append(", 轮费时:").append(timeRound).append("ms, ");

                Application.mainFrame.setTitle(sb.toString());
                for (EnvObject thing : things)// 去除食物、陷阱等物体
                    thing.destory();
            }
            round++;
            FrogEggTool.layEggs(); //能量高的青蛙才有权下蛋   
            timeRound = System.currentTimeMillis() - timerRound;
        } while (true);
    }

}
