package com.votingapp.model;

import java.util.List;

public class SyncRequest {
    // ID del nodo que sincroniza
    private String nodeId;
    // Lista de votos a sincronizar
    private List<Vote> votes;

    // Getters y Setters
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public List<Vote> getVotes() { return votes; }
    public void setVotes(List<Vote> votes) { this.votes = votes; }
}