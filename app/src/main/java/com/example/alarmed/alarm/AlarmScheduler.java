package com.example.alarmed.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.alarmed.data.db.entity.Horario;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmScheduler {
    private final Context context;
    private final AlarmManager alarmManager;
    public AlarmScheduler(Context context) {
        this.context = context.getApplicationContext();
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }
    public void schedule(Context context, int medicamentoId, String medicamentoNome, Horario horario) {
        android.util.Log.d("AlarmScheduler", "Tentando agendar alarme para: " + medicamentoNome + " (ID: " + medicamentoId + ")");
        
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long triggerTime = calculateNextTriggerTime(horario);
        
        android.util.Log.d("AlarmScheduler", "Tempo calculado para próximo alarme: " + triggerTime);

        if (triggerTime == -1) {
            new Handler(Looper.getMainLooper()).post(() -> 
                Toast.makeText(context, "Não há mais alarmes para agendar.", Toast.LENGTH_SHORT).show()
            );
            android.util.Log.w("AlarmScheduler", "Não foi possível calcular o próximo horário do alarme");
            // Cancela qualquer alarme pendente para garantir
            cancel(context, medicamentoId);
            return;
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("MEDICAMENTO_ID", medicamentoId);
        intent.putExtra("MEDICAMENTO_NOME", medicamentoNome);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                medicamentoId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Verifica se a permissão para alarmes exatos foi concedida
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {

                new Handler(Looper.getMainLooper()).post(() -> 
                    Toast.makeText(context, "Permissão para alarmes exatos não concedida.", Toast.LENGTH_LONG).show()
                );
                android.util.Log.e("AlarmScheduler", "Permissão para alarmes exatos não concedida");
                return;
            }
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedTime = sdf.format(new Date(triggerTime));
        new Handler(Looper.getMainLooper()).post(() -> 
            Toast.makeText(context, "Alarme agendado para: " + formattedTime, Toast.LENGTH_LONG).show()
        );
        android.util.Log.d("AlarmScheduler", "Alarme agendado com sucesso para: " + formattedTime);
    }

    public void cancel(Context context, int medicamentoId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                medicamentoId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }

    private long calculateNextTriggerTime(Horario horario) {
        android.util.Log.d("AlarmScheduler", "Calculando próximo horário do alarme...");
        
        if (horario == null || horario.horario_inicial == null || horario.horario_inicial.isEmpty()) {
            android.util.Log.e("AlarmScheduler", "Horário é nulo ou horário inicial está vazio");
            return -1;
        }

        android.util.Log.d("AlarmScheduler", "Horário inicial: " + horario.horario_inicial + ", Intervalo: " + horario.intervalo);

        try {
            String[] parts = horario.horario_inicial.split(":");
            int startHour = Integer.parseInt(parts[0]);
            int startMinute = Integer.parseInt(parts[1]);

            Calendar now = Calendar.getInstance();
            Calendar nextAlarmTime = Calendar.getInstance();
            nextAlarmTime.set(Calendar.HOUR_OF_DAY, startHour);
            nextAlarmTime.set(Calendar.MINUTE, startMinute);
            nextAlarmTime.set(Calendar.SECOND, 0);

            android.util.Log.d("AlarmScheduler", "Hora atual: " + now.getTime());
            android.util.Log.d("AlarmScheduler", "Próximo alarme inicial: " + nextAlarmTime.getTime());

            // Se a hora de início hoje já passou, calcula a próxima ocorrência
            while (nextAlarmTime.before(now)) {
                nextAlarmTime.add(Calendar.HOUR_OF_DAY, horario.intervalo);
                android.util.Log.d("AlarmScheduler", "Ajustando para: " + nextAlarmTime.getTime());
            }

            // Verifica se a data de fim do tratamento foi atingida
            if (horario.dataFim != null && !horario.dataFim.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date endDate = sdf.parse(horario.dataFim);
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(endDate);
                endCal.set(Calendar.HOUR_OF_DAY, 23);
                endCal.set(Calendar.MINUTE, 59);

                if (nextAlarmTime.after(endCal)) {
                    android.util.Log.w("AlarmScheduler", "Tratamento terminou em: " + horario.dataFim);
                    return -1; // O tratamento terminou
                }
            }

            android.util.Log.d("AlarmScheduler", "Próximo alarme agendado para: " + nextAlarmTime.getTime());
            return nextAlarmTime.getTimeInMillis();

        } catch (Exception e) {
            android.util.Log.e("AlarmScheduler", "Erro ao calcular próximo horário", e);
            e.printStackTrace();
            return -1;
        }
    }
}
