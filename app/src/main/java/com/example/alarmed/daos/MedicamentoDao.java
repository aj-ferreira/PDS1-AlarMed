package com.example.alarmed.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.alarmed.model.HistoricoUso;
import com.example.alarmed.model.Horario;
import com.example.alarmed.model.Medicamento;
import com.example.alarmed.relacionamentos.MedicamentoComHistorico;
import com.example.alarmed.relacionamentos.MedicamentoComHorarios;

import java.util.List;

@Dao
public interface MedicamentoDao {
    // --- Operações básicas em Medicamento ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMedicamento(Medicamento medicamento);

    @Query("SELECT * FROM medicamento ORDER BY nome ASC")
    LiveData<List<Medicamento>> getAllMedicamentos();

    @Query("SELECT * FROM medicamento WHERE id = :id")
    LiveData<Medicamento> getMedicamentoById(int id);

    @Query("DELETE FROM medicamento WHERE id = :id")
    void deleteMedicamentoById(int id);

    // --- Consultas com Relações ---
    @Transaction
    @Query("SELECT * FROM medicamento WHERE id = :medicamentoId")
    LiveData<MedicamentoComHorarios> getMedicamentoComHorarios(int medicamentoId);

    @Transaction
    @Query("SELECT * FROM medicamento")
    LiveData<List<MedicamentoComHorarios>> getAllMedicamentosComHorarios();

    @Transaction
    @Query("SELECT * FROM medicamento WHERE id = :medicamentoId")
    LiveData<MedicamentoComHistorico> getMedicamentoComHistorico(int medicamentoId);
}
