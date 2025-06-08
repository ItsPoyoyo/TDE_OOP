# Student Notes Application

Este é um aplicativo Java para gerenciamento de anotações de estudantes, desenvolvido como parte de um trabalho acadêmico.

## Requisitos

- Java 8 ou superior
- MySQL 5.7 ou superior
- Apache Tomcat 9.0 ou superior
- Maven 3.6 ou superior

## Configuração do Banco de Dados

1. Crie um banco de dados MySQL chamado `student_notes`
2. Execute o script SQL localizado em `database/schema.sql` para criar as tabelas e dados iniciais
3. Atualize as credenciais de banco de dados no arquivo `src/main/java/com/studentnotes/util/DatabaseUtil.java` se necessário

## Compilação e Execução

### Usando Maven

1. Navegue até a pasta raiz do projeto
2. Execute o comando: `mvn clean package`
3. O arquivo WAR será gerado na pasta `target/student-notes.war`
4. Implante o arquivo WAR em um servidor Tomcat

### Usando uma IDE

1. Importe o projeto como um projeto Maven em sua IDE preferida (Eclipse, IntelliJ, etc.)
2. Configure um servidor Tomcat na IDE
3. Execute o projeto no servidor

## Estrutura do Projeto

- `src/main/java/com/studentnotes/model/` - Classes de modelo
- `src/main/java/com/studentnotes/dao/` - Classes de acesso a dados
- `src/main/java/com/studentnotes/service/` - Classes de serviço
- `src/main/java/com/studentnotes/controller/` - Servlets e controladores
- `src/main/java/com/studentnotes/util/` - Classes utilitárias
- `src/main/webapp/` - Arquivos da interface web (HTML, CSS, JavaScript)
- `src/main/webapp/WEB-INF/` - Configurações da aplicação web
- `database/` - Scripts SQL para criação do banco de dados

## Funcionalidades

- Registro e login de usuários
- Criação, edição e exclusão de anotações
- Categorização de anotações
- Marcação de anotações como favoritas
- Pesquisa de anotações por título ou conteúdo
- Interface responsiva para diferentes dispositivos

## API REST

A aplicação fornece uma API REST para interação com o frontend:

- `POST /api/auth/login` - Autenticação de usuário
- `POST /api/auth/register` - Registro de novo usuário
- `POST /api/auth/logout` - Logout de usuário
- `GET /api/notes` - Listar todas as anotações do usuário
- `GET /api/notes/{id}` - Obter uma anotação específica
- `POST /api/notes` - Criar uma nova anotação
- `PUT /api/notes/{id}` - Atualizar uma anotação existente
- `DELETE /api/notes/{id}` - Excluir uma anotação
- `GET /api/notes/favorites` - Listar anotações favoritas
- `GET /api/notes/category/{category}` - Listar anotações por categoria
- `GET /api/notes/search/{query}` - Pesquisar anotações

## Autores

Este projeto foi desenvolvido como parte de um trabalho acadêmico.
