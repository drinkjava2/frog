package com.gitee.drinkjava2.frog.brain;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * MouseAction
 * 
 * 这个类用来处理脑图BrainPicture上的鼠标动作，有平移、旋转、缩放三种
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
public class MouseAction implements MouseListener, MouseWheelListener, MouseMotionListener {
	private BrainPicture brainPic;
	private int buttonPressed = 0;
	private int x;
	private int y;

	public MouseAction(BrainPicture brainPic) {
		this.brainPic = brainPic;
	}

	@Override
	public void mousePressed(MouseEvent e) {// 记录当前鼠标点
		if (e.getButton() == 1)// 旋转
			buttonPressed = 1;
		else if (e.getButton() == 2)// 缩放
			buttonPressed = 2;
		else
			buttonPressed = 0;
		x = e.getPoint().x;
		y = e.getPoint().y;
		brainPic.requestFocus();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		buttonPressed = 0;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {// 缩放
		if (e.getWheelRotation() < 0) {
			brainPic.scale *= 1.1;
			brainPic.xOffset *= 1.1;
			brainPic.yOffset *= 1.1;
		} else {
			brainPic.scale /= 1.1;
			brainPic.xOffset /= 1.1;
			brainPic.yOffset /= 1.1;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {// 旋转
		if (buttonPressed == 1) {
			if (e.getX() > x && e.getY() > y)
				brainPic.zAngle -= .00f;
			else if (e.getX() < x && e.getY() < y)
				brainPic.zAngle += .00f;
			else {
				if (e.getX() > x)
					brainPic.yAngle += .02f;
				if (e.getX() < x)
					brainPic.yAngle -= .02f;
				if (e.getY() > y)
					brainPic.xAngle -= .02f;
				if (e.getY() < y)
					brainPic.xAngle += .02f;
			}
			x = e.getX();
			y = e.getY();
		}
		if (buttonPressed == 2) {// 平移
			if (e.getX() > x)
				brainPic.xOffset += 6;
			if (e.getX() < x)
				brainPic.xOffset -= 6;
			if (e.getY() > y)
				brainPic.yOffset += 6;
			if (e.getY() < y)
				brainPic.yOffset -= 6;
			x = e.getX();
			y = e.getY();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {// do nothing
	}

	@Override
	public void mouseEntered(MouseEvent e) {// do nothing
	}

	@Override
	public void mouseExited(MouseEvent e) {// do nothing
	}

	@Override
	public void mouseMoved(MouseEvent e) { // do nothing
	}
}
