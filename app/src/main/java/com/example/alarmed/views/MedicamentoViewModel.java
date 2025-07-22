package com.example.alarmed.views;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import com.example.alarmed.model.Medicamento;
import com.example.alarmed.relacionamentos.MedicamentoComHorarios;
import com.example.alarmed.repos.MedicamentoRepository;

import java.util.List;

public class MedicamentoViewModel extends AndroidViewModel {
    private final MedicamentoRepository mRepository;
    private final LiveData<List<Medicamento>> mAllMedicamentos;

    public MedicamentoViewModel(@NonNull Application application) {
        super(application);
        // Cria uma instância do repositório.
        // O ViewModel não deve ter uma referência direta ao banco de dados ou DAOs.
        mRepository = new MedicamentoRepository(application);

        // Obtém a lista de todos os medicamentos do repositório.
        // Como o DAO retorna LiveData, esta lista será atualizada automaticamente.
        mAllMedicamentos = mRepository.getAllMedicamentos();
    }

    /**
     * Expõe a lista de medicamentos como LiveData para a UI.
     * A UI (Activity/Fragment) irá observar este LiveData para receber atualizações.
     * @return um LiveData contendo a lista de todos os medicamentos.
     */
    public LiveData<List<Medicamento>> getAllMedicamentos() {
        return mAllMedicamentos;
    }

    /**
     * Solicita ao repositório para inserir um novo medicamento.
     * A operação de inserção é executada em uma thread de segundo plano dentro do repositório.
     * @param medicamento O medicamento a ser inserido.
     */
    public void insert(Medicamento medicamento) {
        mRepository.insertMedicamento(medicamento);
    }

    /**
     * Solicita ao repositório para deletar um medicamento pelo seu ID.
     * @param id O ID do medicamento a ser deletado.
     */
    public void deleteById(int id) {
        mRepository.deleteMedicamentoById(id);
    }

    // Você pode adicionar outros métodos aqui para interagir com o repositório,
    // por exemplo, para buscar um medicamento específico ou seus horários.

    public LiveData<MedicamentoComHorarios> getMedicamentoComHorarios(int medicamentoId) {
        return mRepository.getMedicamentoComHorarios(medicamentoId);
    }
}
