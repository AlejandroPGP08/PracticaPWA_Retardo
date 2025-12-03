package com.votingapp.controller;

import com.votingapp.model.Vote;
import com.votingapp.model.Results;
import com.votingapp.model.SyncRequest;
import com.votingapp.service.VotingService;
// Inyección de dependencias
import org.springframework.beans.factory.annotation.Autowired;
// Para respuestas HTTP
import org.springframework.http.ResponseEntity;
// Anotaciones para REST
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Marca esta clase como controlador REST
@RestController
// Prefijo para todos los endpoints
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Para permitir CORS desde el frontend
public class VotingController {
    // Inyecta el servicio automáticamente
    @Autowired
    private VotingService votingService;
    // Endpoint GET para obtener resultados
    @GetMapping("/results")
    public ResponseEntity<Results> getResults() {
        try {
            Results results = votingService.getResults();
            // 200 OK con resultados
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            // 500 Error
            return ResponseEntity.internalServerError().build();
        }
    }

    // Endpoint GET para votos locales
    @GetMapping("/votes/local")
    public ResponseEntity<List<Vote>> getLocalVotes() {
        try {
            List<Vote> votes = votingService.getLocalVotes();
            return ResponseEntity.ok(votes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Endpoint POST para enviar votos
    @PostMapping("/vote")
    // @RequestBody para recibir JSON
    public ResponseEntity<?> submitVote(@RequestBody Vote vote) {
        try {
            votingService.addVote(vote);
            // 200 OK
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            // 400 Bad Request
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    // Endpoint POST para sincronización
    @PostMapping("/sync")
    public ResponseEntity<?> syncVotes(@RequestBody SyncRequest syncRequest) {
        try {
            votingService.syncVotes(syncRequest.getVotes(), syncRequest.getNodeId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error en sincronización");
        }
    }

    // Endpoint POST para reiniciar votación
    @PostMapping("/reset")
    public ResponseEntity<?> resetPoll() {
        try {
            votingService.resetVotes();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al reiniciar votación");
        }
    }
}