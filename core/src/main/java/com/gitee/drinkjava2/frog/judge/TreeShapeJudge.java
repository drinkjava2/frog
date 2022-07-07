package com.gitee.drinkjava2.frog.judge;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.brain.Cells;

/**
 * TreeShapeJudge to create a tree  
 */
public class TreeShapeJudge {//NOSONAR

    private static int countQty(Animal animal, int x, int y, int z) {
        int count = 0;
        for (int xx = -1; xx <= 1; xx++) {
            for (int yy = -1; yy <= 1; yy++) {
                int xxx = x + xx;
                int yyy = y + yy;
                if (!Animal.outBrainRange(xxx, yyy, z)) {
                    if ((animal.cells[xxx][yyy][z] & 1) > 0) {
                        count++;
                    }
                }
            }

        }
        return count;
    }

    public static void judge(Animal animal) {

        for (int x = 0; x < Env.BRAIN_CUBE_SIZE; x++)
            for (int y = 0; y < Env.BRAIN_CUBE_SIZE; y++)
                for (int z = 0; z <= Env.BRAIN_CUBE_SIZE-2; z++) {
                    long cell = animal.cells[x][y][z];
                    if ( (cell & Cells.MOVE_UP) > 0) {
                        int countThis = countQty(animal, x, y, z);
                        int countAbove = countQty(animal, x, y, z + 1);
                        if (countAbove>0 && z==(Env.BRAIN_CUBE_SIZE-1))
                            animal.awardAAA();
                        
                        if (countAbove>0 && (countAbove * 2 <= countThis))
                            animal.awardAAA();
                        if (countAbove>0 && (countAbove * 2 > countThis))
                            animal.penaltyAAA();
                    }

                }

    }

}
