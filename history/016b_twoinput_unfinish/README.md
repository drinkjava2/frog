﻿﻿
# 注意，本目录目标并没有完成，只是半成品
本来打算用连线算法line来实现两点的模式识别，但时间太长做的太累，因为迟早要再用分裂算法再实现一遍，而且连线算法不如分裂算法节点有轻重，有局限性，所以兴趣突然失掉了，就暂时跳过这个连线算法的，直接在这个目录里存个档，有兴趣的朋友可以接着试试用连线算法能否实现两点的模式识别。   
下一步将转到用分裂算法来实现两点的模式识别, 项目版号改为016c, 见core目录。



## core目录简介 
core目录是当前工作目录，如果跑出什么结果就会拷贝一份放到history目录里存档。

当前目标是大方向是由遗传算法来自动排列脑细胞和触突参数，以实现模式识别功能，并与上下左右运动细胞、进食奖罚感觉细胞结合起来，实现吃掉无毒蘑菇，避开有毒蘑菇这个任务。(未完成)    
当前小目标:
1)是要利用阴阳8叉树分裂算法进化出第一个可以工作（向食物运动）的神经网络。(已完成, 见011_yinyan_eatfood)  
2)利用阴阳8叉树或4叉树分裂算法(见012_tree4)来进化出具备模式识别功能的神经网络。即实现图像到声音的关联，比如对应1,2,3,4 数字的图像会反同激活训练时对应的声音细胞(未完成)
3)简单时序信号的模式识别。比如AB后是C, AD后是E, ABC后是F，多次重复后即可形成时序信号的预测关联。功能类似RNN，但用分裂算法来实现参数自动生成。
  以上2和3识别原理类似，都建立在细胞连接的遗忘曲线基础上，但是一个强调细胞的空间位置关系，另一个强调信号留存的时间
  
当前小小目标：
进化出具备简单模式识别功能的神经网络，目前已实现一点的模式识别，现在卡在两个点的模式识别。