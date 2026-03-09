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
import com.example.alarmed.data.db.dao.PerfilUsuarioDao;
import com.example.alarmed.data.db.entity.HistoricoUso;
import com.example.alarmed.data.db.entity.Horario;
import com.example.alarmed.data.db.entity.Medicamento;
import com.example.alarmed.data.db.entity.RelatorioPDF;
import com.example.alarmed.data.db.entity.RelatorioPDFMedicamento;
import com.example.alarmed.data.db.entity.Usuario;
import com.example.alarmed.data.db.entity.PerfilUsuario;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        Medicamento.class,
        Horario.class,
        HistoricoUso.class,
        RelatorioPDF.class,
        RelatorioPDFMedicamento.class,
        Usuario.class,
        PerfilUsuario.class},
        version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    // Métodos abstratos para que o Room possa fornecer as implementações dos DAOs.
    public abstract MedicamentoDao medicamentoDao();
    public abstract HorarioDao horarioDao();
    public abstract HistoricoUsoDao historicoUsoDao();
    public abstract RelatorioPdfDao relatorioDao();
    public abstract UsuarioDao usuarioDao();
    public abstract PerfilUsuarioDao perfilUsuarioDao();

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

    // Migração da versão 3 para 4 - cria tabela perfil_usuario
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Cria a tabela perfil_usuario
            database.execSQL("CREATE TABLE IF NOT EXISTS `perfil_usuario` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`usuario_id` INTEGER NOT NULL, " +
                    "`data_nascimento` TEXT, " +
                    "`telefone` TEXT, " +
                    "`endereco` TEXT, " +
                    "`cidade` TEXT, " +
                    "`estado` TEXT, " +
                    "`cep` TEXT, " +
                    "`peso` REAL, " +
                    "`altura` REAL, " +
                    "`tipo_sanguineo` TEXT, " +
                    "`alergias` TEXT, " +
                    "`condicoes_medicas` TEXT, " +
                    "`medicamentos_continuos` TEXT, " +
                    "`contato_emergencia_nome` TEXT, " +
                    "`contato_emergencia_telefone` TEXT, " +
                    "`contato_emergencia_parentesco` TEXT, " +
                    "`nome_medico` TEXT, " +
                    "`telefone_medico` TEXT, " +
                    "`plano_saude` TEXT, " +
                    "`numero_carteirinha` TEXT, " +
                    "`observacoes` TEXT, " +
                    "`data_atualizacao` TEXT, " +
                    "FOREIGN KEY(`usuario_id`) REFERENCES `usuario`(`id`) ON DELETE CASCADE)");
            
            // Cria índice único para usuario_id
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_perfil_usuario_usuario_id` " +
                    "ON `perfil_usuario` (`usuario_id`)");
        }
    };

    // Migração da versão 5 para 6 - recria todas as tabelas com schema correto
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Drop na ordem correta (tabelas com FK primeiro)
            database.execSQL("DROP TABLE IF EXISTS `relatorio_pdf_medicamento`");
            database.execSQL("DROP TABLE IF EXISTS `historico_uso`");
            database.execSQL("DROP TABLE IF EXISTS `horario`");
            database.execSQL("DROP TABLE IF EXISTS `perfil_usuario`");
            database.execSQL("DROP TABLE IF EXISTS `relatorio_pdf`");
            database.execSQL("DROP TABLE IF EXISTS `usuario`");
            database.execSQL("DROP TABLE IF EXISTS `medicamento`");

            // Recria na ordem correta (tabelas referenciadas primeiro)
            database.execSQL("CREATE TABLE IF NOT EXISTS `medicamento` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`nome` TEXT, `descricao` TEXT, `imagem` TEXT, " +
                    "`estoque_atual` INTEGER NOT NULL DEFAULT 0, " +
                    "`estoque_minimo` INTEGER NOT NULL DEFAULT 0, " +
                    "`tipo` TEXT, `dose` TEXT)");

            database.execSQL("CREATE TABLE IF NOT EXISTS `usuario` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`nome` TEXT, `email` TEXT, `senha` TEXT, " +
                    "`tipo_perfil` TEXT, `data_criacao` TEXT, " +
                    "`ativo` INTEGER NOT NULL DEFAULT 1)");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_usuario_email` ON `usuario` (`email`)");
            database.execSQL("INSERT INTO `usuario` (nome, email, senha, tipo_perfil, data_criacao, ativo) " +
                    "VALUES ('Administrador', 'admin@alarmed.com', 'admin123', 'ADMIN', '" +
                    System.currentTimeMillis() + "', 1)");

            database.execSQL("CREATE TABLE IF NOT EXISTS `relatorio_pdf` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`data_criacao` TEXT, `caminho_arquivo` TEXT, `descricao` TEXT)");

            database.execSQL("CREATE TABLE IF NOT EXISTS `horario` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`id_medicamento` INTEGER NOT NULL, " +
                    "`horario_inicial` TEXT, " +
                    "`intervalo` INTEGER NOT NULL DEFAULT 0, " +
                    "`repetir_dias` TEXT, `data_fim` TEXT, " +
                    "FOREIGN KEY(`id_medicamento`) REFERENCES `medicamento`(`id`) ON DELETE CASCADE ON UPDATE CASCADE)");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_horario_id_medicamento` ON `horario` (`id_medicamento`)");

            database.execSQL("CREATE TABLE IF NOT EXISTS `historico_uso` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`id_medicamento` INTEGER NOT NULL, " +
                    "`data_hora` TEXT, `status` TEXT, `observacao` TEXT, " +
                    "FOREIGN KEY(`id_medicamento`) REFERENCES `medicamento`(`id`) ON DELETE CASCADE ON UPDATE CASCADE)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_historico_uso_id_medicamento` ON `historico_uso` (`id_medicamento`)");

            database.execSQL("CREATE TABLE IF NOT EXISTS `relatorio_pdf_medicamento` (" +
                    "`id_pdf` INTEGER NOT NULL, `id_medicamento` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`id_pdf`, `id_medicamento`), " +
                    "FOREIGN KEY(`id_pdf`) REFERENCES `relatorio_pdf`(`id`) ON DELETE CASCADE ON UPDATE CASCADE, " +
                    "FOREIGN KEY(`id_medicamento`) REFERENCES `medicamento`(`id`) ON DELETE CASCADE ON UPDATE CASCADE)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_relatorio_pdf_medicamento_id_pdf` ON `relatorio_pdf_medicamento` (`id_pdf`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_relatorio_pdf_medicamento_id_medicamento` ON `relatorio_pdf_medicamento` (`id_medicamento`)");

            database.execSQL("CREATE TABLE IF NOT EXISTS `perfil_usuario` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`data_nascimento` TEXT, `telefone` TEXT, `endereco` TEXT, " +
                    "`cidade` TEXT, `estado` TEXT, `cep` TEXT, " +
                    "`peso` REAL, `altura` REAL, `tipo_sanguineo` TEXT, " +
                    "`alergias` TEXT, `condicoes_medicas` TEXT, `medicamentos_continuos` TEXT, " +
                    "`contato_emergencia_nome` TEXT, `contato_emergencia_telefone` TEXT, " +
                    "`contato_emergencia_parentesco` TEXT, `nome_medico` TEXT, " +
                    "`telefone_medico` TEXT, `plano_saude` TEXT, `numero_carteirinha` TEXT, " +
                    "`observacoes` TEXT, `data_atualizacao` TEXT)");
        }
    };

    // Migração da versão 4 para 5 - remove foreign key e campo usuario_id
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Cria nova tabela sem foreign key
            database.execSQL("CREATE TABLE IF NOT EXISTS `perfil_usuario_new` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`data_nascimento` TEXT, " +
                    "`telefone` TEXT, " +
                    "`endereco` TEXT, " +
                    "`cidade` TEXT, " +
                    "`estado` TEXT, " +
                    "`cep` TEXT, " +
                    "`peso` REAL, " +
                    "`altura` REAL, " +
                    "`tipo_sanguineo` TEXT, " +
                    "`alergias` TEXT, " +
                    "`condicoes_medicas` TEXT, " +
                    "`medicamentos_continuos` TEXT, " +
                    "`contato_emergencia_nome` TEXT, " +
                    "`contato_emergencia_telefone` TEXT, " +
                    "`contato_emergencia_parentesco` TEXT, " +
                    "`nome_medico` TEXT, " +
                    "`telefone_medico` TEXT, " +
                    "`plano_saude` TEXT, " +
                    "`numero_carteirinha` TEXT, " +
                    "`observacoes` TEXT, " +
                    "`data_atualizacao` TEXT)");
            
            // Copia dados da tabela antiga (excluindo usuario_id)
            database.execSQL("INSERT INTO perfil_usuario_new (id, data_nascimento, telefone, endereco, " +
                    "cidade, estado, cep, peso, altura, tipo_sanguineo, alergias, condicoes_medicas, " +
                    "medicamentos_continuos, contato_emergencia_nome, contato_emergencia_telefone, " +
                    "contato_emergencia_parentesco, nome_medico, telefone_medico, plano_saude, " +
                    "numero_carteirinha, observacoes, data_atualizacao) " +
                    "SELECT id, data_nascimento, telefone, endereco, cidade, estado, cep, peso, altura, " +
                    "tipo_sanguineo, alergias, condicoes_medicas, medicamentos_continuos, " +
                    "contato_emergencia_nome, contato_emergencia_telefone, contato_emergencia_parentesco, " +
                    "nome_medico, telefone_medico, plano_saude, numero_carteirinha, observacoes, " +
                    "data_atualizacao FROM perfil_usuario");
            
            // Remove tabela antiga
            database.execSQL("DROP TABLE perfil_usuario");
            
            // Renomeia nova tabela
            database.execSQL("ALTER TABLE perfil_usuario_new RENAME TO perfil_usuario");
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
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
