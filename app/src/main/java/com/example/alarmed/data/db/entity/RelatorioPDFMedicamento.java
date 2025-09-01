package com.example.alarmed.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "relatorio_pdf_medicamento",
        primaryKeys = {"id_pdf", "id_medicamento"},
        foreignKeys = {
                @ForeignKey(
                        entity = RelatorioPDF.class,
                        parentColumns = "id",
                        childColumns = "id_pdf",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Medicamento.class,
                        parentColumns = "id",
                        childColumns = "id_medicamento",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                )
        },
        indices = {@Index("id_pdf"), @Index("id_medicamento")}
)
public class RelatorioPDFMedicamento {
    @ColumnInfo(name = "id_pdf")
    public int idPdf;

    @ColumnInfo(name = "id_medicamento")
    public int idMedicamento;
}
