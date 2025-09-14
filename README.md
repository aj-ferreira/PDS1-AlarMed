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

## 🤝 Contribuidores

- **aj-ferreira** - Desenvolvedor 

---

⚡ **Mantenha seus medicamentos sempre em dia com o AlarMed!** ⚡