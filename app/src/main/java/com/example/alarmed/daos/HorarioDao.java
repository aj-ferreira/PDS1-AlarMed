package com.example.alarmed.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.alarmed.model.Horario;

import java.util.List;
@Dao
public interface HorarioDao {
    @Insert
    void inserir(Horario horario);

    @Update
    void atualizar(Horario horario);

    @Delete
    void deletar(Horario horario);

    @Query("SELECT * FROM horario")
    List<Horario> listarTodos();

    @Query("SELECT * FROM horario WHERE id_medicamento = :idMedicamento")
    List<Horario> listarPorMedicamento(int idMedicamento);
}
