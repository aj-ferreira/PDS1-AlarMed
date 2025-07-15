package com.example.alarmed.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = Medicamento.class,
        parentColumns = "id",
        childColumns = "id_medicamento",
        onDelete = ForeignKey.CASCADE
))
public class HistoricoUso {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int id_medicamento;

    public String data_hora; // Ex: "2025-06-22T08:00"
    public String status;    // "Tomado", "Ignorado", "Atrasado"
    public String observacao;
}
