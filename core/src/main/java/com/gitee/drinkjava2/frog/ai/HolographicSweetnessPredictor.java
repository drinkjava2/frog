package com.gitee.drinkjava2.frog.ai;

import java.util.Arrays;

/**
 * 
 *  以上例子FoodSweetnessPredictor的实现，如果不使用反向传播，而是使用全息存贮原理，如何实现同样的要求，要给出Java源码
 *  
 *  (以下为Google Gemini生成内容)

使用全息存储原理（Holographic Associative Memory, HAM）来实现这个要求是一种非常巧妙且不同的方法。与依赖迭代训练（如反向传播）的神经网络不同，全息存储是一种单次学习或一步到位的联想记忆模型。

在这种模型中，记忆被存储在一个关联矩阵（或称为全息图矩阵）中。当输入一个模式时，矩阵会“重构”出与该模式关联的输出模式。
🧠 全息存储原理实现（Java 概念框架）

    输入模式 (X)：食物的视觉细胞激活状态 (P1​,P2​)。为了适应关联记忆模型的数学要求，我们将输入向量进行双极性编码（Bipolar Encoding）：

        0→−1

        1→+1

        例如：模式 10 (甜食) →X=[+1,−1]T

    输出模式 (Y)：食物的甜度/苦度感官状态 (S,B 细胞)。我们使用两个细胞，也进行双极性编码：

        甜 (S=1,B=0) →Y=[+1,−1]T

        苦 (S=0,B=1) →Y=[−1,+1]T

    学习/存储（一步到位）：关联矩阵 M 通过所有训练样本的外积（Outer Product）之和来构建。

M=k=1∑N​Yk​XkT​

    预测/重构：当输入一个新的食物模式 Xnew​ 时，预测的输出 Ypred​ 是矩阵 M 和输入 Xnew​ 的乘积，并通过一个阈值函数（如符号函数 sgn）来恢复原始的双极性输出。

Ypred​=sgn(M⋅Xnew​)


 * 使用全息存储原理（Holographic Associative Memory, HAM）
 * 实现食物视觉模式到甜度感官的单次联想记忆。
 * 
 * 
 * 
 * 关键原理对比（反向传播 vs. 全息存储）
特性  反向传播 (上一个例子)    全息存储 (本例)
学习方式    迭代训练 (需要 EPOCHS=5000 次) 单步计算 (一步到位)
存储介质    权重 (Weights) 和 偏置 (Biases)  关联矩阵 (M)
数学操作    矩阵乘法、激活函数、梯度下降  外积和、矩阵乘法、符号函数 (sgn)
优势  能学习高度复杂的非线性关系   学习速度极快；对输入噪声有一定容错能力
限制  训练时间长   只能学习线性关系；容量有限（当模式过多时容易混淆）

 */
public class HolographicSweetnessPredictor {

    private static final int INPUT_SIZE = 2;  // P1, P2
    private static final int OUTPUT_SIZE = 2; // 甜度, 苦度

    // 训练数据（视觉模式 X 和 感官目标 Y）
    // 视觉输入 (P1, P2) -> 感官目标 (甜, 苦)
    private static final double[][] VISUAL_INPUTS = {
        {1.0, 0.0}, // 模式 10 (甜食)
        {1.0, 1.0}, // 模式 11 (苦食)
        {0.0, 0.0}, // 模式 00 (苦食)
        {0.0, 1.0}  // 模式 01 (苦食)
    };

    private static final double[][] SENSORY_TARGETS = {
        {1.0, 0.0}, // 甜 (S=1, B=0)
        {0.0, 1.0}, // 苦 (S=0, B=1)
        {0.0, 1.0}, // 苦 (S=0, B=1)
        {0.0, 1.0}  // 苦 (S=0, B=1)
    };
    
    // 全息关联矩阵 M
    private double[][] associationMatrix = new double[OUTPUT_SIZE][INPUT_SIZE];

