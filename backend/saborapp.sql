-- Base de datos para Pollería El Buen Sabor (SaborApp)
CREATE DATABASE IF NOT EXISTS saborapp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE saborapp;

-- 1. Tabla Usuario
CREATE TABLE IF NOT EXISTS usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    clave VARCHAR(100) NOT NULL,
    rol ENUM('ADMIN', 'MOZO') NOT NULL DEFAULT 'MOZO'
);

INSERT INTO usuario (usuario, clave, rol) VALUES 
('admin', '1234', 'ADMIN'),
('mozo1', '1234', 'MOZO')
ON DUPLICATE KEY UPDATE usuario=usuario;

-- 2. Tabla Plato
CREATE TABLE IF NOT EXISTS plato (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    precio DECIMAL(10,2) NOT NULL CHECK (precio > 0),
    disponible TINYINT(1) NOT NULL DEFAULT 1
);

INSERT INTO plato (id, nombre, categoria, precio, disponible) VALUES
(1, '1/4 de pollo a la brasa', 'Fondos', 18.00, 1),
(2, 'Lomo saltado', 'Fondos', 25.00, 1),
(3, 'Chicha morada 1 L', 'Bebidas', 12.00, 1),
(4, 'Suspiro limeño', 'Postres', 9.00, 1)
ON DUPLICATE KEY UPDATE nombre=nombre;

-- 3. Tabla Mesa
CREATE TABLE IF NOT EXISTS mesa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL UNIQUE,
    capacidad INT NOT NULL CHECK (capacidad BETWEEN 1 AND 12),
    estado ENUM('LIBRE', 'OCUPADA') NOT NULL DEFAULT 'LIBRE'
);

INSERT INTO mesa (id, numero, capacidad, estado) VALUES
(1, 1, 4, 'LIBRE'),
(2, 2, 4, 'LIBRE'),
(3, 3, 2, 'LIBRE'),
(4, 4, 6, 'LIBRE'),
(5, 5, 4, 'LIBRE'),
(6, 6, 8, 'LIBRE')
ON DUPLICATE KEY UPDATE numero=numero;

-- 4. Tabla Pedido
CREATE TABLE IF NOT EXISTS pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_mesa INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('ABIERTO', 'CERRADO') NOT NULL DEFAULT 'ABIERTO',
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (id_mesa) REFERENCES mesa(id) ON UPDATE CASCADE
);

-- 5. Tabla Detalle Pedido
CREATE TABLE IF NOT EXISTS detalle_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_plato INT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unit DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES pedido(id) ON DELETE CASCADE,
    FOREIGN KEY (id_plato) REFERENCES plato(id)
);
