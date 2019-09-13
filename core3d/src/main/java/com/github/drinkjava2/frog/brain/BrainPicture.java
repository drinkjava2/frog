package com.github.drinkjava2.frog.brain;

import static java.awt.Color.BLUE;
import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.MAGENTA;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.awt.Color.YELLOW;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.github.drinkjava2.frog.Application;
import com.github.drinkjava2.frog.Frog;

/**
 * BrainPicture show first frog's brain structure, for debug purpose only
 * 
 * @author Yong Zhu
 * @since 1.0
 */
@SuppressWarnings("serial")
public class BrainPicture extends JPanel {// TODO: work on here
	private int brainDispWidth; // screen display piexls width
	private float scale; // brain scale
	private float xAngle; // brain rotate on x axis
	private float yAngle; // brain rotate on y axis
	private float zAngle;// brain rotate on z axis
	private Color color = Color.BLACK;
	private Graphics g = this.getGraphics();

	public BrainPicture(int x, int y, float brainWidth, int brainDispWidth) {
		super();
		this.setLayout(null);// 空布局
		this.brainDispWidth = brainDispWidth;
		scale = brainDispWidth / brainWidth;
		this.setBounds(x, y, brainDispWidth + 1, brainDispWidth + 1);
	}

	public void drawCube(Cube c) {
		float x = c.x;
		float y = c.y;
		float z = c.z;
		float r = c.r;
		drawLine(x - r, y - r, z + r, x - r, y + r, z + r);//画立方体的12条边
		drawLine(x - r, y + r, z + r, x + r, y + r, z + r);
		drawLine(x + r, y + r, z + r, x + r, y - r, z + r);
		drawLine(x + r, y + r, z + r, x - r, y - r, z + r);

		drawLine(x - r, y - r, z - r, x - r, y + r, z - r);
		drawLine(x - r, y + r, z - r, x + r, y + r, z - r);
		drawLine(x + r, y + r, z - r, x + r, y - r, z - r);
		drawLine(x + r, y + r, z - r, x - r, y - r, z - r);

		drawLine(x - r, y - r, z + r, x - r, y - r, z - r);
		drawLine(x + r, y - r, z + r, x + r, y - r, z - r);
		drawLine(x + r, y + r, z + r, x + r, y + r, z - r);
		drawLine(x - r, y + r, z + r, x - r, y + r, z - r);
	}

	public void drawLine(float x1, float y1, float z1, float x2, float y2, float z2) {
		g.setColor(color);
		Double r1=Math.sqrt(x1*x1+y1*y1+z1*z1);
		Double r2=Math.sqrt(x1*x1+y1*y1+z1*z1);
		Double newx1=0d;
		
	}

	public void fillZone(Cube z) {
		g.setColor(color);
		int x = Math.round(z.x * scale);
		int y = Math.round(z.y * scale);
		int radius = Math.round(z.r * scale);
		g.fillRect(x - radius, y - radius, radius * 2, radius * 2);
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
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, brainDispWidth, brainDispWidth);
		g.setColor(Color.black);
		g.drawRect(0, 0, brainDispWidth, brainDispWidth);

		for (Organ organ : frog.organs)
			organ.drawOnBrainPicture(frog, this); // each organ draw itself
	}

	// getter & setters
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

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
