package com.example.alarmed.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.alarmed.model.Medicamento;

import java.util.List;

@Dao
public interface MedicamentoDao {
    @Insert
    void inserir(Medicamento m);

    @Update
    void atualizar(Medicamento m);

    @Delete
    void deletar(Medicamento m);

    @Query("SELECT * FROM Medicamento")
    List<Medicamento> listarTodos();
}
