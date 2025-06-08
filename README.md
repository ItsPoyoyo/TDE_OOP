# Guia Rápido: Student Notes App

## 1. Banco de Dados (MySQL)
- Abra o MySQL Workbench.
- Execute `database/schema.sql` para criar o banco.

## 2. Compilar o App
- Abra o terminal na pasta do projeto (`student_notes_app`).
- Execute: `mvn clean package`
- Isso cria o `student-notes.war` na pasta `target`.

## 3. Instalar no Tomcat
- Copie `student-notes.war` para a pasta `webapps` do seu Apache Tomcat.
- Reinicie o Tomcat.

## 4. Acessar o App
- Abra o navegador e vá para: `http://localhost:8080/student-notes/`
- Faça login com as credenciais do banco de dados.


