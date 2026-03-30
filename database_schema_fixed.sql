-- *******************************************************************
-- DROP & CREATE DATABASE
-- *******************************************************************
DROP DATABASE IF EXISTS SocialMediaDB;
GO

CREATE DATABASE SocialMediaDB;
GO

USE SocialMediaDB;
GO

-- *******************************************************************
-- CREATE SCHEMAS
-- *******************************************************************
IF NOT EXISTS (SELECT * FROM sys.schemas WHERE name = 'CoreData')
    EXEC('CREATE SCHEMA CoreData');
GO

IF NOT EXISTS (SELECT * FROM sys.schemas WHERE name = 'Messaging')
    EXEC('CREATE SCHEMA Messaging');
GO

-- *******************************************************************
-- COREDATA SCHEMA - USERS
-- *******************************************************************
CREATE TABLE CoreData.[User] (
    id INT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(50) UNIQUE NOT NULL,
    email NVARCHAR(100) UNIQUE NOT NULL,
    phone NVARCHAR(15),
    passwordHash NVARCHAR(256),
    fullName NVARCHAR(100),
    bio NVARCHAR(500),
    profilePictureURL NVARCHAR(2048),
    createdAt DATETIME DEFAULT GETDATE(),
    updatedAt DATETIME,
    lastLogin DATETIME,
    authProvider NVARCHAR(20),
    isVerified BIT DEFAULT 0,
    isDeleted BIT DEFAULT 0,
    deletedAt DATETIME
);
GO

-- *******************************************************************
-- COREDATA SCHEMA - ROLES
-- *******************************************************************
CREATE TABLE CoreData.Role (
    id INT PRIMARY KEY IDENTITY(1,1),
    roleName NVARCHAR(50) UNIQUE NOT NULL
);
GO

CREATE TABLE CoreData.UserRole (
    roleId INT NOT NULL,
    userId INT NOT NULL,
    assignedAt DATETIME DEFAULT GETDATE(),
    PRIMARY KEY (roleId, userId),
    FOREIGN KEY (roleId) REFERENCES CoreData.Role(id),
    FOREIGN KEY (userId) REFERENCES CoreData.[User](id) ON DELETE CASCADE
);
GO

-- *******************************************************************
-- COREDATA SCHEMA - REFRESH TOKENS
-- *******************************************************************
CREATE TABLE CoreData.RefreshToken (
    id INT PRIMARY KEY IDENTITY(1,1),
    userId INT NOT NULL,
    token NVARCHAR(36) UNIQUE NOT NULL,
    expiryDate DATETIME,
    createdAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (userId) REFERENCES CoreData.[User](id) ON DELETE CASCADE
);
GO

-- *******************************************************************
-- MESSAGING SCHEMA - CONVERSATIONS
-- *******************************************************************
CREATE TABLE Messaging.Conversation (
    conversationId INT PRIMARY KEY IDENTITY(1,1),
    conversationName NVARCHAR(100),
    groupImageURL NVARCHAR(255),
    isGroupChat BIT DEFAULT 0,
    lastMessageId BIGINT,
    createdAt DATETIME DEFAULT GETDATE(),
    createdByUserId INT NOT NULL,
    FOREIGN KEY (createdByUserId) REFERENCES CoreData.[User](id)
);
GO

CREATE INDEX IX_Conversation_LastMessageId ON Messaging.Conversation(lastMessageId);
GO

-- *******************************************************************
-- MESSAGING SCHEMA - CONVERSATION MEMBERS
-- *******************************************************************
CREATE TABLE Messaging.ConversationMember (
    conversationId INT NOT NULL,
    userId INT NOT NULL,
    nickname NVARCHAR(100),
    role NVARCHAR(20) DEFAULT 'MEMBER' CHECK (role IN ('ADMIN', 'MEMBER')),
    lastReadMessageId BIGINT,
    mutedUntil DATETIME,
    joinedAt DATETIME DEFAULT GETDATE(),
    PRIMARY KEY (conversationId, userId),
    FOREIGN KEY (conversationId) REFERENCES Messaging.Conversation(conversationId) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES CoreData.[User](id) ON DELETE CASCADE
);
GO

CREATE INDEX IX_ConversationMember_UserId ON Messaging.ConversationMember(userId);
GO

-- *******************************************************************
-- MESSAGING SCHEMA - MESSAGES
-- *******************************************************************
CREATE TABLE Messaging.[Message] (
    messageId BIGINT PRIMARY KEY IDENTITY(1,1),
    conversationId INT NOT NULL,
    senderId INT NOT NULL,
    replyToMessageId BIGINT,
    messageType NVARCHAR(20) DEFAULT 'TEXT' CHECK (messageType IN ('TEXT', 'NOTIFICATION')),
    sentAt DATETIME DEFAULT GETDATE(),
    isDeleted BIT DEFAULT 0,
    sequenceNumber BIGINT DEFAULT 0,
    FOREIGN KEY (conversationId) REFERENCES Messaging.Conversation(conversationId) ON DELETE CASCADE,
    FOREIGN KEY (senderId) REFERENCES CoreData.[User](id),
    FOREIGN KEY (replyToMessageId) REFERENCES Messaging.[Message](messageId)
);
GO

