package com.example.alarmed;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

import com.example.alarmed.data.db.daos.MedicamentoDao;
import com.example.alarmed.data.db.entity.Medicamento;
import com.example.alarmed.data.db.AppDatabase;

/**
 * Classe de teste instrumentado para o MedicamentoDao.
 * Estes testes rodam em um dispositivo ou emulador Android.
 */
@RunWith(AndroidJUnit4.class)
public class MedicamentoDaoTest {
    // Esta regra executa cada tarefa de forma síncrona, o que é útil para testes de LiveData.
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MedicamentoDao medicamentoDao;
    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        // Usamos um banco de dados em memória para que os dados não persistam entre os testes.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                // Permite que as queries sejam executadas na thread principal (apenas para testes).
                .allowMainThreadQueries()
                .build();
        medicamentoDao = db.medicamentoDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void insertAndGetMedicamento() throws Exception {
        // Arrange: Cria um novo medicamento
        Medicamento medicamento = new Medicamento();
        medicamento.nome = "Paracetamol";
        medicamento.descricao = "Analgésico e antitérmico";

        // Act: Insere o medicamento no banco de dados
        medicamentoDao.insertMedicamento(medicamento);

        // Assert: Busca o medicamento pelo nome e verifica se não é nulo
        List<Medicamento> allMedicamentos = LiveDataTestUtil.getOrAwaitValue(medicamentoDao.getAllMedicamentos());
        assertEquals(allMedicamentos.get(0).nome, medicamento.nome);
    }

    @Test
    public void getAllMedicamentos() throws Exception {
        // Arrange: Cria e insere dois medicamentos
        Medicamento med1 = new Medicamento();
        med1.nome = "Ibuprofeno";
        Medicamento med2 = new Medicamento();
        med2.nome = "Dipirona";

        medicamentoDao.insertMedicamento(med1);
        medicamentoDao.insertMedicamento(med2);

        // Act: Busca todos os medicamentos
        List<Medicamento> allMedicamentos = LiveDataTestUtil.getOrAwaitValue(medicamentoDao.getAllMedicamentos());

        // Assert: Verifica se a lista contém os dois medicamentos
        assertEquals(2, allMedicamentos.size());
    }

    @Test
    public void updateMedicamento() throws Exception {
        // Arrange: Insere um medicamento
        Medicamento medicamento = new Medicamento();
        medicamento.nome = "Aspirina";
        medicamento.descricao = "Descrição Original";
        medicamentoDao.insertMedicamento(medicamento);

        // Act: Pega o medicamento inserido, atualiza a descrição e o salva novamente
        List<Medicamento> allMeds = LiveDataTestUtil.getOrAwaitValue(medicamentoDao.getAllMedicamentos());
        Medicamento medToUpdate = allMeds.get(0);
        medToUpdate.descricao = "Descrição Atualizada";
        medicamentoDao.insertMedicamento(medToUpdate); // O OnConflictStrategy.REPLACE funciona como um update

        // Assert: Busca o medicamento pelo ID e verifica se a descrição foi atualizada
        Medicamento updatedMed = LiveDataTestUtil.getOrAwaitValue(medicamentoDao.getMedicamentoById(medToUpdate.id));
        assertEquals("Descrição Atualizada", updatedMed.descricao);
    }

    @Test
    public void deleteMedicamento() throws Exception {
        // Arrange: Insere um medicamento
        Medicamento medicamento = new Medicamento();
        medicamento.nome = "Omeprazol";
        medicamentoDao.insertMedicamento(medicamento);

        // Act: Pega o medicamento inserido e o deleta
        List<Medicamento> allMeds = LiveDataTestUtil.getOrAwaitValue(medicamentoDao.getAllMedicamentos());
        Medicamento medToDelete = allMeds.get(0);
        medicamentoDao.deleteMedicamentoById(medToDelete.id);

        // Assert: Busca todos os medicamentos e verifica se a lista está vazia
        List<Medicamento> finalList = LiveDataTestUtil.getOrAwaitValue(medicamentoDao.getAllMedicamentos());
        assertTrue(finalList.isEmpty());
    }

}

/**
 * Classe utilitária para obter o valor de um LiveData em testes de forma síncrona.
 */
class LiveDataTestUtil {
    public static <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        // Não espera mais de 2 segundos pelo dado do LiveData
        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new RuntimeException("LiveData value was never set.");
        }
        //noinspection unchecked
        return (T) data[0];
    }
}
