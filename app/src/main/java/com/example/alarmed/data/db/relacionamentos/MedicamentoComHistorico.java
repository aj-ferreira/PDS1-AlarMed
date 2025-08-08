package com.example.alarmed.data.db.relacionamentos;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.alarmed.data.db.entity.HistoricoUso;
import com.example.alarmed.data.db.entity.Medicamento;

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
