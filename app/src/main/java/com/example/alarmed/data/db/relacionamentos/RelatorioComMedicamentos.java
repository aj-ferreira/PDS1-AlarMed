package com.example.alarmed.data.db.relacionamentos;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.alarmed.data.db.entity.Medicamento;
import com.example.alarmed.data.db.entity.RelatorioPDF;
import com.example.alarmed.data.db.entity.RelatorioPDFMedicamento;

import java.util.List;

public class RelatorioComMedicamentos {
    @Embedded
    public RelatorioPDF relatorio;

    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = RelatorioPDFMedicamento.class,
                    parentColumn = "id_pdf",
                    entityColumn = "id_medicamento"
            )
    )
    public List<Medicamento> medicamentos;
}
