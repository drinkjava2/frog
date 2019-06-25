package com.github.drinkjava2.frog.env;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.egg.Egg;
import com.github.drinkjava2.frog.util.EggTool;

/**
 * Env is the living space of frog. draw it on JPanel
 * 
 * @author Yong Zhu
 * @since 1.0
 */
@SuppressWarnings("serial")
public class Env extends JPanel {
	/** Speed of test */
	public static final int SHOW_SPEED = 10; // 测试速度，1~1000,可调, 数值越小，速度越慢

	public static final int ENV_WIDTH = 400; // 虚拟环境的宽度, 可调

	/** Virtual environment y size pixels */
	public static final int ENV_HEIGHT = ENV_WIDTH; // 虚拟环境高度, 可调，通常取正方形

	/** Frog's brain display width on screen, not important */
	public static final int FROG_BRAIN_DISP_WIDTH = 300; // Frog的脑图在屏幕上的显示大小,可调

	/** Steps of one test round */
	public static final int STEPS_PER_ROUND = 3000;// 每轮测试步数,可调

	/** Frog's brain width, fixed to 1000 unit */
	public static final float FROG_BRAIN_WIDTH = 1000; // frog的脑宽度固定为1000,不要随便调整,因为器官的相对位置和大小是按脑大小设定的

	/** Delete eggs at beginning of each run */
	public static final boolean DELETE_EGGS = false;// 每次运行是否先删除保存的蛋

	static {
		if (DELETE_EGGS)
			EggTool.deleteEggs();
	}

	public static final int FOOD_QTY = 2000; // 食物数量, 可调

	public static final int EGG_QTY = 50; // 每轮下n个蛋，可调，只有最优秀的前n个青蛙们才允许下蛋

	private static final Random r = new Random(); // 随机数发生器

	public static boolean pause = false; // 暂停按钮按下将暂停测试

	public static final boolean[][] foods = new boolean[ENV_WIDTH][ENV_HEIGHT];// 食物数组定义

	public List<Frog> frogs = new ArrayList<>();

	public List<Egg> eggs;

	public Env() {
		super();
		this.setLayout(null);// 空布局
		this.setBounds(1, 1, ENV_WIDTH, ENV_HEIGHT);
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
			if (i == 0)
				loop = 8;
			if (i == eggs.size() - 1)
				loop = 0;
			for (int j = 0; j < loop; j++) {
				Egg zygote = new Egg(eggs.get(i), eggs.get(r.nextInt(eggs.size())));
				frogs.add(new Frog(rand.nextInt(ENV_WIDTH), rand.nextInt(ENV_HEIGHT), zygote));
			}
		}

		System.out.println("Created " + 4 * eggs.size() + " frogs");
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

	private static void sleep() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() throws InterruptedException {
		EggTool.loadEggs(this); // 从磁盘加载egg，或新建一批egg
		int round = 1;
		Image buffImg = createImage(this.getWidth(), this.getHeight());
		Graphics g = buffImg.getGraphics();
		long t1, t2;
		do {
			if (pause) {
				sleep();
				continue;
			}
			t1 = System.currentTimeMillis();
			rebuildFrogAndFood();
			boolean allDead = false;
			for (int i = 0; i < STEPS_PER_ROUND; i++) {
				if (allDead) {
					System.out.println("All dead at round:" + i);
					break; // 全死光了就直接跳到下一轮,以节省时间
				}
				allDead = true;
				for (Frog frog : frogs)
					if (frog.active(this))
						allDead = false;
				if (i % SHOW_SPEED != 0) // 画青蛙会拖慢速度
					continue;
				// 开始画青蛙
				g.setColor(Color.white);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				g.setColor(Color.BLACK);
				for (Frog frog : frogs)
					frog.show(g);

				drawFood(g);
				Graphics g2 = this.getGraphics();
				g2.drawImage(buffImg, 0, 0, this);
			}

			EggTool.layEggs(this);
			Application.brainStructure.drawBrainPicture(frogs.get(0));
			t2 = System.currentTimeMillis();
			Application.mainFrame.setTitle("Frog test round: " + round++ + ", time used: " + (t2 - t1) + " ms, x="
					+ frogs.get(0).x + ", y=" + frogs.get(0).y);
		} while (true);
	}
}