CREATE INDEX IX_Message_ConversationId_SentAt ON Messaging.[Message](conversationId, sentAt DESC) INCLUDE (senderId, messageType, isDeleted);
GO

-- *******************************************************************
-- MESSAGING SCHEMA - MESSAGE BODIES
-- *******************************************************************
CREATE TABLE Messaging.MessageBody (
    messageId BIGINT PRIMARY KEY,
    content NVARCHAR(MAX),
    FOREIGN KEY (messageId) REFERENCES Messaging.[Message](messageId) ON DELETE CASCADE
);
GO

-- *******************************************************************
-- MESSAGING SCHEMA - MESSAGE MEDIA
-- *******************************************************************
CREATE TABLE Messaging.MessageMedia (
    mediaId BIGINT PRIMARY KEY IDENTITY(1,1),
    messageId BIGINT NOT NULL,
    mediaName NVARCHAR(255) NOT NULL,
    mediaType NVARCHAR(10) CHECK (mediaType IN ('IMAGE', 'VIDEO', 'AUDIO', 'FILE')),
    fileName NVARCHAR(255),
    fileSize INT,
    thumbnailName NVARCHAR(255),
    FOREIGN KEY (messageId) REFERENCES Messaging.[Message](messageId) ON DELETE CASCADE
);
GO

CREATE INDEX IX_MessageMedia_MessageId ON Messaging.MessageMedia(messageId);
GO

-- *******************************************************************
-- MESSAGING SCHEMA - PINNED MESSAGES
-- *******************************************************************
CREATE TABLE Messaging.PinnedMessage (
    conversationId INT NOT NULL,
    messageId BIGINT NOT NULL,
    pinnedByUserId INT NOT NULL,
    pinnedAt DATETIME DEFAULT GETDATE(),
    PRIMARY KEY (conversationId, messageId),
    FOREIGN KEY (conversationId) REFERENCES Messaging.Conversation(conversationId) ON DELETE CASCADE,
    FOREIGN KEY (messageId) REFERENCES Messaging.[Message](messageId),
    FOREIGN KEY (pinnedByUserId) REFERENCES CoreData.[User](id)
);
GO

-- *******************************************************************
-- MESSAGING SCHEMA - MESSAGE STATUS
-- *******************************************************************
CREATE TABLE Messaging.MessageStatus (
    messageId BIGINT NOT NULL,
    userId INT NOT NULL,
    status NVARCHAR(20) DEFAULT 'SENT',
    deliveredAt DATETIME,
    readAt DATETIME,
    PRIMARY KEY (messageId, userId),
    FOREIGN KEY (messageId) REFERENCES Messaging.[Message](messageId) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES CoreData.[User](id) ON DELETE CASCADE
);
GO

-- *******************************************************************
-- MESSAGING SCHEMA - WEBSOCKET SESSIONS
-- *******************************************************************
CREATE TABLE Messaging.WebSocketSession (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    sessionToken NVARCHAR(255) UNIQUE NOT NULL,
    userId INT NOT NULL,
    isActive BIT DEFAULT 1,
    createdAt DATETIME DEFAULT GETDATE(),
    expiresAt DATETIME NOT NULL,
    FOREIGN KEY (userId) REFERENCES CoreData.[User](id) ON DELETE CASCADE,
    CHECK (expiresAt > createdAt)
);
GO

CREATE INDEX IX_WebSocketSession_User_Active ON Messaging.WebSocketSession(userId, isActive) WHERE isActive = 1;
CREATE INDEX IX_WebSocketSession_Expires_Active ON Messaging.WebSocketSession(expiresAt, isActive) WHERE isActive = 1;
GO

-- *******************************************************************
-- INSERT DEFAULT DATA
-- *******************************************************************
-- Insert roles
INSERT INTO CoreData.Role (roleName) VALUES ('USER'), ('ADMIN'), ('MODERATOR');
GO

-- Insert test users
INSERT INTO CoreData.[User] (username, email, phone, passwordHash, fullName, isVerified)
VALUES 
    ('dungdespam001', 'dungdespam001@gmail.com', '+84901234567', 'hash1', 'Dung Despam', 1),
    ('dungdespam002', 'dungdespam002@gmail.com', '+84902345678', 'hash2', 'Dung Despam 2', 1);
GO

PRINT 'Database schema created successfully!'
