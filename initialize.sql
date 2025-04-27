DROP TABLE IF EXISTS employees;

CREATE TABLE employees (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           password VARCHAR(255),
                           first_name VARCHAR(255),
                           email VARCHAR(255) UNIQUE NOT NULL
);

INSERT INTO employees (password, first_name, email) VALUES
                                                        ('$2a$10$.bTspZOYWRSAi2SpDC1KO.6Xsi.kaPGUN5j82Wxk30toaSWcAoUQu', 'Alice', 'alice@example.com'),
                                                        ('$2a$10$.bTspZOYWRSAi2SpDC1KO.6Xsi.kaPGUN5j82Wxk30toaSWcAoUQu', 'Bob', 'bob@example.com'),
                                                        ('$2a$10$.bTspZOYWRSAi2SpDC1KO.6Xsi.kaPGUN5j82Wxk30toaSWcAoUQu', 'Charlie', 'charlie@example.com'),
                                                        ('$2a$10$.bTspZOYWRSAi2SpDC1KO.6Xsi.kaPGUN5j82Wxk30toaSWcAoUQu', 'Diana', 'diana@example.com'),
                                                        ('$2a$10$.bTspZOYWRSAi2SpDC1KO.6Xsi.kaPGUN5j82Wxk30toaSWcAoUQu', 'Ethan', 'ethan@example.com');
