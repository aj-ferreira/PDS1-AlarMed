# 💊 AlarMed - Gerenciador de Medicamentos

**AlarMed** é um aplicativo Android desenvolvido para ajudar usuários a gerenciar seus medicamentos de forma eficiente, com lembretes automáticos, controle de estoque e histórico completo de uso.

## 📱 Sobre o Aplicativo

### Funcionalidades Principais

- **📋 Cadastro de Medicamentos**: Registre medicamentos com nome, tipo, dose e informações de estoque
- **⏰ Agendamento de Horários**: Configure horários de tomada com intervalo personalizado
- **🔔 Lembretes Automáticos**: Notificações precisas para nunca esquecer um medicamento
- **📊 Controle de Estoque**: Monitore automaticamente o estoque atual e receba alertas quando estiver baixo
- **📈 Histórico Completo**: Acompanhe todo o histórico de uso dos medicamentos
- **📄 Relatórios Semanais**: Gere relatórios detalhados da rotina medicamentosa dos próximos 7 dias

### Tecnologias Utilizadas

- **Android SDK** (Min: API 24 / Android 7.0, Target: API 34 / Android 14)
- **Room Database** para persistência local
- **MVVM Architecture** com LiveData
- **AlarmManager** para notificações precisas
- **Material Design** para interface moderna
- **FileProvider** para compartilhamento seguro de arquivos

### Compatibilidade

- **SDK Mínimo**: API 24 (Android 7.0 Nougat)
- **SDK Target**: API 34 (Android 14)
- **SDK de Compilação**: API 34
- **Versão Java**: 17
- **Dispositivos suportados**: Smartphones e tablets Android com API 24+

## 🚀 Como Clonar e Executar

### Pré-requisitos

- **Android Studio** (versão 4.0 ou superior)
- **JDK 17** ou superior
- **Android SDK** (API 24 ou superior)
- **Git** instalado no sistema

### Passo a Passo

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/aj-ferreira/PDS1-AlarMed.git
   ```

2. **Abra o projeto no Android Studio:**
   - Abra o Android Studio
   - Clique em "Open an Existing Project"
   - Navegue até a pasta clonada e selecione o projeto
   - Aguarde o Gradle sincronizar as dependências

3. **Configure o dispositivo:**
   - **Dispositivo físico**: Ative o modo desenvolvedor e depuração USB (Android 7.0+ / API 24+)
   - **Emulador**: Crie um AVD com API de 24 a 35 no AVD Manager

4. **Execute o aplicativo:**
   - Clique no botão "Run" (▶️)
   - Selecione o dispositivo de destino
   - Aguarde a instalação e execução

### Configurações Adicionais

#### Permissões Necessárias
O app solicita automaticamente as seguintes permissões:
- `POST_NOTIFICATIONS` - Para exibir lembretes
- `SCHEDULE_EXACT_ALARM` - Para alarmes precisos
- `USE_EXACT_ALARM` - Para agendamento exato

#### Estrutura do Projeto
```
app/
├── src/main/java/com/example/alarmed/
│   ├── alarm/           # Sistema de alarmes e notificações
│   ├── data/            # Banco de dados e repositórios
│   ├── ui/              # Interfaces de usuário (Activities/ViewModels)
│   ├── util/            # Utilitários e helpers
│   └── MainActivity.java
├── src/main/res/        # Recursos (layouts, strings, etc.)
└── build.gradle         # Configurações do módulo
```

## 🎯 Como Usar o Aplicativo

### 1. Cadastrar Medicamento
- Toque no botão "+" para adicionar um novo medicamento
- Preencha: nome, tipo, dose, estoque atual e estoque mínimo
- Salve para prosseguir para configuração de horários

### 2. Configurar Horários
- Defina o horário inicial (ex: 08:00)
- Configure o intervalo entre doses (ex: 8 horas)
- Opcionalmente, defina data de fim do tratamento
- Salve para ativar os lembretes

### 3. Gerenciar Medicamentos
- **Tomar medicamento**: Toque em "Tomei" para registrar e reagendar
- **Ver histórico**: Use o botão "Ver Histórico" para acompanhar o uso
- **Gerar relatório**: Clique em "Gerar PDF" para obter cronograma semanal
- **Editar**: Toque no medicamento para modificar informações

### 4. Controle de Estoque
- O estoque é reduzido automaticamente a cada dose tomada
- Receba notificações quando o estoque estiver baixo
- Monitore o status na tela principal

## 🛠️ Desenvolvimento

### Arquitetura
O projeto segue o padrão **MVVM (Model-View-ViewModel)**:
- **Model**: Entidades Room e repositórios de dados
- **View**: Activities e layouts XML
- **ViewModel**: Lógica de negócio e gerenciamento de estado

### Banco de Dados
Utiliza **Room** com as seguintes entidades principais:
- `Medicamento` - Informações dos medicamentos
- `Horario` - Regras de agendamento
- `HistoricoUso` - Registro de uso dos medicamentos

### Sistema de Alarmes
- **AlarmManager** para agendamento preciso
- **AlarmReceiver** para processar notificações
- **AlarmScheduler** para calcular próximos horários

## 📄 Licença

Este projeto foi desenvolvido como parte da disciplina de Prática em Desenvolvimento de Software I.

## 🐛 Documentação de Erros e Soluções

Durante o desenvolvimento do AlarMed, diversos bugs importantes foram identificados e resolvidos. Esta seção documenta os principais problemas encontrados e suas soluções.

### 🚨 Bug Crítico: Horários Perdidos após Atualização de Estoque

#### **Problema:**
Após clicar no botão "Tomei" para registrar a tomada de um medicamento, os horários configurados para esse medicamento desapareciam completamente da base de dados, mesmo que o cálculo do próximo alarme fosse executado corretamente.

#### **Causa:**
O método `save()` do `MedicamentoDao` utilizava `OnConflictStrategy.REPLACE`, que substitui completamente o registro do medicamento no banco de dados quando o estoque é atualizado. Como a tabela `Horario` possui uma foreign key com `CASCADE` para o medicamento, essa substituição completa causava problemas na integridade referencial, resultando na perda dos registros de horários associados.

```java
// PROBLEMÁTICO:
@Insert(onConflict = OnConflictStrategy.REPLACE)
long save(Medicamento medicamento); // Substitui registro completo

