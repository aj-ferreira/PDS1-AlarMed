package com.example.alarmed.ui.medicamentos.list;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmed.R;
import com.example.alarmed.data.db.entity.Medicamento;
import com.example.alarmed.data.db.entity.Horario;
import com.example.alarmed.data.db.relacionamentos.MedicamentoComHorarios;
import com.example.alarmed.data.repos.MedicamentoRepository;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Locale;

public class MedicamentoListAdapter extends ListAdapter<MedicamentoComHorarios, MedicamentoListAdapter.MedicamentoViewHolder> {
    private OnItemClickListener listener;
    private OnButtonClickListener buttonListener;

    public MedicamentoListAdapter(@NonNull DiffUtil.ItemCallback<MedicamentoComHorarios> diffCallback) {
        super(diffCallback);
        Log.d("MedicamentoListAdapter", "Adapter criado");
    }

    @NonNull
    @Override
    public MedicamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("MedicamentoListAdapter", "onCreateViewHolder() chamado");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_medicamento, parent, false);
        return new MedicamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicamentoViewHolder holder, int position) {
        MedicamentoComHorarios current = getItem(position);
        Log.d("MedicamentoListAdapter", "onBindViewHolder() - Posição: " + position + 
              ", Medicamento: " + current.medicamento.nome + ", Dose: " + current.medicamento.dose);
        holder.bind(current);
    }

    public Medicamento getMedicamentoAt(int position) {
        Log.d("MedicamentoListAdapter", "getMedicamentoAt() - Posição: " + position);
        return getItem(position).medicamento;
    }

    class MedicamentoViewHolder extends RecyclerView.ViewHolder {
        private final TextView nomeItemView;
        private final TextView descricaoItemView;
        private final TextView estoqueItemView;
        private final TextView horarioItemView;
        private final TextView frequenciaItemView;
        private final TextView badgeEstoqueBaixo;
        private final TextView badgeTipo;
        private final ImageView imageMedicamento;
        private final ImageView iconMedicamento;
        private final View placeholderContainer;
        private final MaterialButton btnTomei;
        private final ImageButton btnEditar;
        private final ImageButton btnExcluir;

        private MedicamentoViewHolder(View itemView) {
            super(itemView);
            nomeItemView = itemView.findViewById(R.id.textViewNome);
            descricaoItemView = itemView.findViewById(R.id.textViewDescricao);
            estoqueItemView = itemView.findViewById(R.id.textViewEstoque);
            horarioItemView = itemView.findViewById(R.id.textViewHorario);
            frequenciaItemView = itemView.findViewById(R.id.textViewFrequencia);
            badgeEstoqueBaixo = itemView.findViewById(R.id.badgeEstoqueBaixo);
            badgeTipo = itemView.findViewById(R.id.badgeTipo);
            imageMedicamento = itemView.findViewById(R.id.imageMedicamento);
            iconMedicamento = itemView.findViewById(R.id.iconMedicamento);
            placeholderContainer = iconMedicamento.getParent() instanceof View ? (View) iconMedicamento.getParent() : null;
            btnTomei = itemView.findViewById(R.id.btnTomei);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);

            // Click listeners
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if(listener != null && pos != RecyclerView.NO_POSITION){
                    Medicamento medicamento = getItem(pos).medicamento;
                    listener.onItemClick(medicamento);
                }
            });

            btnTomei.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if(buttonListener != null && pos != RecyclerView.NO_POSITION){
                    Medicamento medicamento = getItem(pos).medicamento;
                    buttonListener.onTomeiClick(medicamento);
                }
            });

            btnEditar.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if(buttonListener != null && pos != RecyclerView.NO_POSITION){
                    Medicamento medicamento = getItem(pos).medicamento;
                    buttonListener.onEditClick(medicamento);
                }
            });

            btnExcluir.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if(buttonListener != null && pos != RecyclerView.NO_POSITION){
                    Medicamento medicamento = getItem(pos).medicamento;
                    buttonListener.onDeleteClick(medicamento);
                }
            });
        }

        public void bind(MedicamentoComHorarios medicamentoComHorarios) {
            Medicamento medicamento = medicamentoComHorarios.medicamento;
            List<Horario> horarios = medicamentoComHorarios.horarios;
            
            // Nome do medicamento
            nomeItemView.setText(medicamento.nome);
            
            // Descrição com dose
            String descricaoCompleta = medicamento.descricao != null ? medicamento.descricao : "";
            if (medicamento.dose != null && !medicamento.dose.trim().isEmpty()) {
                descricaoCompleta += " • " + medicamento.dose + " unidade(s)";
            }
            descricaoItemView.setText(descricaoCompleta);

            // Estoque
            estoqueItemView.setText(String.valueOf(medicamento.estoque_atual));

            // Badge estoque baixo
            boolean isLowStock = MedicamentoRepository.isLowStock(medicamento);
            badgeEstoqueBaixo.setVisibility(isLowStock ? View.VISIBLE : View.GONE);

            // Badge tipo
            if (medicamento.tipo != null && !medicamento.tipo.trim().isEmpty()) {
                badgeTipo.setText(medicamento.tipo);
                badgeTipo.setVisibility(View.VISIBLE);
            } else {
                badgeTipo.setVisibility(View.GONE);
            }

            // Imagem do medicamento - exibe apenas se cadastrada e válida
            boolean hasValidImage = false;
            if (medicamento.imagem != null && !medicamento.imagem.trim().isEmpty()) {
                File imageFile = new File(medicamento.imagem);
                if (imageFile.exists()) {
                    // Tem imagem válida - exibe a imagem e esconde o container do ícone
                    imageMedicamento.setImageURI(Uri.fromFile(imageFile));
                    imageMedicamento.setVisibility(View.VISIBLE);
                    if (placeholderContainer != null) {
                        placeholderContainer.setVisibility(View.GONE);
                    }
                    hasValidImage = true;
                    Log.d("MedicamentoListAdapter", "Exibindo imagem para: " + medicamento.nome);
                }
            }
            
            if (!hasValidImage) {
                // Não tem imagem válida - esconde a imagem e exibe o container do ícone placeholder
                imageMedicamento.setVisibility(View.GONE);
                if (placeholderContainer != null) {
                    placeholderContainer.setVisibility(View.VISIBLE);
                }
                Log.d("MedicamentoListAdapter", "Exibindo ícone placeholder para: " + medicamento.nome);
            }

            // Horário e frequência baseados nos horários cadastrados
            if (horarios != null && !horarios.isEmpty()) {
                Horario horario = horarios.get(0); // Pega o primeiro horário
                
                // Calcula e exibe o PRÓXIMO horário
                String proximoHorario = calcularProximoHorario(horario);
                horarioItemView.setText(proximoHorario);
                
                // Exibe a frequência
                if (horario.intervalo > 0) {
                    String frequenciaText = "A cada " + horario.intervalo + " horas";
                
                    if (horario.repetir_dias != null && !horario.repetir_dias.trim().isEmpty() 
                        && !horario.repetir_dias.equals("TODOS")) {
                        frequenciaText += " • " + horario.repetir_dias;
                    }
                    frequenciaItemView.setText(frequenciaText);
                } else {
                    frequenciaItemView.setText("Uma vez ao dia");
                }
            } else {
                // Não tem horários configurados
                horarioItemView.setText("--:--");
                frequenciaItemView.setText("Não configurado");
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Medicamento medicamento);
    }

    public interface OnButtonClickListener {
        void onTomeiClick(Medicamento medicamento);
        void onEditClick(Medicamento medicamento);
        void onDeleteClick(Medicamento medicamento);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        Log.d("MedicamentoListAdapter", "setOnItemClickListener() chamado");
        this.listener = listener;
    }

    public void setOnButtonClickListener(OnButtonClickListener buttonListener) {
        this.buttonListener = buttonListener;
    }

    /**
     * Calcula o próximo horário em que o medicamento deve ser tomado
     * baseado no horário inicial e no intervalo configurado.
     */
    private String calcularProximoHorario(Horario horario) {
        if (horario == null || horario.horario_inicial == null || horario.horario_inicial.trim().isEmpty()) {
            return "--:--";
        }

        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Calendar agora = Calendar.getInstance();
            Calendar proximoHorario = Calendar.getInstance();
            
            // Parse do horário inicial
            Date horarioInicial = timeFormat.parse(horario.horario_inicial);
            proximoHorario.setTime(horarioInicial);
            
            // Ajusta para o dia de hoje
            proximoHorario.set(Calendar.YEAR, agora.get(Calendar.YEAR));
            proximoHorario.set(Calendar.MONTH, agora.get(Calendar.MONTH));
            proximoHorario.set(Calendar.DAY_OF_MONTH, agora.get(Calendar.DAY_OF_MONTH));
            
            // Se o horário inicial já passou hoje, calcular próximos horários
            while (proximoHorario.before(agora) || proximoHorario.equals(agora)) {
                if (horario.intervalo > 0) {
                    // Adiciona o intervalo em horas
                    proximoHorario.add(Calendar.HOUR_OF_DAY, horario.intervalo);
                } else {
                    // Se não tem intervalo, é uma vez por dia - próximo dia no mesmo horário
                    proximoHorario.add(Calendar.DAY_OF_MONTH, 1);
                    // Volta para o horário original
                    Date horarioOriginal = timeFormat.parse(horario.horario_inicial);
                    Calendar temp = Calendar.getInstance();
                    temp.setTime(horarioOriginal);
                    proximoHorario.set(Calendar.HOUR_OF_DAY, temp.get(Calendar.HOUR_OF_DAY));
                    proximoHorario.set(Calendar.MINUTE, temp.get(Calendar.MINUTE));
                    break;
                }
            }
            
            // Verifica se precisa considerar os dias da semana
            if (horario.repetir_dias != null && !horario.repetir_dias.trim().isEmpty() 
                && !horario.repetir_dias.equals("TODOS")) {
                
                // Se tem dias específicos, encontra o próximo dia válido
                String[] diasSemana = horario.repetir_dias.split(",");
                boolean diaValido = false;
                int tentativas = 0;
                
                while (!diaValido && tentativas < 7) {
                    int diaSemana = proximoHorario.get(Calendar.DAY_OF_WEEK);
                    String diaAtual = getDiaSemanaAbrev(diaSemana);
                    
                    for (String dia : diasSemana) {
                        if (dia.trim().equals(diaAtual)) {
                            diaValido = true;
                            break;
                        }
                    }
                    
                    if (!diaValido) {
                        proximoHorario.add(Calendar.DAY_OF_MONTH, 1);
                        // Ajusta para o horário original no próximo dia válido
                        Date horarioOriginal = timeFormat.parse(horario.horario_inicial);
                        Calendar temp = Calendar.getInstance();
                        temp.setTime(horarioOriginal);
                        proximoHorario.set(Calendar.HOUR_OF_DAY, temp.get(Calendar.HOUR_OF_DAY));
                        proximoHorario.set(Calendar.MINUTE, temp.get(Calendar.MINUTE));
                    }
                    tentativas++;
                }
            }
            
            return timeFormat.format(proximoHorario.getTime());
            
        } catch (ParseException e) {
            Log.e("MedicamentoListAdapter", "Erro ao calcular próximo horário: " + e.getMessage());
            return horario.horario_inicial; // Retorna o horário original em caso de erro
        }
    }
    
    /**
     * Converte o dia da semana do Calendar para abreviação
     */
    private String getDiaSemanaAbrev(int diaSemana) {
        switch (diaSemana) {
            case Calendar.SUNDAY: return "DOM";
            case Calendar.MONDAY: return "SEG";
            case Calendar.TUESDAY: return "TER";
            case Calendar.WEDNESDAY: return "QUA";
            case Calendar.THURSDAY: return "QUI";
            case Calendar.FRIDAY: return "SEX";
            case Calendar.SATURDAY: return "SAB";
            default: return "";
        }
    }

    static class MedicamentoDiff extends DiffUtil.ItemCallback<MedicamentoComHorarios> {
        @Override
        public boolean areItemsTheSame(@NonNull MedicamentoComHorarios oldItem, @NonNull MedicamentoComHorarios newItem) {
            return oldItem.medicamento.id == newItem.medicamento.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MedicamentoComHorarios oldItem, @NonNull MedicamentoComHorarios newItem) {
            // Compare o medicamento e os horários
            Medicamento oldMed = oldItem.medicamento;
            Medicamento newMed = newItem.medicamento;
            
            boolean medicamentoEqual = oldMed.nome.equals(newMed.nome) &&
                    java.util.Objects.equals(oldMed.descricao, newMed.descricao) &&
                    java.util.Objects.equals(oldMed.dose, newMed.dose) &&
                    java.util.Objects.equals(oldMed.imagem, newMed.imagem) &&
                    oldMed.estoque_atual == newMed.estoque_atual &&
                    oldMed.estoque_minimo == newMed.estoque_minimo &&
                    java.util.Objects.equals(oldMed.tipo, newMed.tipo);
            
            // Compare os horários
            boolean horariosEqual = java.util.Objects.equals(oldItem.horarios, newItem.horarios);
            
            return medicamentoEqual && horariosEqual;
        }
    }
}
