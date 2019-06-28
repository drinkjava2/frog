package com.github.drinkjava2.frog;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import com.github.drinkjava2.frog.egg.Egg;
import com.github.drinkjava2.frog.egg.EggTool;

/**
 * Env is the living space of frog. draw it on JPanel
 * 
 * @author Yong Zhu
 * @since 1.0
 */
@SuppressWarnings("serial")
public class Env extends JPanel {
	/** Speed of test */
	public static int SHOW_SPEED = 5; // 测试速度，-1000~1000,可调, 数值越小，速度越慢

	/** Delete eggs at beginning of each run */
	public static final boolean DELETE_EGGS = true;// 每次运行是否先删除保存的蛋

	/** Debug mode will print more debug info */
	public static final boolean DEBUG_MODE = false; // Debug 模式下会打印出更多的调试信息

	/** Draw first frog's brain after some steps */
	public static int DRAW_BRAIN_AFTER_STEPS = 50; // 以此值为间隔动态画出脑图，设为0则关闭这个动态脑图功能，只显示一个静态、不闪烁的脑图

	/** Environment x width, unit: pixels */
	public static final int ENV_WIDTH = 400; // 虚拟环境的宽度, 可调

	/** Environment y height, unit: pixels */
	public static final int ENV_HEIGHT = ENV_WIDTH; // 虚拟环境高度, 可调，通常取正方形

	/** Frog's brain display width on screen, not important */
	public static final int FROG_BRAIN_DISP_WIDTH = 300; // Frog的脑图在屏幕上的显示大小,可调

	/** Steps of one test round */
	public static final int STEPS_PER_ROUND = 2000;// 每轮测试步数,可调

	/** Frog's brain width, fixed to 1000 unit */
	public static final float FROG_BRAIN_WIDTH = 1000; // frog的脑宽度固定为1000,不要随便调整,因为器官的相对位置和大小是按脑大小设定的

	public static final int FOOD_QTY = 1500; // 食物数量, 可调

	public static final int EGG_QTY = 50; // 每轮下n个蛋，可调，只有最优秀的前n个青蛙们才允许下蛋

	private static final Random r = new Random(); // 随机数发生器

	public static boolean pause = false; // 暂停按钮按下将暂停测试

	private static final boolean[][] foods = new boolean[ENV_WIDTH][ENV_HEIGHT];// 食物数组定义

	public List<Frog> frogs = new ArrayList<>();

	public List<Egg> eggs;

	static {
		System.out.println("唵缚悉波罗摩尼莎诃!"); // 往生咒
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

	public static boolean foundFoodOrOutEdge(int x, int y) {// 如果指定点看到食物或超出边界
		return x < 0 || y < 0 || x >= ENV_WIDTH || y >= ENV_HEIGHT || Env.foods[x][y];
	}

	public static boolean foundAndDeleteFood(int x, int y) {// 如果x,y有食物，将其清0，返回true
		if (foundFood(x, y)) {
			foods[x][y] = false;
			return true;
		}
		return false;
	}

	private void rebuildFrogAndFood() {
		frogs.clear();
		for (int i = 0; i < ENV_WIDTH; i++) {// 清除食物
			for (int j = 0; j < ENV_HEIGHT; j++) {
				foods[i][j] = false;
			}
		}
		Random rand = new Random();
		for (int i = 0; i < eggs.size(); i++) {// 创建青蛙，每个蛋生成4个蛙，并随机取一个别的蛋作为精子
			int loop = 4;
			if (i <= 3)// 0,1,2,3
				loop = 6;
			if (i >= (eggs.size() - 4))
				loop = 2;
			for (int j = 0; j < loop; j++) {
				Egg zygote = new Egg(eggs.get(i), eggs.get(r.nextInt(eggs.size())));
				frogs.add(new Frog(rand.nextInt(ENV_WIDTH), rand.nextInt(ENV_HEIGHT), zygote));
			}
		}

		System.out.println("Created " + frogs.size() + " frogs");
		for (int i = 0; i < Env.FOOD_QTY; i++) // 生成食物
			foods[rand.nextInt(ENV_WIDTH)][rand.nextInt(ENV_HEIGHT)] = true;
	}

	private void drawFood(Graphics g) {
		for (int x = 0; x < ENV_WIDTH; x++)
			for (int y = 0; y < ENV_HEIGHT; y++)
				if (foods[x][y]) {
					g.fillOval(x, y, 4, 4);
				}
	}

	static final NumberFormat format100 = NumberFormat.getPercentInstance();
	static {
		format100.setMaximumFractionDigits(2);
	}

	private String foodFoundPercent() {// 计算找食效率
		int leftfood = 0;
		for (int x = 0; x < ENV_WIDTH; x++)
			for (int y = 0; y < ENV_HEIGHT; y++)
				if (foods[x][y])
					leftfood++;
		return format100.format((FOOD_QTY - leftfood) * 1.00 / FOOD_QTY);
	}

	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean closeToEdge(Frog f) {// 青蛙靠近边界? 离死不远了
		return f.x < 20 || f.y < 20 || f.x > (Env.WIDTH - 20) || f.y > (Env.HEIGHT - 20);
	}

	public void run() throws InterruptedException {
		EggTool.loadEggs(this); // 从磁盘加载egg，或新建一批egg
		int round = 1;
		Image buffImg = createImage(this.getWidth(), this.getHeight());
		Graphics g = buffImg.getGraphics();
		long t1, t2;
		do {
			if (pause) {
				sleep(300);
				continue;
			}
			t1 = System.currentTimeMillis();
			rebuildFrogAndFood();
			boolean allDead = false;
			Frog firstFrog = frogs.get(0);
			for (int i = 0; i < STEPS_PER_ROUND; i++) {
				if (allDead) {
					System.out.println("All dead at round:" + i);
					break; // 青蛙全死光了就直接跳到下一轮,以节省时间
				}
				allDead = true;
				for (Frog frog : frogs)
					if (frog.active(this))
						allDead = false;

				if (SHOW_SPEED > 0 && i % SHOW_SPEED != 0) // 画青蛙会拖慢速度
					continue;

				if (SHOW_SPEED < 0) // 如果speed小于0，人为加入延迟
					sleep(-SHOW_SPEED);

				// 开始画青蛙
				g.setColor(Color.white);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				g.setColor(Color.BLACK);
				for (Frog frog : frogs)
					frog.show(g);

				if (firstFrog.alive) { // 开始显示第一个Frog的动态脑图
					g.setColor(Color.red);
					g.drawArc(firstFrog.x - 15, firstFrog.y - 15, 30, 30, 0, 360);
					g.setColor(Color.BLACK);
					if (DRAW_BRAIN_AFTER_STEPS > 0 && i % DRAW_BRAIN_AFTER_STEPS == 0)
						Application.brainPic.drawBrainPicture(firstFrog);
				}

				drawFood(g);
				Graphics g2 = this.getGraphics();
				g2.drawImage(buffImg, 0, 0, this);

			}
			Application.brainPic.drawBrainPicture(firstFrog);
			EggTool.layEggs(this);
			t2 = System.currentTimeMillis();
			Application.mainFrame.setTitle("Frog test round: " + round++ + ", 找食效率:" + foodFoundPercent()
					+ ", time used: " + (t2 - t1) + " ms, first frog x=" + firstFrog.x + ", y=" + firstFrog.y);
		} while (true);
	}
}
