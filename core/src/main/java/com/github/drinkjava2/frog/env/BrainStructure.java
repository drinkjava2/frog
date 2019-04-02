package com.github.drinkjava2.frog.env;

import static java.lang.Math.round;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.egg.CellGroup;
import com.github.drinkjava2.frog.egg.Zone;

/**
 * BrainStructure show first frog's brain structure, for debug purpose
 */
@SuppressWarnings("serial")
public class BrainStructure extends JPanel {

	public BrainStructure() {
		super();
		this.setLayout(null);// 空布局
		this.setBounds(500, 0, 1000, 1000);
	}

	void drawZone(Graphics g, Zone z) {
		g.drawRect(round(z.x - z.radius), round(z.y - z.radius), round(z.radius * 2), round(z.radius * 2));
	}

	void drawZoneCircle(Graphics g, Zone z) {
		g.drawArc(round(z.x - 8), round(z.y - 8), 16, 16, 0, 360);
	}

	void fillZone(Graphics g, Zone z) {
		g.fillRect(round(z.x - z.radius), round(z.y - z.radius), round(z.radius * 2), round(z.radius * 2));
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

	public void drawBrain(Frog frog) {
		if (!Application.SHOW_FIRST_FROG_BRAIN)
			return;
		Graphics g = this.getGraphics();// border
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 800, 800);
		g.setColor(Color.black);
		g.drawRect(0, 0, 800, 800);

		g.setColor(Color.red);
		drawZone(g, frog.eye); // eye

		g.setColor(Color.green);
		drawZone(g, frog.happy); // happy

		g.setColor(Color.yellow);
		drawZone(g, frog.hungry); // hungry

		g.setColor(Color.gray);
		drawZone(g, frog.moveUp); // moves
		drawZone(g, frog.moveDown);
		drawZone(g, frog.moveLeft);
		drawZone(g, frog.moveRight);

		for (CellGroup group : frog.cellGroups) {
			if (!group.inherit)
				g.setColor(Color.lightGray); // 如果是本轮随机生成的，灰色表示
			else
				g.setColor(color(group.cellQty)); // 如果是继承的，彩虹色表示，颜色数越往后表示数量越多
			g.drawLine(round(group.groupInputZone.x), round(group.groupInputZone.y), round(group.groupOutputZone.x),
					round(group.groupOutputZone.y));
			drawZone(g, group.groupInputZone);
		 
			fillZone(g, group.groupOutputZone);
			if (group.fat > 0) {
				g.setColor(Color.BLACK);
				drawZoneCircle(g, group.groupOutputZone); // 如果胖了，表示激活过了，下次下蛋少不了这一组
				}
		}

	}
}
