package com.example.alarmed.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "historico_uso",
        foreignKeys = @ForeignKey(
        entity = Medicamento.class,
        parentColumns = "id",
        childColumns = "id_medicamento",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        indices = {@Index("id_medicamento")})
public class HistoricoUso {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "id_medicamento")
    public int id_medicamento;

    @ColumnInfo(name = "data_hora")
    public String data_hora; // Ex: "2025-06-22T08:00"

    @ColumnInfo(name = "status")
    public String status;    // "Tomado", "Ignorado", "Atrasado"
    @ColumnInfo(name = "observacao")
    public String observacao;
}
