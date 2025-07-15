package com.example.alarmed.persistencia;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.alarmed.daos.HistoricoUsoDao;
import com.example.alarmed.daos.HorarioDao;
import com.example.alarmed.daos.MedicamentoDao;
import com.example.alarmed.daos.RelatorioPdfDao;

public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract MedicamentoDao medicamentoDao();
    public abstract HorarioDao horarioDao();
    public abstract HistoricoUsoDao historicoUsoDao();
    public abstract RelatorioPdfDao relatorioPDFDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "agenda_medicacao_db")
                    .build();
        }
        return INSTANCE;
    }
}
