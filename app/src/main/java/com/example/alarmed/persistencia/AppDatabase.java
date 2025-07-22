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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        Medicamento.class,
        Horario.class,
        HistoricoUso.class,
        RelatorioPDF.class,
        RelatorioPDFMedicamento.class},
        version = 1)
public abstract class AppDatabase extends RoomDatabase {
    // Métodos abstratos para que o Room possa fornecer as implementações dos DAOs.
    public abstract MedicamentoDao medicamentoDao();
    public abstract HorarioDao horarioDao();
    public abstract HistoricoUsoDao historicoUsoDao();
    public abstract RelatorioPdfDao relatorioDao();

    // A palavra-chave 'volatile' garante que a instância seja sempre lida da memória principal.
    private static volatile AppDatabase INSTANCE;

    // Define um número fixo de threads para o executor do banco de dados.
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * Implementação do padrão Singleton para obter a instância do banco de dados.
     * Isso garante que apenas uma instância do banco de dados seja aberta por vez.
     *
     * @param context O contexto do aplicativo.
     * @return A instância singleton do AppDatabase.
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            // O bloco 'synchronized' garante que apenas uma thread possa executar este código por vez.
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "alarmed_database")
                            // Estratégias de migração seriam adicionadas aqui se necessário.
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
