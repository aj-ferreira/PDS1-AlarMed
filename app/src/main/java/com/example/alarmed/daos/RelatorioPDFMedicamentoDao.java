package com.example.alarmed.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.alarmed.model.RelatorioPDFMedicamento;

import java.util.List;

@Dao
public interface RelatorioPDFMedicamentoDao {
    @Insert
    void inserir(RelatorioPDFMedicamento relacao);
    
    @Delete
    void deletar(RelatorioPDFMedicamento relacao);

    @Query("SELECT * FROM relatorio_pdf_medicamento WHERE id_pdf = :idPdf")
    List<RelatorioPDFMedicamento> listarPorRelatorio(int idPdf);

    @Query("SELECT * FROM relatorio_pdf_medicamento WHERE id_medicamento = :idMedicamento")
    List<RelatorioPDFMedicamento> listarPorMedicamento(int idMedicamento);
}

