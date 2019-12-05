package com.github.drinkjava2.frog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.github.drinkjava2.frog.brain.BrainPicture;

/**
 * Application's main method start the program
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Application {
	/** SHOW first frog's brain structure */
	public static boolean SHOW_FIRST_FROG_BRAIN = false;
	public static final String CLASSPATH;

	static {
		String classpath = new File("").getAbsolutePath();
		int core = classpath.indexOf("\\frog\\");
		if (core > 0)
			CLASSPATH = classpath.substring(0, core) + "\\frog\\";
		else
			CLASSPATH = classpath.substring(0, core) + "/frog/"; // UNIX
	}
	public static JFrame mainFrame = new JFrame();
	public static Env env = new Env();
	public static BrainPicture brainPic = new BrainPicture(Env.ENV_WIDTH + 5, 0, Env.FROG_BRAIN_WIDTH,
			Env.FROG_BRAIN_DISP_WIDTH);

	public static void main(String[] args) throws InterruptedException {
		mainFrame.setLayout(null);
		mainFrame.setSize(Env.ENV_WIDTH + 20, Env.ENV_HEIGHT + 100); // 窗口大小
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭时退出程序
		mainFrame.add(env);

		mainFrame.add(brainPic);

		JButton button = new JButton("Show first frog's brain");
		int buttonWidth = 180;
		int buttonHeight = 22;
		int buttonXpos = Env.ENV_WIDTH / 2 - buttonWidth / 2;
		button.setBounds(buttonXpos, Env.ENV_HEIGHT + 8, buttonWidth, buttonHeight);
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SHOW_FIRST_FROG_BRAIN = !SHOW_FIRST_FROG_BRAIN;
				if (SHOW_FIRST_FROG_BRAIN) {
					button.setText("Hide first frog's brain");
					int y = Env.ENV_HEIGHT + 100;
					if (Env.FROG_BRAIN_DISP_WIDTH + 41 > y)
						y = Env.FROG_BRAIN_DISP_WIDTH + 41;
					mainFrame.setSize(Env.ENV_WIDTH + Env.FROG_BRAIN_DISP_WIDTH + 25, y);
				} else {
					button.setText("Show first frog's brain");
					mainFrame.setSize(Env.ENV_WIDTH + 20, Env.ENV_HEIGHT + 100);
				}
			}
		};
		button.addActionListener(al);
		mainFrame.add(button);

		JButton stopButton = new JButton("Pause");
		stopButton.setBounds(buttonXpos, Env.ENV_HEIGHT + 35, buttonWidth, buttonHeight);
		ActionListener a2 = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Env.pause = !Env.pause;
				if (Env.pause) {
					stopButton.setText("Resume");
				} else {
					stopButton.setText("Pause");
				}
			}
		};
		stopButton.addActionListener(a2);
		mainFrame.add(stopButton);

		mainFrame.setVisible(true);
		env.run();
	}

}
