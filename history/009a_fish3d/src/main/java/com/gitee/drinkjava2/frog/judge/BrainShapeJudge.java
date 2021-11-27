package com.gitee.drinkjava2.frog.judge;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.brain.BrainPicture;
import com.gitee.drinkjava2.frog.util.Point3D;
import com.gitee.drinkjava2.frog.util.StringPixelUtils;

/**
 * judge method be called after animal's initAnimal method 
 * 
 *  这个类的judge方法在动物的初始化后被调用，根据脑细胞群的三维结构形状来对动物进行奖罚，即加减它的能量值，这是一个临时类，只是用来检验细胞三维成形功能，以后可能改名或删除
 *  这个类的show方法在绘脑图时调用，在脑图里显示脑细胞群的三维形状，用空心圆来表示，这个三维形状就像是一个模子，细胞长在这个模子里的有奖，否则扣分
 */
public class BrainShapeJudge {//NOSONAR
    private static Point3D C = new Point3D(0, 0, Env.BRAIN_ZSIZE / 2); //C是中心点
    private static boolean[][][] shape = new boolean[Env.BRAIN_XSIZE][Env.BRAIN_YSIZE][Env.BRAIN_ZSIZE];
    private static List<Point3D> pointList = new ArrayList<>(); //pointList存放上面shape的所有有效点，用来加快显示循环而不用遍历三维数组
    static {
        putPixiel("🐟");
    }

    private static void putPixiel(String str) {
        byte[][] c = StringPixelUtils.getStringPixels(Font.SANS_SERIF, Font.PLAIN,16, str); //要把frog二维像素变成立体的三维点放到points里和pointsList里供使用
        int w = c.length;
        int h = c[0].length;
        for (int z = 0; z < 5; z++) {
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (c[x][y] > 0) {
                        Point3D p = new Point3D(C.x + x, C.y + y+2, C.z + z);
                        if (!Animal.outBrainRange(p.x, p.y, p.z)) {
                            shape[p.x][p.y][p.z] = true;
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
                            animal.award5000();
                        } else {
                            animal.penalty500();
                        }
                }
            }
        }
    }

    public static void show(BrainPicture pic) {// 在脑图上显示当前形状
        for (Point3D p : pointList)
            pic.drawCircle(p.x + 0.5f, p.y + 0.5f, p.z + 0.5f, 1);
    }

}
