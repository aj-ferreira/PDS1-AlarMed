package com.example.alarmed.data.db.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.alarmed.data.db.entity.Horario;
@Dao
public interface HorarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(Horario horario); //usado pra inserir ou atualizar

    // Agora buscamos apenas UMA regra de horário por medicamento
    @Query("SELECT * FROM horario WHERE id_medicamento = :medicamentoId")
    LiveData<Horario> getHorarioParaMedicamento(int medicamentoId);

    //@Query("SELECT * FROM horario WHERE id_medicamento = :medicamentoId LIMIT 1")
    //LiveData<Horario> getHorarioByMedicamentoId(int medicamentoId); //verificar se faz ou nao sentido o limit

    @Query("DELETE FROM horario WHERE id = :id")
    void deleteHorarioById(int id);

    // Método síncrono para ser chamado de uma background thread
    @Query("SELECT * FROM horario WHERE id_medicamento = :medicamentoId LIMIT 1")
    Horario getHorarioByMedicamentoId(int medicamentoId);
}
