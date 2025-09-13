package com.example.alarmed.data.db.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.alarmed.data.db.entity.HistoricoUso;
import com.example.alarmed.data.db.relacionamentos.HistoricoComMedicamento;

import java.util.List;

@Dao
public interface HistoricoUsoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHistorico(HistoricoUso historico);

    @Query("SELECT * FROM historico_uso WHERE id_medicamento = :medicamentoId ORDER BY data_hora DESC")
    LiveData<List<HistoricoUso>> getHistoricoParaMedicamento(int medicamentoId);

    @Transaction
    @Query("SELECT * FROM historico_uso ORDER BY data_hora DESC")
    LiveData<List<HistoricoComMedicamento>> getAllHistoricoComMedicamento();

    @Query("DELETE FROM historico_uso WHERE id = :id")
    void deleteHistoricoById(int id);
}
