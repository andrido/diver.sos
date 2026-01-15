---------------------------------------------------------
-- 1. LIMPEZA TOTAL DO BANCO (RESET)
---------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE usuarios_grupos_salvos;
TRUNCATE TABLE usuarios_habilidades;
TRUNCATE TABLE usuarios_vagas_salvas;
TRUNCATE TABLE vagas_habilidades;
TRUNCATE TABLE noticia;
TRUNCATE TABLE vaga;
TRUNCATE TABLE grupos;
TRUNCATE TABLE habilidades;
TRUNCATE TABLE usuarios;
TRUNCATE TABLE verification_token;

SET FOREIGN_KEY_CHECKS = 1;

---------------------------------------------------------
-- 2. BIBLIOTECA DE HABILIDADES
---------------------------------------------------------
INSERT INTO habilidades (id, nome) VALUES
                                       (1, 'Java / Spring Boot'),
                                       (2, 'Python / Django'),
                                       (3, 'React / Next.js'),
                                       (4, 'SQL / Bancos de Dados'),
                                       (5, 'Comunicacao Assertiva'),
                                       (6, 'Design UI/UX'),
                                       (7, 'Estatistica Aplicada'),
                                       (8, 'Metodologias Ageis (Scrum)'),
                                       (9, 'Ingles Tecnico'),
                                       (10, 'Node.js / Express');

---------------------------------------------------------
-- 3. USUARIOS DO SISTEMA
---------------------------------------------------------

-- ADMINISTRADOR PADRÃO - Senha: Admin@123
INSERT INTO usuarios (
    id, nome, email, senha, foto_perfil, telefone,
    data_nascimento, cpf, pronomes, status, tipo_de_usuario,
    bairro, cep, cidade, complemento, estado, numero, rua
) VALUES (
             1, 'Suporte Diversos', 'admin@diversos.com',
             '$2a$10$T.daqmK3VglHLzVzKBty2eW51oDbOJeCUnTpIKxaKKazzMwTWZnYS',
             'https://i.pravatar.cc/150?u=admin', '85999999999',
             '1990-01-01', '11122233344', 'Ele/Dele', 'ATIVO',
             'ADMINISTRADOR', 'Centro', '60000000', 'Fortaleza',
             'Sede', 'Ceara', '100', 'Rua Central'
         );

-- MODERADOR (ZECA) Senha: Mod@1234
INSERT INTO usuarios (
    id, nome, email, senha, foto_perfil, telefone,
    data_nascimento, cpf, pronomes, status, tipo_de_usuario,
    bairro, cep, cidade, complemento, estado, numero, rua
) VALUES (
             2, 'Zeca da Silva', 'moderador@diversos.com',
             '$2a$10$3B3KNUhq54Od2HkTCa.pJu/FHU.yjs77IDzu3v48B9jwR6nUMyNSO',
             'https://i.pravatar.cc/150?u=mod', '88988887777',
             '1985-10-20', '44455566677', 'Ele/Dele', 'ATIVO',
             'MODERADOR', 'Derby', '62042030', 'Sobral',
             'Ap 302', 'Ceara', '1200', 'Rua Viriato de Medeiros'
         );

-- USUÁRIA COMUM (FERNANDA) Senha: Senha@123
INSERT INTO usuarios (
    id, nome, email, senha, foto_perfil, telefone,
    data_nascimento, cpf, pronomes, status, tipo_de_usuario,
    bairro, cep, cidade, complemento, estado, numero, rua
) VALUES (
             3, 'Fernanda Oliveira', 'usuario@diversos.com',
             '$2a$10$kbjDhMhALLMcb4K/nozCeeFoYSxLrY4hHRxNpn2q.fsSzk2lfN/Lq',
             'https://i.pravatar.cc/150?u=fernanda', '85977776666',
             '1998-03-12', '22233344455', 'Ela/Dela', 'ATIVO',
             'USUARIO', 'Aldeota', '60115000', 'Fortaleza',
             'Apto 101', 'Ceara', '250', 'Rua Osvaldo Cruz'
         );

---------------------------------------------------------
-- 4. NOTICIAS (conteudo, data_publicacao, titulo, autor_id)
---------------------------------------------------------
INSERT INTO noticia (conteudo, data_publicacao, titulo, autor_id)
VALUES
      ('Abertura de novas vagas afirmativas para o setor de tecnologia.', NOW(), 'Oportunidades Tech', 1),
      ('Dicas exclusivas para mandar bem na sua primeira entrevista remota.', NOW(), 'Guia de Carreira', 2),
      ('Conheça os novos grupos de apoio criados para a comunidade local.', NOW(), 'Rede de Apoio', 1);

---------------------------------------------------------
-- 5. GRUPOS DE APOIO E ESTUDO
---------------------------------------------------------
INSERT INTO grupos (
    id, banner_do_grupo, categoria, cidade,
    descricao, estado, link, nome, responsavel
) VALUES (
             1, 'https://images.unsplash.com/photo-1526628953301-3e589a6a8b74',
             'ESTUDOS', 'Fortaleza', 'Focado em Python para iniciantes.',
             'CE', 'https://chat.whatsapp.com/ex1', 'Tech para Todos', 'ONG Tech'
         ), (
             2, 'https://images.unsplash.com/photo-1573497620053-ea5310f94f17',
             'APOIO', 'Sobral', 'Grupo de acolhimento e suporte emocional.',
             'CE', 'https://ong.org/acolher', 'Acolhida Sobral', 'Coletivo Local'
         );

---------------------------------------------------------
-- 6. VAGAS DISPONÍVEIS
---------------------------------------------------------
INSERT INTO vaga (
    id, data_criacao, data_limite, descricao, cidade,
    estado, empresa, link_da_vaga, titulo,
    modalidade, status, tipo
) VALUES (
             1, NOW(), '2026-12-31', 'Dev Backend Java Junior.',
             'Fortaleza', 'CE', 'Inova Tech', 'https://vagas.com/java',
             'Dev Java Jr', 'REMOTO', 'ATIVA', 'AFIRMATIVA'
         ), (
             2, NOW(), '2026-06-30', 'Analise de dados com foco em dashboards.',
             'Sao Paulo', 'SP', 'Global Data', 'https://vagas.com/bi',
             'Analista BI', 'HIBRIDO', 'ATIVA', 'AFIRMATIVA'
         );

---------------------------------------------------------
-- 7. RELACIONAMENTOS (VÍNCULOS)
---------------------------------------------------------
-- Habilidades: Fernanda (3) com React(3) e Comunicacao(5)
INSERT INTO usuarios_habilidades (usuario_id, habilidade_id) VALUES
                                                                 (3, 3), (3, 5);

-- Habilidades: Admin (1) com Java(1) e SQL(4)
INSERT INTO usuarios_habilidades (usuario_id, habilidade_id) VALUES
                                                                 (1, 1), (1, 4);

-- Favoritos da Fernanda
INSERT INTO usuarios_vagas_salvas (usuario_id, vaga_id) VALUES (3, 1);
INSERT INTO usuarios_grupos_salvos (usuario_id, grupo_id) VALUES (3, 1);