package com.gitee.drinkjava2.frog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.gitee.drinkjava2.frog.brain.BrainPicture;
import com.gitee.drinkjava2.frog.brain.Genes;
import com.gitee.drinkjava2.frog.egg.FrogEggTool;

/**
 * Application's main method start the program
 * Application是程序入口
 * 
 * 关于本项目代码格式化工具的约定:
 * 1.不使用tab，而是使用空白符
 * 2.源码采用UTF-8编码
 * 3.源码换行设定为UNIX风格单个LF字符结尾。 git里设定git config --global core.autocrlf input，即提交时把CRLF改成单个LF字符，签出时不改
 * 4.所有注释内容不允许被格式化，即：
 *    Never join lines 设为true，不允许自动合并注释行 
 *    Enable Javadoc comment formatting 设为false
 *    Enable block comment formatting 设为false
 *    Enable line comment formatting 设为false
 * 5.其他人提交时，只能修改自已修改的部分，不要随便使用代码格式化工具。如果要使用代码格式化工具，也必须参照以上内容设置成不能变动未修改的其它行。 
 * 
 * @author Yong Zhu
 * @since 1.0
 */
@SuppressWarnings("all")
public class Application {

    public static final String CLASSPATH;

    static {
        String classpath = new File("").getAbsolutePath();
        int i = classpath.lastIndexOf("\\frog\\");
        if (i > 0)
            CLASSPATH = classpath.substring(0, i) + "\\frog\\";// windows
        else
            CLASSPATH = classpath + "/"; // UNIX
    }

    public static JFrame mainFrame = new JFrame();
    public static Env env = new Env();
    public static BrainPicture brainPic = new BrainPicture(Env.ENV_WIDTH + 5, 0, Env.BRAIN_SIZE, Env.FROG_BRAIN_DISP_WIDTH);
    public static ActionListener pauseAction;
    public static boolean selectFrog = true;

    private static void checkIfShowBrainPicture(JButton button) {
        int y = Env.ENV_HEIGHT + 150;
        if (Env.SHOW_FIRST_ANIMAL_BRAIN) {
            button.setText("Hide brain");
            if (Env.FROG_BRAIN_DISP_WIDTH + 41 > y)
                y = Env.FROG_BRAIN_DISP_WIDTH + 41;
            mainFrame.setSize(Env.ENV_WIDTH + Env.FROG_BRAIN_DISP_WIDTH + 25, y);
            brainPic.requestFocus();
        } else {
            button.setText("Show brain");
            mainFrame.setSize(Env.ENV_WIDTH + 20, y);
        }
    }

    public static void main(String[] args) {
        mainFrame.setLayout(null);
        mainFrame.setSize(Env.ENV_WIDTH + 200, Env.ENV_HEIGHT + 150); // 窗口大小
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭时退出程序
        mainFrame.add(env); // 添加虚拟环境Panel
        mainFrame.add(brainPic); // 添加脑图Panel

        JButton button = new JButton("Show brain");// 按钮，显示或隐藏脑图
        int buttonWidth = 100;
        int buttonHeight = 22;
        int buttonXpos = Env.ENV_WIDTH / 2 - buttonWidth / 2;
        button.setBounds(buttonXpos, Env.ENV_HEIGHT + 8, buttonWidth, buttonHeight);
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {// 显示或隐藏脑图
                Env.SHOW_FIRST_ANIMAL_BRAIN = !Env.SHOW_FIRST_ANIMAL_BRAIN;
                checkIfShowBrainPicture(button);
            }
        };
        checkIfShowBrainPicture(button);
        button.addActionListener(al);
        mainFrame.add(button);

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
        final JSlider speedSlider = new JSlider(1, 10, (int) Math.round(Math.pow(Env.SHOW_SPEED, 1.0 / 3)));
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

        // 是否把egg文件存盘
        JCheckBox saveFileCheckBox = new JCheckBox("Save egg");
        saveFileCheckBox.setBounds(buttonXpos-50, Env.ENV_HEIGHT + 80, 90, 22);
        ActionListener saveAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (saveFileCheckBox.isSelected())
                    Env.SAVE_EGGS_FILE = true;
                else
                    Env.SAVE_EGGS_FILE = false;
            }
        };
        saveFileCheckBox.addActionListener(saveAction);
        mainFrame.add(saveFileCheckBox); 
        
        //删除蛋文件按钮
        JButton deleteEggButton = new JButton("Delete egg");
        deleteEggButton.setBounds(buttonXpos + 50, Env.ENV_HEIGHT + 80, buttonWidth, buttonHeight);
        deleteEggButton.addActionListener((e) -> {
            FrogEggTool.deleteEggs();
        });
        mainFrame.add(deleteEggButton);
        

        // 基因维数显示控制
        for (int i = 0; i < Genes.GENE_NUMBERS; i++) {
            JRadioButton geneRadio = new JRadioButton();
            geneRadio.setBounds(buttonXpos + 300 + i * 16, Env.ENV_HEIGHT + 8, 20, 22);
            geneRadio.setSelected(Genes.display_gene[i]);
            geneRadio.setName("" + i);
            ActionListener geneRadioAction = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int i = Integer.parseInt(geneRadio.getName());
                    if (geneRadio.isSelected())
                        Genes.display_gene[i] = true;
                    else
                        Genes.display_gene[i] = false;
                }
            };
            geneRadio.addActionListener(geneRadioAction);
            mainFrame.add(geneRadio);
        }

        // 是否显示分裂过程
        JCheckBox showSplitDetailCheckBox = new JCheckBox("Show split detail");
        showSplitDetailCheckBox.setBounds(buttonXpos + 300, Env.ENV_HEIGHT + 40, 120, 22);
        ActionListener detailAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (showSplitDetailCheckBox.isSelected())
                    Env.show_split_detail = true;
                else
                    Env.show_split_detail = false;
            }
        };
        showSplitDetailCheckBox.addActionListener(detailAction);
        mainFrame.add(showSplitDetailCheckBox);
        mainFrame.setBounds(0,590, 900, 550);
        //mainFrame.setBounds(0,100, 1, 1);
        mainFrame.setVisible(true);
        env.run();
    }

}
