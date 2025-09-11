package com.example.alarmed.alarm;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.alarmed.data.db.entity.HistoricoUso;
import com.example.alarmed.data.repos.MedicamentoRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryUpdateServiceNew extends Service {
    private static final String TAG = "HistoryUpdateService";
    
    public static final String ACTION_TAKEN = "com.example.alarmed.ACTION_TAKEN";
    public static final String ACTION_SKIPPED = "com.example.alarmed.ACTION_SKIPPED";
    public static final String EXTRA_MEDICAMENTO_ID = "MEDICAMENTO_ID";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private StockManager stockManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "HistoryUpdateService criado");
        stockManager = new StockManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() iniciado");
        
        if (intent != null) {
            String action = intent.getAction();
            int medicamentoId = intent.getIntExtra(EXTRA_MEDICAMENTO_ID, -1);
            Log.d(TAG, "Action: " + action + ", Medicamento ID: " + medicamentoId);

            if (action != null && medicamentoId != -1) {
                handleAction(action, medicamentoId);
            } else {
                Log.w(TAG, "Parâmetros inválidos - Action: " + action + ", ID: " + medicamentoId);
                stopSelf();
            }
        } else {
            Log.w(TAG, "Intent é null");
            stopSelf();
        }
        
        return START_NOT_STICKY;
    }

    private void handleAction(String action, int medicamentoId) {
        Log.d(TAG, "handleAction() iniciado - Action: " + action + ", ID: " + medicamentoId);
        
        executorService.execute(() -> {
            try {
                MedicamentoRepository repository = new MedicamentoRepository(getApplication());
                String status = null;

                if (ACTION_TAKEN.equals(action)) {
                    status = "TOMADO";
                    Log.d(TAG, "Medicamento " + medicamentoId + " marcado como TOMADO.");
                    
                    // Atualiza o estoque usando o StockManager
                    stockManager.medicamentTaken(medicamentoId);
                    
                } else if (ACTION_SKIPPED.equals(action)) {
                    status = "PULADO";
                    Log.d(TAG, "Medicamento " + medicamentoId + " marcado como PULADO.");
                }

                if (status != null) {
                    // Cria e insere o histórico
                    Log.d(TAG, "Criando histórico de uso...");
                    HistoricoUso historico = new HistoricoUso();
                    historico.id_medicamento = medicamentoId;
                    historico.status = status;
                    historico.data_hora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    repository.insertHistorico(historico);
                    Log.d(TAG, "Histórico inserido: " + status + " às " + historico.data_hora);

                    // Cancela a notificação
                    Log.d(TAG, "Cancelando notificação...");
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(medicamentoId);

                    // Reagenda o próximo alarme
                    reagendarAlarme(repository, medicamentoId);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Erro ao processar ação: " + e.getMessage(), e);
            } finally {
                // Para o serviço
                Log.d(TAG, "Parando serviço...");
                stopSelf();
            }
        });
    }

    private void reagendarAlarme(MedicamentoRepository repository, int medicamentoId) {
        Log.d(TAG, "Reagendando próximo alarme...");
        
        AlarmScheduler alarmScheduler = new AlarmScheduler(this);
        repository.getHorarioByMedicamentoId(medicamentoId, horario -> {
            if (horario != null) {
                Log.d(TAG, "Horário encontrado, buscando medicamento...");
                repository.getMedicamentoById(medicamentoId, medicamento -> {
                    if (medicamento != null) {
                        Log.d(TAG, "Medicamento encontrado, reagendando alarme para: " + medicamento.nome);
                        alarmScheduler.schedule(this, medicamentoId, medicamento.nome, horario);
                    } else {
                        Log.e(TAG, "Medicamento não encontrado para ID: " + medicamentoId);
                    }
                });
            } else {
                Log.w(TAG, "Nenhum horário encontrado para medicamento ID: " + medicamentoId);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "HistoryUpdateService destruído");
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
