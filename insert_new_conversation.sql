-- Insert new conversation between dungdespam001 (user 2) and dungdespam002 (user 3 if exists, or create)
-- First check/create dungdespam002 user if not exists
USE SocialMediaDB;

-- Check if dungdespam002 exists, if not insert
IF NOT EXISTS (SELECT 1 FROM CoreData.[User] WHERE username = 'dungdespam002')
BEGIN
    INSERT INTO CoreData.[User] (username, fullName, email, phone, passwordHash, createdAt, updatedAt)
    VALUES ('dungdespam002', 'Dung Despam 2', 'dungdespam002@example.com', '+84909999999', 'hashed_password_2', GETDATE(), GETDATE());
END

-- Get user IDs
DECLARE @userId1 INT = (SELECT id FROM CoreData.[User] WHERE username = 'dungdespam001');
DECLARE @userId2 INT = (SELECT id FROM CoreData.[User] WHERE username = 'dungdespam002');

-- Create conversation between them
DECLARE @convId INT;
INSERT INTO Messaging.Conversation (conversationName, isGroupChat, createdAt, updatedAt)
VALUES ('dungdespam001 <> dungdespam002', 0, GETDATE(), GETDATE());

SET @convId = SCOPE_IDENTITY();

-- Add both users as members
INSERT INTO Messaging.ConversationMember (conversationId, userId, joinedAt)
VALUES 
    (@convId, @userId1, GETDATE()),
    (@convId, @userId2, GETDATE());

-- Insert sample messages
INSERT INTO Messaging.[Message] (conversationId, senderId, sentAt, updatedAt)
VALUES 
    (@convId, @userId1, GETDATE(), GETDATE()),
    (@convId, @userId2, DATEADD(MINUTE, 1, GETDATE()), DATEADD(MINUTE, 1, GETDATE())),
    (@convId, @userId1, DATEADD(MINUTE, 2, GETDATE()), DATEADD(MINUTE, 2, GETDATE()));

-- Get message IDs and insert bodies
DECLARE @msgId1 INT = (SELECT TOP 1 id FROM Messaging.[Message] WHERE conversationId = @convId AND senderId = @userId1 ORDER BY sentAt);
DECLARE @msgId2 INT = (SELECT TOP 1 id FROM Messaging.[Message] WHERE conversationId = @convId AND senderId = @userId2 ORDER BY sentAt);
DECLARE @msgId3 INT = (SELECT TOP 1 id FROM Messaging.[Message] WHERE conversationId = @convId AND senderId = @userId1 ORDER BY sentAt DESC);

INSERT INTO Messaging.MessageBody (messageId, content)
VALUES 
    (@msgId1, 'Hey, how are you doing?'),
    (@msgId2, 'I am doing great! How about you?'),
    (@msgId3, 'All good here, let''s catch up soon!');

-- Verify insertion
SELECT 
    c.id as ConversationId,
    c.conversationName,
    u1.username as User1,
    u2.username as User2,
    m.id as MessageId,
    m.sentAt,
    mb.content
FROM Messaging.Conversation c
JOIN Messaging.ConversationMember cm1 ON c.id = cm1.conversationId
JOIN CoreData.[User] u1 ON cm1.userId = u1.id
JOIN Messaging.ConversationMember cm2 ON c.id = cm2.conversationId AND cm2.userId != cm1.userId
JOIN CoreData.[User] u2 ON cm2.userId = u2.id
LEFT JOIN Messaging.[Message] m ON c.id = m.conversationId
LEFT JOIN Messaging.MessageBody mb ON m.id = mb.messageId
WHERE c.id = @convId
ORDER BY m.sentAt DESC;
