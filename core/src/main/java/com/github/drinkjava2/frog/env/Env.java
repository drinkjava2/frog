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
 */
@SuppressWarnings("serial")
public class Env extends JPanel {
	/** Speed of test */
	public static int SHOW_SPEED = 1;

	/** Steps of one test round */
	public static int STEPS_PER_ROUND = 3000;

	/** Delete eggs at beginning of each test */
	public static boolean DELETE_EGGS = false;// 每次测试先删除保存的蛋

	static {
		if (DELETE_EGGS)
			EggTool.deleteEggs();
	}

	public static boolean pause = false;

	private static final Random r = new Random();

	/** Virtual environment x size is 500 pixels */
	public int ENV_XSIZE = 300;

	/** Virtual environment y size is 500 pixels */
	public int ENV_YSIZE = 300;

	public byte[][] foods = new byte[ENV_XSIZE][ENV_YSIZE];

	public int FOOD_QTY = 1800; // as name

	public int EGG_QTY = 50; // as name

	public List<Frog> frogs = new ArrayList<Frog>();
	public List<Egg> eggs;

	public Env() {
		super();
		this.setLayout(null);// 空布局
		this.setBounds(100, 100, ENV_XSIZE, ENV_YSIZE);
	}

	private void rebuildFrogAndFood() {
		frogs.clear();
		for (int i = 0; i < ENV_XSIZE; i++) {// clear foods
			for (int j = 0; j < ENV_YSIZE; j++) {
				foods[i][j] = 0;
			}
		}
		Random rand = new Random();
		for (int j = 0; j < 12; j++) {// 第一名多生出12个蛋
			Egg zygote = new Egg(eggs.get(0), eggs.get(r.nextInt(eggs.size())));
			frogs.add(new Frog(ENV_XSIZE / 2 + rand.nextInt(90), ENV_YSIZE / 2 + rand.nextInt(90), zygote));
		}
		for (int i = 0; i < eggs.size() - 3; i++) { // 1个Egg生出4个Frog，但是最后3名不生蛋(名额让给了第一名)
			for (int j = 0; j < 4; j++) {
				Egg zygote = new Egg(eggs.get(i), eggs.get(r.nextInt(eggs.size())));
				frogs.add(new Frog(ENV_XSIZE / 2 + rand.nextInt(90), ENV_YSIZE / 2 + rand.nextInt(90), zygote));
			}
		}

		System.out.println("Created " + 4 * eggs.size() + " frogs");
		for (int i = 0; i < FOOD_QTY; i++)
			foods[rand.nextInt(ENV_XSIZE - 3)][rand.nextInt(ENV_YSIZE - 3)] = 1;
	}

	private void drawFood(Graphics g) {
		for (int x = 0; x < ENV_XSIZE; x++)
			for (int y = 0; y < ENV_YSIZE; y++)
				if (foods[x][y] > 0) {
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
			Application.brainStructure.drawBrain(frogs.get(0));
			t2 = System.currentTimeMillis();
			Application.mainFrame.setTitle("Frog test round: " + round++ + ", time used: " + (t2 - t1) + " ms, x="
					+ frogs.get(0).x + ", y=" + frogs.get(0).y);
		} while (true);
	}
}
