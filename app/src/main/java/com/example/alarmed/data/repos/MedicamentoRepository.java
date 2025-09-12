package com.example.alarmed.data.repos;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.alarmed.data.db.AppDatabase;
import com.example.alarmed.data.db.daos.HistoricoUsoDao;
import com.example.alarmed.data.db.daos.HorarioDao;
import com.example.alarmed.data.db.daos.MedicamentoDao;
import com.example.alarmed.data.db.entity.HistoricoUso;
import com.example.alarmed.data.db.entity.Horario;
import com.example.alarmed.data.db.entity.Medicamento;
import com.example.alarmed.data.db.relacionamentos.MedicamentoComHorarios;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Repositório para gerenciar os dados de Medicamento, Horario e HistoricoUso.
 * Abstrai o acesso às fontes de dados (neste caso, os DAOs do Room).
 */
// Callback para o método save
interface OnSaveCompleteListener {
    void onSaveComplete(long newId);
}
public class MedicamentoRepository {
    private final MedicamentoDao medicamentoDao;
    private final HorarioDao horarioDao;
    private final HistoricoUsoDao historicoUsoDao;
    private final ExecutorService executor;

    public MedicamentoRepository(Context application) {
        Log.d("MedicamentoRepository", "Inicializando repositório...");
        // Em uma implementação real, você obteria a instância do banco de dados aqui
        AppDatabase db = AppDatabase.getDatabase(application);
        this.medicamentoDao = db.medicamentoDao();
        this.horarioDao = db.horarioDao();
        this.historicoUsoDao = db.historicoUsoDao();
        this.executor = Executors.newSingleThreadExecutor();
        Log.d("MedicamentoRepository", "Repositório inicializado com sucesso");
    }



    // --- Operações para Medicamento ---
    // Interface para o callback que retorna o ID do novo item salvo.
    public interface OnSaveCompleteListener {
        void onSaveComplete(long newId);
    }
    // Callback para buscar um único Horario
    public interface HorarioCallback {
        void onHorarioLoaded(Horario horario);
    }

    // Callback para buscar um único Medicamento
    public interface MedicamentoCallback {
        void onMedicamentoLoaded(Medicamento medicamento);
    }

    public LiveData<List<Medicamento>> getAllMedicamentos() {
        Log.d("MedicamentoRepository", "Buscando todos os medicamentos");
        return medicamentoDao.getAllMedicamentos();
    }

    public LiveData<List<MedicamentoComHorarios>> getAllMedicamentosComHorarios() {
        Log.d("MedicamentoRepository", "Buscando todos os medicamentos com horários");
        return medicamentoDao.getAllMedicamentosComHorarios();
    }

    public LiveData<Medicamento> getMedicamentoById(int id) {
        Log.d("MedicamentoRepository", "Buscando medicamento por ID: " + id);
        return medicamentoDao.getMedicamentoById(id);
    }

    public void save(Medicamento medicamento) {
        Log.d("MedicamentoRepository", "Salvando medicamento (sem callback): " + medicamento.nome);
        executor.execute(() -> {
            long result = medicamentoDao.save(medicamento);
            Log.d("MedicamentoRepository", "Medicamento salvo com ID: " + result);
        });
    }

    /**
     * Método save com callback, usado para novas inserções para obter o ID gerado.
     * @param medicamento O novo medicamento a ser inserido.
     * @param listener O listener que será chamado com o novo ID após a conclusão.
     */
    public void save(Medicamento medicamento, OnSaveCompleteListener listener) {
        Log.d("MedicamentoRepository", "Salvando medicamento (com callback): " + medicamento.nome);
        executor.execute(() -> {
            long id = medicamentoDao.save(medicamento);
            Log.d("MedicamentoRepository", "Medicamento salvo com ID: " + id);
            if (listener != null) {
                // O listener é chamado de volta na thread de background com o novo ID.
                listener.onSaveComplete(id);
            }
        });
    }

    public void deleteMedicamentoById(int id) {
        Log.d("MedicamentoRepository", "Deletando medicamento ID: " + id);
        executor.execute(() -> {
            medicamentoDao.deleteMedicamentoById(id);
            Log.d("MedicamentoRepository", "Medicamento deletado: " + id);
        });
    }

