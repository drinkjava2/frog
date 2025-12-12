package com.gitee.drinkjava2.frog.ai;

import java.util.Random;

/**
 * 食物由两个像素组成，有四种模式，有两个细胞分别对应两个视细胞，两个细胞分别对应咬和松口动作细胞，
 * 两个细胞分别对应食物的甜和苦，写出任意一种食物为甜食时，用神经网络都能实现试吃几次后不需要依赖甜苦细胞的Java代码实现 
 * 
 * (以下为Google Gemini生成内容)
 * 
 * 这是一个概念性的Java实现，用于演示如何使用一个简单的
 * 前馈神经网络（Multi-Layer Perceptron, MLP）来学习
 * 食物的视觉模式与其甜度的关联。
 *
 * 目标：在训练完成后，网络仅依赖 P1 和 P2 即可预测甜度，
 * 不需要依赖实际的“甜苦细胞”输入。
 * 
 * 
 * 实现原理总结
    初始化： 网络（FoodSweetnessPredictor）随机初始化权重和偏置。
    训练（试吃）： 在 train 方法中，网络被反复输入食物的视觉模式 (P1​,P2​)，并将其预测结果与实际的甜苦细胞输出 (S) 进行比较（这对应于试吃几次的过程）。
    学习： 使用反向传播（Backpropagation）算法，根据误差（预测值与实际值之差）调整权重，使网络逐渐学会将模式 10 映射到 1（甜），将其他模式映射到 0（苦）。
    预测（不需要依赖甜苦细胞）： 训练完成后，在 main 方法的最后部分，调用 feedForward 方法时，只需提供视觉细胞的输入 (P1​,P2​)，网络就会根据它学到
    的知识直接输出预测的甜度值（接近 1 为甜，接近 0 为苦），从而跳过了实际的甜苦细胞的感官输入。
 */
public class FoodSweetnessPredictor {

    // --- 神经网络参数 ---
    private static final int INPUT_SIZE = 2; // P1, P2
    private static final int HIDDEN_SIZE = 3; // 隐藏层神经元数量
    private static final int OUTPUT_SIZE = 1; // 甜度S
    private static final double LEARNING_RATE = 0.5;
    private static final int EPOCHS = 5000; // 训练次数

    // 权重矩阵
    private double[][] weightsIH = new double[INPUT_SIZE][HIDDEN_SIZE]; // 输入到隐藏层
    private double[] weightsHO = new double[HIDDEN_SIZE]; // 隐藏层到输出层
    private double[] biasH = new double[HIDDEN_SIZE]; // 隐藏层偏置
    private double biasO; // 输出层偏置

    // 随机数生成器
    private final Random rand = new Random();

    public FoodSweetnessPredictor() {
        // 初始化权重和偏置为小的随机值
        initializeWeights();
    }

    private void initializeWeights() {
        // 范围 [-0.5, 0.5]
        for (int i = 0; i < INPUT_SIZE; i++) {
            for (int j = 0; j < HIDDEN_SIZE; j++) {
                weightsIH[i][j] = rand.nextDouble() - 0.5;
            }
        }
        for (int j = 0; j < HIDDEN_SIZE; j++) {
            weightsHO[j] = rand.nextDouble() - 0.5;
            biasH[j] = rand.nextDouble() - 0.5;
        }
        biasO = rand.nextDouble() - 0.5;
    }

    /**
     * Sigmoid激活函数：用于将神经元的输出压缩到 [0, 1] 范围。
     */
    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    /**
     * Sigmoid函数的导数：用于反向传播中的误差计算。
     */
    private double sigmoidDerivative(double x) {
        return x * (1.0 - x);
    }

    /**
     * 前向传播：计算给定输入的网络输出。
     * @param input P1, P2 的值
     * @return 预测的甜度 [0, 1]
     */
    public double feedForward(double[] input) {
        // 1. 计算隐藏层输入和输出
        double[] hiddenOutputs = new double[HIDDEN_SIZE];
        for (int j = 0; j < HIDDEN_SIZE; j++) {
            double netH = 0; // 净输入
            for (int i = 0; i < INPUT_SIZE; i++) {
                netH += input[i] * weightsIH[i][j];
            }
            netH += biasH[j];
            hiddenOutputs[j] = sigmoid(netH);
        }

        // 2. 计算输出层输入和输出 (甜度预测)
        double netO = 0;
        for (int j = 0; j < HIDDEN_SIZE; j++) {
            netO += hiddenOutputs[j] * weightsHO[j];
        }
        netO += biasO;
        return sigmoid(netO);
    }

