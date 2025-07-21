package com.example.alarmed.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "horario",
        foreignKeys = @ForeignKey(
        entity = Medicamento.class,
        parentColumns = "id",
        childColumns = "id_medicamento",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        indices = {@Index("id_medicamento")})
public class Horario {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "id_medicamento")
    public int id_medicamento;

    @NonNull
    @ColumnInfo(name = "horario")
    public String horario; // Ex: "08:00" quer dizer que se toma o remedio a cada 8 horas

    @ColumnInfo(name = "repetir_dias")
    public String repetir_dias; // Ex: "SEG,TER,QUA,TODOS..."
}
