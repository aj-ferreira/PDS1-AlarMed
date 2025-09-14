package com.example.alarmed.ui.medicamentos.detail;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.alarmed.data.db.entity.Medicamento;
import com.example.alarmed.data.db.relacionamentos.MedicamentoComHorarios;
import com.example.alarmed.data.db.relacionamentos.HistoricoComMedicamento;
import com.example.alarmed.data.repos.MedicamentoRepository;

import java.util.List;

public class MedicamentoViewModel extends AndroidViewModel {
    private final MedicamentoRepository mRepository;
    private final LiveData<List<Medicamento>> mAllMedicamentos;

    public MedicamentoViewModel(@NonNull Application application) {
        super(application);
        Log.d("MedicamentoViewModel", "Inicializando ViewModel...");
        // Cria uma instância do repositório.
        mRepository = new MedicamentoRepository(application);

        // Obtém a lista de todos os medicamentos do repositório.
        // Como o DAO retorna LiveData, esta lista será atualizada automaticamente.
        mAllMedicamentos = mRepository.getAllMedicamentos();
        Log.d("MedicamentoViewModel", "ViewModel inicializado com sucesso");
    }

    /**
     * Expõe a lista de medicamentos como LiveData para a UI.
     * A UI (Activity/Fragment) irá observar este LiveData para receber atualizações.
     * @return um LiveData contendo a lista de todos os medicamentos.
     */
    public LiveData<List<Medicamento>> getAllMedicamentos() {
        Log.d("MedicamentoViewModel", "getAllMedicamentos() chamado");
        return mAllMedicamentos;
    }

    /**
     * Solicita ao repositório para inserir um novo medicamento.
     * A operação de inserção é executada em uma thread de segundo plano dentro do repositório.
     * @param medicamento O medicamento a ser inserido.
     */
    public void save(Medicamento medicamento) {
        Log.d("MedicamentoViewModel", "save() chamado - Medicamento: " + medicamento.nome + " (ID: " + medicamento.id + ")");
        mRepository.save(medicamento);
    }

    /**
     * Salva (insere) um novo medicamento e executa um callback com o novo ID.
     * @param medicamento O novo medicamento a ser inserido.
     * @param listener O listener a ser chamado após a conclusão.
     */
    public void save(Medicamento medicamento, MedicamentoRepository.OnSaveCompleteListener listener) {
        Log.d("MedicamentoViewModel", "save() com callback chamado - Medicamento: " + medicamento.nome);
        mRepository.save(medicamento, listener);
    }

    /**
     * Solicita ao repositório para deletar um medicamento pelo seu ID.
     * @param id O ID do medicamento a ser deletado.
     */
    public void deleteById(int id) {
        Log.d("MedicamentoViewModel", "deleteById() chamado - ID: " + id);
        mRepository.deleteMedicamentoById(id);
    }


    public LiveData<MedicamentoComHorarios> getMedicamentoComHorarios(int medicamentoId) {
        Log.d("MedicamentoViewModel", "getMedicamentoComHorarios() chamado - ID: " + medicamentoId);
        return mRepository.getMedicamentoComHorarios(medicamentoId);
    }

    /**
     * Expõe a lista de medicamentos com horários como LiveData para a UI.
     * @return um LiveData contendo a lista de todos os medicamentos com seus horários.
     */
    public LiveData<List<MedicamentoComHorarios>> getAllMedicamentosComHorarios() {
        Log.d("MedicamentoViewModel", "getAllMedicamentosComHorarios() chamado");
        return mRepository.getAllMedicamentosComHorarios();
    }

    /**
     * Expõe a lista de histórico com medicamentos como LiveData para a UI.
     * @return um LiveData contendo a lista de todo o histórico com informações dos medicamentos.
     */
    public LiveData<List<HistoricoComMedicamento>> getAllHistorico() {
        Log.d("MedicamentoViewModel", "getAllHistorico() chamado");
        return mRepository.getAllHistoricoComMedicamento();
    }
}
