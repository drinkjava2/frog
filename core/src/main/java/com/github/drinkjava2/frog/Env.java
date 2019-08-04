package com.github.drinkjava2.frog;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import com.github.drinkjava2.frog.brain.group.RandomConnectGroup;
import com.github.drinkjava2.frog.egg.Egg;
import com.github.drinkjava2.frog.egg.EggTool;
import com.github.drinkjava2.frog.util.RandomUtils;

/**
 * Env is the living space of frog. draw it on JPanel
 * 
 * @author Yong Zhu
 * @since 1.0
 */
@SuppressWarnings("all")
public class Env extends JPanel {
	/** Speed of test */
	public static final int SHOW_SPEED = 300; // 测试速度，-1000~1000,可调, 数值越小，速度越慢

	/** Delete eggs at beginning of each run */
	public static final boolean DELETE_EGGS = true;// 每次运行是否先删除保存的蛋

	public static final int EGG_QTY = 30; // 每轮下n个蛋，可调，只有最优秀的前n个青蛙们才允许下蛋

	public static final int FROG_PER_EGG = 3; // 每个蛋可以孵出几个青蛙

	public static final int SCREEN = 3; // 分几屏测完, 所以每轮待测青蛙总数=EGG_QTY*FROG_PER_EGG*SCREEN

	public static final int FROG_PER_SCREEN = EGG_QTY * FROG_PER_EGG / SCREEN; // 每屏上显示几个青蛙，这个数值由上面三个参数计算得来

	/** Debug mode will print more debug info */
	public static final boolean DEBUG_MODE = false; // Debug 模式下会打印出更多的调试信息

	/** Draw first frog's brain after some steps */
	public static int DRAW_BRAIN_AFTER_STEPS = 50; // 以此值为间隔动态画出脑图，设为0则关闭这个动态脑图功能，只显示一个静态、不闪烁的脑图

	/** Environment x width, unit: pixels */
	public static final int ENV_WIDTH = 400; // 虚拟环境的宽度, 可调

	/** Environment y height, unit: pixels */
	public static final int ENV_HEIGHT = ENV_WIDTH; // 虚拟环境高度, 可调，通常取正方形

	/** Frog's brain display width on screen, not important */
	public static final int FROG_BRAIN_DISP_WIDTH = 400; // Frog的脑图在屏幕上的显示大小,可调

	/** Steps of one test round */
	public static final int STEPS_PER_ROUND = 2000;// 每轮测试步数,可调

	/** Frog's brain width, fixed to 1000 unit */
	public static final float FROG_BRAIN_WIDTH = 1000; // frog的脑宽度固定为1000,不要随便调整,因为器官的相对位置和大小是按脑大小设定的

	public static final int FOOD_QTY = 1500; // 食物数量, 可调

	private static final Random r = new Random(); // 随机数发生器

	public static boolean pause = false; // 暂停按钮按下将暂停测试

	private static final boolean[][] foods = new boolean[ENV_WIDTH][ENV_HEIGHT];// 食物数组定义

	private static final int TRAP_WIDTH = 350; // 陷阱高, 0~200

	private static final int TRAP_HEIGHT = 10; // 陷阱宽, 0~200

	public static List<Frog> frogs = new ArrayList<>(); // 这里存放所有待测的青蛙，可能分几次测完，由FROG_PER_SCREEN大小来决定

	public static List<Egg> eggs = new ArrayList<>(); // 这里存放从磁盘载入或上轮下的蛋，每个蛋可能生成1~n个青蛙，

	static {
		System.out.println("唵缚悉波罗摩尼莎诃!"); // 杀生前先打印往生咒，见码云issue#IW4H8
		if (DELETE_EGGS)
			EggTool.deleteEggs();
	}

	public Env() {
		super();
		this.setLayout(null);// 空布局
		this.setBounds(1, 1, ENV_WIDTH, ENV_HEIGHT);
	}

	public static boolean insideEnv(int x, int y) {// 如果指定点在边界内
		return !(x < 0 || y < 0 || x >= ENV_WIDTH || y >= ENV_HEIGHT);
	}

