package com.github.drinkjava2.frog.brain;

import static java.awt.Color.BLUE;
import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.MAGENTA;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.awt.Color.YELLOW;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.organ.Organ;

/**
 * BrainPicture show first frog's brain structure, for debug purpose only
 * 
 * 这个类用来画出脑图，这不是一个关键类，对脑的运行逻辑无影响，但有了脑图后可以直观地看出脑的3维结构，进行有针对性的改进
 * 
 * @author Yong Zhu
 * @since 1.0
 */
@SuppressWarnings("serial")
public class BrainPicture extends JPanel {
	Color color = Color.red;
	int brainDispWidth; // screen display piexls width
	float scale; // brain scale
	int xOffset = 0; // brain display x offset compare to screen
	int yOffset = 0; // brain display y offset compare to screen
	float xAngle = (float) (Math.PI / 2.5); // brain rotate on x axis
	float yAngle = -(float) (Math.PI / 8); // brain rotate on y axis
	float zAngle = 0;// brain rotate on z axis

	public BrainPicture(int x, int y, float brainWidth, int brainDispWidth) {
		super();
		this.setLayout(null);// 空布局
		this.brainDispWidth = brainDispWidth;
		scale = 0.7f * brainDispWidth / brainWidth;
		this.setBounds(x, y, brainDispWidth + 1, brainDispWidth + 1);
		MouseAction act = new MouseAction(this);
		this.addMouseListener(act);
		this.addMouseWheelListener(act);
		this.addMouseMotionListener(act);
	}

	public void drawCuboid(Cuboid c) {// 在脑图上画一个长立方体框架，视角是TopView
		float x = c.x;
		float y = c.y;
		float z = c.z;
		float xe = c.xe;
		float ye = c.ye;
		float ze = c.ze;

		drawLine(x, y, z, x + xe, y, z);// 画立方体的下面边
		drawLine(x + xe, y, z, x + xe, y + ye, z);
		drawLine(x + xe, y + ye, z, x, y + ye, z);
		drawLine(x, y + ye, z, x, y, z);

		drawLine(x, y, z, x, y, z + ze);// 画立方体的中间边
		drawLine(x + xe, y, z, x + xe, y, z + ze);
		drawLine(x + xe, y + ye, z, x + xe, y + ye, z + ze);
		drawLine(x, y + ye, z, x, y + ye, z + ze);

		drawLine(x, y, z + ze, x + xe, y, z + ze);// 画立方体的上面边
		drawLine(x + xe, y, z + ze, x + xe, y + ye, z + ze);
		drawLine(x + xe, y + ye, z + ze, x, y + ye, z + ze);
		drawLine(x, y + ye, z + ze, x, y, z + ze);
	}

	/*-
	  画线，固定以top视角的角度，所以只需要从x1,y1画一条到x2,y2的直线	
		绕 x 轴旋转 θ
		x, y.cosθ-zsinθ, y.sinθ+z.cosθ
	
		绕 y 轴旋转 θ 
		z.sinθ+x.cosθ,  y,  z.cosθ-x.sinθ	
		
		 绕 z 轴旋转 θ
		x.cosθ-y.sinθ, x.sinθ+y.consθ, z
	 -*/
	public void drawLine(float px1, float py1, float pz1, float px2, float py2, float pz2) {
		double x1 = px1 - Env.FROG_BRAIN_XSIZE / 2;
		double y1 = -py1 + Env.FROG_BRAIN_YSIZE / 2;// 屏幕的y坐标是反的，显示时要正过来
		double z1 = pz1 - Env.FROG_BRAIN_ZSIZE / 2;
		double x2 = px2 - Env.FROG_BRAIN_XSIZE / 2;
		double y2 = -py2 + Env.FROG_BRAIN_YSIZE / 2;// 屏幕的y坐标是反的，显示时要正过来
		double z2 = pz2 - Env.FROG_BRAIN_ZSIZE / 2;
		x1 = x1 * scale;
		y1 = y1 * scale;
		z1 = z1 * scale;
		x2 = x2 * scale;
		y2 = y2 * scale;
		z2 = z2 * scale;
		double x, y, z;
		y = y1 * cos(xAngle) - z1 * sin(xAngle);// 绕x轴转
		z = y1 * sin(xAngle) + z1 * cos(xAngle);
		y1 = y;
		z1 = z;

		x = z1 * sin(yAngle) + x1 * cos(yAngle);// 绕y轴转
		z = z1 * cos(yAngle) - x1 * sin(yAngle);
		x1 = x;
		z1 = z;

		x = x1 * cos(zAngle) - y1 * sin(zAngle);// 绕z轴转
		y = x1 * sin(zAngle) + y1 * cos(zAngle);
		x1 = x;
		y1 = y;

		y = y2 * cos(xAngle) - z2 * sin(xAngle);// 绕x轴转
		z = y2 * sin(xAngle) + z2 * cos(xAngle);
		y2 = y;
		z2 = z;

		x = z2 * sin(yAngle) + x2 * cos(yAngle);// 绕y轴转
		z = z2 * cos(yAngle) - x2 * sin(yAngle);
		x2 = x;
		z2 = z;

		x = x2 * cos(zAngle) - y2 * sin(zAngle);// 绕z轴转
		y = x2 * sin(zAngle) + y2 * cos(zAngle);
		x2 = x;
		y2 = y;

		Graphics g = this.getGraphics();
		g.setColor(color);
		g.drawLine((int) round(x1) + Env.FROG_BRAIN_DISP_WIDTH / 2 + xOffset,
				(int) round(y1) + Env.FROG_BRAIN_DISP_WIDTH / 2 + yOffset,
				(int) round(x2) + Env.FROG_BRAIN_DISP_WIDTH / 2 + xOffset,
				(int) round(y2) + Env.FROG_BRAIN_DISP_WIDTH / 2 + yOffset);
	}

