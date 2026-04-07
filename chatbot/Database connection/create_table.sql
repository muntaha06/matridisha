USE [Anika Database]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[ConversationHistory](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[SessionID] [nvarchar](50) NOT NULL,
	[UserName] [nvarchar](100) NULL,
	[UserQuestion] [nvarchar](max) NULL,
	[BotReply] [nvarchar](max) NULL,
	[Timestamp] [nvarchar](50) NULL,
	[ImageBase64] [nvarchar](max) NULL,
 CONSTRAINT [PK_ConversationHistory] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
