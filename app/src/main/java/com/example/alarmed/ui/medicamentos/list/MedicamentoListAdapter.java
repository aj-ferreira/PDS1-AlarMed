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

    static class MedicamentoViewHolder extends RecyclerView.ViewHolder {
        private final TextView nomeItemView;
        private final TextView descricaoItemView;

        private MedicamentoViewHolder(View itemView) {
            super(itemView);
            nomeItemView = itemView.findViewById(R.id.textViewNome);
            descricaoItemView = itemView.findViewById(R.id.textViewDescricao);
        }

        public void bind(String nome, String descricao) {
            nomeItemView.setText(nome);
            descricaoItemView.setText(descricao);
        }
    }

    static class MedicamentoDiff extends DiffUtil.ItemCallback<Medicamento> {
        @Override
        public boolean areItemsTheSame(@NonNull Medicamento oldItem, @NonNull Medicamento newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Medicamento oldItem, @NonNull Medicamento newItem) {
            return oldItem.nome.equals(newItem.nome) &&
                    oldItem.descricao.equals(newItem.descricao);
        }
    }
}