    /**
     * 编码：将 0/1 输入转换为双极性 -1/+1。
     */
    private double[] bipolarEncode(double[] data) {
        double[] encoded = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            // 0 -> -1, 1 -> +1
            encoded[i] = (data[i] == 1.0) ? 1.0 : -1.0;
        }
        return encoded;
    }

    /**
     * 学习/存储：一步构建关联矩阵 M。
     */
    public void storeMemories() {
        System.out.println("--- 全息存储：一步学习 ---");
        
        // 遍历所有训练样本
        for (int k = 0; k < VISUAL_INPUTS.length; k++) {
            double[] X = bipolarEncode(VISUAL_INPUTS[k]);    // P1, P2 编码后的输入向量
            double[] Y = bipolarEncode(SENSORY_TARGETS[k]); // 甜, 苦 编码后的目标向量

            // 计算外积 Y * X^T 并累加到 M
            // M[i][j] += Y[i] * X[j]
            for (int i = 0; i < OUTPUT_SIZE; i++) {
                for (int j = 0; j < INPUT_SIZE; j++) {
                    associationMatrix[i][j] += Y[i] * X[j];
                }
            }
        }
        
        System.out.println("关联矩阵 M 构建完成：");
        printMatrix(associationMatrix);
    }
    
    /**
     * 预测/重构：使用关联矩阵 M 预测输出。
     * @param input 0/1 编码的视觉模式 (P1, P2)
     * @return 预测的甜度/苦度（双极性编码）
     */
    public double[] recall(double[] input) {
        double[] X_new = bipolarEncode(input);
        double[] Y_reconstruct = new double[OUTPUT_SIZE];

        // 矩阵乘法: Y_reconstruct = M * X_new
        // Y[i] = sum(M[i][j] * X[j])
        for (int i = 0; i < OUTPUT_SIZE; i++) {
            double sum = 0;
            for (int j = 0; j < INPUT_SIZE; j++) {
                sum += associationMatrix[i][j] * X_new[j];
            }
            Y_reconstruct[i] = sum;
        }
        
        // 应用符号函数 (Thresholding) 来恢复双极性输出
        double[] Y_sgn = new double[OUTPUT_SIZE];
        for (int i = 0; i < OUTPUT_SIZE; i++) {
            // sgn(x): 如果 x >= 0 则为 +1，否则为 -1
            Y_sgn[i] = (Y_reconstruct[i] >= 0) ? 1.0 : -1.0;
        }
        
        return Y_sgn;
    }

    /**
     * 解码：将双极性 -1/+1 转换为 0/1 输出。
     */
    private double[] bipolarDecode(double[] encodedData) {
        double[] decoded = new double[encodedData.length];
        for (int i = 0; i < encodedData.length; i++) {
            // +1 -> 1, -1 -> 0
            decoded[i] = (encodedData[i] == 1.0) ? 1.0 : 0.0;
        }
        return decoded;
    }
    
    private void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    public static void main(String[] args) {
        HolographicSweetnessPredictor ham = new HolographicSweetnessPredictor();
        
        // 1. 学习/存储（只需要一次）
        ham.storeMemories();
        
        System.out.println("\n--- 预测结果（无需依赖甜苦细胞） ---");
        
        // 测试模式 10 (目标: 甜)
        double[] input10 = {1.0, 0.0};
        double[] result10 = ham.recall(input10);
        double[] decoded10 = ham.bipolarDecode(result10);
        System.out.printf("视觉模式 10 (目标: 甜): 预测(甜/苦): %s -> 解码: %s%n", 
                          Arrays.toString(result10), Arrays.toString(decoded10));

        // 测试模式 11 (目标: 苦)
        double[] input11 = {1.0, 1.0};
        double[] result11 = ham.recall(input11);
        double[] decoded11 = ham.bipolarDecode(result11);
        System.out.printf("视觉模式 11 (目标: 苦): 预测(甜/苦): %s -> 解码: %s%n", 
                          Arrays.toString(result11), Arrays.toString(decoded11));

        // 测试模式 00 (目标: 苦)
        double[] input00 = {0.0, 0.0};
        double[] result00 = ham.recall(input00);
        double[] decoded00 = ham.bipolarDecode(result00);
        System.out.printf("视觉模式 00 (目标: 苦): 预测(甜/苦): %s -> 解码: %s%n", 
                          Arrays.toString(result00), Arrays.toString(decoded00));
    }
}