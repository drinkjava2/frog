package com.gitee.drinkjava2.frog.judge;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.brain.BrainPicture;
import com.gitee.drinkjava2.frog.util.StringPixelUtils;

/**
 * judge method be called after animal's initAnimal method 
 * 
 *  这个类的judge方法在动物的初始化后被调用，根据脑细胞群的三维结构形状来对动物进行奖罚，即加减它的能量值，这是一个临时类，只是用来检验细胞三维成形功能，以后可能改名或删除
 *  这个类的show方法在绘脑图时调用，在脑图里显示脑细胞群的三维形状，用空心圆来表示，这个三维形状就像是一个模子，细胞长在这个模子里的有奖，否则扣分
 */
public class BrainShapeJudge {//NOSONAR
    private static int[] C = new int[] {0, 0, Env.BRAIN_CUBE_SIZE / 2}; //C是中心点
    private static boolean[][][] shape = new boolean[Env.BRAIN_XSIZE][Env.BRAIN_YSIZE][Env.BRAIN_ZSIZE];
    private static List<int[]> pointList = new ArrayList<>(); //pointList存放上面shape的所有有效点，用来加快显示循环而不用遍历三维数组
    static {
        putPixiel("🐟");
    }

    private static void putPixiel(String str) {
        byte[][] c = StringPixelUtils.getStringPixels(Font.SANS_SERIF, Font.PLAIN, Env.BRAIN_CUBE_SIZE , str); //要把frog二维像素变成立体的三维点放到points里和pointsList里供使用
        int w = c.length;
        int h = c[0].length;
        for (int z = 0; z < 5; z++) {
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (c[x][y] > 0) {
                        int[] p = new int[] {C[0]+ x, C[1]+ y+2, C[2] + z};
                        if (!Animal.outBrainRange(p[0], p[1], p[2])) {
                            shape[p[0]][p[1]][p[2]] = true;
                            pointList.add(p);
                        }
                    }
                }
            }
        }
    }

    public static void judge(Animal animal) {//检查animal的脑细胞是否位于brainShape的范围内 
        for (int x = 0; x < Env.BRAIN_CUBE_SIZE; x++) {
            for (int y = 0; y < Env.BRAIN_CUBE_SIZE; y++) {
                for (int z = 0; z < Env.BRAIN_CUBE_SIZE; z++) {
                    if ((animal.cells[x][y][z] & 1) != 0)
                        if (shape[x][y][z]) {
                            animal.awardAAAA();
                        } else {
                            animal.penaltyAAA();
                        }
                }
            }
        }
    }

    public static void show(BrainPicture pic) {// 在脑图上显示当前形状
        for (int[] p : pointList)
            pic.drawCircle(p[0] + 0.5f, p[1] + 0.5f, p[2] + 0.5f, 1);
    }

}
