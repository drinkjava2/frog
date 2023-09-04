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
package com.gitee.drinkjava2.frog.brain;

import static com.gitee.drinkjava2.frog.util.RandomUtils.percent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.util.Logger;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Here store counts
 * 
 * 这个类存放脑常量变异、日志打印相关的方法
 * 
 * @author Yong Zhu
 * @since 15.0
 */
@SuppressWarnings("all")
public class Consts {
    public static int LENGTH; //总变量数量

    public static final int Eye = 0;
    public static final int ADD_BITE = 1;
    public static final int SUB_ALL = 2;
    public static final int D_BITE_M = 3;
    public static final int E_M = 4;
    public static final int M_L = 5;
    public static final int M_R = 6;
//    public static final int SWT_M = 7;
//    public static final int SWT_B = 8;
//    public static final int BTR_M = 9;
//    public static final int BTR_B = 10;

    private static Map<String, Field> fields = new HashMap<String, Field>();
    private static Map<String, Integer> values = new LinkedHashMap<String, Integer>();

    static {
        try {
            Class c = Consts.class;
            Field[] fs = c.getDeclaredFields();
            for (Field f : fs) {
                if (int.class.equals(f.getType()) && !"LENGTH".equals(f.getName())) {
                    fields.put(f.getName(), f);
                    values.put(f.getName(), f.getInt(null));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        LENGTH = values.size();
    }

    public static boolean[] exist = new boolean[LENGTH]; //不是每个常量组数都用到，只有被用字母代表的才会用到并在这里标记，这种方法比MAP要快

    static StringBuilder sb = new StringBuilder();

    public static void printLog(Animal a) {
        sb.setLength(0);
        int i = 0;
        for (Entry<String, Integer> e : values.entrySet()) {
            sb.append(e.getKey()).append("=").append(a.consts[e.getValue()]).append("\t\t");
            if (i++ % 6 == 5)
                sb.append("\n");
        }
        Logger.debug(sb.toString());
    }

    public static void constMutation(Animal a) { //全局参数变异, 这一个方法变异动物的所有常量 
        for (int i = 0; i < LENGTH; i++) {
            if (percent(20))
                a.consts[i] = RandomUtils.vary(a.consts[i]);
        }
    }

}
