package com.gitee.drinkjava2.frog.judge;

import java.awt.Font;

import com.gitee.drinkjava2.frog.Application;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.brain.BrainPicture;
import com.gitee.drinkjava2.frog.objects.EnvObject;
import com.gitee.drinkjava2.frog.util.Logger;
import com.gitee.drinkjava2.frog.util.StringPixelUtils;

/**
 * CharJudge，image recognition. This is a temperary test class
 * 
 * CharJudge用来检查模式识别结果是否正确，训练时随机生成一个字符并激活对应的声音区，检测时只生成字符，检查对应的声音区是否活跃最强
 * 这是个临时类，只用于本次任务
 * 
 */
public class CharJudge implements EnvObject {

    private static String STR = "1234567";
    private static int STR_LENGTH = STR.length();
    private static float[][][] charPictures = new float[STR_LENGTH][Env.BRAIN_CUBE_SIZE][Env.BRAIN_CUBE_SIZE]; //STR所有字母的像素点阵对应的视网膜图像暂存在这个三维数组里，第一维是字母在STR中的序号
    private static int HALF_STEPS = Env.STEPS_PER_ROUND / 2;
    private static int HALF_CUBE = Env.BRAIN_CUBE_SIZE / 2;

    static {
        for (int i = 0; i < STR.length(); i++) { //生成STR每个字符的二维图片并缓存到charPictures
            byte[][] pic = StringPixelUtils.getStringPixels(Font.SANS_SERIF, Font.PLAIN, Env.BRAIN_CUBE_SIZE + 2, "" + STR.charAt(i));
            for (int x = 0; x < Math.min(pic.length, Env.BRAIN_CUBE_SIZE); x++) {
                for (int y = 0; y < Math.min(pic[0].length, Env.BRAIN_CUBE_SIZE); y++) {
                    if (pic[x][y] > 0)
                        charPictures[i][x][y] = 99999f;//设成99999这么大是为了方便拷到视网膜上时，有足够的能量来扣掉，一个图形会在视网膜保留一段时间，细胞的基因行为有可能退化或移走能量，设大点可以多扣一会儿
                }
            }
        }
    }

    @Override
    public void build() {// do nothing
    }

    @Override
    public void destory() {// do nothing
    }

    static void copyArray(float[][] src, float[][] target) { //拷贝两个同样大小的二维数组
        for (int i = 0; i < src.length; i++) {
            for (int j = 0; j < src[0].length; j++) {
                target[i][j] = src[i][j];
            }
        }
    }

    @Override
    public void active(int screen, int step) { 
        //前半段是训练，在前半段时间里，每隔一段时间生成一个随机文本图像, 然后把每个青蛙视网膜上画上这个图像,同时激活对应图像的声音细胞
        int space = HALF_STEPS / STR_LENGTH;
        if (step < HALF_STEPS && step % space == 0) {
            int index = step / space;
            if (index < STR_LENGTH) {
                BrainPicture.setNote("第" + (index + 1) + "个字训练:" + STR.charAt(index));
                Application.brainPic.drawBrainPicture();
                for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++) {
                    copyArray(charPictures[index], Env.frogs.get(i).energys[0]); //先把每个青蛙视网膜上画上这个图像, energys[0]作为视网膜

                    for (int j = 0; j < Env.BRAIN_CUBE_SIZE; j++) {
                        if (j == index)
                            Env.frogs.get(i).energys[1][HALF_CUBE][j] = 99999f; // energys[1]作为脑内声音区
                        else
                            Env.frogs.get(i).energys[1][HALF_CUBE][j] = 0f;
                    }
                }

            }
            return;
        }

        if (step == HALF_STEPS) { //中点把声音区清空
            for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++) {
                for (int j = 0; j < Env.BRAIN_CUBE_SIZE; j++) {
                    Env.frogs.get(i).energys[1][HALF_CUBE][j] = 0f;
                }
            }
        }

        //后半段是识别，每隔一段时间把每个青蛙视网膜重置为以前出现的某个图像，然后再检查与对应图像关联的声音细胞是否激活, 如正确激活则奖励细胞激活并加分,（奖励细胞用于构成条件反射链的一环，加分是用于优胜劣汰）
        if (step >= HALF_STEPS && (step - HALF_STEPS) % space == 0) {
            int index = (step - HALF_STEPS) / space;
            if (index < STR_LENGTH) {
                BrainPicture.setNote("第" + (index + 1) + "个字识别:" + STR.charAt(index));
                for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++) {
                    Frog frog = Env.frogs.get(i);
                    copyArray(charPictures[index], frog.energys[0]);
                    frog.energys[2][HALF_CUBE][0] = 0; //随便设定energys[2][HALF_CUBE][0]这个位置为快乐感受区，识别之前先清0
                    frog.energys[2][HALF_CUBE][1] = 0; //随便设定energys[2][HALF_CUBE][1]这个位置为痛苦感受区，识别之前先清0
                }
                Application.brainPic.drawBrainPicture();
            }
            return;
        }

        //如错误激活则痛苦细胞激活，并扣分。错误激活分为两类，一是没有反应，二是声音区有激活，但最强激活区没有位于正确区
        if (step >= HALF_STEPS && (step - HALF_STEPS) % space != 0) {//TODO  把打分判断放在字符切换的之前一帧，而不是每个step都加减分以提高速度!
            int index = (step - HALF_STEPS) / space;
            if (index < STR_LENGTH) {
                for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++) {
                    for (int j = 0; j < Env.BRAIN_CUBE_SIZE; j++) {
                        Frog frog = Env.frogs.get(i);
                        float e = frog.energys[1][HALF_CUBE][j];
                        if (j == index) {//某图片激活时
                            if (e > 0) {
                                Logger.debug("有激活了");
                                frog.energys[2][HALF_CUBE][0] = 10; //识别正确，快乐区激活
                                frog.awardAAA(); //并且加分
                            } else {
                                frog.energys[2][HALF_CUBE][1] = 10; //识别错误, 痛苦区激活
                                frog.penaltyAAA(); //并且扣分
                            }
                        } else if (e > 0) { //某图片不激活时
                            frog.penaltyAAA(); //对应声应区激活则扣分
                        }
                    }
                }
            }
            return;
        }

    }

}
