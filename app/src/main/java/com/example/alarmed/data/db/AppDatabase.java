package com.example.alarmed.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.alarmed.data.db.daos.HistoricoUsoDao;
import com.example.alarmed.data.db.daos.HorarioDao;
import com.example.alarmed.data.db.daos.MedicamentoDao;
import com.example.alarmed.data.db.daos.RelatorioPdfDao;
import com.example.alarmed.data.db.daos.UsuarioDao;
import com.example.alarmed.data.db.entity.HistoricoUso;
import com.example.alarmed.data.db.entity.Horario;
import com.example.alarmed.data.db.entity.Medicamento;
import com.example.alarmed.data.db.entity.RelatorioPDF;
import com.example.alarmed.data.db.entity.RelatorioPDFMedicamento;
import com.example.alarmed.data.db.entity.Usuario;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        Medicamento.class,
        Horario.class,
        HistoricoUso.class,
        RelatorioPDF.class,
        RelatorioPDFMedicamento.class,
        Usuario.class},
        version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    // Métodos abstratos para que o Room possa fornecer as implementações dos DAOs.
    public abstract MedicamentoDao medicamentoDao();
    public abstract HorarioDao horarioDao();
    public abstract HistoricoUsoDao historicoUsoDao();
    public abstract RelatorioPdfDao relatorioDao();
    public abstract UsuarioDao usuarioDao();

    // A palavra-chave 'volatile' garante que a instância seja sempre lida da memória principal.
    private static volatile AppDatabase INSTANCE;

    // Define um número fixo de threads para o executor do banco de dados.
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Migração da versão 1 para 2 - adiciona campo dose
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE medicamento ADD COLUMN dose TEXT");
        }
    };

    // Migração da versão 2 para 3 - cria tabela usuario
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Cria a tabela usuario
            database.execSQL("CREATE TABLE IF NOT EXISTS `usuario` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`nome` TEXT, " +
                    "`email` TEXT, " +
                    "`senha` TEXT, " +
                    "`tipo_perfil` TEXT, " +
                    "`data_criacao` TEXT NOT NULL, " +
                    "`ativo` INTEGER NOT NULL)");
            
            // Cria índice único para email
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_usuario_email` ON `usuario` (`email`)");
            
            // Insere usuário admin padrão
            database.execSQL("INSERT INTO usuario (nome, email, senha, tipo_perfil, data_criacao, ativo) " +
                    "VALUES ('Administrador', 'admin@alarmed.com', 'admin123', 'ADMIN', '" + 
                    System.currentTimeMillis() + "', 1)");
        }
    };

    // Migração da versão 3 para 4 - update tabela usuario
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Cria a tabela usuario
            database.execSQL("CREATE TABLE IF NOT EXISTS `usuario` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`nome` TEXT, " +
                    "`email` TEXT, " +
                    "`senha` TEXT, " +
                    "`tipo_perfil` TEXT, " +
                    "`data_criacao` TEXT NOT NULL, " +
                    "`ativo` INTEGER NOT NULL)");

            // Cria índice único para email
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_usuario_email` ON `usuario` (`email`)");

            // Insere usuário admin padrão
            database.execSQL("INSERT INTO usuario (nome, email, senha, tipo_perfil, data_criacao, ativo) " +
                    "VALUES ('Administrador', 'admin@alarmed.com', 'admin123', 'ADMIN', '" +
                    System.currentTimeMillis() + "', 1)");
        }
    };

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
                            // Adiciona as migrações
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
