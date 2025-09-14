package com.example.alarmed.data.db.relacionamentos;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.alarmed.data.db.entity.HistoricoUso;
import com.example.alarmed.data.db.entity.Medicamento;

public class HistoricoComMedicamento {
    @Embedded
    public HistoricoUso historico;

    @Relation(
            parentColumn = "id_medicamento",
            entityColumn = "id"
    )
    public Medicamento medicamento;
}
