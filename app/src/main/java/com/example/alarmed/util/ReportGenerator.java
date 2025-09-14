package com.example.alarmed.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.alarmed.data.db.entity.Horario;
import com.example.alarmed.data.db.entity.Medicamento;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportGenerator {
    private static final String TAG = "ReportGenerator";
    
    public static class MedicamentoComHorario {
        public Medicamento medicamento;
        public Horario horario;
        
        public MedicamentoComHorario(Medicamento medicamento, Horario horario) {
            this.medicamento = medicamento;
            this.horario = horario;
        }
    }
    
    public static void generateWeeklyReport(Context context, List<MedicamentoComHorario> medicamentosComHorarios) {
        Log.d(TAG, "Gerando relatório semanal...");
        
        try {
            // Cria o arquivo
            File reportsDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "AlarMed");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }
            
            String fileName = "relatorio_medicamentos_" + new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(new Date()) + ".txt";
            File reportFile = new File(reportsDir, fileName);
            
            // Gera o conteúdo do relatório
            String content = generateReportContent(medicamentosComHorarios);
            
            // Escreve no arquivo
            FileWriter writer = new FileWriter(reportFile);
            writer.write(content);
            writer.close();
            
            Log.d(TAG, "Relatório gerado: " + reportFile.getAbsolutePath());
            
            // Abre o arquivo
            openFile(context, reportFile);
            
            // Toast na thread principal
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(context, "Relatório gerado com sucesso!", Toast.LENGTH_SHORT).show();
            });
            
        } catch (IOException e) {
            Log.e(TAG, "Erro ao gerar relatório", e);
            // Toast na thread principal
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(context, "Erro ao gerar relatório: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
    }
    
    private static String generateReportContent(List<MedicamentoComHorario> medicamentosComHorarios) {
        StringBuilder content = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        
        // Cabeçalho
        content.append("===========================================\n");
        content.append("         RELATÓRIO DE MEDICAMENTOS\n");
        content.append("           PRÓXIMOS 7 DIAS\n");
        content.append("===========================================\n");
        content.append("Data de geração: ").append(dateFormat.format(new Date())).append("\n\n");
        
        // Calcula horários para os próximos 7 dias de forma contínua
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DAY_OF_MONTH, 7);
        endCalendar.set(Calendar.HOUR_OF_DAY, 0);
        endCalendar.set(Calendar.MINUTE, 0);
        endCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);
        
        // Para cada medicamento, calcula todos os horários da semana
        Map<String, List<String>> horariosPorDia = new HashMap<>();
        
        for (MedicamentoComHorario medicamentoComHorario : medicamentosComHorarios) {
            if (medicamentoComHorario.horario != null) {
                List<Date> horariosCompletos = calculateWeeklySchedule(medicamentoComHorario, startCalendar.getTime(), endCalendar.getTime());
                
                for (Date horarioCompleto : horariosCompletos) {
                    Calendar horarioCalendar = Calendar.getInstance();
                    horarioCalendar.setTime(horarioCompleto);
                    
                    String dayKey = dateFormat.format(horarioCompleto);
                    String horarioFormatted = timeFormat.format(horarioCompleto) + " - " + 
                                             medicamentoComHorario.medicamento.nome + 
                                             " (" + medicamentoComHorario.medicamento.dose + ")";
                    
                    horariosPorDia.computeIfAbsent(dayKey, k -> new ArrayList<>()).add(horarioFormatted);
                }
            }
        }
        
        // Para cada dia da semana
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        for (int day = 0; day < 7; day++) {
            Date currentDate = calendar.getTime();
            String dayKey = dateFormat.format(currentDate);
            
            content.append("📅 ").append(getDayOfWeekName(calendar.get(Calendar.DAY_OF_WEEK)))
                   .append(" - ").append(dayKey).append("\n");
            content.append("-------------------------------------------\n");
            
            List<String> horariosParaODia = horariosPorDia.get(dayKey);
            
            if (horariosParaODia == null || horariosParaODia.isEmpty()) {
                content.append("   ⚪ Nenhum medicamento agendado\n");
            } else {
                // Ordena os horários
                horariosParaODia.sort(String::compareTo);
                for (String horario : horariosParaODia) {
                    content.append("   🔔 ").append(horario).append("\n");
                }
            }
            
            content.append("\n");
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        // Resumo dos medicamentos
        content.append("===========================================\n");
        content.append("           RESUMO DOS MEDICAMENTOS\n");
        content.append("===========================================\n");
        
        for (MedicamentoComHorario medicamentoComHorario : medicamentosComHorarios) {
            Medicamento med = medicamentoComHorario.medicamento;
            Horario hor = medicamentoComHorario.horario;
            
            content.append("💊 ").append(med.nome).append("\n");
            content.append("   Tipo: ").append(med.tipo).append("\n");
            content.append("   Dose: ").append(med.dose).append("\n");
            content.append("   Estoque atual: ").append(med.estoque_atual).append("\n");
            content.append("   Estoque mínimo: ").append(med.estoque_minimo).append("\n");
            
            if (hor != null) {
                content.append("   Horário inicial: ").append(hor.horario_inicial).append("\n");
                content.append("   Intervalo: ").append(hor.intervalo).append(" horas\n");
            } else {
                content.append("   ⚠️ Sem horário definido\n");
            }
            content.append("\n");
        }
        
        content.append("===========================================\n");
        content.append("Relatório gerado automaticamente pelo AlarMed\n");
        content.append("===========================================\n");
        
        return content.toString();
    }
    
    private static List<Date> calculateWeeklySchedule(MedicamentoComHorario medicamentoComHorario, Date startDate, Date endDate) {
        List<Date> horarios = new ArrayList<>();
        Horario horario = medicamentoComHorario.horario;
        
        if (horario == null || horario.horario_inicial == null) {
            return horarios;
        }
        
        try {
            // Parseia o horário inicial
            String[] parts = horario.horario_inicial.split(":");
            int startHour = Integer.parseInt(parts[0]);
            int startMinute = Integer.parseInt(parts[1]);
            
            // Começa do horário inicial hoje
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.set(Calendar.HOUR_OF_DAY, startHour);
            calendar.set(Calendar.MINUTE, startMinute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            
            // Se o horário inicial já passou hoje, avança para a próxima ocorrência
            Calendar now = Calendar.getInstance();
            while (calendar.before(now)) {
                calendar.add(Calendar.HOUR_OF_DAY, horario.intervalo);
            }
            
            // Gera horários até o fim da semana
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);
            
            while (calendar.before(endCalendar)) {
                horarios.add(new Date(calendar.getTimeInMillis()));
                calendar.add(Calendar.HOUR_OF_DAY, horario.intervalo);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Erro ao calcular horários semanais", e);
        }
        
        return horarios;
    }
    
    private static String getDayOfWeekName(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY: return "Domingo";
            case Calendar.MONDAY: return "Segunda-feira";
            case Calendar.TUESDAY: return "Terça-feira";
            case Calendar.WEDNESDAY: return "Quarta-feira";
            case Calendar.THURSDAY: return "Quinta-feira";
            case Calendar.FRIDAY: return "Sexta-feira";
            case Calendar.SATURDAY: return "Sábado";
            default: return "Dia desconhecido";
        }
    }
    
    private static void openFile(Context context, File file) {
        try {
            Uri fileUri = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                file
            );
            
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "text/plain");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                // Se não conseguir abrir, pelo menos mostra onde foi salvo
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(context, "Arquivo salvo em: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                });
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Erro ao abrir arquivo", e);
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(context, "Arquivo salvo em: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            });
        }
    }
}
