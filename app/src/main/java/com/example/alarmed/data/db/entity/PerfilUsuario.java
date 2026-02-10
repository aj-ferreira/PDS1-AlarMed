package com.example.alarmed.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entidade que representa o perfil do usuário comum.
 * Contém informações pessoais e de saúde.
 * Como existe apenas um usuário comum, existe apenas um registro nesta tabela.
 * Segue o princípio da Responsabilidade Única.
 */
@Entity(tableName = "perfil_usuario")
public class PerfilUsuario {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    // Informações pessoais
    @ColumnInfo(name = "data_nascimento")
    public String dataNascimento; // Formato: dd/MM/yyyy

    @ColumnInfo(name = "telefone")
    public String telefone;

    @ColumnInfo(name = "endereco")
    public String endereco;

    @ColumnInfo(name = "cidade")
    public String cidade;

    @ColumnInfo(name = "estado")
    public String estado;

    @ColumnInfo(name = "cep")
    public String cep;

    // Informações de saúde
    @ColumnInfo(name = "peso")
    public Double peso; // em kg

    @ColumnInfo(name = "altura")
    public Double altura; // em cm

    @ColumnInfo(name = "tipo_sanguineo")
    public String tipoSanguineo; // A+, A-, B+, B-, AB+, AB-, O+, O-

    @ColumnInfo(name = "alergias")
    public String alergias; // Lista de alergias separadas por vírgula

    @ColumnInfo(name = "condicoes_medicas")
    public String condicoesMedicas; // Condições médicas pré-existentes

    @ColumnInfo(name = "medicamentos_continuos")
    public String medicamentosContinuos; // Medicamentos de uso contínuo

    // Contatos de emergência
    @ColumnInfo(name = "contato_emergencia_nome")
    public String contatoEmergenciaNome;

    @ColumnInfo(name = "contato_emergencia_telefone")
    public String contatoEmergenciaTelefone;

    @ColumnInfo(name = "contato_emergencia_parentesco")
    public String contatoEmergenciaParentesco;

    // Informações médicas adicionais
    @ColumnInfo(name = "nome_medico")
    public String nomeMedico;

    @ColumnInfo(name = "telefone_medico")
    public String telefoneMedico;

    @ColumnInfo(name = "plano_saude")
    public String planoSaude;

    @ColumnInfo(name = "numero_carteirinha")
    public String numeroCarteirinha;

    @ColumnInfo(name = "observacoes")
    public String observacoes;

    @ColumnInfo(name = "data_atualizacao")
    public String dataAtualizacao;

    /**
     * Construtor padrão necessário para o Room.
     */
    public PerfilUsuario() {
        this.dataAtualizacao = String.valueOf(System.currentTimeMillis());
    }

    /**
     * Calcula o IMC (Índice de Massa Corporal) se peso e altura estiverem disponíveis.
     */
    public Double calcularIMC() {
        if (peso != null && altura != null && altura > 0) {
            double alturaMetros = altura / 100.0;
            return peso / (alturaMetros * alturaMetros);
        }
        return null;
    }

    /**
     * Retorna a classificação do IMC.
     */
    public String classificacaoIMC() {
        Double imc = calcularIMC();
        if (imc == null) return "Não disponível";
        
        if (imc < 18.5) return "Abaixo do peso";
        else if (imc < 25) return "Peso normal";
        else if (imc < 30) return "Sobrepeso";
        else if (imc < 35) return "Obesidade grau I";
        else if (imc < 40) return "Obesidade grau II";
        else return "Obesidade grau III";
    }

    /**
     * Calcula a idade aproximada com base na data de nascimento.
     */
    public Integer calcularIdade() {
        if (dataNascimento == null || dataNascimento.isEmpty()) return null;
        
        try {
            String[] partes = dataNascimento.split("/");
            if (partes.length != 3) return null;
            
            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            int ano = Integer.parseInt(partes[2]);
            
            java.util.Calendar hoje = java.util.Calendar.getInstance();
            java.util.Calendar nascimento = java.util.Calendar.getInstance();
            nascimento.set(ano, mes - 1, dia);
            
            int idade = hoje.get(java.util.Calendar.YEAR) - nascimento.get(java.util.Calendar.YEAR);
            
            if (hoje.get(java.util.Calendar.DAY_OF_YEAR) < nascimento.get(java.util.Calendar.DAY_OF_YEAR)) {
                idade--;
            }
            
            return idade;
        } catch (Exception e) {
            return null;
        }
    }
}
