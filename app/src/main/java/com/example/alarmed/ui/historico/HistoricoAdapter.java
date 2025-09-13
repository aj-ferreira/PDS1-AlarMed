package com.example.alarmed.ui.historico;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmed.R;
import com.example.alarmed.data.db.relacionamentos.HistoricoComMedicamento;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoricoAdapter extends ListAdapter<HistoricoComMedicamento, HistoricoAdapter.HistoricoViewHolder> {

    private static final SimpleDateFormat INPUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat OUTPUT_FORMAT = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'às' HH:mm", new Locale("pt", "BR"));

    public HistoricoAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<HistoricoComMedicamento> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<HistoricoComMedicamento>() {
                @Override
                public boolean areItemsTheSame(@NonNull HistoricoComMedicamento oldItem, @NonNull HistoricoComMedicamento newItem) {
                    return oldItem.historico.id == newItem.historico.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull HistoricoComMedicamento oldItem, @NonNull HistoricoComMedicamento newItem) {
                    return oldItem.historico.id == newItem.historico.id &&
                           oldItem.historico.status.equals(newItem.historico.status) &&
                           oldItem.historico.data_hora.equals(newItem.historico.data_hora) &&
                           oldItem.medicamento.nome.equals(newItem.medicamento.nome);
                }
            };

    @NonNull
    @Override
    public HistoricoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historico, parent, false);
        return new HistoricoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoricoViewHolder holder, int position) {
        HistoricoComMedicamento current = getItem(position);
        holder.bind(current);
    }

    class HistoricoViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtNomeMedicamento;
        private final TextView txtStatus;
        private final TextView txtDataHora;
        private final TextView txtObservacao;
        private final ImageView iconeStatus;

        public HistoricoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeMedicamento = itemView.findViewById(R.id.txtNomeMedicamento);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtDataHora = itemView.findViewById(R.id.txtDataHora);
            txtObservacao = itemView.findViewById(R.id.txtObservacao);
            iconeStatus = itemView.findViewById(R.id.iconeStatus);
        }

        public void bind(HistoricoComMedicamento historicoComMedicamento) {
            // Nome do medicamento com dose
            String nomeCompleto = historicoComMedicamento.medicamento.nome;
            if (historicoComMedicamento.medicamento.dose != null && !historicoComMedicamento.medicamento.dose.isEmpty()) {
                nomeCompleto += " " + historicoComMedicamento.medicamento.dose;
            }
            txtNomeMedicamento.setText(nomeCompleto);

            // Status
            String status = historicoComMedicamento.historico.status;
            txtStatus.setText(status);

            // Configurar ícone e cor baseado no status
            configureStatusDisplay(status);

            // Data e hora formatada
            String dataHoraFormatada = formatarDataHora(historicoComMedicamento.historico.data_hora);
            txtDataHora.setText(dataHoraFormatada);

            // Observação (se houver)
            if (historicoComMedicamento.historico.observacao != null && 
                !historicoComMedicamento.historico.observacao.trim().isEmpty()) {
                txtObservacao.setText("Observação: " + historicoComMedicamento.historico.observacao);
                txtObservacao.setVisibility(View.VISIBLE);
            } else {
                txtObservacao.setVisibility(View.GONE);
            }
        }

        private void configureStatusDisplay(String status) {
            switch (status.toLowerCase()) {
                case "tomado":
                    iconeStatus.setImageResource(R.drawable.ic_check_circle);
                    iconeStatus.setColorFilter(itemView.getContext().getColor(R.color.green_500));
                    txtStatus.setTextColor(itemView.getContext().getColor(R.color.green_600));
                    break;
                case "ignorado":
                case "pulado":
                    iconeStatus.setImageResource(R.drawable.ic_cancel);
                    iconeStatus.setColorFilter(itemView.getContext().getColor(R.color.red));
                    txtStatus.setTextColor(itemView.getContext().getColor(R.color.red));
                    break;
                case "atrasado":
                    iconeStatus.setImageResource(R.drawable.ic_warning);
                    iconeStatus.setColorFilter(itemView.getContext().getColor(R.color.yellow_600));
                    txtStatus.setTextColor(itemView.getContext().getColor(R.color.yellow_600));
                    break;
                default:
                    iconeStatus.setImageResource(R.drawable.ic_historico);
                    iconeStatus.setColorFilter(itemView.getContext().getColor(R.color.gray_500));
                    txtStatus.setTextColor(itemView.getContext().getColor(R.color.gray_600));
                    break;
            }
        }

        private String formatarDataHora(String dataHora) {
            try {
                Date date = INPUT_FORMAT.parse(dataHora);
                if (date != null) {
                    return OUTPUT_FORMAT.format(date);
                }
            } catch (ParseException e) {
                // Se não conseguir parsear, retorna a string original
                return dataHora;
            }
            return dataHora;
        }
    }
}
