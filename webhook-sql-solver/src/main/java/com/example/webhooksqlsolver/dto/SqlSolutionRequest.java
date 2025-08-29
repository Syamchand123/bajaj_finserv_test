package com.example.webhooksqlsolver.dto;

public class SqlSolutionRequest {
    private String finalQuery;

    public SqlSolutionRequest() {}

    public SqlSolutionRequest(String finalQuery) {
        this.finalQuery = finalQuery;
    }

    // Getters and Setters
    public String getFinalQuery() { return finalQuery; }
    public void setFinalQuery(String finalQuery) { this.finalQuery = finalQuery; }
}