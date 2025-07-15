package com.example.alarmed.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.alarmed.model.HistoricoUso;

import java.util.List;

@Dao
public interface HistoricoUsoDao {
    @Insert
    void inserir(HistoricoUso historico);

    @Update
    void atualizar(HistoricoUso historico);

    @Delete
    void deletar(HistoricoUso historico);

    @Query("SELECT * FROM historico_uso ORDER BY data_hora DESC")
    List<HistoricoUso> listarTodos();

    @Query("SELECT * FROM historico_uso WHERE id_medicamento = :idMedicamento ORDER BY data_hora DESC")
    List<HistoricoUso> listarPorMedicamento(int idMedicamento);
}
