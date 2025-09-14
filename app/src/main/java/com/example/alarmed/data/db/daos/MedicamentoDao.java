package com.example.alarmed.data.db.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.alarmed.data.db.entity.Medicamento;
import com.example.alarmed.data.db.relacionamentos.MedicamentoComHistorico;
import com.example.alarmed.data.db.relacionamentos.MedicamentoComHorarios;

import java.util.List;

@Dao
public interface MedicamentoDao {
    // --- Operações básicas em Medicamento ---

    /**
     * Insere ou atualiza um medicamento.
     * Se o medicamento já existir (mesmo ID), ele será substituído.
     * Se for novo (ID 0), será inserido.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(Medicamento medicamento);
    
    @Insert
    long insert(Medicamento medicamento);
    
    @Update
    void update(Medicamento medicamento);

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

    // Método síncrono para ser chamado de uma background thread
    @Query("SELECT * FROM medicamento WHERE id = :id")
    Medicamento getMedicamentoByIdSync(int id);
}