// No StockManager:
repository.save(medicamento); // Triggava REPLACE
```

#### **Solução:**
1. **Adicionado método `@Update` no `MedicamentoDao`:**
   ```java
   @Update
   void update(Medicamento medicamento);
   ```

2. **Criado método `updateMedicamento()` no `MedicamentoRepository`:**
   ```java
   public void updateMedicamento(Medicamento medicamento) {
       executor.execute(() -> {
           medicamentoDao.update(medicamento);
           Log.d("Repository", "Medicamento atualizado via UPDATE - foreign keys preservadas");
       });
   }
   ```

3. **Modificado `StockManager` para usar UPDATE específico:**
   ```java
   // ANTES:
   repository.save(medicamento);
   
   // DEPOIS:
   repository.updateMedicamento(medicamento);
   ```

4. **Corrigidos tipos inconsistentes (`int` vs `long`) no `HorarioDao`**

5. **Adicionados logs detalhados para rastreamento**

#### **Resultado:**
A atualização de estoque agora preserva completamente os horários configurados, utilizando `UPDATE` em vez de `REPLACE`, mantendo a integridade das foreign keys e garantindo que os lembretes continuem funcionando corretamente.

---

### 🔧 Bug: Threading em Toast Messages

#### **Problema:**
Crashes com `NullPointerException: Can't toast on a thread that has not called Looper.prepare()` ao tentar mostrar mensagens Toast em threads de background.

#### **Causa:**
Callbacks de operações de banco de dados (Room) são executados em threads de background, mas `Toast.makeText()` deve ser chamado na thread principal (UI thread).

#### **Solução:**
```java
// ANTES (problemático):
Toast.makeText(context, "Mensagem", Toast.LENGTH_LONG).show();

// DEPOIS (corrigido):
new Handler(Looper.getMainLooper()).post(() -> 
    Toast.makeText(context, "Mensagem", Toast.LENGTH_LONG).show()
);
```

---

### 📝 Lições Aprendidas

- **Room Database:** Sempre usar `@Update` para modificações de registros existentes quando há foreign keys
- **Threading:** UI operations devem sempre ser executadas na thread principal
- **Foreign Keys:** `OnConflictStrategy.REPLACE` pode causar problemas com relacionamentos CASCADE
- **Debugging:** Logs detalhados são essenciais para identificar problemas em operações assíncronas

## 🤝 Contribuidores

- **aj-ferreira** - Desenvolvedor
- **LayzaDev** - Desenvolvedor
- **jaquelinegon** - Desenvolvedor/Tester

---

⚡ **Mantenha seus medicamentos sempre em dia com o AlarMed!** ⚡
