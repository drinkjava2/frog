/**接着改进，把每个节点(或细胞)改成可以鼠标按下拖放到任意位置 
*/
package ai;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * 4像素神经网络：高速演化与定时画面定格仿真器 (鼠标可拖拽细胞版)
 */
public class EvolutionWindow extends JFrame {
    private EvolutionPanel evolutionPanel;
    private JLabel statusLabel;
    private JTextField speedField;
    private JButton startBtn;
    private JButton pauseBtn;
    private JButton resumeBtn;

    private volatile long generationCount = 0;
    private volatile VisualOrganism volatileCurrentOrg = null;
    private volatile String volatileStageText = "等待开始...";
    
    private volatile boolean isRunning = false;
    private volatile boolean isPaused = false;
    private Thread evolutionThread = null;
    private javax.swing.Timer renderTimer = null;

    public EvolutionWindow() {
        this.setTitle("4像素神经网络：高速演化与定量定格仿真器 (支持鼠标拖拽细胞)");
        this.setSize(950, 720);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // 主画布
        evolutionPanel = new EvolutionPanel();
        this.getContentPane().add(evolutionPanel, BorderLayout.CENTER);

        // 底部控制面板
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(30, 35, 45));
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        statusLabel = new JLabel("系统就绪，请点击开始。暂停或成功后可鼠标拖拽细胞。");
        statusLabel.setForeground(Color.YELLOW);
        statusLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));

        startBtn = new JButton("开始新演化");
        pauseBtn = new JButton("暂停");
        resumeBtn = new JButton("继续");
        pauseBtn.setEnabled(false);
        resumeBtn.setEnabled(false);

        JLabel speedLabel = new JLabel("速度 (1-1000):");
        speedLabel.setForeground(Color.WHITE);
        speedField = new JTextField("200", 4);

        controlPanel.add(statusLabel);
        controlPanel.add(startBtn);
        controlPanel.add(pauseBtn);
        controlPanel.add(resumeBtn);
        controlPanel.add(speedLabel);
        controlPanel.add(speedField);

        this.getContentPane().add(controlPanel, BorderLayout.SOUTH);

        // 按钮事件
        startBtn.addActionListener(e -> startNewEvolution());
        pauseBtn.addActionListener(e -> {
            isPaused = true;
            pauseBtn.setEnabled(false);
            resumeBtn.setEnabled(true);
        });
        resumeBtn.addActionListener(e -> {
            isPaused = false;
            pauseBtn.setEnabled(true);
            resumeBtn.setEnabled(false);
        });
    }

    private synchronized void startNewEvolution() {
        isRunning = false;
        isPaused = false;
        if (evolutionThread != null) {
            evolutionThread.interrupt();
        }
        if (renderTimer != null) {
            renderTimer.stop();
        }

        generationCount = 0;
        volatileStageText = "加速冲刺中...";
        statusLabel.setText("演化中，请稍候...");
        statusLabel.setForeground(Color.YELLOW);
        
        startBtn.setEnabled(false);
        pauseBtn.setEnabled(true);
        resumeBtn.setEnabled(false);

        // 每100毫秒刷新一次画布
        renderTimer = new javax.swing.Timer(100, e -> {
            VisualOrganism snapshotOrg = volatileCurrentOrg;
            if (snapshotOrg != null) {
                evolutionPanel.updateOrganism(snapshotOrg, generationCount, volatileStageText);
            }
        });
        renderTimer.start();

        isRunning = true;
        evolutionThread = new Thread(() -> {
            while (isRunning) {
                while (isPaused && isRunning) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        return;
                    }
                }

                generationCount++;
                
                int speed = 1000;
                try {
                    speed = Integer.parseInt(speedField.getText().trim());
                    if (speed < 1) speed = 1;
                    if (speed > 1000) speed = 1000;
                } catch (Exception ex) {
                    speed = 1000;
                }
                
                if (speed < 1000) {
                    try {
                        long sleepTime = (1000 - speed) / 20;
                        if (sleepTime > 0) Thread.sleep(sleepTime);
                    } catch (InterruptedException ex) {
                        return;
                    }
                }

                VisualOrganism org = VisualOrganism.createRandomOrganism(850, 420);
                volatileCurrentOrg = org;

                // 训练
                org.forward(new boolean[]{true, true, false, false}, true, false);
                org.forward(new boolean[]{false, true, true, true}, false, true);

                // 纯视觉盲测考核
                boolean[] testEnemy = org.forward(new boolean[]{true, true, false, false}, false, false);
                if (!testEnemy[0] || testEnemy[1]) continue;

                boolean[] testFood = org.forward(new boolean[]{false, true, true, true}, false, false);
                if (!testFood[1] || testFood[0]) continue;

                boolean[] testNoise = org.forward(new boolean[]{false, false, false, true}, false, false);
                if (testNoise[0] || testNoise[1]) continue;

                // 成功突围
                renderTimer.stop();
                isRunning = false;

                org.forward(new boolean[]{true, true, false, false}, false, false);
                evolutionPanel.updateOrganism(org, generationCount, "进化成功！已涌现出闭环后天条件反射。");

                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("�� 演化成功！历经 " + generationCount + " 代盲目试错。您现在可以任意拖动细胞节点。");
                    statusLabel.setForeground(new Color(46, 204, 113));
                    startBtn.setEnabled(true);
                    pauseBtn.setEnabled(false);
                    resumeBtn.setEnabled(false);
                });
                break;
            }
        });
        evolutionThread.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EvolutionWindow window = new EvolutionWindow();
            window.setVisible(true);
        });
    }

    public enum Chem {
        信号1, 信号2, 信号3, 信号4, 无
    }

    // =========================================================================
    // 内部类 2: 支持鼠标拖拽的渲染面板
    // =========================================================================
    public static class EvolutionPanel extends JPanel {
        private VisualOrganism currentOrganism;
        private long currentGeneration = 0;
        private String currentStageText = "等待进化开始...";
        
        private final int GRAPHICS_Y_OFFSET = 120; // 画面整体下移，防文字覆盖
        private final int NODE_RADIUS = 22;        // 细胞碰撞球半径

        // 鼠标拖拽状态持有变量
        private VisualNode draggedNode = null;

        public EvolutionPanel() {
            this.setBackground(new Color(20, 24, 35));

            // 构建鼠标适配器，统一处理按下与拖动事件
            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (currentOrganism == null) return;
                    
                    // 考虑画面整体下移了 GRAPHICS_Y_OFFSET，需要对鼠标的真实 Y 坐标进行反向偏置换算
                    int mouseX = e.getX();
                    int mouseY = e.getY() - GRAPHICS_Y_OFFSET;

                    // 遍历当前生命的细胞节点，判断鼠标点中了哪一个细胞
                    for (VisualNode node : currentOrganism.allNodes.values()) {
                        double distance = Math.sqrt(Math.pow(node.position.x - mouseX, 2) + Math.pow(node.position.y - mouseY, 2));
                        if (distance <= NODE_RADIUS) {
                            draggedNode = node; // 捕获被拖拽节点
                            break;
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    draggedNode = null; // 释放鼠标，清空状态
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (draggedNode != null) {
                        // 实时更新被选中的节点坐标（同样计算 Y 偏置）
                        int newX = e.getX();
                        int newY = e.getY() - GRAPHICS_Y_OFFSET;

                        // 简单的边界限制，防止细胞被拽出窗体丢失
                        if (newX < 30) newX = 30;
                        if (newX > getWidth() - 30) newX = getWidth() - 30;
                        if (newY < 20) newY = 20;
                        if (newY > getHeight() - GRAPHICS_Y_OFFSET - 40) newY = getHeight() - GRAPHICS_Y_OFFSET - 40;

                        draggedNode.position.setLocation(newX, newY);
                        repaint(); // 强制触发界面重绘，让连线和球体平滑跟着鼠标走
                    }
                }
            };

            this.addMouseListener(mouseHandler);
            this.addMouseMotionListener(mouseHandler);
        }

        public void updateOrganism(VisualOrganism org, long generation, String stageText) {
            this.currentOrganism = org;
            this.currentGeneration = generation;
            this.currentStageText = stageText;
            this.repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (currentOrganism == null) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. 顶部文字
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("微软雅黑", Font.BOLD, 14));
            g2.drawString("大自然盲目演化代数: " + currentGeneration, 30, 30);

            g2.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            g2.setColor(new Color(200, 200, 200));
            g2.drawString("状态探针: " + currentStageText, 30, 55);

            // 刺激环境指示灯点亮
            boolean isPain = currentOrganism.allNodes.get("痛觉").isActivated;
            boolean isSweet = currentOrganism.allNodes.get("甜味").isActivated;
            boolean hasPredator = currentOrganism.allNodes.get("像素A").isActivated && currentOrganism.allNodes.get("像素B").isActivated && !currentOrganism.allNodes.get("像素C").isActivated;
            boolean hasFood = !currentOrganism.allNodes.get("像素A").isActivated && currentOrganism.allNodes.get("像素B").isActivated && currentOrganism.allNodes.get("像素C").isActivated;

            g2.drawString("当前刺激信号: ", 30, 85);
            drawIndicator(g2, 125, 73, "天敌近身", hasPredator, Color.RED);
            drawIndicator(g2, 225, 73, "食物出现", hasFood, Color.GREEN);
            drawIndicator(g2, 325, 73, "剧烈痛觉", isPain, Color.MAGENTA);
            drawIndicator(g2, 425, 73, "高糖甜味", isSweet, Color.ORANGE);

            // 2. 绘制轴突连接线
            for (VisualLink link : currentOrganism.links) {
                VisualNode fromNode = currentOrganism.allNodes.get(link.fromId);
                VisualNode toNode = currentOrganism.allNodes.get(link.toId);
                if (fromNode == null || toNode == null) continue;

                float strokeWidth = 0.5f + (float) (link.weight / 100.0 * 4.5);
                g2.setStroke(new BasicStroke(strokeWidth));

                if (link.modulatedBy != Chem.无) {
                    g2.setColor(new Color(243, 156, 18, 180)); 
                } else {
                    g2.setColor(new Color(127, 140, 141, 70)); 
                }

                if (fromNode.isActivated && link.weight > 10) {
                    g2.setColor(Color.YELLOW); 
                }

                g2.drawLine(fromNode.position.x, fromNode.position.y + GRAPHICS_Y_OFFSET, 
                            toNode.position.x, toNode.position.y + GRAPHICS_Y_OFFSET);
            }

            // 3. 绘制细胞球体
            for (VisualNode node : currentOrganism.allNodes.values()) {
                if (node.isActivated) {
                    if (node.id.equals("逃跑") || node.id.equals("痛觉")) {
                        g2.setColor(new Color(231, 76, 60));
                    } else if (node.id.equals("进食") || node.id.equals("甜味")) {
                        g2.setColor(new Color(46, 204, 113));
                    } else {
                        g2.setColor(new Color(52, 152, 219));
                    }
                } else {
                    g2.setColor(new Color(44, 62, 80));
                }

                int drawX = node.position.x;
                int drawY = node.position.y + GRAPHICS_Y_OFFSET;

                g2.fillOval(drawX - NODE_RADIUS, drawY - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawOval(drawX - NODE_RADIUS, drawY - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

                g2.setFont(new Font("Consolas", Font.PLAIN, 10));
                g2.setColor(Color.CYAN);
                g2.drawString("Th:" + (int)node.threshold, drawX - 14, drawY + 4);

                g2.setFont(new Font("微软雅黑", Font.PLAIN, 11));
                g2.setColor(Color.WHITE);
                g2.drawString(node.id, drawX - NODE_RADIUS, drawY - NODE_RADIUS - 4);
                
                if (node.layer == 1 && node.releaseChem != Chem.无) {
                    g2.setFont(new Font("微软雅黑", Font.ITALIC, 10));
                    g2.setColor(Color.YELLOW);
                    g2.drawString("释:" + node.releaseChem.name(), drawX - NODE_RADIUS, drawY + NODE_RADIUS + 13);
                }
            }
        }

        private void drawIndicator(Graphics2D g2, int x, int y, String label, boolean isActive, Color activeColor) {
            g2.setColor(isActive ? activeColor : Color.DARK_GRAY);
            g2.fillOval(x, y, 12, 12);
            g2.setColor(Color.GRAY);
            g2.drawOval(x, y, 12, 12);
            g2.setColor(Color.LIGHT_GRAY);
            g2.setFont(new Font("微软雅黑", Font.PLAIN, 11));
            g2.drawString(label, x + 16, y + 11);
        }
    }

    public static class VisualLink {
        public String fromId;
        public String toId;
        public double weight;
        public Chem modulatedBy = Chem.无;
        public double plasticityDelta = 0;

        public VisualLink(String fromId, String toId, double weight, Chem modulatedBy, double plasticityDelta) {
            this.fromId = fromId;
            this.toId = toId;
            this.weight = weight;
            this.modulatedBy = modulatedBy;
            this.plasticityDelta = plasticityDelta;
        }
    }

    public static class VisualNode {
        public String id;
        public double threshold;
        public double currentInput = 0;
        public boolean isActivated = false;
        public Chem releaseChem = Chem.无;
        public Point position; 
        public int layer;

        public VisualNode(String id, double threshold, Chem releaseChem, int layer, Point position) {
            this.id = id;
            this.threshold = threshold;
            this.releaseChem = releaseChem;
            this.layer = layer;
            this.position = position;
        }

        public void reset() {
            this.currentInput = 0;
            this.isActivated = false;
        }

        public void evaluate() {
            this.isActivated = (this.currentInput >= this.threshold);
        }
    }

    public static class VisualOrganism {
        public static final String[] INPUTS = {"像素A", "像素B", "像素C", "像素D", "痛觉", "甜味"};
        public static final String ACTION_FLEE = "逃跑";
        public static final String ACTION_BITE = "进食";

        public Map<String, VisualNode> allNodes = new LinkedHashMap<>();
        public List<VisualLink> links = new ArrayList<>();
        public Map<Chem, Boolean> activeChems = new HashMap<>();

        public static VisualOrganism createRandomOrganism(int netWidth, int netHeight) {
            VisualOrganism org = new VisualOrganism();
            Random rand = new Random();
            
            for (Chem c : Chem.values()) {
                org.activeChems.put(c, false);
            }

            // 输入端固定布局
            int inputCount = INPUTS.length;
            for (int i = 0; i < inputCount; i++) {
                int x = 80;
                int y = 40 + i * (netHeight - 80) / (inputCount - 1);
                org.allNodes.put(INPUTS[i], new VisualNode(INPUTS[i], 10, Chem.无, 0, new Point(x, y)));
            }

            // 中继胞矩阵化排布（保持初始间距相同）
            int index = 1;
            for (int col = 0; col < 2; col++) {
                for (int row = 0; row < 3; row++) {
                    String id = "中继" + index;
                    double randTh = 10 + rand.nextInt(140);
                    Chem randChem = Chem.values()[rand.nextInt(Chem.values().length)];
                    int x = 320 + col * 220; 
                    int y = 50 + row * (netHeight - 100) / 2;
                    
                    org.allNodes.put(id, new VisualNode(id, randTh, randChem, 1, new Point(x, y)));
                    index++;
                }
            }

            // 输出行动端固定布局
            org.allNodes.put(ACTION_FLEE, new VisualNode(ACTION_FLEE, 50, Chem.无, 2, new Point(netWidth - 80, netHeight / 3)));
            org.allNodes.put(ACTION_BITE, new VisualNode(ACTION_BITE, 50, Chem.无, 2, new Point(netWidth - 80, (netHeight / 3) * 2)));

            // 突触拉线
            List<String> allSrc = new ArrayList<>(org.allNodes.keySet());
            allSrc.remove(ACTION_FLEE);
            allSrc.remove(ACTION_BITE);
            
            List<String> allDst = new ArrayList<>(org.allNodes.keySet());
            for (String in : INPUTS) {
                allDst.remove(in);
            }

            int linkCount = 20 + rand.nextInt(12);
            for (int i = 0; i < linkCount; i++) {
                String src = allSrc.get(rand.nextInt(allSrc.size()));
                String dst = allDst.get(rand.nextInt(allDst.size()));
                if (src.equals(dst)) continue;

                double randWeight = rand.nextDouble() * 100;
                Chem randMod = Chem.values()[rand.nextInt(Chem.values().length)];
                double randDelta = rand.nextBoolean() ? 100 : -100;
                
                org.links.add(new VisualLink(src, dst, randWeight, randMod, randDelta));
            }

            return org;
        }

        public boolean[] forward(boolean[] pixelInputs, boolean pain, boolean sweet) {
            for (VisualNode node : allNodes.values()) {
                node.reset();
            }

            allNodes.get("像素A").currentInput = pixelInputs[0] ? 100.0 : 0.0;
            allNodes.get("像素B").currentInput = pixelInputs[1] ? 100.0 : 0.0;
            allNodes.get("像素C").currentInput = pixelInputs[2] ? 100.0 : 0.0;
            allNodes.get("像素D").currentInput = pixelInputs[3] ? 100.0 : 0.0;
            allNodes.get("痛觉").currentInput = pain ? 100.0 : 0.0;
            allNodes.get("甜味").currentInput = sweet ? 100.0 : 0.0;

            for (String in : INPUTS) {
                allNodes.get(in).evaluate();
            }

            for (int iter = 0; iter < 3; iter++) {
                for (VisualNode n : allNodes.values()) {
                    if (n.layer == 1) {
                        n.evaluate();
                        if (n.isActivated && n.releaseChem != Chem.无) {
                            activeChems.put(n.releaseChem, true);
                        }
                    }
                }

                for (VisualLink link : links) {
                    VisualNode fromNode = allNodes.get(link.fromId);
                    VisualNode toNode = allNodes.get(link.toId);
                    double srcOut = fromNode.isActivated ? 100.0 : 0.0;
                    toNode.currentInput += srcOut * (link.weight / 100.0);
                }
            }

            allNodes.get(ACTION_FLEE).evaluate();
            allNodes.get(ACTION_BITE).evaluate();

            boolean fleeTriggered = allNodes.get(ACTION_FLEE).isActivated;
            boolean biteTriggered = allNodes.get(ACTION_BITE).isActivated;

            for (VisualLink link : links) {
                if (link.modulatedBy != Chem.无 && activeChems.getOrDefault(link.modulatedBy, false)) {
                    link.weight = Math.max(0, Math.min(100, link.weight + link.plasticityDelta));
                }
            }

            for (Chem c : Chem.values()) {
                activeChems.put(c, false);
            }

            return new boolean[]{fleeTriggered, biteTriggered};
        }
    }
}