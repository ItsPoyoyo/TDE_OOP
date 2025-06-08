-- Database schema for Student Notes Application

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS student_notes;

-- Use the database
USE student_notes;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create notes table
CREATE TABLE IF NOT EXISTS notes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    category VARCHAR(50),
    is_favorite BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create tags table
CREATE TABLE IF NOT EXISTS tags (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Create note_tags junction table for many-to-many relationship
CREATE TABLE IF NOT EXISTS note_tags (
    note_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (note_id, tag_id),
    FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- Insert some sample data
INSERT INTO users (username, password, email) VALUES
('student1', 'password123', 'student1@example.com'),
('student2', 'password456', 'student2@example.com');

INSERT INTO notes (user_id, title, content, category) VALUES
(1, 'Java Programming Notes', 'Object-oriented programming concepts include inheritance, polymorphism, encapsulation, and abstraction.', 'Programming'),
(1, 'Database Design', 'Normalization is the process of organizing data to minimize redundancy.', 'Database'),
(2, 'Web Development', 'HTML is used for structure, CSS for styling, and JavaScript for behavior.', 'Programming');

INSERT INTO tags (name) VALUES
('Java'),
('Programming'),
('Database'),
('Web'),
('Important');

INSERT INTO note_tags (note_id, tag_id) VALUES
(1, 1), -- Java Programming Notes - Java
(1, 2), -- Java Programming Notes - Programming
(2, 3), -- Database Design - Database
(3, 2), -- Web Development - Programming
(3, 4); -- Web Development - Web
