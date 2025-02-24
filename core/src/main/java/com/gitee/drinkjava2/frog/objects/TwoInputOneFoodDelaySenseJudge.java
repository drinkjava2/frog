package com.gitee.drinkjava2.frog.objects;

import java.awt.Graphics;

/**
 * 
 * TwoInputOneFoodDelaySenseJudge继承于TwoInputOneFoodFullSenseJudge 
 * 区别在于甜苦味会延迟咬动作几个时间单位产生，强迫它必须根据之前形成的条件反射来预判决定咬不咬。开发中，未完成
 *  
 */
public class TwoInputOneFoodDelaySenseJudge extends TwoInputOneFoodFullSenseJudge {

    @Override
    public void active(int screen, int step, Graphics g) {//重写active方法，改成甜苦味会延迟咬动作几个时间单位产生，其实就是把sweet和bitter延时从0改成一个数值
        super.active(screen, step, g); 
    }

}
