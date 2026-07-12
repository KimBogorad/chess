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
    private final List<MoveRecord> moveHistory;

    public Game() {
        this.board = new Board();
        this.currentPlayer = PieceColor.WHITE; // White always starts (racism aside)
        this.gameStatus = GameStatus.ACTIVE;
        this.moveFactory = new MoveFactory();
        this.moveHistory = new ArrayList<>();
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

    public void addMoveToHistory(MoveRecord record) {
        moveHistory.add(record);
    }

    public List<MoveRecord> getMoveHistory() {
        return moveHistory;
    }
    
    // ----------------------------------------------------------------
    // Private helper methods 
    // ----------------------------------------------------------------

    private void switchPlayer() {
        currentPlayer = (currentPlayer == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }

    private void updateGameStatus() {

        // If there are insufficient game pieces on the board, DRAW
        if (isInsufficientMaterial()) {
            this.gameStatus = GameStatus.DRAW;
            return;
        }
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

    // Returns the minor piece (knight or bishop), aside from the King.
    private GamePiece getMinorPieceOrNull(List<GamePiece> pieces) {
        if (pieces.size() != 2) {
            return null; // Only relevant when there's a King and one other piece
        }
        
        for (GamePiece piece : pieces) {
            if (piece.getPieceType() != PieceType.KING) {
                if (piece.getPieceType() == PieceType.KNIGHT || piece.getPieceType() == PieceType.BISHOP) {
                    return piece;
                }
                // There is a King and a piece that isn't a Bishop or a Knight
                return null; 
            }
        }
        return null;
    }

    // Check for DRAW scenario due to insufficient game pieces on the board
    private boolean isInsufficientMaterial() {
        List<GamePiece> whitePieces = new ArrayList<>();
        List<GamePiece> blackPieces = new ArrayList<>();

        // 1. Gather all the game pieces into designated lists
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                GamePiece piece = board.getPieceAt(new Position(row, col));
                if (piece != null) {
                    if (piece.getColor() == PieceColor.WHITE) {
                        whitePieces.add(piece);
                    } else {
                        blackPieces.add(piece);
                    }
                }
            }
        }

        int whiteCount = whitePieces.size();
        int blackCount = blackPieces.size();
        int totalCount = whiteCount + blackCount;

        // If one of the sides has more than 2 pieces it's not a draw
        if (whiteCount > 2 || blackCount > 2) {
            return false;
        }

        // 1. King vs King
        if (totalCount == 2) {
            return true;
        }

        // Get the light pieces aside from the Kings
        GamePiece whiteMinor = getMinorPieceOrNull(whitePieces);
        GamePiece blackMinor = getMinorPieceOrNull(blackPieces);

        // King + light piece vs King
        if (totalCount == 3 && (whiteMinor != null || blackMinor != null)) {
            return true;
        }

        // King + light piece vs King + light piece
        if (totalCount == 4 && whiteMinor != null && blackMinor != null) {
            
            // Bishop vs Bishop is a draw only if they are both on the same color! 
            if (whiteMinor.getPieceType() == PieceType.BISHOP && blackMinor.getPieceType() == PieceType.BISHOP) {
                boolean isWhiteOnLight = (whiteMinor.getPosition().row() + whiteMinor.getPosition().col()) % 2 == 0;
                boolean isBlackOnLight = (blackMinor.getPosition().row() + blackMinor.getPosition().col()) % 2 == 0;
                
                return isWhiteOnLight == isBlackOnLight; 
            }
            
            // Knight vs Knight or Knight vs Bishop
            return true;
        }
        // Covered all draw scenarios, here it's safe to declare not a DRAW
        return false;
    }
}