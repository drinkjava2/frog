package com.gitee.drinkjava2.frog.judge;

import java.awt.Font;

import com.gitee.drinkjava2.frog.Application;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.brain.BrainPicture;
import com.gitee.drinkjava2.frog.objects.EnvObject;
import com.gitee.drinkjava2.frog.util.StringPixelUtils;

/**
 * CharJudge，image recognition. This is a temperary test class
 * 
 * CharJudge用来检查模式识别结果是否正确，训练时随机生成一个字符并激活对应的声音区，检测时只生成字符，检查对应的声音区是否活跃最强
 * 这是个临时类，只用于本次任务
 * 
 * x=0层, 视网膜
 * x=1层, 声音细胞
 * x=2层, 奖罚感受细胞 , 训练细胞  
 */
public class CharJudge implements EnvObject {

    private static String STR = "12345";
    private static int STR_LENGTH = STR.length();
    private static float[][][] charPictures = new float[STR_LENGTH][Env.BRAIN_CUBE_SIZE][Env.BRAIN_CUBE_SIZE]; //STR所有字母的像素点阵对应的视网膜图像暂存在这个三维数组里，第一维是字母在STR中的序号
    private static int HALF_STEPS = Env.STEPS_PER_ROUND / 2;
    private static int HALF_CUBE = Env.BRAIN_CUBE_SIZE / 2;
    private static int SPACE = HALF_STEPS / STR_LENGTH; //每个字母训练或识别点用的时间步长
    private static int EYE = 0; //视网膜层
    private static int SOUND = 1; //声音输入输出层
    private static int CONTROL = 2; //训练或提问信号放这层
    private static int FEEL = 3; //痛苦或快乐感觉层

    static {
        for (int i = 0; i < STR.length(); i++) { //生成STR每个字符的二维图片并缓存到charPictures
            byte[][] pic = StringPixelUtils.getStringPixels(Font.SANS_SERIF, Font.PLAIN, Env.BRAIN_CUBE_SIZE + 2, "" + STR.charAt(i));
            for (int x = 0; x < Math.min(pic.length, Env.BRAIN_CUBE_SIZE); x++) {
                for (int y = 0; y < Math.min(pic[0].length, Env.BRAIN_CUBE_SIZE); y++) {
                    if (pic[x][y] > 0)
                        charPictures[i][x][y] = 99999f;//设成99999这么大是为了方便拷到视网膜上时，有足够的能量来扣掉，一个图形会在视网膜保留一段时间，细胞的基因行为有可能消减或移走视网膜能量，设大点可以多扣一会儿
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
        int char_index;
        //前半段是训练，在前半段时间里，每隔一段时间生成一个随机文本图像, 然后把每个青蛙视网膜上画上这个图像,同时激活对应图像的声音细胞 
        if (step < HALF_STEPS && step % SPACE == 0) {
            char_index = step / SPACE;
            BrainPicture.setNote("第" + (char_index + 1) + "个字训练:" + STR.charAt(char_index));
            for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++) {
                Frog frog = Env.frogs.get(i);
                copyArray(charPictures[char_index], frog.energys[EYE]); //先把每个青蛙视网膜上画上这个图像, energys[0]作为视网膜

                frog.energys[CONTROL][HALF_CUBE][0] = 99999f; // 训练信号打开,随便设定energys[CONTROL][HALF_CUBE][0]这个位置为训练信号打开标记
                frog.energys[CONTROL][HALF_CUBE][HALF_CUBE] = 0f; // 提问信号关掉,随便设定energys[CONTROL][HALF_CUBE][HALF_CUBE]这个位置为提问信号打开标记
                frog.energys[SOUND][HALF_CUBE][char_index] = 99999f; //输入声音信号
                if (char_index > 0)
                    frog.energys[SOUND][HALF_CUBE][char_index - 1] = 0f;
            }
            Application.brainPic.drawBrainPicture();
            return;
        }

        if (step == HALF_STEPS) { //中点把声音区清空
            char_index = (step - 1) / SPACE;
            for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++) {
                Frog frog = Env.frogs.get(i);
                frog.energys[CONTROL][HALF_CUBE][0] = 0f; // 训练信号关掉
                frog.energys[SOUND][HALF_CUBE][char_index] = 0f; //上一次的声音信号输入关掉
            }
            Application.brainPic.drawBrainPicture();
        }

        //后半段是识别，每隔一段时间把每个青蛙视网膜重置为以前出现的某个图像
        if (step >= HALF_STEPS && (step - HALF_STEPS) % SPACE == 0) {
            char_index = (step - HALF_STEPS) / SPACE;
            if (char_index < STR_LENGTH) {
                BrainPicture.setNote("第" + (char_index + 1) + "个字识别:" + STR.charAt(char_index));
                for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++) {
                    Frog frog = Env.frogs.get(i);
                    frog.energys[CONTROL][HALF_CUBE][0] = 0f; // 训练信号关掉
                    frog.energys[CONTROL][HALF_CUBE][HALF_CUBE] = 99999f; // 提问信号打开
                    copyArray(charPictures[char_index], frog.energys[0]);
                    frog.energys[FEEL][HALF_CUBE][0] = 0; //随便设定energys[2][HALF_CUBE][0]这个位置为快乐感受区，识别之前先清0
                    frog.energys[FEEL][HALF_CUBE][1] = 0; //随便设定energys[2][HALF_CUBE][1]这个位置为痛苦感受区，识别之前先清0
                }
                Application.brainPic.drawBrainPicture();
            }
            return;
        }

        //然后再检查与对应图像关联的声音细胞是否激活, 如正确激活则奖励细胞激活并加分,（奖励细胞用于构成条件反射链的一环，加分是用于优胜劣汰）
        //如错误激活则痛苦细胞激活，并扣分。错误激活分为两类，一是没有反应，二是声音区有激活，但最强激活区没有位于正确区
        if (step >= HALF_STEPS && ((step - HALF_STEPS) % 10 == 5)) {//把打分判断仅放在个别帧，而不是每个帧都对比，以提高速度 
            char_index = (step - HALF_STEPS) / SPACE;
            if (char_index < STR_LENGTH) {
                for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++) {
                    for (int j = 0; j < Env.BRAIN_CUBE_SIZE; j++) {
                        Frog frog = Env.frogs.get(i);
                        float e = frog.energys[1][HALF_CUBE][j];
                        if (j == char_index) {//某图片激活时
                            if (e > 0) {
                                // Logger.debug("正确");
                                frog.energys[FEEL][HALF_CUBE][0] = 99999f; //识别正确，快乐区信号打开
                                frog.energys[FEEL][HALF_CUBE][HALF_CUBE] = 0f; //识别正确, 痛苦信号关掉
                                frog.awardAAA(); //并且加分
                            } else {
                                frog.energys[FEEL][HALF_CUBE][0] = 0f; //识别错误，快乐区信号关掉
                                frog.energys[FEEL][HALF_CUBE][HALF_CUBE] = 99999f; //识别错误, 痛苦信号打开
                                frog.penaltyAAA(); //并且扣分
                            }
                        } else if (e > 0) { //声音区误激活，扣分
                            frog.energys[FEEL][HALF_CUBE][0] = 0f; //识别错误，快乐区信号关掉
                            frog.energys[FEEL][HALF_CUBE][HALF_CUBE] = 99999f; //识别错误, 痛苦信号打开
                            frog.penaltyAAA(); //对应声应区激活则扣分
                        }
                    }
                }
                Application.brainPic.drawBrainPicture();
            }
            return;
        }

    }

}
