package com.gitee.drinkjava2.frog.judge;

import java.awt.Font;

import com.gitee.drinkjava2.frog.Application;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.brain.BrainPicture;
import com.gitee.drinkjava2.frog.objects.EnvObject;
import com.gitee.drinkjava2.frog.util.StringPixelUtils;

/**
 * Paused
 * =======================================
 * 暂停!!! 用CharJudge来做模式识别的起点，还是太复杂了，不符合循序渐进原则，改用BiteJudge，它的信号更精简，详见之。
 * =======================================
 * 
 * 
 * CharJudge，image recognition
 * 
 * CharJudge用来进行模式识别训练并检查结果是否正确，这是个临时类，但如果做成功，将会是一个基础的脑模型了。
 * 
 * 基本流程是
 * 1.训练：随机在视网膜EYE上生成一个图像，并同时输入它对应的声音SOUND信号，并同时激活训练信号TRAIN开关
 * 2.检查：随机在视网膜上生成一个图像，并打开询问ASK信号，然后检查对应的说话信号SPEAK是否正确，
 *   判断：如说话正确，愉快信号HAPPY激活并奖励能量，如无反应或说话错误，痛苦信号PAIN激活并扣除能量
 * 
 * 这个类只是用来模仿外界和内部信号的产生和输入输出, 以及判断正误（以淘汰生物），脑的结构（即逻辑功能）的生成不在这里，而是由8叉树或4叉树算法配合遗传算法来生成，见Cells.java
 * 脑的结构生成算法是内因，这里的内外界信号模拟和判断是外因，脑的最终结构取决于内外因的合作，缺一不可。
 * 
 * 这个类不涉及信号的传输，只涉及信号的产生、输入和输出。信号的传输是由脑结构来负责的。
 * 
 */
public class CharJudge extends EnvObject {

    private static String STR = "1234"; //字符串长度不要超过Env.BRAIN_CUBE_SIZE边长，否则数组越界
    private static int STR_LENGTH = STR.length();
    private static float[][][] charPictures = new float[STR_LENGTH][Env.BRAIN_CUBE_SIZE][Env.BRAIN_CUBE_SIZE]; //STR所有字母的像素点阵对应的视网膜图像暂存在这个三维数组里，第一维是字母在STR中的序号
    private static int HALF_STEPS = Env.STEPS_PER_ROUND / 2;
    private static int HALF_CUBE = Env.BRAIN_CUBE_SIZE / 2;
    private static int SPACE = HALF_STEPS / STR_LENGTH; //每个字母训练或识别点用的时间步长

    private static int EYE = 0; //视网膜 是一个位于EYE层的平面

    private static int SOUND = 1; //声音输入层 位于[SOUND, 0, 0~Env.BRAIN_CUBE_SIZE-1]的一条直线
    private static int TALK = 1; //说话输出层位于[TALK, 1, 0~Env.BRAIN_CUBE_SIZE-1]的一条直线, TALK激活会导致声音区也激活（即自己能听到自己的讲话），反之，声音区激活TALK区不一定激活

    private static int TRAIN[] = {1, 2, 0}; //训练信号是位于1层的一个单点
    private static int ASK[] = {1, 2, 1}; //提问信号是位于1层的一个单点
    private static int HAPPY[] = {1, 2, 2}; //快乐感觉信号是位于1层的一个单点
    private static int PAIN[] = {1, 2, 3}; //痛苦感觉信号是位于1层的一个单点

    //private static int MEMORY = 2; //记忆细胞放在2层整个平面

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
        Frog f;
        //前半段是训练，在前半段时间里，每隔一段时间生成一个随机文本图像, 然后把每个青蛙视网膜上画上这个图像,同时激活对应图像的声音细胞 
        if (step < HALF_STEPS && step % SPACE == 0) {
            char_index = step / SPACE;
            BrainPicture.setNote("第" + (char_index + 1) + "个字训练:" + STR.charAt(char_index));
            for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++) {
                f = Env.frogs.get(i);
                copyArray(charPictures[char_index], f.energys[EYE]); //先把每个青蛙视网膜上画上这个图像, energys[0]作为视网膜

                f.open(TRAIN);// 训练信号打开
                f.close(ASK);// 提问信号关掉 
                f.open(SOUND, 0, char_index);//输入声音信号 
                if (char_index > 0)
                    f.energys[SOUND][0][char_index - 1] = 0f;
            }
            Application.brainPic.drawBrainPicture();
            return;
        }

        if (step == HALF_STEPS) { //中点把声音区清空
            char_index = (step - 1) / SPACE;
            for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++) {
                f = Env.frogs.get(i);
                f.close(TRAIN);// 训练信号打开  
                f.close(SOUND, 0, char_index); //上一次的声音信号输入关掉
            }
            Application.brainPic.drawBrainPicture();
        }

        //后半段是识别，每隔一段时间把每个青蛙视网膜重置为以前出现的某个图像
        if (step >= HALF_STEPS && (step - HALF_STEPS) % SPACE == 0) {
            char_index = (step - HALF_STEPS) / SPACE;
            if (char_index < STR_LENGTH) {
                BrainPicture.setNote("第" + (char_index + 1) + "个字识别:" + STR.charAt(char_index));
                for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++) {
                    f = Env.frogs.get(i);
                    copyArray(charPictures[char_index], f.energys[0]); //把每个青蛙视网膜重置为以前出现的某个图像
                    f.close(TRAIN);
                    f.open(ASK); //打开询问信号
                    f.close(HAPPY);
                    f.close(PAIN);
                }
                Application.brainPic.drawBrainPicture();
            }
            return;
        }

        //然后再检查与对应图像关联的说话细胞是否激活, 如正确激活则奖励细胞激活并加分,（奖励细胞用于构成条件反射链的一环，加分是用于优胜劣汰）
        //如错误激活则痛苦细胞激活，并扣分。错误激活分为两类，一是没有反应，二是说话区有激活，但最强激活区没有位于正确区
        if (step >= HALF_STEPS && ((step - HALF_STEPS) % 10 == 5)) {//把打分判断仅放在个别帧，而不是每个帧都对比，以提高速度 
            char_index = (step - HALF_STEPS) / SPACE;
            if (char_index < STR_LENGTH) {
                for (int i = screen; i < screen + Env.FROG_PER_SCREEN; i++) {
                    for (int j = 0; j < Env.BRAIN_CUBE_SIZE; j++) {
                        f = Env.frogs.get(i);
                        float e = f.energys[TALK][HALF_CUBE][j];
                        if (j == char_index) {//某图片激活时
                            if (e > 0) {
                                // Logger.debug("识别正确");
                                f.open(HAPPY);
                                f.close(PAIN);
                                f.awardAAA(); //并且加分
                            } else {
                                f.close(HAPPY);
                                f.open(PAIN);
                                f.penaltyAAA();//并且扣分 
                            }
                        } else if (e > 0) { //声音区误激活，扣分
                            f.close(HAPPY);
                            f.open(PAIN);
                            f.penaltyAAA();//并且扣分 
                        }
                    }
                }
                Application.brainPic.drawBrainPicture();
            }
            return;
        }
    }

}
