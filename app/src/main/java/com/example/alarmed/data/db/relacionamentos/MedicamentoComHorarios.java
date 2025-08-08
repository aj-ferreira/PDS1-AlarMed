package com.example.alarmed.data.db.relacionamentos;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.alarmed.data.db.entity.Horario;
import com.example.alarmed.data.db.entity.Medicamento;

import java.util.List;

public class MedicamentoComHorarios {
    @Embedded
    public Medicamento medicamento;

    @Relation(
            parentColumn = "id",
            entityColumn = "id_medicamento"
    )
    public List<Horario> horarios;
}
