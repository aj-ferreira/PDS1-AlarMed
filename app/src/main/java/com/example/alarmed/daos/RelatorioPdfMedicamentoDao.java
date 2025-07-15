package com.example.alarmed.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface RelatorioPdfMedicamentoDao {
    @Insert
    void inserir(RelatorioPdfMedicamento relacao);
    
    @Delete
    void deletar(RelatorioPdfMedicamento relacao);

    @Query("SELECT * FROM relatorio_pdf_medicamento WHERE id_pdf = :idPdf")
    List<RelatorioPdfMedicamento> listarPorRelatorio(int idPdf);

    @Query("SELECT * FROM relatorio_pdf_medicamento WHERE id_medicamento = :idMedicamento") 
    List<RelatorioPdfMedicamento> listarPorMedicamento(int idMedicamento);
}

