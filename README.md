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

* **Linguagem:** Java 17
* **Framework:** Spring Boot 3.x
* **Segurança:** Spring Security & JWT
* **Banco de Dados:** MySQL / PostgreSQL
* **Serviço de E-mail:** Spring Mail (SMTP)

## ⚙️ Instalação e Execução (Via Release)

Para facilitar o uso, disponibilizamos o executável da aplicação nas [Releases](https://github.com/seu-usuario/seu-repositorio/releases).

### 📋 Pré-requisitos
* **Java Runtime Environment (JRE) 17** ou superior instalado e configurado no PATH.
* Banco de Dados MySQL rodando localmente.

### ▶️ Como rodar
1.  Baixe o arquivo `diversos-backend.zip` na aba de Releases.
2.  Extraia o conteúdo em uma pasta.
3.  Certifique-se de que o seu banco de dados está criado (ex: `diversos_db`).
4.  Execute o arquivo `run.bat` (Windows) ou o comando abaixo no terminal:
    ```bash
    java -jar diversos-backend.jar
    ```

A API estará disponível em: `http://localhost:8080`

## 🔐 Configuração das Variáveis de Ambiente

O arquivo `.bat` ou o sistema operacional deve conter as seguintes variáveis para o funcionamento correto:

| Variável | Descrição |
| :--- | :--- |
| `DB_URL` | URL de conexão do banco (ex: `jdbc:mysql://localhost:3306/diversos_db`) |
| `DB_USERNAME` | Usuário do banco de dados |
| `DB_PASSWORD` | Senha do banco de dados |
| `MAIL_USERNAME` | E-mail para envio de recuperação de senha |
| `MAIL_PASSWORD` | Senha de aplicativo do e-mail (SMTP) |
| `JWT_SECRET` | Chave mestra para criptografia dos tokens |

## 📁 Estrutura do Executável
Ao baixar a release, você encontrará:
* `diversos-backend.jar`: O executável Java.
* `run.bat`: Script para execução rápida no Windows.
* `/uploads`: Pasta onde serão armazenadas as imagens enviadas para a plataforma.

## 🔗 Repositório do Front-end
[Acesse o repositório do Front-end aqui](https://github.com/seu-usuario/seu-repo-frontend)
