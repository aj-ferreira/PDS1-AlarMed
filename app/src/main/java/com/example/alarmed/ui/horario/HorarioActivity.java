package com.example.alarmed.ui.horario;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.alarmed.R;
import com.example.alarmed.data.db.entity.Horario;
import com.example.alarmed.ui.medicamentos.list.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class HorarioActivity extends AppCompatActivity {
    public static final String EXTRA_MEDICAMENTO_ID = "EXTRA_MEDICAMENTO_ID";
    public static final String EXTRA_MEDICAMENTO_NOME = "EXTRA_MEDICAMENTO_NOME";

    private HorarioViewModel mHorarioViewModel;
    private int medicamentoId;
    private TextView textHorarioConfigurado;
    private Horario mHorarioAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HorarioActivity", "onCreate() iniciado");
        setContentView(R.layout.activity_horario);

        // Configurar ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        medicamentoId = getIntent().getIntExtra(EXTRA_MEDICAMENTO_ID, -1);
        String medicamentoNome = getIntent().getStringExtra(EXTRA_MEDICAMENTO_NOME);

        Log.d("HorarioActivity", "Parâmetros recebidos - ID: " + medicamentoId + ", Nome: " + medicamentoNome);

        if (medicamentoId == -1) {
            Log.e("HorarioActivity", "ID do medicamento inválido: " + medicamentoId);
            Toast.makeText(this, "Erro: ID do medicamento não encontrado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("HorarioActivity", "Inicializando views...");
        TextView textMedicamentoNome = findViewById(R.id.text_medicamento_nome);
        textHorarioConfigurado = findViewById(R.id.text_horario_configurado);
        textMedicamentoNome.setText("Horários para " + medicamentoNome);

        Log.d("HorarioActivity", "Criando ViewModel...");
        HorarioViewModelFactory factory = new HorarioViewModelFactory(getApplication(), medicamentoId);
        mHorarioViewModel = new ViewModelProvider(this, factory).get(HorarioViewModel.class);

        // Observa a regra de horário única
        Log.d("HorarioActivity", "Configurando observador de horário...");
        mHorarioViewModel.getHorario().observe(this, horario -> {
            mHorarioAtual = horario;
            if (horario != null) {
                Log.d("HorarioActivity", "Horário encontrado - Inicial: " + horario.horario_inicial + 
                      ", Intervalo: " + horario.intervalo);
                String texto = String.format(Locale.getDefault(),
                        "Primeira dose às %s, a cada %d horas.",
                        horario.horario_inicial, horario.intervalo);
                textHorarioConfigurado.setText(texto);
            } else {
                Log.d("HorarioActivity", "Nenhum horário definido");
                textHorarioConfigurado.setText("Nenhum horário definido.");
            }
        });

        Log.d("HorarioActivity", "Configurando FAB...");
        FloatingActionButton fab = findViewById(R.id.fab_add_horario);
        fab.setOnClickListener(view -> {
            Log.d("HorarioActivity", "FAB clicado - abrindo dialog de configuração");
            showSetIntervaloDialog();
        });
        
        Log.d("HorarioActivity", "onCreate() finalizado");
    }

    private void showSetIntervaloDialog() {
        Log.d("HorarioActivity", "showSetIntervaloDialog() iniciado");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_set_intervalo, null);
        builder.setView(dialogView);

        final TimePicker timePicker = dialogView.findViewById(R.id.time_picker_inicio);
        final EditText editIntervalo = dialogView.findViewById(R.id.edit_intervalo_horas);
        timePicker.setIs24HourView(true);

        // Preenche o diálogo com os dados existentes, se houver
        if (mHorarioAtual != null) {
            Log.d("HorarioActivity", "Preenchendo dialog com dados existentes - Horário: " + 
                  mHorarioAtual.horario_inicial + ", Intervalo: " + mHorarioAtual.intervalo);
            String[] timeParts = mHorarioAtual.horario_inicial.split(":");
            timePicker.setHour(Integer.parseInt(timeParts[0]));
            timePicker.setMinute(Integer.parseInt(timeParts[1]));
            editIntervalo.setText(String.valueOf(mHorarioAtual.intervalo));
        } else {
            Log.d("HorarioActivity", "Dialog aberto para novo horário");
        }

        builder.setTitle("Definir Agendamento")
                .setPositiveButton("Salvar", (dialog, id) -> {
                    Log.d("HorarioActivity", "Usuário clicou em Salvar");
                    String intervaloStr = editIntervalo.getText().toString();
                    if (intervaloStr.isEmpty()) {
                        Log.w("HorarioActivity", "Intervalo não informado");
                        Toast.makeText(this, "Por favor, insira um intervalo.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();
                    int intervalo = Integer.parseInt(intervaloStr);

                    String horarioInicialStr = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

                    Log.d("HorarioActivity", "Dados coletados - Horário inicial: " + horarioInicialStr + 
                          ", Intervalo: " + intervalo + " horas");

                    Horario horarioParaSalvar = mHorarioAtual;
                    if (horarioParaSalvar == null) {
                        Log.d("HorarioActivity", "Criando novo horário");
                        horarioParaSalvar = new Horario();
                        horarioParaSalvar.id_medicamento = medicamentoId;
                    } else {
                        Log.d("HorarioActivity", "Atualizando horário existente");
                    }

                    horarioParaSalvar.horario_inicial = horarioInicialStr;
                    horarioParaSalvar.intervalo = intervalo;
                    
                    // Garantir que campos opcionais tenham valores padrão
                    if (horarioParaSalvar.repetir_dias == null) {
                        horarioParaSalvar.repetir_dias = "TODOS";
                    }
                    if (horarioParaSalvar.dataFim == null) {
                        horarioParaSalvar.dataFim = "";
                    }

                    Log.d("HorarioActivity", "Salvando horário com todos os campos - ID medicamento: " + 
                          horarioParaSalvar.id_medicamento + ", Horário: " + horarioParaSalvar.horario_inicial + 
                          ", Intervalo: " + horarioParaSalvar.intervalo + 
                          ", Repetir dias: " + horarioParaSalvar.repetir_dias);

                    Log.d("HorarioActivity", "Salvando horário...");
                    mHorarioViewModel.save(horarioParaSalvar);
                    
                    // Exibe mensagem de sucesso
                    Toast.makeText(HorarioActivity.this, "Horário configurado com sucesso!", Toast.LENGTH_SHORT).show();
                    
                    // Redireciona para a tela principal após salvar
                    Log.d("HorarioActivity", "Redirecionando para MainActivity...");
                    Intent intent = new Intent(HorarioActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish(); // Fecha a HorarioActivity
                })
                .setNegativeButton("Cancelar", (dialog, id) -> {
                    Log.d("HorarioActivity", "Usuário cancelou configuração");
                    dialog.cancel();
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Log.d("HorarioActivity", "Dialog exibido");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
