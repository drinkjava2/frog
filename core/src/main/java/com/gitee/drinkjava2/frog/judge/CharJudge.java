package com.gitee.drinkjava2.frog.judge;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.brain.BrainPicture;
import com.gitee.drinkjava2.frog.objects.EnvObject;

/**
 * CharJudge，image recognition. This is a temperary test class
 * 
 * CharJudge用来检查模式识别结果是否正确，训练时随机生成一个字符并激活对应的声音区，检测时只生成字符，检查对应的声音区是否活跃最强
 * 这是个临时类，只用于本次任务
 * 
 */
public class CharJudge implements EnvObject {
    public static final String STR = "01234青青园中葵朝露待日晞阳春布德泽万物生光辉";
    //public static final String STR = "青青园中葵朝露待日晞阳春布德泽万物生光辉常恐秋节至焜黄华叶衰百川东到海何时复西归少壮不努力老大徒伤悲";
    public static final int TIME = 120;

    @Override
    public void build() { // do nothing
    }

    @Override
    public void destory() {// do nothing
    }
 
    @Override  
    public void active(int screen) { //TODO
        Frog frog = Env.frogs.get(screen * Env.FROG_PER_SCREEN); // 这个测试只针对每屏的第一只青蛙，因为脑图固定只显示第一只青蛙
 
        int index = Env.step / TIME;
        if (Env.step % TIME == 0)
            {}//frog.prepareNewTraining();

        if (index < STR.length()) {
            BrainPicture.setNote("第" + (index + 1) + "个字训练:"+STR.charAt(index));
            //ear.hearSound(frog, index);
           // eye.seeImageWithOffset(frog, StringPixelUtils.getSanserif12Pixels(STR.substring(index, index + 1)),0,0);
        } else {
            int index2 = index % STR.length();
            BrainPicture.setNote("第" + (index2 + 1) + "个字识别");
            //eye.seeImageWithOffset(frog, StringPixelUtils.getSanserif12Pixels(STR.substring(index2, index2 + 1)),0,0);
            if (Env.step % TIME > (TIME - 2)) {
               // int result = ear.readcode(frog);
                //System.out.println("Max=" + result+", 即 '"+STR.substring(result, result+1)+"'");
               //frog.prepareNewTraining();
            }
        }
    }

   
    
     

}
