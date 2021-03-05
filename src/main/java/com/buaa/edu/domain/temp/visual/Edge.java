package com.buaa.edu.domain.temp.visual;

public class Edge {
    public String aboveLabel;

    public String nextLabel;

    public int lineType;

    public Edge(String aboveLabel, String nextLabel, int lineType) {
        this.aboveLabel = aboveLabel;
        this.nextLabel = nextLabel;
        this.lineType = lineType;
    }
}
