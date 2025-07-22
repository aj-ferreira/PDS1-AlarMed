package com.example.alarmed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.alarmed.daos.MedicamentoDao;
import com.example.alarmed.model.Medicamento;
import com.example.alarmed.persistencia.AppDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
@RunWith(AndroidJUnit4.class)
public class dbTest {
//    private AppDatabase db=null;
//    private MedicamentoDao medicamentoDao;
//
//    @Before
//    public void criarBancoEmMemoria() {
//        Context context = ApplicationProvider.getApplicationContext();
//        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
//                .allowMainThreadQueries() // permitido apenas para testes
//                .build();
//        medicamentoDao = db.medicamentoDao();
//    }
//
//    @After
//    public void fecharBanco() {
//        if (db != null) {
//            db.close();
//        }
//    }
//
//    @Test
//    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);
//    }
//    @Test
//    public void testInserirBuscarDeletarMedicamento() {
//        Medicamento medicamento = new Medicamento();
//        medicamento.nome = "Paracetamol";
//        medicamento.descricao = "Analgésico";
//        medicamento.estoque_atual = 10;
//        medicamento.estoque_minimo = 2;
//        medicamento.tipo = "Comprimido";
//
//        // Inserir
//        medicamentoDao.inserir(medicamento);
//
//        // Buscar
//        List<Medicamento> lista = medicamentoDao.listarTodos();
//        assertEquals(1, lista.size());
//        assertEquals("Paracetamol", lista.get(0).nome);
//
//        // Atualizar
//        Medicamento m = lista.get(0);
//        m.estoque_atual = 20;
//        medicamentoDao.atualizar(m);
//
//        Medicamento atualizado = medicamentoDao.listarTodos().get(0);
//        assertEquals(20, atualizado.estoque_atual);
//
//        // Deletar
//        medicamentoDao.deletar(atualizado);
//        assertTrue(medicamentoDao.listarTodos().isEmpty());
//    }
}
