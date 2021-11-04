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

import com.gitee.drinkjava2.frog.Animal;

/**
 * Organ is a zone represents a group of similar cells
 * Organ器官表示一组相似参数的细胞, 器官的作用是影响细胞的行为，器官可以包含多个细胞，一个细胞也可以被多个器官包含
 * 从这个版本开始，器官和细胞一样，由基因决定随机生成，不再是手工排列了
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Organ { //organ数量也很多，不需要序列化
    public int x; //x,y,z 是organ的中心点在脑中的位置
    public int y;
    public int z; 
    public int r;//organ的半径
    
    
    public int geneIndex; //指向青蛙基因单例中的行号，保留这个是为了将来可能的用进废退做准备，即细胞对生存的影响反过来对基因变异率发生作用 
    
    public Organ(Animal animal, int x, int y, int z, int geneIndex) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.geneIndex = geneIndex; 
        if (!Animal.outBrainRange(x, y, z)) {
            Cell c= animal.cells3D.getCell(x, y, z);
            if(c!=null)
                animal.normalPenalty();
            else {
//            animal.cells.add(this);
//            animal.cells3D.putCell(this, animal.cells.size()); //在cell3D中登记cell序号
            }
        }
    }
   
}