	/** 画出Cube的中心点 */
	public void drawCubeCenter(float x, float y, float z) {
		drawPoint(x + 0.5f, y + 0.5f, z + 0.5f, (int) Math.max(1, Math.round(scale * .7)));
	}

	/** 画点，固定以top视角的角度，所以只需要在x1,y1位置画一个点 */
	public void drawPoint(float px1, float py1, float pz1, int diameter) {
		double x1 = px1 - Env.FROG_BRAIN_XSIZE / 2;
		double y1 = -py1 + Env.FROG_BRAIN_YSIZE / 2;// 屏幕的y坐标是反的，显示时要正过来
		double z1 = pz1 - Env.FROG_BRAIN_ZSIZE / 2;
		x1 = x1 * scale;
		y1 = y1 * scale;
		z1 = z1 * scale;
		double x, y, z;
		y = y1 * cos(xAngle) - z1 * sin(xAngle);// 绕x轴转
		z = y1 * sin(xAngle) + z1 * cos(xAngle);
		y1 = y;
		z1 = z;

		x = z1 * sin(yAngle) + x1 * cos(yAngle);// 绕y轴转
		z = z1 * cos(yAngle) - x1 * sin(yAngle);
		x1 = x;
		z1 = z;

		x = x1 * cos(zAngle) - y1 * sin(zAngle);// 绕z轴转
		y = x1 * sin(zAngle) + y1 * cos(zAngle);
		x1 = x;
		y1 = y;

		Graphics g = this.getGraphics();
		g.setColor(color);
		g.fillOval((int) round(x1) + Env.FROG_BRAIN_DISP_WIDTH / 2 + xOffset,
				(int) round(y1) + Env.FROG_BRAIN_DISP_WIDTH / 2 + yOffset - diameter / 2, diameter, diameter);
	}

	private static final Color[] rainbow = new Color[] { RED, ORANGE, YELLOW, GREEN, CYAN, BLUE, MAGENTA };
	private static int nextColor = 0;

	public static Color nextRainbowColor() {
		if (nextColor == rainbow.length)
			nextColor = 0;
		return rainbow[nextColor++];
	}

	public static Color rainbowColor(float i) {
		if (i == 0)
			return Color.black;
		if (i == 1)
			return Color.RED;
		if (i <= 3)
			return Color.ORANGE;
		if (i <= 10)
			return Color.YELLOW;
		if (i <= 20)
			return Color.GREEN;
		if (i <= 50)
			return Color.CYAN;
		if (i <= 100)
			return Color.BLUE;
		return Color.MAGENTA;
	}

	private static Cuboid brain = new Cuboid(0, 0, 0, Env.FROG_BRAIN_XSIZE, Env.FROG_BRAIN_YSIZE, Env.FROG_BRAIN_ZSIZE);

	public void drawBrainPicture(Frog f) {// 在这个方法里进行青蛙三维脑结构的绘制
		if (!f.alive)
			return;
		if (!Env.SHOW_FIRST_FROG_BRAIN)
			return;
		Graphics g = this.getGraphics();
		g.setColor(Color.WHITE);// 先清空旧图
		g.fillRect(0, 0, brainDispWidth, brainDispWidth);
		g.setColor(Color.black); // 画边框
		g.drawRect(0, 0, brainDispWidth, brainDispWidth);

		drawCuboid(brain);// 先把脑的框架画出来

		for (Organ organ : f.organs)// 每个器官负责画出自已在脑图中的位置和形状
			organ.drawOnBrainPicture(f, this); // each organ draw itself
		this.setColor(Color.RED);
		drawLine(0, 0, 0, 1, 0, 0);
		drawLine(0, 0, 0, 0, 1, 0);
		drawLine(0, 0, 0, 0, 0, 1);

		for (int x = 0; x < Env.FROG_BRAIN_XSIZE; x++) {
			if (f.cubes[x] != null)
				for (int y = 0; y < Env.FROG_BRAIN_YSIZE; y++) {
					if (f.cubes[x][y] != null)
						for (int z = 0; z < Env.FROG_BRAIN_ZSIZE; z++) {
							if (f.existCube(x, y, z) && f.getCube(x, y, z).getActive() > 0) {
								setColor(rainbowColor(f.getCube(x, y, z).getActive()));
								drawCubeCenter(x, y, z);
							}
						}
				}

		}
	}

	// getters & setters
	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getxAngle() {
		return xAngle;
	}

	public void setxAngle(float xAngle) {
		this.xAngle = xAngle;
	}

	public float getyAngle() {
		return yAngle;
	}

	public void setyAngle(float yAngle) {
		this.yAngle = yAngle;
	}

	public float getzAngle() {
		return zAngle;
	}

	public void setzAngle(float zAngle) {
		this.zAngle = zAngle;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getxOffset() {
		return xOffset;
	}

	public void setxOffset(int xOffset) {
		this.xOffset = xOffset;
	}

	public int getyOffset() {
		return yOffset;
	}

	public void setyOffset(int yOffset) {
		this.yOffset = yOffset;
	}

}
