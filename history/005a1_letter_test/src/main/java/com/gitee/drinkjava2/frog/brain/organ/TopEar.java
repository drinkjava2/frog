/*
 * Copyright 2018 the original author or authors. 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.gitee.drinkjava2.frog.brain.organ;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Frog;
import com.gitee.drinkjava2.frog.brain.Cuboid;
import com.gitee.drinkjava2.frog.brain.Organ;
import com.gitee.drinkjava2.frog.util.ColorUtils;

/**
 * Ear can accept sound input
 * 
 * 耳朵的信号输入区能输入多少个信号取决于它的长度，它的每个输入点都与视觉视号正交
 * 
 * 耳朵的输入区在2021年6月后改只取 0,2,4,6,8处的细胞作为输入点以获取更清晰反向激活信号，这样总共就是5个有效输入点
 * 
 * 耳朵目前只有一个, 以后可以在侧面再开一个耳朵，就可以达到5x5=25个声音的输入
 * 
 * 为简单化，耳朵的识别区和输入区重叠，而不是象人脑一样有单独的虚拟声音，将来在信号强度上作区分，直接听到的信号强度大，反向激活后得到的信号弱
 * 
 * @author Yong Zhu
 * @since 2.0.2
 */
@SuppressWarnings("all")
public class TopEar extends Organ {// 耳朵位于脑的顶上，也是长方体
	private static final long serialVersionUID = 1L;

	public TopEar() {
		this.shape = new Cuboid(6, 5, Env.FROG_BRAIN_ZSIZE - 1, 1, 10, 1);// 手工固定耳区的大小
		this.type = Organ.TOPEAR;
		this.organName = "TopEar";
		this.allowVary = false;// 不允许变异
		this.allowBorrow = false;// 不允许借出
		this.color = ColorUtils.BLUE;
	}

    public void hearSound(Frog f, int code) {//code取0~4，分听代表5个听力细胞的位置
        Cuboid c = (Cuboid) this.shape;
        int y1 = code / 5;
        f.getOrCreateCell(c.x , c.y+y1*2 , c.z).hasInput = true; //听力细胞间隔一个分布
    }

    public int readcode(Frog f) {//找出收取光子数最多的点
        int temp = -10000;
        int code = -1;
        Cuboid c = (Cuboid) this.shape;
        System.out.print("Ear received photons qty: ");
        for (int y = 0; y <= 4; y++) {
            int sum = f.getOrCreateCell(c.x, c.y + y * 2, c.z).photonSum;
            System.out.print(sum + ",");
            if (sum > temp) {
                code = y;
                temp = sum;
            }
        }
        return code; //code表示保存声母最大激活信号的y方向序号
    }

    /** 给这个耳朵听到一个字母，激活它的听觉输入区, 注意听觉输入区并不等于听觉成像区 */
    public void hearNothing(Frog f) {
        f.setCuboidVales((Cuboid) shape, false);
    }

}