	public static boolean outsideEnv(int x, int y) {// 如果指定点超出边界
		return x < 0 || y < 0 || x >= ENV_WIDTH || y >= ENV_HEIGHT;
	}

	public static boolean foundFood(int x, int y) {// 如果指定点看到食物
		return !(x < 0 || y < 0 || x >= ENV_WIDTH || y >= ENV_HEIGHT) && Env.foods[x][y];
	}

	public static boolean closeToEdge(Frog f) {// 青蛙靠近边界? 离死不远了
		return f.x < 20 || f.y < 20 || f.x > (Env.ENV_WIDTH - 20) || f.y > (Env.ENV_HEIGHT - 20);
	}

	public static boolean inTrap(int x, int y) {// 如果指定点看到食物
		return x >= ENV_WIDTH / 2 - TRAP_WIDTH / 2 && x <= ENV_WIDTH / 2 + TRAP_WIDTH / 2
				&& y >= ENV_HEIGHT / 2 - TRAP_HEIGHT / 2 && y <= ENV_HEIGHT / 2 + TRAP_HEIGHT / 2;
	}

	public static boolean foundAnyThing(int x, int y) {// 如果指定点看到食物或超出边界
		return x < 0 || y < 0 || x >= ENV_WIDTH || y >= ENV_HEIGHT || Env.foods[x][y] || inTrap(x, y);
	}

	public static boolean foundAndDeleteFood(int x, int y) {// 如果x,y有食物，将其清0，返回true
		if (foundFood(x, y)) {
			foods[x][y] = false;
			return true;
		}
		return false;
	}

	private void rebuildFood() {
		for (int i = 0; i < ENV_WIDTH; i++) {// 清除食物
			for (int j = 0; j < ENV_HEIGHT; j++)
				foods[i][j] = false;
		}
		for (int i = 0; i < Env.FOOD_QTY; i++) // 生成食物
			foods[RandomUtils.nextInt(ENV_WIDTH)][RandomUtils.nextInt(ENV_HEIGHT)] = true;
	}

	private void rebuildFrogs() {
		frogs.clear();
		for (int i = 0; i < eggs.size(); i++) {// 创建青蛙，每个蛋生成n个蛙，并随机取一个别的蛋作为精子
			int loop = FROG_PER_EGG;
			if (eggs.size() > 20) { // 如果数量多，进行一些优化，让排名靠前的Egg多孵出青蛙
				if (i < FROG_PER_EGG)// 0,1,2,3
					loop = FROG_PER_EGG + 1;
				if (i >= (eggs.size() - FROG_PER_EGG))
					loop = FROG_PER_EGG - 1;
			}
			for (int j = 0; j < loop; j++) {
				Egg zygote = new Egg(eggs.get(i), eggs.get(r.nextInt(eggs.size())));
				frogs.add(new Frog(RandomUtils.nextInt(ENV_WIDTH), RandomUtils.nextInt(ENV_HEIGHT), zygote));
			}
		}
	}

	private void drawFood(Graphics g) {
		for (int x = 0; x < ENV_WIDTH; x++)
			for (int y = 0; y < ENV_HEIGHT; y++)
				if (foods[x][y]) {
					g.fillOval(x, y, 4, 4);
				}
	}

	private void drawTrap(Graphics g) {// 所有走到陷阱边沿上的的青蛙都死掉
		g.fillRect(ENV_HEIGHT / 2 - TRAP_WIDTH / 2, ENV_HEIGHT / 2 - TRAP_HEIGHT / 2, TRAP_WIDTH, TRAP_HEIGHT);
		g.setColor(Color.white);
		g.fillRect(ENV_HEIGHT / 2 - TRAP_WIDTH / 2 + 3, ENV_HEIGHT / 2 - TRAP_HEIGHT / 2 + 3, TRAP_WIDTH - 6,
				TRAP_HEIGHT - 6);
		g.setColor(Color.black);
	}

