package com.example.alarmed.alarm;

import android.app.IntentService;
import android.app.NotificationManager;
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

public class HistoryUpdateService extends IntentService {

    public static final String ACTION_TAKEN = "com.example.seuprojeto.ACTION_TAKEN";
    static final String ACTION_DISMISS = "com.example.alarmed.ACTION_DISMISS";
    public static final String EXTRA_MEDICAMENTO_ID = "EXTRA_MEDICAMENTO_ID";
    public static final String ACTION_SKIPPED = "com.example.seuprojeto.ACTION_SKIPPED";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public HistoryUpdateService() {
        super("HistoryUpdateService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();
        final int medicamentoId = intent.getIntExtra(EXTRA_MEDICAMENTO_ID, -1);

        if (medicamentoId == -1) {
            return;
        }

        MedicamentoRepository repository = new MedicamentoRepository(getApplication());
        String status = null;

        if (ACTION_TAKEN.equals(action)) {
            status = "TOMADO";
            Log.d("HistoryUpdateService", "Ação 'Tomei' recebida para o medicamento ID: " + medicamentoId);
            // Lógica de atualização do estoque
            updateStock(repository, medicamentoId);
        } else if (ACTION_DISMISS.equals(action)) {
            status = "PULADO";
            Log.d("HistoryUpdateService", "Ação 'Dispensar' recebida para o medicamento ID: " + medicamentoId);
        }

        if (status != null) {
            // Insere o registro no histórico
            HistoricoUso historico = new HistoricoUso();
            historico.id_medicamento = medicamentoId;
            historico.status = status;
            historico.data_hora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            repository.insertHistorico(historico);
        }

        // Reagenda o próximo alarme
        rescheduleNextAlarm(repository, medicamentoId);
    }
    private void updateStock(MedicamentoRepository repository, int medicamentoId) {
        repository.getMedicamentoById(medicamentoId, medicamento -> {
            if (medicamento != null) {
                if (medicamento.estoque_atual > 0) {
                    medicamento.estoque_atual--; // Subtrai 1 do estoque
                    repository.save(medicamento, null); // Salva a alteração
                    Log.d("HistoryUpdateService", "Estoque atualizado para " + medicamento.nome + ": " + medicamento.estoque_atual);

                    // Verifica se o estoque atingiu o mínimo
                    if (medicamento.estoque_atual <= medicamento.estoque_minimo) {
                        Log.d("HistoryUpdateService", "Estoque baixo detectado para " + medicamento.nome);
                        NotificationHelper notificationHelper = new NotificationHelper(this);
                        notificationHelper.sendLowStockNotification(medicamento);
                    }
                }
            }
        });
    }
    private void rescheduleNextAlarm(MedicamentoRepository repository, int medicamentoId) {
        // Reagenda o próximo alarme
        AlarmScheduler alarmScheduler = new AlarmScheduler(this);
        repository.getHorarioByMedicamentoId(medicamentoId, horario -> {
            if (horario != null) {
                repository.getMedicamentoById(medicamentoId, medicamento -> {
                    if (medicamento != null) {
                        alarmScheduler.schedule(this, medicamentoId, medicamento.nome, horario);
                    }
                });
            }
        });
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("HistoryUpdateService", "onStartCommand() iniciado");
        if (intent != null) {
            String action = intent.getAction();
            int medicamentoId = intent.getIntExtra("MEDICAMENTO_ID", -1);
            Log.d("HistoryUpdateService", "Action: " + action + ", Medicamento ID: " + medicamentoId);

            if (action != null && medicamentoId != -1) {
                handleAction(action, medicamentoId);
            } else {
                Log.w("HistoryUpdateService", "Parâmetros inválidos - Action: " + action + ", ID: " + medicamentoId);
            }
        } else {
            Log.w("HistoryUpdateService", "Intent é null");
        }
        return START_NOT_STICKY;
    }

    private void handleAction(String action, int medicamentoId) {
        Log.d("HistoryUpdateService", "handleAction() iniciado - Action: " + action + ", ID: " + medicamentoId);
        executorService.execute(() -> {
            MedicamentoRepository repository = new MedicamentoRepository(getApplication());
            String status = null;

            if (ACTION_TAKEN.equals(action)) {
                status = "TOMADO";
                Log.d("HistoryUpdateService", "Medicamento " + medicamentoId + " marcado como TOMADO.");
            } else if (ACTION_SKIPPED.equals(action)) {
                status = "PULADO";
                Log.d("HistoryUpdateService", "Medicamento " + medicamentoId + " marcado como PULADO.");
            }

            if (status != null) {
                Log.d("HistoryUpdateService", "Criando histórico de uso...");
                // 1. Cria e insere o registo de histórico
                HistoricoUso historico = new HistoricoUso();
                historico.id_medicamento = medicamentoId;
                historico.status = status;
                historico.data_hora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                repository.insertHistorico(historico);
                Log.d("HistoryUpdateService", "Histórico inserido: " + status + " às " + historico.data_hora);

                // 2. Cancela a notificação que foi clicada
                Log.d("HistoryUpdateService", "Cancelando notificação...");
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(medicamentoId);

                // 3. Reagenda o próximo alarme
                Log.d("HistoryUpdateService", "Reagendando próximo alarme...");
                AlarmScheduler alarmScheduler = new AlarmScheduler(this);
                repository.getHorarioByMedicamentoId(medicamentoId, horario -> {
                    if (horario != null) {
                        Log.d("HistoryUpdateService", "Horário encontrado, buscando medicamento...");
                        repository.getMedicamentoById(medicamentoId, medicamento -> {
                            if (medicamento != null) {
                                Log.d("HistoryUpdateService", "Medicamento encontrado, reagendando alarme para: " + medicamento.nome);
                                alarmScheduler.schedule(this, medicamentoId, medicamento.nome, horario);
                            } else {
                                Log.e("HistoryUpdateService", "Medicamento não encontrado para ID: " + medicamentoId);
                            }
                        });
                    } else {
                        Log.w("HistoryUpdateService", "Nenhum horário encontrado para medicamento ID: " + medicamentoId);
                    }
                });
            }

            // Para o serviço após a conclusão da tarefa
            Log.d("HistoryUpdateService", "Parando serviço...");
            stopSelf();
        });
    }
}

