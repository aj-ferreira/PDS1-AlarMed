package com.example.alarmed.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
// A tabela agora guarda uma REGRA (início + intervalo), não vários horários.
@Entity(tableName = "horario",
        foreignKeys = @ForeignKey(
            entity = Medicamento.class,
            parentColumns = "id",
            childColumns = "id_medicamento",
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        indices = {@Index(value = "id_medicamento", unique = true)}) // Um medicamento só pode ter uma regra de horário
public class Horario {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "id_medicamento")
    public int id_medicamento;

    @ColumnInfo(name = "horario_inicial")
    public String horario_inicial;

    @ColumnInfo(name = "intervalo")
    public int intervalo; // Ex: "8" quer dizer que se toma o remedio a cada 8 horas

    @ColumnInfo(name = "repetir_dias")
    public String repetir_dias; // Ex: "SEG,TER,QUA,TODOS..."

    @ColumnInfo(name = "data_fim") // Formato "AAAA-MM-DD"
    public String dataFim = null;
}
