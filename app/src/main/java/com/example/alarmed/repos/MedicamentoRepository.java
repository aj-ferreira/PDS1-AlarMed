package com.example.alarmed.repos;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.alarmed.daos.HistoricoUsoDao;
import com.example.alarmed.daos.HorarioDao;
import com.example.alarmed.daos.MedicamentoDao;
import com.example.alarmed.model.HistoricoUso;
import com.example.alarmed.model.Horario;
import com.example.alarmed.model.Medicamento;
import com.example.alarmed.persistencia.AppDatabase;
import com.example.alarmed.relacionamentos.MedicamentoComHorarios;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Repositório para gerenciar os dados de Medicamento, Horario e HistoricoUso.
 * Abstrai o acesso às fontes de dados (neste caso, os DAOs do Room).
 */
public class MedicamentoRepository {
    private final MedicamentoDao medicamentoDao;
    private final HorarioDao horarioDao;
    private final HistoricoUsoDao historicoUsoDao;
    private final ExecutorService executor;

    public MedicamentoRepository(Application application) {
        // Em uma implementação real, você obteria a instância do banco de dados aqui
        AppDatabase db = AppDatabase.getDatabase(application);
        this.medicamentoDao = db.medicamentoDao();
        this.horarioDao = db.horarioDao();
        this.historicoUsoDao = db.historicoUsoDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    // --- Operações para Medicamento ---

    public LiveData<List<Medicamento>> getAllMedicamentos() {
        return medicamentoDao.getAllMedicamentos();
    }

    public LiveData<Medicamento> getMedicamentoById(int id) {
        return medicamentoDao.getMedicamentoById(id);
    }

    public void insertMedicamento(Medicamento medicamento) {
        executor.execute(() -> medicamentoDao.insertMedicamento(medicamento));
    }

    public void deleteMedicamentoById(int id) {
        executor.execute(() -> medicamentoDao.deleteMedicamentoById(id));
    }

    // --- Operações para Horario ---

    public LiveData<List<Horario>> getHorariosParaMedicamento(int medicamentoId) {
        return horarioDao.getHorariosParaMedicamento(medicamentoId);
    }

    public void insertHorario(Horario horario) {
        executor.execute(() -> horarioDao.insertHorario(horario));
    }

    public void deleteHorarioById(int id) {
        executor.execute(() -> horarioDao.deleteHorarioById(id));
    }

    // --- Operações para HistoricoUso ---

    public LiveData<List<HistoricoUso>> getHistoricoParaMedicamento(int medicamentoId) {
        return historicoUsoDao.getHistoricoParaMedicamento(medicamentoId);
    }

    public void insertHistorico(HistoricoUso historico) {
        executor.execute(() -> historicoUsoDao.insertHistorico(historico));
    }

    // --- Operações com Relações ---

    public LiveData<MedicamentoComHorarios> getMedicamentoComHorarios(int medicamentoId) {
        return medicamentoDao.getMedicamentoComHorarios(medicamentoId);
    }
}
