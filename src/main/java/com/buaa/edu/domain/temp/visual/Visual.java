package com.buaa.edu.domain.temp.visual;

import com.alibaba.fastjson.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Visual {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    private ArrayList<Node> oldNodes;
    private ArrayList<Node> newNodes;

    private int edgeX;
    private int edgeY;

    private double xGap;
    private double yGap;

    private String name;

    private HashMap<Integer, ArrayList<Node>> integerArrayListHashMap;

    public Visual(int edgeX, int edgeY, int xLevel, int yLine, String name) {
        this.edgeX = edgeX;
        this.edgeY = edgeY;
        DecimalFormat df = new DecimalFormat("0.00");
        this.xGap = Double.parseDouble(df.format((double)this.edgeX / xLevel));
        nodes = new ArrayList<Node>();
        edges = new ArrayList<Edge>();
        oldNodes = new ArrayList<Node>();
        newNodes = new ArrayList<Node>();
        this.name = name;
        integerArrayListHashMap = new HashMap<Integer, ArrayList<Node>>();
    }

    public void addNode(Node node) {
        if (this.nodes.contains(node)) {
            return;
        }
        this.nodes.add(node);
        if (node.isInit()) {
            oldNodes.add(node);
        }
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }

    public JSONObject generateJsonObjectOfVisual() {
        JSONObject jsonObject = new JSONObject();

        HashMap<String, Object> tmpMap = new HashMap<>();
        tmpMap.put("text", "Graph of OMSRoute");
        jsonObject.put("title", tmpMap);
        jsonObject.put("animation", false);

        HashMap<String, Object> series_map = new HashMap<>();

        ArrayList<Object> series_list = new ArrayList<>();

        series_map.put("name", this.name);
        series_map.put("type", "graph");
        series_map.put("layout", "none");
        series_map.put("roam", true);
        HashMap<String, Integer> force_map = new HashMap<>();
        force_map.put("repulsion", 100);
        //series_map.put("force", new JSONObject(force_map));
        series_map.put("force", force_map);

        ArrayList<String> tmp_list = new ArrayList<>();
        tmp_list.add("circle");
        tmp_list.add("arrow");
        series_map.put("edgeSymbol", tmp_list);

        ArrayList<Object> nodeJson = new ArrayList<>();
        ArrayList<Object> edgeJson = new ArrayList<>();
        HashSet<Node> tmpNodes = new HashSet<>(nodes);
        int level = 0;
        // Count numbers and orders;

//        while (true) {
//            int i = 0;
//            newNodes.clear();
//            int k = 0;
//            for (Node node: oldNodes) {
//                if (node.getLevel() != level) {
//                    k++;
//                }
//            }
//
//            for (Node node: oldNodes) {
//                Map<String, Node> nodes = node.getNodeHashMap();
//                for (String str: nodes.keySet()) {
//                    Node tmpNode = nodes.get(str);
//                    if (!newNodes.contains(tmpNode) && tmpNodes.contains(tmpNode)) {
//                        newNodes.add(tmpNode);
//                    }
//
//                }
//
//                tmpNodes.remove(node);
//                i++;
//            }
//            if (newNodes.isEmpty()) {
//                break;
//            }
//            oldNodes.clear();
//            oldNodes.addAll(newNodes);
//            newNodes.clear();
//            level++;
//        }
        level = 0;
        while (true) {
            int i = 1;
            newNodes.clear();
            DecimalFormat df = new DecimalFormat("0.00");
            int k = 0;
            for (Node node: oldNodes) {
                if (node.getLevel() != level) {
                    k++;
                }
            }
            this.yGap = Double.parseDouble(df.format((double)this.edgeY / (oldNodes.size() + 1)));
            System.out.println("level: " + level + "k: " + k);
            for (Node node: oldNodes) {
                if (node.getLevel() > level) {
                    if (tmpNodes.contains(node)) {
                        newNodes.add(node);
                        i++;
                    }
                    continue;
                } else if (node.getLevel() < level) {
                    continue;
                }
                if (!tmpNodes.contains(node)) {
                    continue;
                }
                HashMap<String, Object> map = new HashMap<>();
                double placeX = node.getLevel() * xGap;
                double placeY = i * yGap;
                map.put("name", node.getLabel());
                map.put("x", placeX);
                map.put("y", placeY);
                //
                HashMap<String, Object> stringObjectHashMap = new HashMap<>();

                int alertType = node.getAlertType();
                if (alertType == 2) {
                    stringObjectHashMap.put("color", "rgb(205,38,38)");
                } else if (alertType == 1) {
                    stringObjectHashMap.put("color", "rgb(255,193,37)");
                } else {
                    stringObjectHashMap.put("color", "rgb(0,250,154)");
                }

                map.put("itemStyle", stringObjectHashMap);

                Map<String, Node> nodes = node.getNodeHashMap();
                for (String str: nodes.keySet()) {
                    Node tmpNode = nodes.get(str);
                    if (!newNodes.contains(tmpNode) && tmpNodes.contains(tmpNode)) {
                        newNodes.add(tmpNode);
                    }

                }
                nodeJson.add(map);
                tmpNodes.remove(node);
                i++;
            }
            if (newNodes.isEmpty()) {
                break;
            }
            oldNodes.clear();
            oldNodes.addAll(newNodes);
            newNodes.clear();
            level++;
        }

        series_map.put("data", nodeJson);
        //series_map.put("data", new JSONObject(node_list));
        //series_map.put("links", new JSONObject(link_list));

        for (Edge edge: edges) {
            HashMap<String, String> map = new HashMap<>();
            map.put("source", edge.aboveLabel);
            map.put("target", edge.nextLabel);
            //link_list.add(new JSONObject(map));
            edgeJson.add(map);
        }
        series_map.put("links", edgeJson);

        series_list.add(series_map);
        jsonObject.put("series", series_list);

        return jsonObject;
    }
}
