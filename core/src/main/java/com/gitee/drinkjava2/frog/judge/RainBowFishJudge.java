package com.gitee.drinkjava2.frog.judge;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.brain.BrainPicture;
import com.gitee.drinkjava2.frog.brain.Cells;
import com.gitee.drinkjava2.frog.util.StringPixelUtils;

/**
 * RainBowFishJudge to judge if is a rainbow fish, judge method be called in animal's initAnimal method 
 * 
 *  这个类的judge方法在动物的初始化后被调用，根据脑细胞群的三维结构和参数来对动物进行奖罚，即加减它的能量值，这是一个临时类，只是用来检验细胞三维成形功能，以后可能改名或删除
 *  这个类的show方法在绘脑图时调用，在脑图里显示脑细胞群的三维形状和参数，用不同颜色直径的空心圆来表示不同参数，judge方法就像是一个模子，细胞长在这个模子里的有奖，否则扣分
 */
public class RainBowFishJudge {//NOSONAR
    private static int[] C = new int[]{0, 0, Env.BRAIN_CUBE_SIZE / 2}; //C是中心点
    private static boolean[][][] shape = new boolean[Env.BRAIN_XSIZE][Env.BRAIN_YSIZE][Env.BRAIN_ZSIZE];
    private static List<int[]> pointList = new ArrayList<>(); //pointList存放上面shape的所有有效点，用来加快显示循环而不用遍历三维数组
    static {
        putPixiel("🐟");
    }

    public static void putPixiel(String str) {
        byte[][] c = StringPixelUtils.getStringPixels(Font.SANS_SERIF, Font.PLAIN, Env.BRAIN_CUBE_SIZE, str); //要把frog二维像素变成立体的三维点放到points里和pointsList里供使用
        int w = c.length;
        int h = c[0].length;
        for (int z = 0; z < 5; z++) {
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (c[x][y] > 0) {
                        int[] p = new int[]{C[0] + x, C[1] + y + 2, C[2] + z};
                        if (!Animal.outBrainRange(p[0], p[1], p[2])) {
                            shape[p[0]][p[1]][p[2]] = true;
                            pointList.add(p);
                        }
                    }
                }
            }
        }
    }

    private static void judgeShape(Animal animal) {//检查animal的脑细胞是否位于brainShape的范围内 
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

    public static void judgeColor(Animal animal) {//检查颜色与小鱼图像重合，且呈斑马纹色彩分布，是就加分，否则扣分
        float colorWidth = 1.0f * Env.BRAIN_CUBE_SIZE / Cells.GENE_NUMBERS; //每条彩带宽度
        for (int x = 0; x < Env.BRAIN_CUBE_SIZE; x++)
            for (int y = 0; y < Env.BRAIN_CUBE_SIZE; y++)
                for (int z = 0; z < Env.BRAIN_CUBE_SIZE; z++)
                    for (int i = 1; i <= Cells.GENE_NUMBERS; i++)
                        if ((animal.cells[x][y][z] & (1 << i)) != 0) //如果色彩存
                            if ((animal.cells[x][y][z] & 1) != 0 && x >= (i - 1) * colorWidth && x <= (i * colorWidth))
                                animal.awardAA();
                            else
                                animal.penaltyA();
    }

    public static void judge(Animal animal) {////检查animal的脑细胞分布和参数是否符合要求并加减分
        judgeShape(animal);
        judgeColor(animal);
    }

    public static void show(BrainPicture pic) {// 在脑图上显示当前形状
        for (int[] p : pointList)
            pic.drawCircle(p[0] + 0.5f, p[1] + 0.5f, p[2] + 0.5f, 1);
    }

}
