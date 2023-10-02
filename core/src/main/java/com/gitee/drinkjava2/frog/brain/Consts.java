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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.util.Logger;
import com.gitee.drinkjava2.frog.util.RandomUtils;

/**
 * Here store counts
 * 
 * 这个类存放脑常量定义、变异、日志打印相关的方法。
 * 神经网络中有一些全局常量，与结构生成无关，这时可以把所有常量定义到animal和egg的constGenes常量数组中，用统一的随机方法来变异这些常量
 * 
 * @author Yong Zhu
 * @since 15.0
 */
@SuppressWarnings("all")
public class Consts {
    public static int CountsQTY; //总常量数量

    private static int index_ = 0;

    private static int index() {
        return index_++;
    }

    public static final int ADD_EYE = index(); //用index()这种编程技巧而不是直接给定数值是为了增删常量定义时比较方便，不会影响其它行
    public static final int ADD_BITE = index(); 
    public static final int REDUCE_BITE = index();  
    public static final int HOLE_FRESH = index();
    public static final int HOLE_REDUCE = index();

    private static Map<String, Integer> indexs = new LinkedHashMap<String, Integer>();

    static {
        try {
            Class c = Consts.class;
            Field[] fs = c.getDeclaredFields();
            for (Field f : fs) { // 用反射来获取常量的名称并保存下来，将来在printLog中要按顺序打印出所有常量名
                if (int.class.equals(f.getType()) && !"LENGTH".equals(f.getName()) && !"index_".equals(f.getName())) {
                    indexs.put(f.getName(), f.getInt(null));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        CountsQTY = indexs.size();
    }

    public static boolean[] exist = new boolean[CountsQTY]; //不是每个常量组数都用到，只有被用字母代表的才会用到并在这里标记，这种方法比MAP要快

    static StringBuilder sb = new StringBuilder();

    public static void printLog(Animal a) {
        sb.setLength(0);
        int i = 0;
        for (Entry<String, Integer> e : indexs.entrySet()) {
            sb.append(e.getKey()).append("=").append(a.consts[e.getValue()]).append("\t\t");
            if (i++ % 6 == 5)
                sb.append("\n");
        }
        Logger.debug(sb.toString());
    }

    public static void constMutation(Animal a) { //全局参数变异, 这一个方法变异动物的所有常量 
        for (int i = 0; i < CountsQTY; i++) {
            if (percent(20))
                a.consts[i] = RandomUtils.vary(a.consts[i]);
        }
    }

}
