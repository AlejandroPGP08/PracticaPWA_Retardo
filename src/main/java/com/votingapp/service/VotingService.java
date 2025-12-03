package com.votingapp.service;

// Para serializar/deserializar JSON
import com.fasterxml.jackson.databind.ObjectMapper;
import com.votingapp.model.Vote;
import com.votingapp.model.VotingData;
import com.votingapp.model.Results;
// Marca esta clase como servicio Spring
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VotingService {
    // Archivo JSON como "base de datos"
    private static final String DB_FILE = "votes.json";
    // Mapeador JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VotingService() {
        // Constructor que inicializa la base de datos
        initializeDB();
    }

    private void initializeDB() {
        File file = new File(DB_FILE);
        // Si el archivo no existe, lo crea
        if (!file.exists()) {
            VotingData initialData = new VotingData();
            initialData.setLastSync(LocalDateTime.now().toString());
            saveVotingData(initialData);
        }
    }

    private VotingData readVotingData() {
        try {
            // Lee datos del JSON
            return objectMapper.readValue(new File(DB_FILE), VotingData.class);
        } catch (IOException e) {
            // Si hay error, crea datos vacíos
            VotingData data = new VotingData();
            data.setLastSync(LocalDateTime.now().toString());
            return data;
        }
    }

    private void saveVotingData(VotingData data) {
        try {
            // Actualiza timestamp
            data.setLastSync(LocalDateTime.now().toString());
            // Guarda con formato bonito
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(DB_FILE), data);
        } catch (IOException e) {
            throw new RuntimeException("Error guardando datos", e);
        }
    }

    public Results getResults() {
        VotingData data = readVotingData();
        List<Vote> votes = data.getVotes();
        // Cuenta votos para cada equipo usando streams
        int americaVotes = (int) votes.stream().filter(v -> "america".equals(v.getTeam())).count();
        int chivasVotes = (int) votes.stream().filter(v -> "chivas".equals(v.getTeam())).count();
        int totalVotes = votes.size();
        // Calcula porcentajes
        double americaPercentage = totalVotes > 0 ? (americaVotes * 100.0) / totalVotes : 0;
        double chivasPercentage = totalVotes > 0 ? (chivasVotes * 100.0) / totalVotes : 0;
        // Construye objeto de resultados
        Results results = new Results();
        results.setQuestion(data.getQuestion());
        results.setTotalVotes(totalVotes);
        results.setAmericaVotes(americaVotes);
        results.setChivasVotes(chivasVotes);
        results.setAmericaPercentage(americaPercentage);
        results.setChivasPercentage(chivasPercentage);
        results.setLastUpdate(data.getLastSync());
        
        return results;
    }

    public List<Vote> getLocalVotes() {
        // Retorna todos los votos locales
        return readVotingData().getVotes();
    }

    public synchronized void addVote(Vote vote) {
        // synchronized para evitar condiciones de carrera
        VotingData data = readVotingData();
        
        // Verificar si ya votó este nodeId
        boolean alreadyVoted = data.getVotes().stream()
                .anyMatch(v -> v.getNodeId().equals(vote.getNodeId()));
        
        if (!alreadyVoted) {
            // Agrega el voto si no ha votado
            data.getVotes().add(vote);
            saveVotingData(data);
        } else {
            // Error si ya votó
            throw new RuntimeException("Este nodo ya ha votado");
        }
    }

    public synchronized void syncVotes(List<Vote> externalVotes, String nodeId) {
        VotingData data = readVotingData();
        List<Vote> currentVotes = data.getVotes();
        // Sincroniza votos externos que no existan localmente
        for (Vote externalVote : externalVotes) {
            // Solo agregar votos que no existan
            boolean voteExists = currentVotes.stream()
                    .anyMatch(v -> v.getNodeId().equals(externalVote.getNodeId()));
            
            if (!voteExists) {
                // Agrega voto si no existe
                currentVotes.add(externalVote);
            }
        }
        
        saveVotingData(data);
    }

    public synchronized void resetVotes() {
        // Crea nueva instancia vacía
        VotingData data = new VotingData();
        data.setLastSync(LocalDateTime.now().toString());
        // Guarda sobreescribiendo el archivo
        saveVotingData(data);
    }
}