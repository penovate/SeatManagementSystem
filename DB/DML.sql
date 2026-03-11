USE SeatDB;
GO

DELETE FROM Employee;
DELETE FROM SeatingChart;
DBCC CHECKIDENT ('SeatingChart', RESEED, 0);
GO

INSERT INTO SeatingChart (FLOOR_NO, SEAT_NO) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10),
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 6), (3, 7), (3, 8), (3, 9), (3, 10),
(4, 1), (4, 2), (4, 3), (4, 4), (4, 5), (4, 6), (4, 7), (4, 8), (4, 9), (4, 10);

INSERT INTO Employee (EMP_ID, NAME, EMAIL, FLOOR_SEAT_SEQ) VALUES 
('00001', N'©P¿³­õ', 'eric@test.com', 40),  
('00002', N'·¨¥àµY', 'rainie@test.com', 2),
('00003', N'³¯ªüµØ', 'hua@test.com', 25),   
('00004', N'½²¨ÌªL', 'lin@test.com', NULL),   
('00005', N'©PªN­Û', 'jay@test.com', 14),
('00006', N'³¯³Ç¾Ë', 'ch.chen@test.com', NULL),
('00007', N'­SÏÉºÕ', 'alex.fan@test.com', 1),
('00008', N'¶À¤¯¾±', 'jensen.huang@test.com', NULL),
('00009', N'ªL«T³Ç', 'jj.lin@test.com', NULL),
('00010', N'¬´ªv­¦', 'tanjiro@test.com', NULL);
GO

IF OBJECT_ID('UpdateEmployeeSeat', 'P') IS NOT NULL
    DROP PROCEDURE UpdateEmployeeSeat;
GO

CREATE PROCEDURE UpdateEmployeeSeat
    @EmpID CHAR(5),
    @NewSeatSeq INT
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRANSACTION;
    
    BEGIN TRY
        IF @NewSeatSeq IS NOT NULL
        BEGIN
            UPDATE Employee 
            SET FLOOR_SEAT_SEQ = NULL 
            WHERE FLOOR_SEAT_SEQ = @NewSeatSeq;
        END

        UPDATE Employee 
        SET FLOOR_SEAT_SEQ = @NewSeatSeq
        WHERE EMP_ID = @EmpID;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        THROW; 
    END CATCH
END
GO