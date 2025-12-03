package com.votingapp.model;

import java.util.ArrayList;
import java.util.List;

public class VotingData {
    // Lista de votos
    private List<Vote> votes = new ArrayList<>();
    // Pregunta por defecto
    private String question = "¿Quién es tu equipo favorito?";
    // Última sincronización
    private String lastSync;

    // Getters y Setters
    public List<Vote> getVotes() { return votes; }
    public void setVotes(List<Vote> votes) { this.votes = votes; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getLastSync() { return lastSync; }
    public void setLastSync(String lastSync) { this.lastSync = lastSync; }
}