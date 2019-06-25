package com.github.drinkjava2.frog.env;

import java.awt.Color;
import static java.awt.Color.*;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.egg.CellGroup;
import com.github.drinkjava2.frog.egg.Zone;

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

	void drawZone(Graphics g, Zone z) {
		float rate = brainDispWidth / brainWidth;
		int x = Math.round(z.x * rate);
		int y = Math.round(z.y * rate);
		int radius = Math.round(z.radius * rate);
		g.drawRect(x - radius, y - radius, radius * 2, radius * 2);
	}

	void drawCircle(Graphics g, Zone z) {
		float rate = brainDispWidth / brainWidth;
		int x = Math.round(z.x * rate);
		int y = Math.round(z.y * rate);
		g.drawArc(x - 5, y - 5, 10, 10, 0, 360);
	}

	void fillZone(Graphics g, Zone z) {
		float rate = brainDispWidth / brainWidth;
		int x = Math.round(z.x * rate);
		int y = Math.round(z.y * rate);
		int radius = Math.round(z.radius * rate);
		g.fillRect(x - radius, y - radius, radius * 2, radius * 2);
	}

	void drawLine(Graphics g, Zone z1, Zone z2) {
		float rate = brainDispWidth / brainWidth;
		int x1 = Math.round(z1.x * rate);
		int y1 = Math.round(z1.y * rate);
		int x2 = Math.round(z2.x * rate);
		int y2 = Math.round(z2.y * rate);
		g.drawLine(x1, y1, x2, y2);
	}

	void drawText(Graphics g, Zone z, String text) {
		float rate = brainDispWidth / brainWidth;
		int x = Math.round(z.x * rate);
		int y = Math.round(z.y * rate);
		g.drawString(text, x, y);
	}

	private static final Color[] rainbow = new Color[] { RED, ORANGE, YELLOW, GREEN, CYAN, BLUE, MAGENTA };
	private static int nextColor = 0;

	private static Color nextRainbowColor() {
		if (nextColor == rainbow.length)
			nextColor = 0;
		return rainbow[nextColor++];
	}

	private static Color color(float i) {
		if (i <= 1)
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

		for (Organ organ : frog.organs) {
			g.setColor(nextRainbowColor());
			drawZone(g, organ);
			drawText(g, organ, String.valueOf(organ.type));
		}

		for (CellGroup group : frog.cellGroups) {
			if (!group.inherit)
				g.setColor(Color.lightGray); // 如果是本轮随机生成的，灰色表示
			else
				g.setColor(color(group.cellQty)); // 如果是继承的，彩虹色表示，颜色数越往后表示数量越多
			drawLine(g, group.groupInputZone, group.groupOutputZone);
			drawZone(g, group.groupInputZone);
			fillZone(g, group.groupOutputZone);
			if (group.fat > 0) {
				g.setColor(Color.BLACK);
				drawCircle(g, group.groupOutputZone); // 如果胖了，表示激活过了，下次下蛋少不了这一组
			}
		}

	}
}
