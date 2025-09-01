package com.example.alarmed.ui.medicamentos.list;

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
    }

    @NonNull
    @Override
    public MedicamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_medicamento, parent, false);
        return new MedicamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicamentoViewHolder holder, int position) {
        Medicamento current = getItem(position);
        holder.bind(current.nome, current.descricao);
    }

    public Medicamento getMedicamentoAt(int position) {
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
                if(listener != null && pos != RecyclerView.NO_POSITION){
                    listener.onItemClick(getItem(pos));
                }
            });
        }

        public void bind(String nome, String descricao) {
            nomeItemView.setText(nome);
            descricaoItemView.setText(descricao);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Medicamento medicamento);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
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
                    java.util.Objects.equals(oldItem.imagem, newItem.imagem) &&
                    oldItem.estoque_atual == newItem.estoque_atual &&
                    oldItem.estoque_minimo == newItem.estoque_minimo &&
                    java.util.Objects.equals(oldItem.tipo, newItem.tipo);
        }
    }
}
