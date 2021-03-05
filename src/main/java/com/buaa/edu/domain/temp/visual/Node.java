package com.buaa.edu.domain.temp.visual;

import java.util.HashMap;

public class Node {
    private String label;

    private int level;
    private int line;

    private int alertType;

    private HashMap<String, Node> nodeHashMap;

    public Node(String label, int level, int line, int alertType) {
        this.label = label;
        this.level = level;
        this.line = line;
        nodeHashMap = new HashMap<>();
        this.alertType = alertType;
    }
    public boolean isInit() {
        return level == 0;
    }

    public String getLabel() {
        return this.label;
    }

    public void addNextNode(Node node) {
        String label = node.getLabel();
        if (nodeHashMap.containsKey(label)) {
            return;
        } else {
            nodeHashMap.put(label, node);
        }
    }

    public int getAlertType() {
        return alertType;
    }

    public int getLine() {
        return line;
    }

    public HashMap<String, Node> getNodeHashMap() {
        return nodeHashMap;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Node) {
            if (((Node) o).label.equals(this.label)) {
                return true;
            }
        }
        return false;
    }
}
