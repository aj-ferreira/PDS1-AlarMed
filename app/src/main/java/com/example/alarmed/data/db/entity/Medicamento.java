package com.example.alarmed.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medicamento")
public class Medicamento {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "nome")
    public String nome;

    @ColumnInfo(name = "descricao")
    public String descricao;

    @ColumnInfo(name = "imagem")
    public String imagem;

    @ColumnInfo(name = "estoque_atual")
    public int estoque_atual;

    @ColumnInfo(name = "estoque_minimo")
    public int estoque_minimo;

    @ColumnInfo(name = "tipo")
    public String tipo;
}
