package com.example.alarmed.ui.horario;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class HorarioViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final int mMedicamentoId;

    /**
     * Construtor da Factory.
     * @param application A instância da aplicação.
     * @param medicamentoId O ID do medicamento para o qual o ViewModel será criado.
     */
    public HorarioViewModelFactory(Application application, int medicamentoId) {
        mApplication = application;
        mMedicamentoId = medicamentoId;
    }

    /**
     * Cria uma nova instância do ViewModel solicitado.
     * @param modelClass A classe do ViewModel a ser criada.
     * @return Uma nova instância de HorarioViewModel.
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HorarioViewModel.class)) {
            //noinspection unchecked
            return (T) new HorarioViewModel(mApplication, mMedicamentoId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
