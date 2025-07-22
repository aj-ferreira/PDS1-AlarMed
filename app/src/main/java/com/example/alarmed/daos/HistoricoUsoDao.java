package com.example.alarmed.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.alarmed.model.HistoricoUso;

import java.util.List;

@Dao
public interface HistoricoUsoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHistorico(HistoricoUso historico);

    @Query("SELECT * FROM historico_uso WHERE id_medicamento = :medicamentoId ORDER BY data_hora DESC")
    LiveData<List<HistoricoUso>> getHistoricoParaMedicamento(int medicamentoId);

    @Query("DELETE FROM historico_uso WHERE id = :id")
    void deleteHistoricoById(int id);
}
