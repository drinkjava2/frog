package com.gitee.drinkjava2.frog.util;

/**
 * Debug utility to replace System.out
 * 
 * @author Yong Zhu
 * @since 2.0.5
 */
@SuppressWarnings("all")
public class Systemout {
    public static boolean allowPrint = true;

    public static boolean isAllowPrint() {
        return allowPrint;
    }

    public static void setAllowPrint(boolean allowPrint) {
        Systemout.allowPrint = allowPrint;
    }

    public static void print(Object obj) {
        if (allowPrint)
            System.out.print(obj);
    }

    public static void println(Object obj) {
        if (allowPrint)
            System.out.println(obj);
    }

    public static void println() {
        if (allowPrint)
            System.out.println();
    }

}
