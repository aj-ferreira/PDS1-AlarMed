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
import com.example.alarmed.data.repos.MedicamentoRepository;
import com.google.android.material.button.MaterialButton;

import java.io.File;

public class MedicamentoListAdapter extends ListAdapter<Medicamento, MedicamentoListAdapter.MedicamentoViewHolder> {
    private OnItemClickListener listener;
    private OnButtonClickListener buttonListener;

    public MedicamentoListAdapter(@NonNull DiffUtil.ItemCallback<Medicamento> diffCallback) {
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
        Medicamento current = getItem(position);
        Log.d("MedicamentoListAdapter", "onBindViewHolder() - Posição: " + position + 
              ", Medicamento: " + current.nome + ", Dose: " + current.dose);
        holder.bind(current);
    }

    public Medicamento getMedicamentoAt(int position) {
        Log.d("MedicamentoListAdapter", "getMedicamentoAt() - Posição: " + position);
        return getItem(position);
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
                    Medicamento medicamento = getItem(pos);
                    listener.onItemClick(medicamento);
                }
            });

            btnTomei.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if(buttonListener != null && pos != RecyclerView.NO_POSITION){
                    Medicamento medicamento = getItem(pos);
                    buttonListener.onTomeiClick(medicamento);
                }
            });

            btnEditar.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if(buttonListener != null && pos != RecyclerView.NO_POSITION){
                    Medicamento medicamento = getItem(pos);
                    buttonListener.onEditClick(medicamento);
                }
            });

            btnExcluir.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if(buttonListener != null && pos != RecyclerView.NO_POSITION){
                    Medicamento medicamento = getItem(pos);
                    buttonListener.onDeleteClick(medicamento);
                }
            });
        }

        public void bind(Medicamento medicamento) {
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

            // Horário (placeholder - será implementado quando houver integração com horários)
            horarioItemView.setText("--:--");
            frequenciaItemView.setText("Não configurado");
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

    static class MedicamentoDiff extends DiffUtil.ItemCallback<Medicamento> {
        @Override
        public boolean areItemsTheSame(@NonNull Medicamento oldItem, @NonNull Medicamento newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Medicamento oldItem, @NonNull Medicamento newItem) {
            // Compare todos os campos para uma atualização de conteúdo mais precisa
            return oldItem.nome.equals(newItem.nome) &&
                    java.util.Objects.equals(oldItem.descricao, newItem.descricao) &&
                    java.util.Objects.equals(oldItem.dose, newItem.dose) &&
                    java.util.Objects.equals(oldItem.imagem, newItem.imagem) &&
                    oldItem.estoque_atual == newItem.estoque_atual &&
                    oldItem.estoque_minimo == newItem.estoque_minimo &&
                    java.util.Objects.equals(oldItem.tipo, newItem.tipo);
        }
    }
}
