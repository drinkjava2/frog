package com.gitee.drinkjava2.frog.brain;

import static java.awt.Color.BLACK;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
//import static java.awt.BLUE; 
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.gitee.drinkjava2.frog.Application;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;

/**
 * BrainPicture show first frog's brain structure, for debug purpose only
 * 
 * 这个类用来画出脑图，这不是一个关键类，对脑的运行逻辑无影响，但有了脑图后可以直观地看出脑的3维结构，进行有针对性的改进
 * 可以用鼠标进行平移、缩放、旋转，以及t、f、l、r,x五个键来选择顶视、前视、左视、右视、斜视这5个方向的视图，以及空格暂停、方向键调整切面
 * 鼠标的动作定义在MouseAction类中。
 * 
 * @author Yong Zhu
 * @since 1.0
 */
@SuppressWarnings("all")
public class BrainPicture extends JPanel {
    private static final float d90 = (float) (Math.PI / 2);

    Color picColor = RED;
    int brainDispWidth; // screen display piexls width
    float scale; // brain scale
    int xOffset = 0; // brain display x offset compare to screen
    int yOffset = 0; // brain display y offset compare to screen
    float xAngle = d90 * .8f; // brain rotate on x axis
    float yAngle = d90 / 4; // brain rotate on y axis
    float zAngle = 0;// brain rotate on z axis
    int xMask = -1;// x Mask
    int yMask = -1;// y Mask
    BufferedImage buffImg;
    Graphics g;
    public KeyAdapter keyAdapter;