	static final NumberFormat format100 = NumberFormat.getPercentInstance();
	static {
		format100.setMaximumFractionDigits(2);
	}

	private static int foodFoundAmount() {// 统计找食数等
		int leftfood = 0;
		for (int x = 0; x < ENV_WIDTH; x++)
			for (int y = 0; y < ENV_HEIGHT; y++)
				if (foods[x][y])
					leftfood++;
		return FOOD_QTY - leftfood;
	}

	private String foodFoundCountText() {// 统计找食数等
		int foodFound = foodFoundAmount();
		int maxFound = 0;
		for (Frog f : frogs)
			if (f.ateFood > maxFound)
				maxFound = f.ateFood;
		return new StringBuilder("找食率:").append(format100.format(foodFound * 1.00 / FOOD_QTY)).append(", 平均: ")
				.append(foodFound * 1.0f / (EGG_QTY * FROG_PER_EGG)).append("，最多:").append(maxFound).toString();
	}

	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		EggTool.loadEggs(); // 从磁盘加载egg，或新建一批egg
		Image buffImg = createImage(this.getWidth(), this.getHeight());
		Graphics g = buffImg.getGraphics();
		long time0;// 计时用
		int round = 1;
		do {
			rebuildFrogs();
			for (int screen = 0; screen < SCREEN; screen++) {// 分屏测试，每屏FROG_PER_SCREEN个蛙
				time0 = System.currentTimeMillis();
				if (pause)
					do {
						sleep(300);
					} while (pause);
				rebuildFood();
				boolean allDead = false;
				Frog firstFrog = frogs.get(screen * FROG_PER_SCREEN);
				for (int i = 0; i < STEPS_PER_ROUND; i++) {
					if (allDead)
						break; // 青蛙全死光了就直接跳到下一轮,以节省时间

					allDead = true;
					for (int j = 0; j < FROG_PER_SCREEN; j++) {
						Frog f = frogs.get(screen * FROG_PER_SCREEN + j);
						if (f.active(this))
							allDead = false;
						if (f.alive && RandomUtils.percent(0.2f)) {// 有很小的机率在青蛙活着时就创建新的器官
							RandomConnectGroup newConGrp = new RandomConnectGroup();
							newConGrp.initFrog(f);
							f.organs.add(newConGrp);
						}
					}

					if (SHOW_SPEED > 0 && i % SHOW_SPEED != 0) // 用画青蛙的方式来拖慢速度
						continue;

					if (SHOW_SPEED < 0) // 如果speed小于0，人为加入延迟
						sleep(-SHOW_SPEED);

					// 开始画青蛙
					g.setColor(Color.white);
					g.fillRect(0, 0, this.getWidth(), this.getHeight());
					g.setColor(Color.BLACK);
					for (int j = 0; j < FROG_PER_SCREEN; j++) {
						Frog f = frogs.get(screen * FROG_PER_SCREEN + j);
						f.show(g);
					}

					if (firstFrog.alive) { // 开始显示第一个Frog的动态脑图
						if (Application.SHOW_FIRST_FROG_BRAIN) {
							g.setColor(Color.red);
							g.drawArc(firstFrog.x - 15, firstFrog.y - 15, 30, 30, 0, 360);
							g.setColor(Color.BLACK);
						}
						if (DRAW_BRAIN_AFTER_STEPS > 0 && i % DRAW_BRAIN_AFTER_STEPS == 0)
							Application.brainPic.drawBrainPicture(firstFrog);
					}
					drawTrap(g);
					drawFood(g);
					Graphics g2 = this.getGraphics();
					g2.drawImage(buffImg, 0, 0, this);

				}
				Application.brainPic.drawBrainPicture(firstFrog);
				Application.mainFrame.setTitle(new StringBuilder("Round: ").append(round).append(", screen:")
						.append(screen).append(", ").append(foodFoundCountText()).append(", 用时: ")
						.append(System.currentTimeMillis() - time0).append("ms").toString());
			}
			round++;
			EggTool.layEggs();
		} while (true);
	}

}
