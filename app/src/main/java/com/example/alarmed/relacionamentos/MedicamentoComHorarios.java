package com.example.alarmed.relacionamentos;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.alarmed.model.Horario;
import com.example.alarmed.model.Medicamento;

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
