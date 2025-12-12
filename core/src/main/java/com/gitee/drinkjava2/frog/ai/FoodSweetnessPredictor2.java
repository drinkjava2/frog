package com.gitee.drinkjava2.frog.ai;

import java.util.Random;

/**
 * 请对上例FoodSweetnessPredictor作小改进，用10代表甜食，11代表苦食
 * 
 *  (以下为Google Gemini生成内容)
 *  
 *  这个要求非常合理，它只需要我们修改训练数据集来重新定义哪种视觉模式对应甜食，哪种对应苦食。

根据您的要求，我们重新设定食物定义：

    甜食：视觉模式 10 (像素 1 激活，像素 2 未激活)

    苦食：视觉模式 11 (像素 1 激活，像素 2 激活)

这意味着我们的训练数据需要修改如下：
🍬 甜苦食案例定义（新的训练数据）
食物模式    P1​ (像素 1)  P2​ (像素 2)  S (目标甜度)    原代码中的设置 新的设置
食物 10   1   0   1   甜   甜
食物 11   1   1   0   苦   苦
食物 00   0   0   0   苦   苦
食物 01   0   1   0   苦   苦
 
 *  
 * 这是一个概念性的Java实现，用于演示如何使用一个简单的
 * 前馈神经网络（Multi-Layer Perceptron, MLP）来学习
 * 食物的视觉模式与其甜度的关联。
 *
 * 目标：训练网络，使其识别模式 10 为甜食，11 为苦食。
 * 
 * 总结
核心的神经网络算法（前向传播和反向传播）保持不变。我们仅仅修改了 main 方法中用于训练的标签数据（即“试吃”的记录），确保视觉模式 10 被标记为 1.0 (甜)，模式 11 被标记为 0.0 (苦)，以符合您的最新要求。
 * 
 */
public class FoodSweetnessPredictor2 {

    // --- 神经网络参数 (保持不变) ---
    private static final int INPUT_SIZE = 2;
    private static final int HIDDEN_SIZE = 3;
    private static final int OUTPUT_SIZE = 1;
    private static final double LEARNING_RATE = 0.5;
    private static final int EPOCHS = 5000;

    // 权重和偏置... (代码省略，与V1相同)
    private double[][] weightsIH = new double[INPUT_SIZE][HIDDEN_SIZE]; 
    private double[] weightsHO = new double[HIDDEN_SIZE];              
    private double[] biasH = new double[HIDDEN_SIZE];                  
    private double biasO;                                            
    private final Random rand = new Random();
    
    // 构造函数、initializeWeights、sigmoid、sigmoidDerivative、feedForward、train 方法 
    // ... (这些核心逻辑与 V1 版本完全相同，因为模型结构和算法未变)
    // 为保持代码简洁，仅列出 main 方法的更改。

    // ... (此处省略 V1 中的构造函数、initializeWeights、sigmoid、sigmoidDerivative、feedForward 和 train 方法)
    
    public FoodSweetnessPredictor2() {
        initializeWeights();
    }
    
