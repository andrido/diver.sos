<h1 align="center">diver.sos - Back-end</h1>

<p align="center">
  <img width="286.56" height="229.92" alt="Logo diver.sos" src="https://github.com/user-attachments/assets/3bccd4c7-386d-401e-b7fb-2b422c53b3c2" />
</p>

<p align="center">
  API REST responsável por gerenciar a inteligência, segurança e persistência de dados para a comunidade LGBTQIAPN+.
</p>

---

## 🧠 Sobre o projeto

> O back-end do **diver.sos** fornece a infraestrutura necessária para a listagem de vagas, grupos de apoio e notícias, além de gerenciar a autenticação e permissões de usuários.
>
> A API foi desenvolvida pela equipe **Atemporal** como parte do Projeto Integrado I da Universidade Federal do Ceará (UFC).

---

## 🚀 Funcionalidades Principais

- **Gestão de Usuários:** Autenticação via JWT e controle de acesso (RBAC).
- **Recuperação de Senha:** Fluxo automatizado via e-mail.
- **Gestão de Conteúdo:** CRUD completo de vagas, grupos e notícias.
- **Upload de Arquivos:** Gerenciamento de imagens e perfis.

---

<h3 align="center">Tecnologias Utilizadas</h3>

<p align="center">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" alt="Java" width="45"/>
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" alt="Spring Boot" width="45"/>
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/mysql/mysql-original.svg" alt="MySQL" width="45"/>
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/maven/maven-original.svg" alt="Maven" width="45"/>
</p>

---

## ⚙️ Instalação e Execução Simplificada (Windows)

Para facilitar o uso, disponibilizamos um executável (`.exe`) que não exige comandos no terminal.

### 📋 Pré-requisitos
1.  **Java Runtime Environment (JRE) 22** ou superior instalado.
2.  **MySQL Server** instalado e rodando (ou acesso a um banco na nuvem).

---

### 📦 Como Rodar

1. **Baixe a pasta** do projeto (Release) e extraia em seu computador.
2. Certifique-se de que a estrutura da pasta contém os seguintes arquivos juntos:
   - `diverSOS.exe` (O aplicativo)
   - `.env` (Configurações)

3. **Configure o Banco de Dados:**
   Abra seu gerenciador MySQL e crie o banco:
   ```sql
   CREATE DATABASE diversos;