    // --- Operações para Horario ---
    public LiveData<Horario> getHorarioParaMedicamento(int medicamentoId) {
        Log.d("MedicamentoRepository", "Buscando horário para medicamento ID: " + medicamentoId);
        return horarioDao.getHorarioParaMedicamento(medicamentoId);
    }

    // Método para buscar um Horario de forma assíncrona
    public void getHorarioByMedicamentoId(int medicamentoId, HorarioCallback callback) {
        Log.d("MedicamentoRepository", "Buscando horário assíncrono para medicamento ID: " + medicamentoId);
        executor.execute(() -> {
            Horario horario = horarioDao.getHorarioByMedicamentoId(medicamentoId);
            Log.d("MedicamentoRepository", "Horário encontrado: " + (horario != null ? horario.horario_inicial : "null"));
            callback.onHorarioLoaded(horario);
        });
    }

    // Método para buscar um Medicamento de forma assíncrona
    public void getMedicamentoById(int medicamentoId, MedicamentoCallback callback) {
        Log.d("MedicamentoRepository", "Buscando medicamento assíncrono ID: " + medicamentoId);
        executor.execute(() -> {
            // Assumindo que um método síncrono existe no DAO para chamadas em background
            Medicamento medicamento = medicamentoDao.getMedicamentoByIdSync(medicamentoId);
            Log.d("MedicamentoRepository", "Medicamento encontrado: " + (medicamento != null ? medicamento.nome : "null"));
            callback.onMedicamentoLoaded(medicamento);
        });
    }

    public void saveHorario(Horario horario) {
        Log.d("MedicamentoRepository", "Salvando horário - Medicamento ID: " + horario.id_medicamento + 
              ", Horário inicial: " + horario.horario_inicial + ", Intervalo: " + horario.intervalo);
        executor.execute(() -> {
            long result = horarioDao.save(horario);
            Log.d("MedicamentoRepository", "Horário salvo com resultado: " + result);
        });
    }

    public void insertHorario(Horario horario) {
        Log.d("MedicamentoRepository", "Inserindo horário...");
        executor.execute(() -> {
            long result = horarioDao.save(horario);
            Log.d("MedicamentoRepository", "Horário inserido com resultado: " + result);
        });
    }

    public void deleteHorarioById(int id) {
        Log.d("MedicamentoRepository", "Deletando horário ID: " + id);
        executor.execute(() -> {
            horarioDao.deleteHorarioById(id);
            Log.d("MedicamentoRepository", "Horário deletado: " + id);
        });
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
        Log.d("MedicamentoRepository", "Buscando medicamento com horários ID: " + medicamentoId);
        return medicamentoDao.getMedicamentoComHorarios(medicamentoId);
    }

    // --- Operações de Estoque ---
    
    /**
     * Verifica se um medicamento está com estoque baixo
     */
    public static boolean isLowStock(Medicamento medicamento) {
        return medicamento.estoque_atual <= medicamento.estoque_minimo;
    }

    /**
     * Atualiza estoque de um medicamento após ser tomado
     */
    public void decreaseStock(int medicamentoId) {
        Log.d("MedicamentoRepository", "Reduzindo estoque para medicamento ID: " + medicamentoId);
        executor.execute(() -> {
            Medicamento medicamento = medicamentoDao.getMedicamentoByIdSync(medicamentoId);
            if (medicamento != null && medicamento.estoque_atual > 0) {
                // Converte dose para int (assumindo que dose é um número)
                int doseQuantity = 1; // valor padrão
                try {
                    if (medicamento.dose != null && !medicamento.dose.trim().isEmpty()) {
                        doseQuantity = Integer.parseInt(medicamento.dose.trim());
                    }
                } catch (NumberFormatException e) {
                    Log.w("MedicamentoRepository", "Erro ao converter dose para número: " + medicamento.dose + ". Usando dose = 1");
                    doseQuantity = 1;
                }
                
                // Reduz o estoque pela quantidade da dose
                int novoEstoque = Math.max(0, medicamento.estoque_atual - doseQuantity);
                medicamento.estoque_atual = novoEstoque;
                medicamentoDao.save(medicamento);
                Log.d("MedicamentoRepository", "Estoque reduzido em " + doseQuantity + " unidade(s) para " + medicamento.nome + 
                      ": " + medicamento.estoque_atual);
            }
        });
    }
}
