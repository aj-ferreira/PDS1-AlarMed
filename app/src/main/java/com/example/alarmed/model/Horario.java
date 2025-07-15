package com.example.alarmed.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = Medicamento.class,
        parentColumns = "id",
        childColumns = "id_medicamento",
        onDelete = ForeignKey.CASCADE
))
public class Horario {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int id_medicamento;

    @NonNull
    public String horario; // Ex: "08:00"

    public String repetir_dias; // Ex: "SEG,TER,QUA,TODOS..."
}
