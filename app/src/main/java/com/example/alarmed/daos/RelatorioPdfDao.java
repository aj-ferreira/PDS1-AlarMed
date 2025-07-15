package com.example.alarmed.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.alarmed.model.RelatorioPDF;

import java.util.List;

@Dao
public interface RelatorioPdfDao {
    @Insert
    void inserir(RelatorioPDF relatorio);

    @Update
    void atualizar(RelatorioPDF relatorio);

    @Delete
    void deletar(RelatorioPDF relatorio);

    @Query("SELECT * FROM relatorio_pdf ORDER BY data_criacao DESC")
    List<RelatorioPDF> listarTodos();
}
