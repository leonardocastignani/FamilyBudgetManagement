package it.unicam.cs.mpgc.jbudget125667.service;

import it.unicam.cs.mpgc.jbudget125667.model.*;

import javafx.stage.*;
import javafx.stage.FileChooser.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.*;

import java.io.*;
import java.time.LocalDate;

public class DataTransferService {

    private final FamilyService familyService = new FamilyService();
    private final ObjectMapper objectMapper;

    public DataTransferService() {
        this.objectMapper = new ObjectMapper();
        // Configura l'ObjectMapper per scrivere le date in formato leggibile e per indentare l'output JSON
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Esporta i dati di una famiglia in un file JSON.
     * Chiede all'utente dove salvare il file.
     *
     * @param family La famiglia da esportare (con tutti i conti e movimenti caricati).
     */
    public void exportFamilyData(Family family) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Family Data");
        fileChooser.setInitialFileName(family.getUsername() + "-export-" + LocalDate.now() + ".json");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                this.objectMapper.writeValue(file, family);
                // Potresti mostrare un dialog di successo qui
            } catch (IOException e) {
                e.printStackTrace();
                // Potresti mostrare un dialog di errore qui
            }
        }
    }

    /**
     * Importa i dati di una famiglia da un file JSON.
     * Chiede all'utente quale file importare.
     *
     * @return La famiglia importata, o null se l'operazione fallisce o viene annullata.
     * @throws Exception se la famiglia esiste già nel database.
     */
    public Family importFamilyData() throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Family Data");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try {
                Family importedFamily = this.objectMapper.readValue(file, Family.class);

                // Controllo di sicurezza: non importare se una famiglia con lo stesso nome esiste già
                if (this.familyService.getFamilyByUsername(importedFamily.getUsername()).isPresent()) {
                    throw new Exception("Family '" + importedFamily.getUsername() + "' already exists in the database.");
                }

                // Salva la famiglia e tutti i suoi dati a cascata
                this.familyService.saveFamilyWithDetails(importedFamily);
                return importedFamily;

            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Error reading or parsing the JSON file.", e);
            }
        }
        return null; // L'utente ha annullato la selezione del file
    }
}