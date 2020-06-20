package com.gitee.drinkjava2.frog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.gitee.drinkjava2.frog.brain.BrainPicture;

/**
 * Application's main method start the program
 * 
 * @author Yong Zhu
 * @since 1.0
 */
@SuppressWarnings("all")
public class Application {

	public static final String CLASSPATH;

	static {
		String classpath = new File("").getAbsolutePath();
		int i = classpath.indexOf("\\frog\\");
		if (i > 0)
			CLASSPATH = classpath.substring(0, i) + "\\frog\\";// windows
		else
			CLASSPATH = classpath + "/"; // UNIX
	}

	public static JFrame mainFrame = new JFrame();
	public static Env env = new Env();
	public static BrainPicture brainPic = new BrainPicture(Env.ENV_WIDTH + 5, 0, Env.FROG_BRAIN_XSIZE,
			Env.FROG_BRAIN_DISP_WIDTH);
	public static ActionListener pauseAction;
	public static boolean selectFrog = true;

	private static void checkIfShowBrainPicture(JButton button) {
		if (Env.SHOW_FIRST_ANIMAL_BRAIN) {
			button.setText("Hide brain");
			int y = Env.ENV_HEIGHT + 120;
			if (Env.FROG_BRAIN_DISP_WIDTH + 41 > y)
				y = Env.FROG_BRAIN_DISP_WIDTH + 41;
			mainFrame.setSize(Env.ENV_WIDTH + Env.FROG_BRAIN_DISP_WIDTH + 25, y);
			brainPic.requestFocus(); 
		} else {
			button.setText("Show brain");
			mainFrame.setSize(Env.ENV_WIDTH + 20, Env.ENV_HEIGHT + 120);
		}
	}

	public static void main(String[] args) {
		mainFrame.setLayout(null);
		mainFrame.setSize(Env.ENV_WIDTH + 20, Env.ENV_HEIGHT + 100); // 窗口大小
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭时退出程序
		mainFrame.add(env); // 添加虚拟环境Panel
		mainFrame.add(brainPic); // 添加脑图Panel

		JButton button = new JButton("Show brain");// 按钮，显示或隐藏脑图
		int buttonWidth = 100;
		int buttonHeight = 22;
		int buttonXpos = Env.ENV_WIDTH / 2 - buttonWidth / 2;

		// select frog or snake 显示青蛙或蛇的RadioButton
		JRadioButton radioFrog = new JRadioButton("Frog");
		radioFrog.setBounds(buttonXpos + buttonWidth + 10, Env.ENV_HEIGHT + 8, 50, buttonHeight);
		radioFrog.addActionListener(e -> {
			selectFrog = radioFrog.isSelected();
			checkIfShowBrainPicture(button); 
		});
		JRadioButton radioSnake = new JRadioButton("Snake");

		button.setBounds(buttonXpos, Env.ENV_HEIGHT + 8, buttonWidth, buttonHeight);
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {//显示或隐藏脑图
				Env.SHOW_FIRST_ANIMAL_BRAIN = !Env.SHOW_FIRST_ANIMAL_BRAIN;
				if (Env.SHOW_FIRST_ANIMAL_BRAIN && Env.SNAKE_MODE) {
					radioFrog.setVisible(true);
					radioSnake.setVisible(true);
				} else {
					radioFrog.setVisible(false);
					radioSnake.setVisible(false);
				}
				checkIfShowBrainPicture(button);
			}
		};
		checkIfShowBrainPicture(button);
		button.addActionListener(al);
		mainFrame.add(button);

		radioSnake.setBounds(buttonXpos + buttonWidth + 60, Env.ENV_HEIGHT + 8, 80, buttonHeight);
		radioSnake.addActionListener(e -> {
			selectFrog = radioFrog.isSelected();
			checkIfShowBrainPicture(button);
		});
		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(radioFrog);
		btnGroup.add(radioSnake);
		radioFrog.setSelected(true);
		mainFrame.add(radioFrog);
		mainFrame.add(radioSnake);

		JButton stopButton = new JButton("Pause");// 暂停或继续按钮
		stopButton.setBounds(buttonXpos, Env.ENV_HEIGHT + 35, buttonWidth, buttonHeight);
		pauseAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Env.pause = !Env.pause;
				if (Env.pause) {
					stopButton.setText("Resume");
				} else {
					stopButton.setText("Pause");
					brainPic.requestFocus();
				}
			}
		};
		stopButton.addActionListener(pauseAction);
		mainFrame.add(stopButton);

		// 速度条
		final JSlider speedSlider = new JSlider(1, 10, (int) Math.round(Math.sqrt(Env.SHOW_SPEED)));
		speedSlider.setBounds(buttonXpos - 50, stopButton.getY() + 25, buttonWidth + 100, buttonHeight);
		ChangeListener slideAction = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Env.SHOW_SPEED = speedSlider.getValue() * speedSlider.getValue() * speedSlider.getValue();
				brainPic.requestFocus();
			}
		};
		speedSlider.addChangeListener(slideAction);
		mainFrame.add(speedSlider);
		final JLabel label = new JLabel("Speed:");
		label.setBounds(buttonXpos - 90, stopButton.getY() + 23, 100, buttonHeight);
		mainFrame.add(label);

		// 允许加入蛇进来
		JCheckBox snakeModeCkBox = new JCheckBox("Snake Mode");
		snakeModeCkBox.setBounds(buttonXpos, speedSlider.getY() + 25, 120, buttonHeight);
		snakeModeCkBox.addActionListener(e -> {
			Env.SNAKE_MODE = snakeModeCkBox.isSelected();
			if (Env.SHOW_FIRST_ANIMAL_BRAIN && Env.SNAKE_MODE) {
				radioFrog.setVisible(true);
				radioSnake.setVisible(true);
			} else {
				radioFrog.setSelected(true);
				selectFrog = true;
				radioFrog.setVisible(false);
				radioSnake.setVisible(false);
			}
		});
		snakeModeCkBox.setSelected(true);
		mainFrame.add(snakeModeCkBox);

		mainFrame.setVisible(true);
		env.run();
	}

}
