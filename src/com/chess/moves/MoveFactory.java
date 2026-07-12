package com.chess.moves;

import com.chess.board.Board;
import com.chess.board.Position;
import com.chess.enums.MoveFlag;
import com.chess.enums.PieceColor;
import com.chess.enums.PieceType;
import com.chess.parser.ParsedIntent;
import com.chess.pieces.*;

import java.util.ArrayList;
import java.util.List;

public class MoveFactory {

    public List<Move> createMoves(Board board, ParsedIntent intent, PieceColor currentPlayer) {
        List<Move> candidates = new ArrayList<>();

        // 1. Handle castling
        if (intent.isCastle()) {
        candidates.add(createCastlingMove(board, intent, currentPlayer));
        return candidates;
    }

        // 2. Find all geometrically relevant pieces
        List<GamePiece> piecesToMove = findPiecesForStandardMove(board, intent, currentPlayer);
        
        if ( piecesToMove == null || piecesToMove.isEmpty()) {
            throw new IllegalArgumentException("No valid piece can make this move.");
        }
        // 3. Create the correct move object for each candidate
        for (GamePiece candidate : piecesToMove) {
            // 3.1. Validate capture notation and logic 
            validateCaptureNotation(board, candidate, intent);

            // 3.2. Handle promotion
            if (isPromotion(candidate, intent.destination())) {
                candidates.add(createPromotionMove(candidate, intent, currentPlayer));
            }

            // 3.3. Handle En Passant
            else if (isEnPassant(board, candidate, intent.destination())) {
                candidates.add(createEnPassantMove(board, candidate, intent.destination()));
            }

            // 3.4. Default: Standard Move
            else {
                candidates.add(new StandardMove(candidate, intent.destination()));
            }
        }
        //4. Finally, return all candidates left
        return candidates;
    }

    /**
     * This function attempts to find the single disambiguous piece that can move according to the intent.
     * It succeeds if exactly one piece passes all the validity tests.
     */
    private List<GamePiece> findPiecesForStandardMove(Board board, ParsedIntent intent, PieceColor color) {
        List<GamePiece> candidates = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                GamePiece piece = board.getPieceAt(new Position(row, col));
                
                // Is this our piece and the piece intended?
                if (piece != null && piece.getColor() == color && piece.getPieceType() == intent.pieceType()) {
                    
                    // Can this piece geometrically move to intended position?
                    List<Position> validMoves = board.getValidMovesForPiece(piece);
                           
                    if (validMoves.contains(intent.destination())) {
                        
                        // Disambiguation
                        boolean isValidCandidate = true;
                        if (intent.fileDisambiguation() != null) {
                            char pieceFile = (char) ('a' + piece.getPosition().col());
                            if (pieceFile != intent.fileDisambiguation()) isValidCandidate = false;
                        }
                        if (intent.rankDisambiguation() != null) {
                            int pieceRank = 8 - piece.getPosition().row();
                            if (pieceRank != intent.rankDisambiguation()) isValidCandidate = false;
                        }

                        if (isValidCandidate) {
                            candidates.add(piece);
                        }
                    }
                }
            }
        }

        if (candidates.isEmpty()) {
            return null; // No valid moves
        }
        return candidates; // Success! Return the only valid game piece
    }
    
    // ----------------------------------------------------------------
    // Private helper methods 
    // ----------------------------------------------------------------
    
    private Move createCastlingMove(Board board, ParsedIntent intent, PieceColor color) {
        // row 0 : black king's row // row 7 : white king's row
        int homeRow = (color == PieceColor.WHITE) ? 7 : 0; 
        
        GamePiece king = board.getPieceAt(new Position(homeRow, 4)); // King always starts at index 4 (column e)
        GamePiece rook;
        Position kingEnd;
        Position rookEnd;

        if (intent.isKingsideCastle()) { // O-O
            rook = board.getPieceAt(new Position(homeRow, 7)); // Kingside Rook at index 7 (column h)
            kingEnd = new Position(homeRow, 6); // King takes 2 steps to the right (e->g, 4->6)
            rookEnd = new Position(homeRow, 5); // Rook sticks to the King's left (h->f, 7->5)
        } else { // O-O-O
            rook = board.getPieceAt(new Position(homeRow, 0)); // Queenside Rook starts at index 0 (column a)
            kingEnd = new Position(homeRow, 2); // King takes 2 steps to the left (e->c, 4->2)
            rookEnd = new Position(homeRow, 3); // Rook sticks to the King's right (a->d, 0->3)
        }

        if (king == null || rook == null || king.hasMoved() || rook.hasMoved()) {
            throw new IllegalArgumentException("Castling is not allowed (pieces have moved or are missing).");
        }

        return new CastlingMove(king, kingEnd, rook, rookEnd);
    }

    private void validateCaptureNotation(Board board, GamePiece piece, ParsedIntent intent) {
        // Is the move a capture?
        boolean isActualCapture = (board.getPieceAt(intent.destination()) != null) || 
                                  isEnPassant(board, piece, intent.destination());

        // Was the move intended to be a capture?
        boolean isIntentCapture = intent.flags().contains(MoveFlag.CAPTURE);

        // Player intended a capture but the move is not a capture
        if (isIntentCapture && !isActualCapture) {
            throw new IllegalArgumentException("Invalid notation: 'x' used but there is no piece to capture.");
        }
        
        // Player didn't intend a capture but the move is a capture
        if (!isIntentCapture && isActualCapture) {
            throw new IllegalArgumentException("Invalid notation: missing 'x' for a capture move.");
        }
    }
    
    private boolean isPromotion(GamePiece piece, Position dest) {
        if (piece.getPieceType() != PieceType.PAWN) return false;
        
        // White pawn reaches row at index 0 OR black pawn reaches row at index 7.
        return (piece.getColor() == PieceColor.WHITE && dest.row() == 0) || 
               (piece.getColor() == PieceColor.BLACK && dest.row() == 7);
    }
    
    private GamePiece createPromotedPiece(ParsedIntent intent, PieceColor color, Position pos) {
        PieceType type = intent.promotionPiece() != null ? intent.promotionPiece() : PieceType.QUEEN; // Default: Queen
        
        return switch (type) {
            case ROOK -> new Rook(color, pos);
            case BISHOP -> new Bishop(color, pos);
            case KNIGHT -> new Knight(color, pos);
            default -> new Queen(color, pos);
        };
    }

    private Move createPromotionMove(GamePiece pawn, ParsedIntent intent, PieceColor color) {
        GamePiece newPiece = createPromotedPiece(intent, color, intent.destination());
        return new PromotionMove(pawn, intent.destination(), newPiece);
    }
    
    private boolean isEnPassant(Board board, GamePiece piece, Position dest) {
        if (piece.getPieceType() != PieceType.PAWN) return false;
        
        return dest.equals(board.getEnPassantTarget());
    }
    
    private GamePiece getEnPassantVictim(Board board, GamePiece movingPawn, Position dest) {
        // victim will be on the capturing pawn's row, and on the destination column 
        Position victimPos = new Position(movingPawn.getPosition().row(), dest.col());
        return board.getPieceAt(victimPos);
    }

    private Move createEnPassantMove(Board board, GamePiece movingPawn, Position dest) {
        GamePiece capturedPawn = getEnPassantVictim(board, movingPawn, dest);
        return new EnPassantMove(movingPawn, dest, capturedPawn);
    }
}