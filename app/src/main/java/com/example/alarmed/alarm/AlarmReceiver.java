package com.example.alarmed.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "onReceive() iniciado");
        Log.d("AlarmReceiver", "Intent action: " + intent.getAction());

        int medicamentoId = intent.getIntExtra("MEDICAMENTO_ID", -1);
        String medicamentoNome = intent.getStringExtra("MEDICAMENTO_NOME");

        Log.d("AlarmReceiver", "Dados recebidos - ID: " + medicamentoId + ", Nome: " + medicamentoNome);

        if (medicamentoId != -1 && medicamentoNome != null) {
            Log.d("AlarmReceiver", "Criando e exibindo notificação...");
            NotificationHelper notificationHelper = new NotificationHelper(context);
            notificationHelper.showNotification(context, medicamentoId, medicamentoNome);
            Log.d("AlarmReceiver", "Notificação enviada com sucesso");
        } else {
            Log.w("AlarmReceiver", "Dados inválidos - ID: " + medicamentoId + ", Nome: " + medicamentoNome);
        }
        
        Log.d("AlarmReceiver", "onReceive() finalizado");
    }
}
