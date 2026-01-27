CREATE DATABASE [Anika Database];
GO

USE [Anika Database];
GO

CREATE TABLE ConversationHistory (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    SessionID NVARCHAR(100),
    UserName NVARCHAR(100),
    UserQuestion NVARCHAR(MAX),
    BotReply NVARCHAR(MAX),
    Timestamp NVARCHAR(50),
    ImageBase64 NVARCHAR(MAX) -- Can be NULL if no image
);
GO
