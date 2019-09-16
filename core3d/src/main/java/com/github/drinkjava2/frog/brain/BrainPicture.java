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

import com.github.drinkjava2.frog.Application;
import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;

/**
 * BrainPicture show first frog's brain structure, for debug purpose only
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
	// float xAngle = (float) (Math.PI / 4); // brain rotate on x axis
	// float yAngle = (float) (Math.PI / 4); // brain rotate on y axis
	// float zAngle = (float) (Math.PI / 4);// brain rotate on z axis
	float xAngle = 0; // brain rotate on x axis
	float yAngle = 0; // brain rotate on y axis
	float zAngle = 0;// brain rotate on z axis

	public BrainPicture(int x, int y, float brainWidth, int brainDispWidth) {
		super();
		this.setLayout(null);// 空布局
		this.brainDispWidth = brainDispWidth;
		scale = 0.6f * brainDispWidth / brainWidth;
		this.setBounds(x, y, brainDispWidth + 1, brainDispWidth + 1);
		MouseAction act = new MouseAction(this);
		this.addMouseListener(act);
		this.addMouseWheelListener(act);
		this.addMouseMotionListener(act);
	}

	public void drawCube(Cuboid c) {
		float x = c.x;
		float y = c.y;
		float z = c.z;
		float xr = c.xr;
		float yr = c.yr;
		float zr = c.zr;

		setColor(Color.BLUE);
		drawTopViewLine(x - xr, y - yr, z - zr, x - xr, y + yr, z - zr);// 画立方体的下面边
		drawTopViewLine(x - xr, y + yr, z - zr, x + xr, y + yr, z - zr);
		drawTopViewLine(x + xr, y + yr, z - zr, x + xr, y - yr, z - zr);
		drawTopViewLine(x + xr, y - yr, z - zr, x - xr, y - yr, z - zr);

		setColor(Color.green);
		drawTopViewLine(x - xr, y - yr, z + zr, x - xr, y - yr, z - zr);// 画立方体的中间边
		setColor(Color.YELLOW);
		drawTopViewLine(x + xr, y - yr, z + zr, x + xr, y - yr, z - zr);
		setColor(Color.YELLOW);
		drawTopViewLine(x + xr, y + yr, z + zr, x + xr, y + yr, z - zr);
		setColor(Color.green);
		drawTopViewLine(x - xr, y + yr, z + zr, x - xr, y + yr, z - zr);

		setColor(Color.red);
		drawTopViewLine(x - xr, y - yr, z + zr, x - xr, y + yr, z + zr); // 画立方体的上面边
		drawTopViewLine(x - xr, y + yr, z + zr, x + xr, y + yr, z + zr);
		drawTopViewLine(x + xr, y + yr, z + zr, x + xr, y - yr, z + zr);
		drawTopViewLine(x + xr, y - yr, z + zr, x - xr, y - yr, z + zr);
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
	private void drawTopViewLine(float px1, float py1, float pz1, float px2, float py2, float pz2) {
		double x1 = px1 * scale, y1 = py1 * scale, z1 = pz1 * scale, x2 = px2 * scale, y2 = py2 * scale,
				z2 = pz2 * scale;
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

	private static final Color[] rainbow = new Color[] { RED, ORANGE, YELLOW, GREEN, CYAN, BLUE, MAGENTA };
	private static int nextColor = 0;

	public static Color nextRainbowColor() {
		if (nextColor == rainbow.length)
			nextColor = 0;
		return rainbow[nextColor++];
	}

	public void drawBrainPicture(Frog frog) {
		if (!Application.SHOW_FIRST_FROG_BRAIN)
			return;
		Graphics g = this.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, brainDispWidth, brainDispWidth);
		g.setColor(Color.black);
		g.drawRect(0, 0, brainDispWidth, brainDispWidth);

		for (Organ organ : frog.organs)
			organ.drawOnBrainPicture(frog, this); // each organ draw itself
	}

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
