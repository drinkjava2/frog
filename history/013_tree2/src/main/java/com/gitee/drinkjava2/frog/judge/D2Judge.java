package com.gitee.drinkjava2.frog.judge;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.brain.BrainPicture;
import com.gitee.drinkjava2.frog.util.StringPixelUtils;

/**
 * D2Judge， this is a temperary test class will be deleted later
 * 
 * D2Judge是临时类，这个类以后会删除。它用来测试二维平面的4叉树算法， 以前有一个类似的RainBowFishJudge是三维的，现在这个是二维的
 * 
 * judge方法在动物的初始化后被调用，根据脑细胞群的2维结构和参数来对动物进行奖罚，即加减它的能量值。
 * 这个类的show方法在绘脑图时调用，在脑图里显示脑细胞群的2维形状和参数，用不同颜色直径的空心圆来表示不同参数，judge方法就像是一个模子，细胞长在这个模子里的有奖，否则扣分
 */
public class D2Judge {
    public static D2Judge pic1 = new D2Judge(0, "S");
    public static D2Judge pic2 = new D2Judge(7, "A");
    public static D2Judge pic3 = new D2Judge(14, "M");

    public int xLayer; //2维图位于哪个x层对应的yz平面上
    private int[] C = new int[]{0, 0, Env.BRAIN_CUBE_SIZE / 2}; //C是中心点
    private boolean[][][] shape = new boolean[Env.BRAIN_XSIZE][Env.BRAIN_YSIZE][Env.BRAIN_ZSIZE];
    private List<int[]> pointList = new ArrayList<>(); //pointList存放上面shape的所有有效点，用来加快显示循环而不用遍历三维数组

    public D2Judge(int xLayer, String str) {//根据指定的层和字符，在shape和pointList里缓存一个像素数组内容
        this.xLayer = xLayer;
        for (int x = 0; x < Env.BRAIN_XSIZE; x++)
            for (int y = 0; y < Env.BRAIN_XSIZE; y++)
                for (int z = 0; z < Env.BRAIN_XSIZE; z++)
                    shape[x][y][z] = false;
        byte[][] c = StringPixelUtils.getStringPixels(Font.SANS_SERIF, Font.PLAIN, Env.BRAIN_CUBE_SIZE, str); //要把frog二维像素变成立体的三维点放到points里和pointsList里供使用
        int w = c.length;
        int h = c[0].length;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (c[x][y] > 0) {
                    int[] p = new int[]{C[0] + x, C[1] + y + 2};
                    if (!Animal.outBrainRange(xLayer, p[0], p[1])) {
                        shape[xLayer][p[0]][p[1]] = true;
                        pointList.add(p);
                    }
                }
            }
        }
    }

    public void judge(Animal animal) {//检查animal的脑细胞是否位于shape模板的范围内 
        for (int x = 0; x < Env.BRAIN_CUBE_SIZE; x++)
            for (int y = 0; y < Env.BRAIN_CUBE_SIZE; y++)
                for (int z = 0; z < Env.BRAIN_CUBE_SIZE; z++) {
                    if ((animal.cells[x][y][z]) > 0) {
                        if (shape[x][y][z])
                            animal.awardAAA();
                        else
                            animal.penaltyAAA();
                    } else {
                        if (shape[x][y][z])
                            animal.penaltyAAAA();
                    }
                }
    }

    public void show(BrainPicture pic) {// 在脑图上显示当前模板形状，用小圆圈表示
        pic.setPicColor(Color.BLACK);
        for (int[] p : pointList)
            pic.drawCircle(xLayer + 0.5f, p[0] + 0.5f, p[1] + 0.5f, 1);
    }

}
