package com.example.alarmed.views;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.alarmed.model.Medicamento;
import com.example.alarmed.repos.MedicamentoRepository;

import java.util.List;

public class MedicamentoViewModel extends AndroidViewModel {
    private MedicamentoRepository repositorio;
    private LiveData<List<Medicamento>> todosOsMedicamentos;

    public MedicamentoViewModel(@NonNull Application application) {
        super(application);
        repositorio = new MedicamentoRepository(application);
        todosOsMedicamentos = repositorio.listarTodos();
    }

    public LiveData<List<Medicamento>> getTodosOsMedicamentos() {
        return todosOsMedicamentos;
    }

    public void inserir(Medicamento medicamento) {
        repositorio.inserir(medicamento);
    }

    public void atualizar(Medicamento medicamento) {
        repositorio.atualizar(medicamento);
    }

    public void deletar(Medicamento medicamento) {
        repositorio.deletar(medicamento);
    }
}
