package com.example.alarmed.data.db.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.alarmed.data.db.entity.RelatorioPDF;
import com.example.alarmed.data.db.entity.RelatorioPDFMedicamento;
import com.example.alarmed.data.db.relacionamentos.RelatorioComMedicamentos;

import java.util.List;

@Dao
public interface RelatorioPdfDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRelatorio(RelatorioPDF relatorio); // Retorna o ID do relatório inserido

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRelatorioMedicamentoCrossRef(RelatorioPDFMedicamento crossRef);

    @Transaction
    @Query("SELECT * FROM relatorio_pdf WHERE id = :relatorioId")
    LiveData<RelatorioComMedicamentos> getRelatorioComMedicamentos(int relatorioId);

    @Transaction
    @Query("SELECT * FROM relatorio_pdf ORDER BY data_criacao DESC")
    LiveData<List<RelatorioComMedicamentos>> getAllRelatoriosComMedicamentos();
}
