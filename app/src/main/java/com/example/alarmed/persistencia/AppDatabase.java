package com.example.alarmed.persistencia;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.alarmed.daos.HistoricoUsoDao;
import com.example.alarmed.daos.HorarioDao;
import com.example.alarmed.daos.MedicamentoDao;
import com.example.alarmed.daos.RelatorioPdfDao;
import com.example.alarmed.model.HistoricoUso;
import com.example.alarmed.model.Horario;
import com.example.alarmed.model.Medicamento;
import com.example.alarmed.model.RelatorioPDF;
import com.example.alarmed.model.RelatorioPDFMedicamento;

@Database(entities = {Medicamento.class, Horario.class, HistoricoUso.class, RelatorioPDF.class, RelatorioPDFMedicamento.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract MedicamentoDao medicamentoDao();
    public abstract HorarioDao horarioDao();
    public abstract HistoricoUsoDao historicoUsoDao();
    public abstract RelatorioPdfDao relatorioPDFDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "alarmed_db")
                    .build();
        }
        return INSTANCE;
    }
}
