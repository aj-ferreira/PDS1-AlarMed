package com.example.alarmed.relacionamentos;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.alarmed.model.HistoricoUso;
import com.example.alarmed.model.Medicamento;

import java.util.List;

public class MedicamentoComHistorico {
    @Embedded
    public Medicamento medicamento;

    @Relation(
            parentColumn = "id",
            entityColumn = "id_medicamento"
    )
    public List<HistoricoUso> historico;
}
