package com.example.alarmed.repos;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.alarmed.daos.MedicamentoDao;
import com.example.alarmed.model.Medicamento;
import com.example.alarmed.persistencia.AppDatabase;

import java.util.List;
import java.util.concurrent.Executors;

public class MedicamentoRepository {
    private MedicamentoDao medicamentoDao;

    public MedicamentoRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        medicamentoDao = db.medicamentoDao();
    }

    public void inserir(Medicamento medicamento) {
        Executors.newSingleThreadExecutor().execute(() -> medicamentoDao.inserir(medicamento));
    }

    public void atualizar(Medicamento medicamento) {
        Executors.newSingleThreadExecutor().execute(() -> medicamentoDao.atualizar(medicamento));
    }

    public void deletar(Medicamento medicamento) {
        Executors.newSingleThreadExecutor().execute(() -> medicamentoDao.deletar(medicamento));
    }

    public LiveData<List<Medicamento>> listarTodos() {
        return new MutableLiveData<>(medicamentoDao.listarTodos());
    }
}
