class VotingApp {
    constructor() {
        // Equipo seleccionado actualmente
        this.selectedTeam = null;
        // ID único para este navegador/nodo
        this.nodeId = this.generateNodeId();
        // Otros nodos para sincronización
        this.otherNodes = ['http://localhost:8081', 'http://localhost:8082'];
        // URL base actual
        this.baseUrl = window.location.origin;
        // Inicializa la aplicación
        this.init();
    }

    generateNodeId() {
        // Genera ID aleatorio
        return 'node_' + Math.random().toString(36).substr(2, 9);
    }

    async init() {
        // Registra Service Worker para PWA
        await this.registerServiceWorker();
        // Verifica si el usuario ya votó
        await this.checkVotingStatus();
        // Actualiza estado de conexión
        this.updateConnectionStatus();
    }

    async registerServiceWorker() {
        // Verifica si el navegador soporta Service Workers
        if ('serviceWorker' in navigator) {
            try {
                // Registra el Service Worker
                await navigator.serviceWorker.register('/sw.js');
                console.log('Service Worker registrado');
            } catch (error) {
                console.log('Error registrando Service Worker:', error);
            }
        }
    }

    async checkVotingStatus() {
        try {
            // Obtiene votos locales
            const response = await fetch('/api/votes/local');
            const votes = await response.json();
            // Verifica si este nodeId ya votó
            const hasVoted = votes.some(vote => vote.nodeId === this.nodeId);
            
            if (hasVoted) {
                // Muestra mensaje de ya votado
                this.showAlreadyVoted();
            } else {
                // Muestra formulario de votación
                this.showVoteForm();
            }
        } catch (error) {
            console.error('Error verificando estado de voto:', error);
            // En caso de error, muestra formulario
            this.showVoteForm();
        }
    }

    showVoteForm() {
        // Muestra formulario
        document.getElementById('voteForm').style.display = 'block';
        // Oculta mensaje de ya votado
        document.getElementById('alreadyVoted').style.display = 'none';
    }

    showAlreadyVoted() {
        // Oculta formulario
        document.getElementById('voteForm').style.display = 'none';
        // Muestra mensaje de ya votado
        document.getElementById('alreadyVoted').style.display = 'block';
    }

    selectTeam(team) {
        // Guarda equipo seleccionado
        this.selectedTeam = team;
        
        // Remover selección anterior
        document.querySelectorAll('.team-card').forEach(card => {
            card.classList.remove('selected');
        });
        
        // Agregar selección actual
        document.querySelectorAll('.team-card').forEach(card => {
            if (card.querySelector('h3').textContent.toLowerCase().includes(team)) {
                // Aplica estilo de selección
                card.classList.add('selected');
            }
        });
        
        // Habilitar botón de votar
        document.getElementById('voteButton').disabled = false;
    }

    async submitVote() {
        if (!this.selectedTeam) {
            alert('Por favor selecciona un equipo');
            return;
        }

        try {
            const voteData = {
                team: this.selectedTeam,
                nodeId: this.nodeId
            };

            const response = await fetch('/api/vote', {
                method: 'POST',
                headers: {
                    // Especifica que envía JSON
                    'Content-Type': 'application/json'
                },
                // Convierte objeto a JSON
                body: JSON.stringify(voteData)
            });

            if (response.ok) {
                alert(`¡Has votado por ${this.selectedTeam === 'america' ? 'América' : 'Chivas'}!`);
                this.showAlreadyVoted();
                // Actualiza resultados
                this.loadResults();
            } else {
                const error = await response.text();
                alert('Error: ' + error);
            }
        } catch (error) {
            console.error('Error enviando voto:', error);
            alert('Error al enviar el voto. Intentando guardar localmente...');
            // Guarda voto offline si hay error
            this.saveVoteOffline();
        }
    }

    saveVoteOffline() {
        const voteData = {
            team: this.selectedTeam,
            nodeId: this.nodeId,
            // Timestamp actual
            timestamp: new Date().toISOString()
        };

        // Obtiene votos offline existentes
        const offlineVotes = JSON.parse(localStorage.getItem('offline_votes') || '[]');
        // Agrega nuevo voto
        offlineVotes.push(voteData);
        // Guarda en localStorage
        localStorage.setItem('offline_votes', JSON.stringify(offlineVotes));
        
        alert('Voto guardado localmente. Se sincronizará cuando haya conexión.');
        this.showAlreadyVoted();
    }

    async loadResults() {
        try {
            // Obtiene resultados del servidor
            const response = await fetch('/api/results');
            const results = await response.json();
            // Muestra resultados en UI
            this.displayResults(results);
        } catch (error) {
            console.error('Error cargando resultados:', error);
        }
    }

