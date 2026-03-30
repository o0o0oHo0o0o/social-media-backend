-- Insert conversation between dungdespam001 (ID 2) and dungdespam002 (ID 1002)
USE SocialMediaDB;

DECLARE @userId1 INT = 2;  -- dungdespam001
DECLARE @userId2 INT = 1002;  -- dungdespam002
DECLARE @convId INT;

-- Create conversation
INSERT INTO Messaging.Conversation (conversationName, isGroupChat, createdAt, updatedAt)
VALUES ('dungdespam001 <> dungdespam002', 0, GETDATE(), GETDATE());

SET @convId = SCOPE_IDENTITY();

-- Add both users as members
INSERT INTO Messaging.ConversationMember (conversationId, userId, joinedAt)
VALUES 
    (@convId, @userId1, GETDATE()),
    (@convId, @userId2, GETDATE());

-- Done
SELECT @convId as ConversationId;
