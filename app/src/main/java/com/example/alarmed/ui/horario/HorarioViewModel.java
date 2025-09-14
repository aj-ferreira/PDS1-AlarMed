package com.example.alarmed.ui.horario;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.alarmed.alarm.AlarmScheduler;
import com.example.alarmed.data.db.entity.Horario;
import com.example.alarmed.data.db.entity.Medicamento;
import com.example.alarmed.data.repos.MedicamentoRepository;

public class HorarioViewModel extends AndroidViewModel {
    private final MedicamentoRepository mRepository;
    private final LiveData<Horario> mHorario; // Agora observa um único Horario
    private final int medicamentoId;
    private final AlarmScheduler alarmScheduler;

    public HorarioViewModel(@NonNull Application application, int medicamentoId) {
        super(application);
        Log.d("HorarioViewModel", "Inicializando ViewModel para medicamento ID: " + medicamentoId);
        this.medicamentoId = medicamentoId;
        mRepository = new MedicamentoRepository(application);
        mHorario = mRepository.getHorarioParaMedicamento(medicamentoId); // Método atualizado
        alarmScheduler = new AlarmScheduler(application);
        Log.d("HorarioViewModel", "ViewModel inicializado com sucesso");
    }

    public LiveData<Horario> getHorario() {
        Log.d("HorarioViewModel", "getHorario() chamado");
        return mHorario;
    }

    public void save(Horario horario) {
        Log.d("HorarioViewModel", "save() iniciado - Medicamento ID: " + medicamentoId + 
              ", Horário inicial: " + horario.horario_inicial + ", Intervalo: " + horario.intervalo);
        
        // Salva o horário no banco
        mRepository.saveHorario(horario);
        
        // Agenda o alarme
        scheduleAlarm(horario);
        Log.d("HorarioViewModel", "save() finalizado");
    }
    
    private void scheduleAlarm(Horario horario) {
        Log.d("HorarioViewModel", "scheduleAlarm() iniciado");
        // Busca o medicamento para obter o nome usando callback assíncrono
        mRepository.getMedicamentoById(medicamentoId, medicamento -> {
            if (medicamento != null) {
                Log.d("HorarioViewModel", "Medicamento encontrado: " + medicamento.nome + " - Agendando alarme...");
                alarmScheduler.schedule(getApplication(), medicamentoId, medicamento.nome, horario);
                Log.d("HorarioViewModel", "Alarme agendado com sucesso");
            } else {
                Log.e("HorarioViewModel", "Medicamento não encontrado para ID: " + medicamentoId);
            }
        });
    }
}
