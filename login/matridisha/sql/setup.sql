-- ===============================
-- 1. Create Database
-- ===============================
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'AnikaDatabase')
BEGIN
    CREATE DATABASE AnikaDatabase;
END
GO

USE AnikaDatabase;
GO

-- ===============================
-- 2. Create Table
-- ===============================
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='MatriUsers' AND xtype='U')
BEGIN
    CREATE TABLE MatriUsers (
        ID INT IDENTITY(1,1) PRIMARY KEY,
        FullName NVARCHAR(100) NOT NULL,
        Age INT NOT NULL,
        Weight FLOAT NOT NULL,
        PregnancyWeek INT NOT NULL,
        HasPreviousChild NVARCHAR(10),
        DeliveryType NVARCHAR(50),
        MedicalIssue NVARCHAR(255),
        Username NVARCHAR(50) UNIQUE NOT NULL,
        Password NVARCHAR(100) NOT NULL
    );
END
GO

-- ===============================
-- 3. Create Login (SQL Authentication)
-- ===============================
IF NOT EXISTS (SELECT * FROM sys.server_principals WHERE name = 'projectuser')
BEGIN
    CREATE LOGIN projectuser WITH PASSWORD = 'Project@123';
END
GO

ALTER SERVER ROLE sysadmin ADD MEMBER projectuser;
GO

-- ===============================
-- 4. Map User to Database
-- ===============================
USE AnikaDatabase;
GO

IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'projectuser')
BEGIN
    CREATE USER projectuser FOR LOGIN projectuser;
END
GO

ALTER ROLE db_owner ADD MEMBER projectuser;
GO

PRINT 'Database setup completed successfully!';
