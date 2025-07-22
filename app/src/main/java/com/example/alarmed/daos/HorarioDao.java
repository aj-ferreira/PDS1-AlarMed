package com.example.alarmed.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.alarmed.model.Horario;

import java.util.List;
@Dao
public interface HorarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHorario(Horario horario);

    @Query("SELECT * FROM horario WHERE id_medicamento = :medicamentoId ORDER BY horario ASC")
    LiveData<List<Horario>> getHorariosParaMedicamento(int medicamentoId);

    @Query("DELETE FROM horario WHERE id = :id")
    void deleteHorarioById(int id);
}
