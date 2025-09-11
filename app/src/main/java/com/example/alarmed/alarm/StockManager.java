package com.example.alarmed.alarm;

import android.content.Context;
import android.util.Log;

import com.example.alarmed.data.db.entity.Medicamento;
import com.example.alarmed.data.repos.MedicamentoRepository;

/**
 * Classe responsável por gerenciar o estoque de medicamentos
 * e disparar notificações quando necessário
 */
public class StockManager {
    private static final String TAG = "StockManager";
    private final Context context;
    private final MedicamentoRepository repository;

    public StockManager(Context context) {
        this.context = context.getApplicationContext();
        this.repository = new MedicamentoRepository(context);
    }

    /**
     * Atualiza o estoque de um medicamento quando tomado
     */
    public void medicamentTaken(int medicamentoId) {
        Log.d(TAG, "Atualizando estoque após medicamento tomado - ID: " + medicamentoId);
        
        repository.getMedicamentoById(medicamentoId, medicamento -> {
            if (medicamento != null) {
                Log.d(TAG, "Medicamento encontrado: " + medicamento.nome + 
                      " - Estoque atual: " + medicamento.estoque_atual);
                
                if (medicamento.estoque_atual > 0) {
                    // Converte dose para int (assumindo que dose é um número)
                    int doseQuantity = 1; // valor padrão
                    try {
                        if (medicamento.dose != null && !medicamento.dose.trim().isEmpty()) {
                            doseQuantity = Integer.parseInt(medicamento.dose.trim());
                        }
                    } catch (NumberFormatException e) {
                        Log.w(TAG, "Erro ao converter dose para número: " + medicamento.dose + ". Usando dose = 1");
                        doseQuantity = 1;
                    }
                    
                    // Reduz o estoque pela quantidade da dose
                    int novoEstoque = Math.max(0, medicamento.estoque_atual - doseQuantity);
                    medicamento.estoque_atual = novoEstoque;
                    Log.d(TAG, "Reduzindo estoque em " + doseQuantity + " unidade(s) para: " + medicamento.estoque_atual);
                    
                    // Salva a atualização
                    repository.save(medicamento);
                    
                    // Verifica se precisa notificar estoque baixo
                    checkAndNotifyLowStock(medicamento);
                } else {
                    Log.w(TAG, "Estoque já está zerado para " + medicamento.nome);
                    // Ainda assim verifica se precisa notificar
                    checkAndNotifyLowStock(medicamento);
                }
            } else {
                Log.e(TAG, "Medicamento não encontrado para ID: " + medicamentoId);
            }
        });
    }

    /**
     * Verifica se o estoque está baixo e envia notificação se necessário
     */
    private void checkAndNotifyLowStock(Medicamento medicamento) {
        Log.d(TAG, "Verificando estoque baixo para " + medicamento.nome + 
              " - Atual: " + medicamento.estoque_atual + ", Mínimo: " + medicamento.estoque_minimo);
        
        if (NotificationHelper.isLowStock(medicamento)) {
            Log.w(TAG, "Estoque baixo detectado para " + medicamento.nome + "!");
            NotificationHelper.sendLowStockNotification(context, medicamento);
        } else {
            Log.d(TAG, "Estoque OK para " + medicamento.nome);
            // Cancela notificação de estoque baixo se existir
            NotificationHelper.cancelLowStockNotification(context, medicamento.id);
        }
    }

    /**
     * Verifica estoque baixo para todos os medicamentos
     */
    public void checkAllMedicationsStock() {
        Log.d(TAG, "Verificando estoque de todos os medicamentos...");
        
        repository.getAllMedicamentos().observeForever(medicamentos -> {
            if (medicamentos != null) {
                Log.d(TAG, "Verificando " + medicamentos.size() + " medicamentos");
                
                for (Medicamento medicamento : medicamentos) {
                    checkAndNotifyLowStock(medicamento);
                }
            }
        });
    }

    /**
     * Atualiza o estoque de um medicamento (para reposição)
     */
    public void updateStock(int medicamentoId, int newStock) {
        Log.d(TAG, "Atualizando estoque manualmente - ID: " + medicamentoId + ", Novo estoque: " + newStock);
        
        repository.getMedicamentoById(medicamentoId, medicamento -> {
            if (medicamento != null) {
                int oldStock = medicamento.estoque_atual;
                medicamento.estoque_atual = newStock;
                repository.save(medicamento);
                
                Log.d(TAG, "Estoque atualizado para " + medicamento.nome + 
                      " - De " + oldStock + " para " + newStock);
                
                // Verifica se ainda precisa da notificação de estoque baixo
                if (!NotificationHelper.isLowStock(medicamento)) {
                    // Remove notificação de estoque baixo se não precisar mais
                    NotificationHelper.cancelLowStockNotification(context, medicamento.id);
                }
            }
        });
    }
}
