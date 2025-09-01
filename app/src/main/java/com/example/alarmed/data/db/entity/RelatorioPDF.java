package com.example.alarmed.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "relatorio_pdf")
public class RelatorioPDF {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "data_criacao") // Formato ISO 8601
    public String dataCriacao;

    @ColumnInfo(name = "caminho_arquivo")
    public String caminhoArquivo;

    @ColumnInfo(name = "descricao")
    public String descricao;
}
