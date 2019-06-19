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
public class BrainPicture extends JPanel {
	private float brainWidth; // real brain width
	private int brainDispWidth; // screen display width

	public BrainPicture(int x, int y, float brainWidth, int brainDispWidth) {
		super();
		this.setLayout(null);// 空布局
		this.brainDispWidth = brainDispWidth;
		this.brainWidth = brainWidth;
		this.setBounds(x, y, brainDispWidth + 1, brainDispWidth + 1);
	}

	public void drawZone(Graphics g, Zone z) {
		float rate = brainDispWidth / brainWidth;
		int x = Math.round(z.x * rate);
		int y = Math.round(z.y * rate);
		int radius = Math.round(z.r * rate);
		g.drawRect(x - radius, y - radius, radius * 2, radius * 2);
	}

	public void drawCircle(Graphics g, Zone z) {
		float rate = brainDispWidth / brainWidth;
		int x = Math.round(z.x * rate);
		int y = Math.round(z.y * rate);
		g.drawArc(x - 5, y - 5, 10, 10, 0, 360);
	}

	public void fillZone(Graphics g, Zone z) {
		float rate = brainDispWidth / brainWidth;
		int x = Math.round(z.x * rate);
		int y = Math.round(z.y * rate);
		int radius = Math.round(z.r * rate);
		g.fillRect(x - radius, y - radius, radius * 2, radius * 2);
	}

	public void drawLine(Graphics g, Zone z1, Zone z2) {
		float rate = brainDispWidth / brainWidth;
		int x1 = Math.round(z1.x * rate);
		int y1 = Math.round(z1.y * rate);
		int x2 = Math.round(z2.x * rate);
		int y2 = Math.round(z2.y * rate);
		g.drawLine(x1, y1, x2, y2);
	}

	public void drawText(Graphics g, Zone z, String text) {
		float rate = brainDispWidth / brainWidth;
		int x = Math.round(z.x * rate);
		int y = Math.round(z.y * rate);
		g.drawString(text, x - text.length() * 3 - 2, y);
	}

	private static final Color[] rainbow = new Color[] { RED, ORANGE, YELLOW, GREEN, CYAN, BLUE, MAGENTA };
	private static int nextColor = 0;

	public static Color nextRainbowColor() {
		if (nextColor == rainbow.length)
			nextColor = 0;
		return rainbow[nextColor++];
	}

	public static Color color(float i) {
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

	public void drawBrainPicture(Frog frog) {
		if (!Application.SHOW_FIRST_FROG_BRAIN)
			return;
		Graphics g = this.getGraphics();// border
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, brainDispWidth, brainDispWidth);
		g.setColor(Color.black);
		g.drawRect(0, 0, brainDispWidth, brainDispWidth);

		for (Organ organ : frog.organs)
			organ.drawOnBrainPicture(this); // each organ draw itself
	}
}
