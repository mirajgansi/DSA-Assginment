import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// Main Application Class
public class NetworkOptimizerApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Network Topology Optimizer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            NetworkPanel networkPanel = new NetworkPanel();
            frame.add(networkPanel);

            frame.setVisible(true);
        });
    }
}

// Node Class
class Node {
    private String id;
    private int x, y;
    private boolean isServer;

    public Node(String id, int x, int y, boolean isServer) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.isServer = isServer;
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isServer() {
        return isServer;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}

// Edge Class
class NetworkEdge {
    private Node source;
    private Node target;
    private double cost;
    private double latency;
    private double bandwidth;

    public NetworkEdge(Node source, Node target, double cost, double latency, double bandwidth) {
        this.source = source;
        this.target = target;
        this.cost = cost;
        this.latency = latency;
        this.bandwidth = bandwidth;
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    public double getCost() {
        return cost;
    }

    public double getLatency() {
        return latency;
    }

    public double getBandwidth() {
        return bandwidth;
    }
}

// Network Panel
class NetworkPanel extends JPanel {
    private List<Node> nodes;
    private List<NetworkEdge> edges;
    private Node selectedNode;
    private Node firstNodeForEdge;
    private JLabel metricsLabel;

    public NetworkPanel() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        setLayout(new BorderLayout());
        setupUI();
        setupEventHandlers();
    }

    private void setupUI() {
        JPanel controlPanel = new JPanel();
        JButton addNodeBtn = new JButton("Add Node");
        JButton addEdgeBtn = new JButton("Add Edge");
        JButton optimizeBtn = new JButton("Optimize (Simple MST)");

        controlPanel.add(addNodeBtn);
        controlPanel.add(addEdgeBtn);
        controlPanel.add(optimizeBtn);

        metricsLabel = new JLabel("Cost: 0 | Latency: 0 | Bandwidth: 0");
        controlPanel.add(metricsLabel);

        add(controlPanel, BorderLayout.NORTH);

        addNodeBtn.addActionListener(e -> {
            String id = "N" + nodes.size();
            nodes.add(new Node(id, 100, 100, false));
            repaint();
        });

        addEdgeBtn.addActionListener(e -> {
            firstNodeForEdge = null; // Reset for new edge creation
            JOptionPane.showMessageDialog(this, "Click two nodes to create an edge");
        });

        optimizeBtn.addActionListener(e -> optimizeNetwork());
    }

    private void setupEventHandlers() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Node clicked = findNodeAt(e.getX(), e.getY());
                if (clicked != null) {
                    if (firstNodeForEdge == null) {
                        firstNodeForEdge = clicked;
                    } else {
                        edges.add(new NetworkEdge(firstNodeForEdge, clicked, 10.0, 5.0, 100.0));
                        firstNodeForEdge = null;
                        updateMetrics();
                        repaint();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                selectedNode = findNodeAt(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedNode != null) {
                    selectedNode.setX(e.getX());
                    selectedNode.setY(e.getY());
                    repaint();
                }
            }
        });
    }

    private Node findNodeAt(int x, int y) {
        for (Node node : nodes) {
            if (Math.abs(node.getX() - x) < 10 && Math.abs(node.getY() - y) < 10) {
                return node;
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw edges
        for (NetworkEdge edge : edges) {
            Node source = edge.getSource();
            Node target = edge.getTarget();
            g2d.setColor(Color.BLACK);
            g2d.drawLine(source.getX(), source.getY(), target.getX(), target.getY());

            String label = String.format("C:%.1f L:%.1f B:%.1f",
                    edge.getCost(), edge.getLatency(), edge.getBandwidth());
            int midX = (source.getX() + target.getX()) / 2;
            int midY = (source.getY() + target.getY()) / 2;
            g2d.drawString(label, midX, midY);
        }

        // Draw nodes
        for (Node node : nodes) {
            g2d.setColor(node.isServer() ? Color.BLUE : Color.GREEN);
            g2d.fillOval(node.getX() - 10, node.getY() - 10, 20, 20);
            g2d.setColor(Color.BLACK);
            g2d.drawString(node.getId(), node.getX() - 5, node.getY() - 12);
        }
    }

    private void optimizeNetwork() {
        // Simple MST implementation (greedy minimum cost)
        if (nodes.size() < 2)
            return;

        List<NetworkEdge> mstEdges = new ArrayList<>();
        List<Node> connected = new ArrayList<>();
        connected.add(nodes.get(0));

        while (connected.size() < nodes.size()) {
            NetworkEdge minEdge = null;
            double minCost = Double.MAX_VALUE;

            for (NetworkEdge edge : edges) {
                Node source = edge.getSource();
                Node target = edge.getTarget();
                if (connected.contains(source) && !connected.contains(target) && edge.getCost() < minCost) {
                    minEdge = edge;
                    minCost = edge.getCost();
                } else if (connected.contains(target) && !connected.contains(source) && edge.getCost() < minCost) {
                    minEdge = edge;
                    minCost = edge.getCost();
                }
            }

            if (minEdge != null) {
                mstEdges.add(minEdge);
                connected.add(minEdge.getSource());
                connected.add(minEdge.getTarget());
            } else {
                break; // No more valid edges
            }
        }

        edges = mstEdges;
        updateMetrics();
        repaint();
    }

    private void updateMetrics() {
        double totalCost = edges.stream().mapToDouble(NetworkEdge::getCost).sum();
        double totalLatency = edges.stream().mapToDouble(NetworkEdge::getLatency).sum();
        double avgBandwidth = edges.stream().mapToDouble(NetworkEdge::getBandwidth).average().orElse(0);
        metricsLabel.setText(String.format("Cost: %.1f | Latency: %.1f | Bandwidth: %.1f",
                totalCost, totalLatency, avgBandwidth));
    }
}