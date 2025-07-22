package com.example.alarmed.repos;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.alarmed.daos.RelatorioPdfDao;
import com.example.alarmed.persistencia.AppDatabase;
import com.example.alarmed.model.Medicamento;
import com.example.alarmed.model.RelatorioPDF;
import com.example.alarmed.model.RelatorioPDFMedicamento;
import com.example.alarmed.relacionamentos.RelatorioComMedicamentos;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Repositório para gerenciar os dados de Medicamento, Horario e HistoricoUso.
 * Abstrai o acesso às fontes de dados (neste caso, os DAOs do Room).
 */
public class RelatorioRepository {
    private final RelatorioPdfDao relatorioDao;
    private final ExecutorService executor;

    public RelatorioRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.relatorioDao = db.relatorioDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<RelatorioComMedicamentos>> getAllRelatorios() {
        return relatorioDao.getAllRelatoriosComMedicamentos();
    }

    public LiveData<RelatorioComMedicamentos> getRelatorioComMedicamentos(int relatorioId) {
        return relatorioDao.getRelatorioComMedicamentos(relatorioId);
    }

    /**
     * Insere um novo relatório e suas associações com medicamentos.
     * Esta operação é mais complexa e pode exigir um callback para notificar sobre a conclusão.
     */
    public void insertRelatorioCompleto(RelatorioPDF relatorio, List<Medicamento> medicamentos) {
        executor.execute(() -> {
            // Insere o relatório e obtém seu novo ID
            long relatorioId = relatorioDao.insertRelatorio(relatorio);

            // Associa cada medicamento ao novo relatório
            for (Medicamento med : medicamentos) {
                RelatorioPDFMedicamento crossRef = new RelatorioPDFMedicamento();
                crossRef.idPdf = (int) relatorioId;
                crossRef.idMedicamento = med.id;
                relatorioDao.insertRelatorioMedicamentoCrossRef(crossRef);
            }
        });
    }
}
