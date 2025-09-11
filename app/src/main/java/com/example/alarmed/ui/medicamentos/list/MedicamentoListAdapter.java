package com.example.alarmed.ui.medicamentos.list;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmed.R;
import com.example.alarmed.data.db.entity.Medicamento;

public class MedicamentoListAdapter  extends ListAdapter<Medicamento, MedicamentoListAdapter.MedicamentoViewHolder> {
    private OnItemClickListener listener;
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
        String descricaoCompleta = current.descricao;
        if (current.dose != null && !current.dose.trim().isEmpty()) {
            descricaoCompleta = current.descricao + " • Dose: " + current.dose;
        }
        holder.bind(current.nome, descricaoCompleta);
    }

    public Medicamento getMedicamentoAt(int position) {
        Log.d("MedicamentoListAdapter", "getMedicamentoAt() - Posição: " + position);
        return getItem(position);
    }

     class MedicamentoViewHolder extends RecyclerView.ViewHolder {
        private final TextView nomeItemView;
        private final TextView descricaoItemView;

        private MedicamentoViewHolder(View itemView) {
            super(itemView);
            nomeItemView = itemView.findViewById(R.id.textViewNome);
            descricaoItemView = itemView.findViewById(R.id.textViewDescricao);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                Log.d("MedicamentoListAdapter", "Item clicado - Posição: " + pos);
                if(listener != null && pos != RecyclerView.NO_POSITION){
                    Medicamento medicamento = getItem(pos);
                    Log.d("MedicamentoListAdapter", "Notificando clique do medicamento: " + medicamento.nome);
                    listener.onItemClick(medicamento);
                }
            });
        }

        public void bind(String nome, String descricao) {
            Log.d("MedicamentoListAdapter", "bind() - Nome: " + nome + ", Descrição: " + descricao);
            nomeItemView.setText(nome);
            descricaoItemView.setText(descricao);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Medicamento medicamento);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        Log.d("MedicamentoListAdapter", "setOnItemClickListener() chamado");
        this.listener = listener;
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