    /**
     * 训练网络 (反向传播算法)。
     * @param trainingData 训练数据集
     */
    public void train(double[][][] trainingData) {
        System.out.println("--- 开始训练 ---");

        for (int epoch = 0; epoch < EPOCHS; epoch++) {
            double totalError = 0;

            for (double[][] dataPair : trainingData) {
                double[] input = dataPair[0]; // P1, P2
                double[] target = dataPair[1]; // 目标甜度 S (如 [1.0])

                // 1. 前向传播 (与 feedForward 类似，但需要保存中间结果)

                // 隐藏层
                double[] hiddenInputs = new double[HIDDEN_SIZE];
                double[] hiddenOutputs = new double[HIDDEN_SIZE];
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    double netH = 0;
                    for (int i = 0; i < INPUT_SIZE; i++) {
                        netH += input[i] * weightsIH[i][j];
                    }
                    hiddenInputs[j] = netH + biasH[j]; // 净输入
                    hiddenOutputs[j] = sigmoid(hiddenInputs[j]);
                }

                // 输出层
                double outputInput = 0;
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    outputInput += hiddenOutputs[j] * weightsHO[j];
                }
                outputInput += biasO;
                double finalOutput = sigmoid(outputInput); // 预测值

                // 计算误差
                double outputError = target[0] - finalOutput;
                totalError += outputError * outputError;

                // 2. 反向传播 (更新权重)

                // 计算输出层误差梯度 (δO)
                double outputDelta = outputError * sigmoidDerivative(finalOutput);

                // 更新隐藏层到输出层的权重 (weightsHO)
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    weightsHO[j] += LEARNING_RATE * outputDelta * hiddenOutputs[j];
                }
                // 更新输出层偏置 (biasO)
                biasO += LEARNING_RATE * outputDelta;

                // 计算隐藏层误差梯度 (δH)
                double[] hiddenDeltas = new double[HIDDEN_SIZE];
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    double hiddenError = outputDelta * weightsHO[j];
                    // 此处使用了隐藏层激活后的输出作为计算 sigmoidDerivative 的输入
                    // 严格来说应该用 netH，但用 hiddenOutputs[j] 更常见于简化实现。
                    hiddenDeltas[j] = hiddenError * sigmoidDerivative(hiddenOutputs[j]);
                }

                // 更新输入层到隐藏层的权重 (weightsIH)
                for (int i = 0; i < INPUT_SIZE; i++) {
                    for (int j = 0; j < HIDDEN_SIZE; j++) {
                        weightsIH[i][j] += LEARNING_RATE * hiddenDeltas[j] * input[i];
                    }
                }
                // 更新隐藏层偏置 (biasH)
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    biasH[j] += LEARNING_RATE * hiddenDeltas[j];
                }
            }

            // 打印训练进度
            if (epoch % 1000 == 0) {
                System.out.printf("Epoch %d: 均方误差 (MSE) = %.4f%n", epoch, totalError / trainingData.length);
            }
        }
        System.out.println("--- 训练结束 ---");
    }

    public static void main(String[] args) {
        FoodSweetnessPredictor net = new FoodSweetnessPredictor();

        // 训练数据 (视觉模式 -> 目标甜度S)
        // 模式 10 (P1=1, P2=0) 被定义为甜食 (S=1)
        double[][][] trainingData = { { { 1.0, 0.0 }, { 1.0 } }, // 食物 10 -> 甜 (1)
                { { 0.0, 0.0 }, { 0.0 } }, // 食物 00 -> 苦 (0)
                { { 0.0, 1.0 }, { 0.0 } }, // 食物 01 -> 苦 (0)
                { { 1.0, 1.0 }, { 0.0 } } // 食物 11 -> 苦 (0)
        };

        // 训练模型
        net.train(trainingData);

        System.out.println("\n--- 预测结果（无需依赖甜苦细胞） ---");

        // 经过训练后，网络可以直接根据视觉细胞(P1, P2)的活性来预测甜度。
        // 食物 10 (甜食)
        double[] input10 = { 1.0, 0.0 };
        double prediction10 = net.feedForward(input10);
        System.out.printf("视觉模式 10 (目标: 甜/1): 预测甜度 = %.4f (%s)%n", prediction10, prediction10 > 0.5 ? "甜" : "苦");

        // 食物 00 (苦食)
        double[] input00 = { 0.0, 0.0 };
        double prediction00 = net.feedForward(input00);
        System.out.printf("视觉模式 00 (目标: 苦/0): 预测甜度 = %.4f (%s)%n", prediction00, prediction00 > 0.5 ? "甜" : "苦");
    }
}