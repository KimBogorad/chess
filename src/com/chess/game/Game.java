package com.chess.game;

import com.chess.board.Board;
import com.chess.board.Position;
import com.chess.enums.*;
import com.chess.moves.*;
import com.chess.parser.ParsedIntent;
import com.chess.pieces.GamePiece;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

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
    public void playTurn(ParsedIntent intent) throws IllegalArgumentException {
        
        List<Move> candidates = moveFactory.createMoves(board, intent, currentPlayer);

        if (intent.castlingType() != CastlingType.NONE) {
            validateCastlingRules(intent);
        }

        List<Move> legalMoves = getLegalMoves(candidates);

        if (legalMoves.isEmpty()) {
            throw new IllegalArgumentException("Illegal move: This move leaves your King in check.");
        }

        if (legalMoves.size() > 1) {
            throw new IllegalArgumentException("Ambiguous move. Please specify file or rank (e.g., Ndf3).");
        }

        Move finalMove = legalMoves.get(0);
        finalMove.execute(board);
        
        switchPlayer();
        updateGameStatus();
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

    // ----------------------------------------------------------------
    // Private helper methods 
    // ----------------------------------------------------------------

    private void switchPlayer() {
        currentPlayer = (currentPlayer == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }

    private void updateGameStatus() {
        // If there are no legal moves:
        if (!hasAnyLegalMoves(currentPlayer)) {
            if (isKingInCheck(currentPlayer)) {
                this.gameStatus = GameStatus.MATE; // No more moves + Check = Mate
            } else {
                this.gameStatus = GameStatus.STALEMATE; // No moves + no check = stalemate
            }
        } else { // If there is at least one legal move:
            this.gameStatus = GameStatus.ACTIVE;
        }
    }

    private boolean hasAnyLegalMoves(PieceColor color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                GamePiece piece = board.getPieceAt(new Position(row, col));
                
                if (piece != null && piece.getColor() == color) {
                    List<Position> potentialMoves = board.getValidMovesForPiece(piece);

                    for (Position dest : potentialMoves) {
                        try {
                            // Ensure our simulated intent gets a flag for capture if relevant
                            boolean isCapture = board.getPieceAt(dest) != null || dest.equals(board.getEnPassantTarget());
                            Set<MoveFlag> flags = isCapture ? EnumSet.of(MoveFlag.CAPTURE) : EnumSet.noneOf(MoveFlag.class);

                            // Create a simulated intent to check for legal moves
                            ParsedIntent intent = new ParsedIntent(
                                    piece.getPieceType(), dest, flags, null, null, null, CastlingType.NONE);

                            // Get a list of all legal moves using the simulated intent 
                            List<Move> candidates = moveFactory.createMoves(board, intent, color);
                            List<Move> legalMoves = getLegalMoves(candidates);

                            // Found a legal move = stop everything!
                            if (!legalMoves.isEmpty()) {
                                return true; 
                            }
                        } catch (IllegalArgumentException e) {
                            // Illegal move for whatever reason, move on to next piece
                        }
                    }
                }
            }
        }
        return false; // Scanned all possible moves, no legal moves found
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

    private void validateCastlingRules(ParsedIntent intent) {
        if (isKingInCheck(currentPlayer)) {
            throw new IllegalArgumentException("Cannot castle out of check.");
        }
        
        int row = (currentPlayer == PieceColor.WHITE) ? 7 : 0;
        int colStep = (intent.castlingType() == CastlingType.KINGSIDE) ? 1 : -1;
        Position crossedSquare = new Position(row, 4 + colStep); 
        
        if (isPositionThreatened(currentPlayer, crossedSquare)) {
            throw new IllegalArgumentException("Cannot castle through check.");
        }
    }

    private List<Move> getLegalMoves(List<Move> candidates) {
        List<Move> legalMoves = new ArrayList<>();
        for (Move move : candidates) {
            if (!leavesKingInCheck(move)) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }


}