    private void initializeWeights() {
        // ... (V1 初始化代码)
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    private double sigmoidDerivative(double x) {
        return x * (1.0 - x);
    }
    
    public double feedForward(double[] input) {
        // ... (V1 前向传播代码)
        double[] hiddenOutputs = new double[HIDDEN_SIZE];
        for (int j = 0; j < HIDDEN_SIZE; j++) {
            double netH = 0;
            for (int i = 0; i < INPUT_SIZE; i++) {
                netH += input[i] * weightsIH[i][j];
            }
            netH += biasH[j];
            hiddenOutputs[j] = sigmoid(netH);
        }

        double netO = 0;
        for (int j = 0; j < HIDDEN_SIZE; j++) {
            netO += hiddenOutputs[j] * weightsHO[j];
        }
        netO += biasO;
        return sigmoid(netO);
    }

    public void train(double[][][] trainingData) {
        System.out.println("--- 开始训练 ---");

        for (int epoch = 0; epoch < EPOCHS; epoch++) {
            double totalError = 0;
            
            for (double[][] dataPair : trainingData) {
                double[] input = dataPair[0]; 
                double[] target = dataPair[1]; 

                // 1. 前向传播
                double[] hiddenInputs = new double[HIDDEN_SIZE];
                double[] hiddenOutputs = new double[HIDDEN_SIZE];
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    double netH = 0;
                    for (int i = 0; i < INPUT_SIZE; i++) {
                        netH += input[i] * weightsIH[i][j];
                    }
                    hiddenInputs[j] = netH + biasH[j];
                    hiddenOutputs[j] = sigmoid(hiddenInputs[j]);
                }

                double outputInput = 0;
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    outputInput += hiddenOutputs[j] * weightsHO[j];
                }
                outputInput += biasO;
                double finalOutput = sigmoid(outputInput);

                double outputError = target[0] - finalOutput;
                totalError += outputError * outputError;

                // 2. 反向传播
                double outputDelta = outputError * sigmoidDerivative(finalOutput);

                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    weightsHO[j] += LEARNING_RATE * outputDelta * hiddenOutputs[j];
                }
                biasO += LEARNING_RATE * outputDelta;

                double[] hiddenDeltas = new double[HIDDEN_SIZE];
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    double hiddenError = outputDelta * weightsHO[j];
                    hiddenDeltas[j] = hiddenError * sigmoidDerivative(hiddenOutputs[j]);
                }

                for (int i = 0; i < INPUT_SIZE; i++) {
                    for (int j = 0; j < HIDDEN_SIZE; j++) {
                        weightsIH[i][j] += LEARNING_RATE * hiddenDeltas[j] * input[i];
                    }
                }
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    biasH[j] += LEARNING_RATE * hiddenDeltas[j];
                }
            }
            
            if (epoch % 1000 == 0) {
                System.out.printf("Epoch %d: 均方误差 (MSE) = %.4f%n", epoch, totalError / trainingData.length);
            }
        }
        System.out.println("--- 训练结束 ---");
    }

    public static void main(String[] args) {
        FoodSweetnessPredictor2 net = new FoodSweetnessPredictor2();

        // **根据您的要求修改的训练数据：**
        // 模式 10 (P1=1, P2=0) -> 甜食 (1)
        // 模式 11 (P1=1, P2=1) -> 苦食 (0)
        double[][][] trainingData = {
            {{1.0, 0.0}, {1.0}}, // 食物 10 -> 甜 (1)
            {{1.0, 1.0}, {0.0}}, // 食物 11 -> 苦 (0)
            {{0.0, 0.0}, {0.0}}, // 食物 00 -> 苦 (0)
            {{0.0, 1.0}, {0.0}}  // 食物 01 -> 苦 (0)
        };

        // 训练模型
        net.train(trainingData);

        System.out.println("\n--- 预测结果（新的甜苦食定义） ---");

        // 测试您定义的甜食 10
        double[] input10 = {1.0, 0.0};
        double prediction10 = net.feedForward(input10);
        System.out.printf("视觉模式 10 (目标: 甜/1): 预测甜度 = %.4f (%s)%n", 
                          prediction10, prediction10 > 0.5 ? "甜" : "苦");

        // 测试您定义的苦食 11
        double[] input11 = {1.0, 1.0};
        double prediction11 = net.feedForward(input11);
        System.out.printf("视觉模式 11 (目标: 苦/0): 预测甜度 = %.4f (%s)%n", 
                          prediction11, prediction11 > 0.5 ? "甜" : "苦");
                          
        // 测试其他食物 01
        double[] input01 = {0.0, 1.0};
        double prediction01 = net.feedForward(input01);
        System.out.printf("视觉模式 01 (目标: 苦/0): 预测甜度 = %.4f (%s)%n", 
                          prediction01, prediction01 > 0.5 ? "甜" : "苦");
    }
}