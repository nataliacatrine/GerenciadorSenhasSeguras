# 🔐 Gerenciador de Senhas Seguras

Este projeto Java é um **gerenciador de senhas seguro**, com foco em proteção de credenciais por meio de criptografia AES, autenticação de dois fatores (2FA), geração de senhas fortes e verificação de vazamentos em bases públicas. É voltado para fins educacionais e demonstra boas práticas de segurança da informação.

---

## 🚀 Funcionalidades

- Cadastro e armazenamento seguro de credenciais.
- Criptografia AES com chave derivada da senha mestra.
- Autenticação 2FA com Google Authenticator (TOTP).
- Geração de senhas seguras.
- Verificação de senhas vazadas com API do [Have I Been Pwned](https://haveibeenpwned.com/).

---

## 🛠️ Tecnologias utilizadas

- Java 17
- Maven
- JUnit 5
- BCrypt (hash de senha)
- AES-GCM (criptografia simétrica)
- Google Authenticator (TOTP)
- Apache HttpClient
- Jackson Databind

---

## 📦 Instalação

Clone o repositório:

```bash
git clone https://github.com/nataliacatrine/GerenciadorSenhasSeguras.git
cd GerenciadorSenhasSeguras
```

Compile o projeto com:

```bash
mvn compile
```

Execute com:

```bash
mvn exec:java -Dexec.mainClass="app.Main"
```

---

### 🧪 Executar Testes

Este projeto utiliza o **JUnit 5** para testes automatizados. Para executar os testes:

```bash
mvn test
```

Isso executará todos os testes localizados no diretório `src/test/java`.

---

### 📚 Gerar Javadoc

Para gerar a documentação Javadoc:

```bash
mvn javadoc:javadoc
```

A documentação será gerada no diretório:

```
target/site/apidocs
```

Você pode visualizá-la localmente abrindo o arquivo `index.html` ou acessar online:

📄 [Documentação Javadoc](https://nataliacatrine.github.io/GerenciadorSenhasSeguras/javadoc/)

---

### 📁 Estrutura do Projeto

```
GerenciadorSenhasSeguras/
├── docs/javadoc/             # Documentação gerada pelo Javadoc
├── src/main/java/            # Código-fonte principal
│   ├── app/                  # Classe principal e aplicação
│   ├── model/                # Classes de modelo (Credencial)
│   ├── repository/           # Classes para acesso a dados
│   ├── service/              # Lógica de negócio (serviços)
│   └── util/                 # Utilitários diversos
├── src/test/java/            # Testes automatizados
├── pom.xml                   # Configuração do Maven
├── README.md                 # Documentação do projeto
└── target/                   # Build output gerado automaticamente pelo Maven

```

---

### 📝 Observação

> O projeto é de uso educacional e demonstra boas práticas de segurança em Java, incluindo criptografia com AES, autenticação 2FA e verificação de senhas em vazamentos públicos.

---
