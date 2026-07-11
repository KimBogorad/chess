package com.chess.game;

import com.chess.board.Board;
import com.chess.board.Position;
import com.chess.enums.*;
import com.chess.moves.*;
import com.chess.parser.ParsedIntent;
import com.chess.pieces.GamePiece;

import java.util.EnumSet;
import java.util.List;

public class Game {
    private Board board;
    private PieceColor currentPlayer;
    private GameStatus gameStatus;
    private final MoveFactory moveFactory;

    public Game() {
        this.board = new Board();
        this.currentPlayer = PieceColor.WHITE; // White always starts (racism aside)
        this.gameStatus = GameStatus.ACTIVE;
        this.moveFactory = new MoveFactory();
    }

    // validate move, play, switch currentPlayer and update game status
    public boolean playTurn(ParsedIntent intent) {
        // 1. Create the move, make sure there is a legal move
        Move move = moveFactory.createMove(board, intent, currentPlayer);

        if (move == null) {
            return false; // no piece can make this move
        }
        // Castling : validate castling path
        if (intent.castlingType() != CastlingType.NONE) {
        
            // 1. Can't castle while checked
            if (isKingInCheck(currentPlayer)) {
                return false; 
            }
            
            int row = (currentPlayer == PieceColor.WHITE) ? 7 : 0;
            // Set the column the King will pass through during castling
            int colStep = (intent.castlingType() == CastlingType.KINGSIDE) ? 1 : -1;
            Position crossedSquare = new Position(row, 4 + colStep); 
            
            // Validate the square in the King's path
            if (isPositionThreatened(currentPlayer, crossedSquare)) {
                return false;
            }
        }
        // 2. Check if making this move will leave the king checked - illegal
        if(leavesKingInCheck(move)) {
            return false;
        }       

        move.execute(board);
        switchPlayer();
        updateGameStatus();

        return true;
    }

    public Board getBoard() {
        return this.board;
    }

    public PieceColor getCurrentPlayer() {
        return this.currentPlayer;
    }

    public GameStatus getGameStatus() {
        return this.gameStatus;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }

    private void updateGameStatus() {
        boolean hasAnyLegalMove = false;

        // Iterate through the board to find enemy pieces.
        // used label searchloop to break both loops at once.
        searchLoop: 
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                GamePiece piece = board.getPieceAt(new Position(row, col));
                
                if (piece != null && piece.getColor() == currentPlayer) {
                    List<Position> potentialMoves = board.getValidMovesForPiece(piece);
                    
                
                    for (Position dest : potentialMoves) {
                        char fileDisambiguation = (char) ('a' + piece.getPosition().col());
                        int rankDisambiguation = 8 - piece.getPosition().row();
                        ParsedIntent intent = new ParsedIntent(
                                            piece.getPieceType(), 
                                            dest, 
                                            EnumSet.noneOf(MoveFlag.class), 
                                            null, 
                                            fileDisambiguation,
                                            rankDisambiguation, 
                                            CastlingType.NONE);
                        Move move = moveFactory.createMove(board, intent, currentPlayer);
                        if (!leavesKingInCheck(move)) {
                            hasAnyLegalMove = true;
                            break searchLoop; //  found a legal move: game isn't over
                        }
                    }
                }
            }
        }

        // If there are no legal moves:
        if (!hasAnyLegalMove) {
            if (isKingInCheck(currentPlayer)) {
                this.gameStatus = GameStatus.MATE; // No more moves + Check = Mate
            } else {
                this.gameStatus = GameStatus.STALEMATE; // No moves + no check = stalemate
            }
        } else { // If there is at least one legal move:
            this.gameStatus = GameStatus.ACTIVE;
        }
    }

    private boolean isPositionThreatened(PieceColor pieceColor, Position position) {
        PieceColor enemyColor = (pieceColor == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                GamePiece piece = board.getPieceAt(new Position(row, col));
                
                if (piece != null && piece.getColor() == enemyColor) {
                    List<Position> validMoves = board.getValidMovesForPiece(piece);
                    
                    if (validMoves.contains(position)) {
                        return true; // Found threat!
                    }
                }
            }
        }

        return false; // No threats!
    }

    private boolean isKingInCheck(PieceColor kingColor) {
        Position kingPos = null;

        // 1. Find the current player's King on the board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                GamePiece piece = board.getPieceAt(new Position(row, col));
                if (piece != null && piece.getColor() == kingColor && piece.getPieceType() == PieceType.KING) {
                    kingPos = piece.getPosition();
                    break;
                }
            }
            if (kingPos != null) break;
        }

        if (kingPos == null) {
            return false; // Technical edge case: King is not on board
        }
        // 2. Find all enemy pieces, check if any of them threaten the king's position
        return isPositionThreatened(kingColor, kingPos);
    }

    private boolean leavesKingInCheck(Move move) {
        move.execute(board);
        boolean leaves = isKingInCheck(currentPlayer);
        move.undo(board);
        return leaves;
    }
}