    displayResults(results) {
        const container = document.getElementById('resultsContainer');
        
        container.innerHTML = `
            <h3>${results.question}</h3>
            <div class="total-votes">
                Total de votos: ${results.totalVotes}
            </div>
            
            <div class="team-result america">
                <div class="team-info">
                    <div class="team-mini-logo america">A</div>
                    <div>
                        <strong>América</strong><br>
                        Águilas
                    </div>
                </div>
                <div class="bar-container">
                    <div class="bar-fill america" style="width: ${results.americaPercentage}%">
                        ${results.americaPercentage.toFixed(1)}%
                    </div>
                </div>
                <div class="vote-count">${results.americaVotes} votos</div>
            </div>
            
            <div class="team-result chivas">
                <div class="team-info">
                    <div class="team-mini-logo chivas">C</div>
                    <div>
                        <strong>Chivas</strong><br>
                        Rebaño Sagrado
                    </div>
                </div>
                <div class="bar-container">
                    <div class="bar-fill chivas" style="width: ${results.chivasPercentage}%">
                        ${results.chivasPercentage.toFixed(1)}%
                    </div>
                </div>
                <div class="vote-count">${results.chivasVotes} votos</div>
            </div>
        `;
    }

    async syncWithOtherNodes() {
        try {
            for (const nodeUrl of this.otherNodes) {
                if (nodeUrl !== this.baseUrl) {
                    // Sincroniza con cada nodo
                    await this.syncWithNode(nodeUrl);
                }
            }
            //alert('Sincronización completada');
            // Actualiza resultados después de sincronizar
            this.loadResults();
        } catch (error) {
            console.error('Error en sincronización:', error);
            alert('Error en sincronización');
        }
    }

    async syncWithNode(nodeUrl) {
        try {
            // Obtener votos locales
            const localVotes = await this.getLocalVotes();
            
            // Enviar nuestros votos al nodo remoto
            await fetch(`${nodeUrl}/api/sync`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    nodeId: this.nodeId,
                    votes: localVotes
                })
            });
            
            console.log(`Sincronizado con ${nodeUrl}`);
        } catch (error) {
            console.error(`Error sincronizando con ${nodeUrl}:`, error);
        }
    }

    async getLocalVotes() {
        const response = await fetch('/api/votes/local');
        // Retorna promesa con los votos
        return await response.json();
    }

    updateConnectionStatus() {
        const statusElement = document.getElementById('connectionStatus');
        statusElement.textContent = `Estado: Conectado (${this.nodeId})`;
        // Aplica estilo de conectado
        statusElement.className = 'status online';
    }

    async syncData() {
        // Sincroniza votos offline primero
        await this.syncOfflineVotes();
        // Luego sincroniza con otros nodos
        this.syncWithOtherNodes().catch(console.error);
    }

    async syncOfflineVotes() {
        const offlineVotes = JSON.parse(localStorage.getItem('offline_votes') || '[]');
        
        if (offlineVotes.length > 0) {
            for (const voteData of offlineVotes) {
                try {
                    await fetch('/api/vote', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(voteData)
                    });
                } catch (error) {
                    console.error('Error sincronizando voto offline:', error);
                    // Si hay error, detiene el proceso
                    break;
                }
            }
            
            // Limpiar votos offline sincronizados
            localStorage.removeItem('offline_votes');
        }
    }

    async resetPoll() {
        if (confirm('¿Estás seguro de que quieres reiniciar la votación? Se perderán todos los votos.')) {
            try {
                await fetch('/api/reset', { method: 'POST' });
                alert('Votación reiniciada');
                // Actualiza resultados
                this.loadResults();
                // Verifica estado de votación
                this.checkVotingStatus();
            } catch (error) {
                console.error('Error reiniciando votación:', error);
                alert('Error al reiniciar la votación');
            }
        }
    }
}

// Funciones globales
let votingApp;

function openTab(tabName) {
    // Oculta todos los tabs
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelectorAll('.tab-button').forEach(button => {
        button.classList.remove('active');
    });
    
    // Muestra el tab seleccionado
    document.getElementById(tabName).classList.add('active');
    event.currentTarget.classList.add('active');

    if (tabName === 'results') {
        // Carga resultados si es el tab de resultados
        votingApp.loadResults();
    }
}

function selectTeam(team) {
    // Delega a la clase VotingApp
    votingApp.selectTeam(team);
}

function submitVote() {
    // Delega a la clase VotingApp
    votingApp.submitVote();
}

function syncWithOtherNodes() {
    // Delega a la clase VotingApp
    votingApp.syncWithOtherNodes();
}

function resetPoll() {
    // Delega a la clase VotingApp
    votingApp.resetPoll();
}

// Inicializar la aplicación
document.addEventListener('DOMContentLoaded', () => {
    // Crea instancia de la aplicación
    votingApp = new VotingApp();
    
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker.register('/sw.js')
            .then(registration => console.log('SW registered'))
            .catch(error => console.log('SW registration failed'));
    }
});