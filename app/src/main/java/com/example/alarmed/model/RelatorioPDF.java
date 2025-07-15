package com.example.alarmed.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RelatorioPDF {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String data_criacao;
    public String caminho_arquivo;
    public String descricao;
}
