package com.example.alarmed.alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.alarmed.R;
import com.example.alarmed.data.db.entity.Medicamento;
import com.example.alarmed.ui.medicamentos.list.MainActivity;

public class NotificationHelper extends ContextWrapper {

    private static final String CHANNEL_ID = "alarm_channel";
    public static final String REMINDER_CHANNEL_ID = "reminder_channel";
    public static final String REMINDER_CHANNEL_NAME = "Lembretes de Medicação";
    public static final String LOW_STOCK_CHANNEL_ID = "low_stock_channel";
    public static final String LOW_STOCK_CHANNEL_NAME = "Alertas de Estoque";
    private static final String CHANNEL_NAME = "Lembretes de Medicamentos";

    private static NotificationManager manager;
    private static final int LOW_STOCK_NOTIFICATION_ID_OFFSET = 10000;


    public NotificationHelper(Context context) {
        super(context);
        Log.d("NotificationHelper", "NotificationHelper inicializado");
    }
    public NotificationCompat.Builder getReminderNotification(String title, String body, PendingIntent takenIntent, PendingIntent dismissIntent) {
        return new NotificationCompat.Builder(getApplicationContext(), REMINDER_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_check, "Tomei", takenIntent)
                .addAction(R.drawable.ic_close, "Dispensar", dismissIntent);
    }

    /**
     * Cria o canal de notificação. Essencial para Android 8.0+.
     * Deve ser chamado quando a aplicação inicia.
     */
    public static void createNotificationChannel(Context context) {
        Log.d("NotificationHelper", "Criando canais de notificação...");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            
            // Canal principal de lembretes
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal para os lembretes de tomar medicamentos.");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
            
            // Canal de lembretes (compatibilidade)
            NotificationChannel reminderChannel = new NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    REMINDER_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            reminderChannel.enableLights(true);
            reminderChannel.setLightColor(Color.BLUE);
            reminderChannel.enableVibration(true);
            notificationManager.createNotificationChannel(reminderChannel);

            // Canal de alertas de estoque baixo
            NotificationChannel lowStockChannel = new NotificationChannel(
                    LOW_STOCK_CHANNEL_ID,
                    LOW_STOCK_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            lowStockChannel.setDescription("Alertas quando o estoque de medicamentos está baixo.");
            lowStockChannel.enableLights(true);
            lowStockChannel.setLightColor(Color.YELLOW);
            lowStockChannel.enableVibration(true);
            notificationManager.createNotificationChannel(lowStockChannel);
            
            Log.d("NotificationHelper", "Canais de notificação criados com sucesso");
        }
    }
    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    /**
     * Cria e exibe a notificação do alarme.
     */
    public void showNotification(Context context, int medicamentoId, String medicamentoNome) {
        android.util.Log.d("NotificationHelper", "Tentando criar notificação para: " + medicamentoNome + " (ID: " + medicamentoId + ")");
        
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Intent para a ação "Tomei"
        Intent takenIntent = new Intent(context, HistoryUpdateServiceNew.class);
        takenIntent.setAction(HistoryUpdateServiceNew.ACTION_TAKEN);
        takenIntent.putExtra("MEDICAMENTO_ID", medicamentoId);
        PendingIntent takenPendingIntent = PendingIntent.getService(
                context,
                medicamentoId * 10 + 1, // Request code único
                takenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Intent para a ação "Dispensar"
        Intent skippedIntent = new Intent(context, HistoryUpdateServiceNew.class);
        skippedIntent.setAction(HistoryUpdateServiceNew.ACTION_SKIPPED);
        skippedIntent.putExtra("MEDICAMENTO_ID", medicamentoId);
        PendingIntent skippedPendingIntent = PendingIntent.getService(
                context,
                medicamentoId * 10 + 2, // Request code único
                skippedIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Substitua por um ícone de notificação adequado
                .setContentTitle("Hora de tomar o seu medicamento!")
                .setContentText(medicamentoNome)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(0, "Tomei", takenPendingIntent)
                .addAction(0, "Dispensar", skippedPendingIntent);

        // O ID da notificação deve ser único para cada medicamento
        notificationManager.notify(medicamentoId, builder.build());
        android.util.Log.d("NotificationHelper", "Notificação enviada com sucesso para ID: " + medicamentoId);
    }

    /**
     * Método para testar se as notificações estão funcionando
     */
    public static void testNotification(Context context) {
        android.util.Log.d("NotificationHelper", "Testando notificação...");
        NotificationHelper helper = new NotificationHelper(context);
        helper.showNotification(context, 999, "Teste de Medicamento");
    }

    /**
     * Envia notificação de estoque baixo para um medicamento
     */
    public static void sendLowStockNotification(Context context, Medicamento medicamento) {
        Log.d("NotificationHelper", "Enviando notificação de estoque baixo para: " + medicamento.nome +
              " (Atual: " + medicamento.estoque_atual + ", Mínimo: " + medicamento.estoque_minimo + ")");
        
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        // Intent para abrir a tela principal
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                medicamento.id + 20000, // ID único para evitar conflitos
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String title = "⚠️ Estoque Baixo";
        String body;
        
        if (medicamento.estoque_atual == 0) {
            body = "O medicamento '" + medicamento.nome + "' acabou! Providencie a reposição urgentemente.";
        } else if (medicamento.estoque_atual == 1) {
            body = "Resta apenas 1 unidade do medicamento '" + medicamento.nome + "'. Providencie a reposição.";
        } else {
            body = "O medicamento '" + medicamento.nome + "' está com estoque baixo. Restam apenas " + 
                   medicamento.estoque_atual + " unidades.";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, LOW_STOCK_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Idealmente usar um ícone de aviso
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER);

        // ID único para notificação de estoque baixo
        int notificationId = LOW_STOCK_NOTIFICATION_ID_OFFSET + medicamento.id;
        notificationManager.notify(notificationId, builder.build());
        
        Log.d("NotificationHelper", "Notificação de estoque baixo enviada com ID: " + notificationId);
    }

    /**
     * Método de compatibilidade (instance method)
     */
    public void sendLowStockNotification(Medicamento medicamento) {
        sendLowStockNotification(this, medicamento);
    }

    /**
     * Verifica se um medicamento está com estoque baixo
     */
    public static boolean isLowStock(Medicamento medicamento) {
        return medicamento.estoque_atual <= medicamento.estoque_minimo;
    }

    /**
     * Cancela notificação de estoque baixo para um medicamento específico
     */
    public static void cancelLowStockNotification(Context context, int medicamentoId) {
        Log.d("NotificationHelper", "Cancelando notificação de estoque baixo para medicamento ID: " + medicamentoId);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = LOW_STOCK_NOTIFICATION_ID_OFFSET + medicamentoId;
        notificationManager.cancel(notificationId);
    }


}
