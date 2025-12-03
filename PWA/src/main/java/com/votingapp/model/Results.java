package com.votingapp.model;

public class Results {
    // Pregunta de la votación
    private String question;
    // Total de votos
    private int totalVotes;
    // Votos para América
    private int americaVotes;
    // Votos para Chivas
    private int chivasVotes;
    // Porcentaje para América
    private double americaPercentage;
    // Porcentaje para Chivas
    private double chivasPercentage;
    // Última actualizaciónSS
    private String lastUpdate;

    // Getters y Setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public int getTotalVotes() { return totalVotes; }
    public void setTotalVotes(int totalVotes) { this.totalVotes = totalVotes; }

    public int getAmericaVotes() { return americaVotes; }
    public void setAmericaVotes(int americaVotes) { this.americaVotes = americaVotes; }

    public int getChivasVotes() { return chivasVotes; }
    public void setChivasVotes(int chivasVotes) { this.chivasVotes = chivasVotes; }

    public double getAmericaPercentage() { return americaPercentage; }
    public void setAmericaPercentage(double americaPercentage) { this.americaPercentage = americaPercentage; }

    public double getChivasPercentage() { return chivasPercentage; }
    public void setChivasPercentage(double chivasPercentage) { this.chivasPercentage = chivasPercentage; }

    public String getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }
}