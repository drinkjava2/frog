package com.gitee.drinkjava2.frog.brain;

import static java.awt.Color.BLACK;
import static com.gitee.drinkjava2.frog.brain.Cells.*;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Application;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.util.ColorUtils;
import com.gitee.drinkjava2.frog.util.Tree8Util;

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
    private static final long serialVersionUID = 1L;

    private static final float D90 = (float) (Math.PI / 2);

    Color picColor = RED;
    int brainDispWidth; // screen display piexls width
    float scale; // brain scale
    int xOffset = 0; // brain display x offset compare to screen
    int yOffset = 0; // brain display y offset compare to screen
    float xAngle = D90 * .8f; // brain rotate on x axis
    float yAngle = D90 / 4; // brain rotate on y axis
    float zAngle = 0;// brain rotate on z axis
    int xMask = -1;// x Mask
    int yMask = -1;// y Mask
    BufferedImage buffImg;
    Graphics g;
    String note;
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
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()){
                case KeyEvent.VK_UP:// Y切面向上
                    yMask++;
                    if (yMask > Env.BRAIN_YSIZE)
                        yMask = Env.BRAIN_YSIZE;
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
                    if (xMask > Env.BRAIN_XSIZE)
                        xMask = Env.BRAIN_XSIZE;
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
                    xAngle = D90;
                    yAngle = 0;
                    zAngle = 0;
                    break;
                case 'L':// 左视
                    xAngle = D90;
                    yAngle = D90;
                    zAngle = 0;
                    break;
                case 'R':// 右视
                    xAngle = D90;
                    yAngle = -D90;
                    zAngle = 0;
                    break;
                case 'X':// 斜视
                    xAngle = D90 * .8f;
                    yAngle = D90 / 4;
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
        drawCuboid(c.x, c.y, c.z, c.xe, c.ye, c.ze);
    }

    public void drawCuboid(float x, float y, float z, float xe, float ye, float ze) {// 在脑图上画一个长立方体框架，视角是TopView
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
        double x1 = px1 - Env.BRAIN_XSIZE / 2;
        double y1 = -py1 + Env.BRAIN_YSIZE / 2;// 屏幕的y坐标是反的，显示时要正过来
        double z1 = pz1 - Env.BRAIN_ZSIZE / 2;
        double x2 = px2 - Env.BRAIN_XSIZE / 2;
        double y2 = -py2 + Env.BRAIN_YSIZE / 2;// 屏幕的y坐标是反的，显示时要正过来
        double z2 = pz2 - Env.BRAIN_ZSIZE / 2;
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
        // z = z1 * cos(yAngle) - x1 * sin(yAngle);
        x1 = x;
        // z1 = z;

        x = x1 * cos(zAngle) - y1 * sin(zAngle);// 绕z轴转
        y = x1 * sin(zAngle) + y1 * cos(zAngle);
        x1 = x;
        y1 = y;

        y = y2 * cos(xAngle) - z2 * sin(xAngle);// 绕x轴转
        z = y2 * sin(xAngle) + z2 * cos(xAngle);
        y2 = y;
        z2 = z;

        x = z2 * sin(yAngle) + x2 * cos(yAngle);// 绕y轴转
        // z = z2 * cos(yAngle) - x2 * sin(yAngle);
        x2 = x;
        // z2 = z;

        x = x2 * cos(zAngle) - y2 * sin(zAngle);// 绕z轴转
        y = x2 * sin(zAngle) + y2 * cos(zAngle);
        x2 = x;
        y2 = y;

        g.setColor(picColor);
        g.drawLine((int) round(x1) + Env.FROG_BRAIN_DISP_WIDTH / 2 + xOffset, (int) round(y1) + Env.FROG_BRAIN_DISP_WIDTH / 2 + yOffset, (int) round(x2) + Env.FROG_BRAIN_DISP_WIDTH / 2 + xOffset,
                (int) round(y2) + Env.FROG_BRAIN_DISP_WIDTH / 2 + yOffset);
    }

    /** 画点，固定以top视角的角度，所以只需要在x1,y1位置画一个点 */
    public void drawPoint(float px1, float py1, float pz1, float r) {
        double x1 = px1 - Env.BRAIN_XSIZE / 2;
        double y1 = -py1 + Env.BRAIN_YSIZE / 2;// 屏幕的y坐标是反的，显示时要正过来
        double z1 = pz1 - Env.BRAIN_ZSIZE / 2;
        x1 = x1 * scale;
        y1 = y1 * scale;
        z1 = z1 * scale;
        double x, y, z;
        y = y1 * cos(xAngle) - z1 * sin(xAngle);// 绕x轴转
        z = y1 * sin(xAngle) + z1 * cos(xAngle);
        y1 = y;
        z1 = z;

        x = z1 * sin(yAngle) + x1 * cos(yAngle);// 绕y轴转
        // z = z1 * cos(yAngle) - x1 * sin(yAngle);
        x1 = x;
        // z1 = z;

        x = x1 * cos(zAngle) - y1 * sin(zAngle);// 绕z轴转
        y = x1 * sin(zAngle) + y1 * cos(zAngle);
        x1 = x;
        y1 = y;

        g.setColor(picColor);
        g.fillOval(round((float) x1 + Env.FROG_BRAIN_DISP_WIDTH / 2 + xOffset - r * scale * .5f), round((float) y1 + Env.FROG_BRAIN_DISP_WIDTH / 2 + yOffset - r * scale * .5f), round(r * scale),
                round(r * scale));
    }

    /** 画一个圆 */
    public void drawCircle(float px1, float py1, float pz1, float r) {//这个方法实际和上面的一样的，只是改成了drawOval
        double x1 = px1 - Env.BRAIN_XSIZE / 2;
        double y1 = -py1 + Env.BRAIN_YSIZE / 2;// 屏幕的y坐标是反的，显示时要正过来
        double z1 = pz1 - Env.BRAIN_ZSIZE / 2;
        x1 = x1 * scale;
        y1 = y1 * scale;
        z1 = z1 * scale;
        double x, y, z;
        y = y1 * cos(xAngle) - z1 * sin(xAngle);// 绕x轴转
        z = y1 * sin(xAngle) + z1 * cos(xAngle);
        y1 = y;
        z1 = z;

        x = z1 * sin(yAngle) + x1 * cos(yAngle);// 绕y轴转
        // z = z1 * cos(yAngle) - x1 * sin(yAngle);
        x1 = x;
        // z1 = z;

        x = x1 * cos(zAngle) - y1 * sin(zAngle);// 绕z轴转
        y = x1 * sin(zAngle) + y1 * cos(zAngle);
        x1 = x;
        y1 = y;

        g.setColor(picColor);
        g.drawOval(round((float) x1 + Env.FROG_BRAIN_DISP_WIDTH / 2 + xOffset - r * scale * .5f), round((float) y1 + Env.FROG_BRAIN_DISP_WIDTH / 2 + yOffset - r * scale * .5f), round(r * scale),
                round(r * scale));
    }

    public void drawText(float px1, float py1, float pz1, String text, float textSize) {
        double x1 = px1 - Env.BRAIN_XSIZE / 2;
        double y1 = -py1 + Env.BRAIN_YSIZE / 2;// 屏幕的y坐标是反的，显示时要正过来
        double z1 = pz1 - Env.BRAIN_ZSIZE / 2;
        x1 = x1 * scale;
        y1 = y1 * scale;
        z1 = z1 * scale;
        double x, y, z;
        y = y1 * cos(xAngle) - z1 * sin(xAngle);// 绕x轴转
        z = y1 * sin(xAngle) + z1 * cos(xAngle);
        y1 = y;
        z1 = z;

        x = z1 * sin(yAngle) + x1 * cos(yAngle);// 绕y轴转
        // z = z1 * cos(yAngle) - x1 * sin(yAngle);
        x1 = x;
        // z1 = z;

        x = x1 * cos(zAngle) - y1 * sin(zAngle);// 绕z轴转
        y = x1 * sin(zAngle) + y1 * cos(zAngle);
        x1 = x;
        y1 = y;

        g.setColor(picColor);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, (int) round(textSize * scale)));
        g.drawString(text, (int) round(x1) + Env.FROG_BRAIN_DISP_WIDTH / 2 + xOffset, (int) round(y1) + Env.FROG_BRAIN_DISP_WIDTH / 2 + yOffset);

    }

    private static Cuboid brain = new Cuboid(0, 0, 0, Env.BRAIN_XSIZE, Env.BRAIN_YSIZE, Env.BRAIN_ZSIZE);

    public void drawBrainPicture() {// 在这个方法里进行动物的三维脑结构的绘制,蛇是青蛙的子类，所以也可以当参数传进来
        if (!Env.SHOW_FIRST_ANIMAL_BRAIN)
            return;
        if (Env.show_split_detail)
            drawSplitDetail();
        else
            drawBrainStructure();
    }

    public void drawSplitDetail() {// 在这个方法里绘制脑细胞分裂的显示步聚，即从一个细胞开始分裂成最终脑结构的每一步
        Animal a = Env.getShowAnimal(); // 第一个青蛙或蛇

        for (int i = Env.BRAIN_CUBE_SIZE; i >= 1; i /= 2) {
            g.setColor(WHITE);// 先清空旧图, g是buffImg，绘在内存中
            g.fillRect(0, 0, brainDispWidth, brainDispWidth);
            g.setColor(BLACK); // 画边框
            g.drawRect(0, 0, brainDispWidth, brainDispWidth);

            for (int geneIndex = 0; geneIndex < Cells.GENE_NUMBERS; geneIndex++) {
                ArrayList<Integer> gene = a.genes.get(geneIndex);
                Tree8Util.knockNodesByGene(gene);
                for (int j = 0; j < Tree8Util.NODE_QTY; j++) {
                    if (Tree8Util.keep[j] > 0) {
                        int[] node = Tree8Util.TREE8[j];
                        int size = node[0];
                        if (size == i && Cells.display_gene[geneIndex]) {//如果允许显示的话, 显示当前层级的节点
                            setPicColor(ColorUtils.colorByCode(geneIndex));
                            drawPoint(node[1] + size / 2, node[2] + size / 2, node[3] + size / 2, size * (0.5f - geneIndex * 0.05f));
                        }
                    }
                }
            }
            g.setColor(BLACK);
            drawCuboid(brain);// 把脑的框架画出来
            this.getGraphics().drawImage(buffImg, 0, 0, this);// 利用缓存避免画面闪烁，这里输出缓存图片 
            if (!Env.show_split_detail)
                return;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
    }

    public void drawBrainStructure() {// 在这个方法里进行动物的三维脑结构的绘制,蛇是青蛙的子类，所以也可以当参数传进来
        Animal a = Env.getShowAnimal(); // 显示第一个青蛙或蛇
        if (a == null || !a.alive)
            return;
        g.setColor(WHITE);// 先清空旧图, g是buffImg，绘在内存中
        g.fillRect(0, 0, brainDispWidth, brainDispWidth);
        g.setColor(BLACK); // 画边框
        g.drawRect(0, 0, brainDispWidth, brainDispWidth);

        for (int z = 0; z < Env.BRAIN_CUBE_SIZE; z++) {
            for (int y = Env.BRAIN_CUBE_SIZE - 1; y >= 0; y--) {
                for (int x = Env.BRAIN_CUBE_SIZE - 1; x >= 0; x--) {
                    long cell = a.cells[x][y][z];
                    if ((cell & Cells.EXIST) == 0) //只显示有效的细胞点
                        continue;
                    if (a.energys[x][y][z] > 1) { //用大红色圆形画出能量大于1的细胞格
                        setPicColor(Color.MAGENTA); //开始画出对应的细胞基因参数，用不同颜色直径圆表示
                        drawCircle(x + 0.5f, y + 0.5f, z + 0.5f, 1.2f);
                    }
                    if (x >= xMask && y >= yMask && cell != 0)//画出细胞每个基因存在的细胞格子
                        for (int geneIndex = 0; geneIndex < Cells.GENE_NUMBERS; geneIndex++) {
                            if ((cell & (1 << geneIndex)) != 0 && Cells.display_gene[geneIndex]) {
                                setPicColor(ColorUtils.colorByCode(geneIndex)); //开始画出对应的细胞基因参数，用不同颜色直径圆表示
                                drawPoint(x + 0.5f, y + 0.5f, z + 0.5f, geneIndex == 0 ? 0.8f : 0.5f - geneIndex * 0.05f);
                            }
                        }
                    if (((cell & ZT) > 0)) { //如有轴突基因，则从当前细胞到轴突端点画一条红线
                        int x_ = (int) ((cell & ZTX) >> ZTX0);
                        if (x_ == 0)
                            x_ = -1;
                        int y_ = (int) ((cell & ZTY) >> ZTY0);
                        if (y_ == 0)
                            y_ = -1;
                        int z_ = (int) ((cell & ZTZ) >> ZTZ0);
                        if (z_ == 0)
                            z_ = -1;
                        int zt_long = (int) ((cell & ZT_LONG) >> ZT_LONG0) + 1; //轴突长度, 大小为1~8
                        int xx = x + x_ * zt_long;
                        int yy = y + y_ * zt_long;
                        int zz = z + z_ * zt_long;
                        if (Env.insideBrain(xx, yy, zz)) {
                            setPicColor(Color.RED);
                            drawLine(x+.5f, y+.5f, z+.5f, xx+.5f, yy+.5f, zz+.5f);
                        }
                    }
                }
            }
        }

        Eye.drawEye(this);//画眼睛
        
        setPicColor(Color.BLACK);
        drawCuboid(brain);// 把脑的框架画出来

        setPicColor(BLACK); //把x,y,z坐标画出来
        drawText(Env.BRAIN_CUBE_SIZE, 0, 0, "x", 2);
        drawText(0, Env.BRAIN_CUBE_SIZE, 0, "y", 2);
        drawText(0, 0, Env.BRAIN_CUBE_SIZE, "z", 2);
        setPicColor(RED);
        drawLine(0, 0, 0, Env.BRAIN_CUBE_SIZE, 0, 0);
        drawLine(0, 0, 0, 0, Env.BRAIN_CUBE_SIZE, 0);
        drawLine(0, 0, 0, 0, 0, Env.BRAIN_CUBE_SIZE);

        g.setColor(Color.black);
        if (note != null) // 全局注释
            g.drawString(note, 30, 55);
        this.getGraphics().drawImage(buffImg, 0, 0, this);// 利用缓存避免画面闪烁，这里输出缓存图片
    }

    public static void setNote(String note) {
        Application.brainPic.note = note;
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

    public Color getPicColor() {
        return picColor;
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
