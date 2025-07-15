package com.example.alarmed.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Medicamento {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String nome;

    public String descricao;
    public String imagem;
    public int estoque_atual;
    public int estoque_minimo;
    public String tipo;
}