    public BrainPicture(int x, int y, float brainWidth, int brainDispWidth) {
        super();
        this.setLayout(null);// 空布局
        this.brainDispWidth = brainDispWidth;
        scale = 0.5f * brainDispWidth / brainWidth;
        this.setBounds(x, y, brainDispWidth + 1, brainDispWidth + 1);
        buffImg = new BufferedImage(Env.FROG_BRAIN_DISP_WIDTH, Env.FROG_BRAIN_DISP_WIDTH, BufferedImage.TYPE_INT_RGB);
        g = buffImg.getGraphics();
        MouseAction act = new MouseAction(this);
        this.addMouseListener(act); // 添加鼠标动作监听
        this.addMouseWheelListener(act);// 添加鼠标滚轮动作监听
        this.addMouseMotionListener(act);// 添加鼠标移动动作监听

        keyAdapter = new KeyAdapter() {// 处理t,f,l,r，x键盘命令
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()){
                case KeyEvent.VK_UP:// Y切面向上
                    yMask++;
                    if (yMask > Env.FROG_BRAIN_YSIZE)
                        yMask = Env.FROG_BRAIN_YSIZE;
                    break;
                case KeyEvent.VK_DOWN:// Y切面向下
                    yMask--;
                    if (yMask < 0)
                        yMask = 0;
                    break;
                case KeyEvent.VK_LEFT:// x切面向左
                    xMask--;
                    if (xMask < 0)
                        xMask = 0;
                    break;
                case KeyEvent.VK_RIGHT:// x切面向右
                    xMask++;
                    if (xMask > Env.FROG_BRAIN_XSIZE)
                        xMask = Env.FROG_BRAIN_XSIZE;
                    break;
                case ' ':// 暂停及继续
                    Application.pauseAction.actionPerformed(null);
                    break;
                case 'T':// 顶视
                    xAngle = 0;
                    yAngle = 0;
                    zAngle = 0;
                    break;
                case 'F':// 前视
                    xAngle = d90;
                    yAngle = 0;
                    zAngle = 0;
                    break;
                case 'L':// 左视
                    xAngle = d90;
                    yAngle = d90;
                    zAngle = 0;
                    break;
                case 'R':// 右视
                    xAngle = d90;
                    yAngle = -d90;
                    zAngle = 0;
                    break;
                case 'X':// 斜视
                    xAngle = d90 * .8f;
                    yAngle = d90 / 4;
                    zAngle = 0;
                    break;
                default:
                }
            }
        };
        addKeyListener(keyAdapter);
        this.setFocusable(true);
    }

    public void drawCuboid(Cuboid c) {// 在脑图上画一个长立方体框架，视角是TopView
        float x = c.x;
        float y = c.y;
        float z = c.z;
        float xe = c.xe;
        float ye = c.ye;
        float ze = c.ze;

        drawLine(x, y, z, x + xe, y, z);// 画立方体的下面边
        drawLine(x + xe, y, z, x + xe, y + ye, z);
        drawLine(x + xe, y + ye, z, x, y + ye, z);
        drawLine(x, y + ye, z, x, y, z);

        drawLine(x, y, z, x, y, z + ze);// 画立方体的中间边
        drawLine(x + xe, y, z, x + xe, y, z + ze);
        drawLine(x + xe, y + ye, z, x + xe, y + ye, z + ze);
        drawLine(x, y + ye, z, x, y + ye, z + ze);

        drawLine(x, y, z + ze, x + xe, y, z + ze);// 画立方体的上面边
        drawLine(x + xe, y, z + ze, x + xe, y + ye, z + ze);
        drawLine(x + xe, y + ye, z + ze, x, y + ye, z + ze);
        drawLine(x, y + ye, z + ze, x, y, z + ze);
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
    public void drawLine(float px1, float py1, float pz1, float px2, float py2, float pz2) {
        double x1 = px1 - Env.FROG_BRAIN_XSIZE / 2;
        double y1 = -py1 + Env.FROG_BRAIN_YSIZE / 2;// 屏幕的y坐标是反的，显示时要正过来
        double z1 = pz1 - Env.FROG_BRAIN_ZSIZE / 2;
        double x2 = px2 - Env.FROG_BRAIN_XSIZE / 2;
        double y2 = -py2 + Env.FROG_BRAIN_YSIZE / 2;// 屏幕的y坐标是反的，显示时要正过来
        double z2 = pz2 - Env.FROG_BRAIN_ZSIZE / 2;
        x1 = x1 * scale;
        y1 = y1 * scale;
        z1 = z1 * scale;
        x2 = x2 * scale;
        y2 = y2 * scale;
        z2 = z2 * scale;
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

        g.setColor(picColor);
        g.drawLine((int) round(x1) + Env.FROG_BRAIN_DISP_WIDTH / 2 + xOffset, (int) round(y1) + Env.FROG_BRAIN_DISP_WIDTH / 2 + yOffset, (int) round(x2) + Env.FROG_BRAIN_DISP_WIDTH / 2 + xOffset,
                (int) round(y2) + Env.FROG_BRAIN_DISP_WIDTH / 2 + yOffset);
    }

    /** 画出cell的中心点 */
    public void drawCellCenter(float x, float y, float z, float diameter) {
        if (x > 0 && (x < xMask || y < yMask))
            return;
        drawPoint(x + 0.5f, y + 0.5f, z + 0.5f, (int) Math.max(2, Math.round(scale * diameter)));
    }

    /** 画点，固定以top视角的角度，所以只需要在x1,y1位置画一个点 */
    public void drawPoint(float px1, float py1, float pz1, int diameter) {
        double x1 = px1 - Env.FROG_BRAIN_XSIZE / 2;
        double y1 = -py1 + Env.FROG_BRAIN_YSIZE / 2;// 屏幕的y坐标是反的，显示时要正过来
        double z1 = pz1 - Env.FROG_BRAIN_ZSIZE / 2;
        x1 = x1 * scale;
        y1 = y1 * scale;
        z1 = z1 * scale;
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

        g.setColor(picColor);
        g.fillOval((int) round(x1) + Env.FROG_BRAIN_DISP_WIDTH / 2 + xOffset, (int) round(y1) + Env.FROG_BRAIN_DISP_WIDTH / 2 + yOffset - diameter / 2, diameter, diameter);
    }

    private static Cuboid brain = new Cuboid(0, 0, 0, Env.FROG_BRAIN_XSIZE, Env.FROG_BRAIN_YSIZE, Env.FROG_BRAIN_ZSIZE);

    public void drawBrainPicture(Frog f) {// 在这个方法里进行青蛙三维脑结构的绘制
        if (!f.alive)
            return;
        if (!Env.SHOW_FIRST_FROG_BRAIN)
            return;
        g.setColor(WHITE);// 先清空旧图
        g.fillRect(0, 0, brainDispWidth, brainDispWidth);
        g.setColor(BLACK); // 画边框
        g.drawRect(0, 0, brainDispWidth, brainDispWidth);
        setPicColor(BLACK);
        drawCuboid(brain);// 先把脑的框架画出来

        setPicColor(RED);
        drawLine(0, 0, 0, 1, 0, 0);
        drawLine(0, 0, 0, 0, 1, 0);
        drawLine(0, 0, 0, 0, 0, 1);

        Graphics g2 = this.getGraphics(); // 这两行是将缓存中的图像写到屏幕上
        g2.drawImage(buffImg, 0, 0, this);

    }

    // getters & setters
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

    public void setPicColor(Color color) {
        this.picColor = color;
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
