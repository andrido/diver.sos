# diver.sos - Back-end

API REST desenvolvida para a plataforma **diver.sos**, responsável por gerenciar a inteligência, segurança e persistência de dados para a comunidade LGBTQIAPN+.

## 🧠 Sobre o projeto

O back-end do diver.sos fornece a infraestrutura necessária para a listagem de vagas, grupos de apoio e notícias, além de gerenciar a autenticação e permissões de usuários. A API foi desenvolvida pela equipe Atemporal como parte do Projeto Integrado I da Universidade Federal do Ceará (UFC).

## 🚀 Funcionalidades Principais

* **Gestão de Usuários:** Autenticação via JWT (JSON Web Token) e controle de acesso baseado em cargos (ADMIN, MODERADOR, RH, USUARIO).
* **Recuperação de Senha:** Fluxo automatizado com envio de e-mail e tokens de segurança temporários.
* **Gestão de Conteúdo:** CRUD de vagas, grupos, notícias e habilidades.
* **Upload de Arquivos:** Processamento e armazenamento de imagens para perfis, banners e notícias.
* **Favoritos:** Sistema de persistência de conteúdos salvos por usuário.

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 22 ou superior
* **Framework:** Spring Boot 3.x
* **Segurança:** Spring Security & JWT
* **Banco de Dados:** MySQL
* **Serviço de E-mail:** Spring Mail (SMTP)

## ⚙️ Instalação e Execução (Via Release)

### 📋 Pré-requisitos
1. **Java 22 ou superior:** Certifique-se de ter o JDK/JRE 22 instalado e configurado no seu PATH.
2. **MySQL Server:** É necessário ter o MySQL instalado na máquina.
3. **Banco de Dados:** Crie um banco de dados vazio chamado `diversos` no seu MySQL antes de rodar a aplicação:
   ```sql
   CREATE DATABASE diversos;
