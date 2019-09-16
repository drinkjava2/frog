package com.github.drinkjava2.frog.brain;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * BrainPicture show first frog's brain structure, for debug purpose only
 * 
 * @author Yong Zhu
 * @since 1.0
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
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == 1)
			buttonPressed = 1;
		else if (e.getButton() == 2)
			buttonPressed = 2;
		else
			buttonPressed = 0;
		x = e.getPoint().x;
		y = e.getPoint().y;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		buttonPressed = 0;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0)
			brainPic.scale *= 1.1;
		else
			brainPic.scale /= 1.1;
	}

	@Override
	public void mouseDragged(MouseEvent e) {// do nothing
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
		if (buttonPressed == 2) {
			if (e.getX() > x)
				brainPic.xOffset++;
			if (e.getX() < x)
				brainPic.xOffset--;
			if (e.getY() > y)
				brainPic.yOffset++;
			if (e.getY() < y)
				brainPic.yOffset--;
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
