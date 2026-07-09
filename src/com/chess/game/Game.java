package com.chess.game;

import com.chess.board.Board;
import com.chess.board.Position;
import com.chess.enums.*;
import com.chess.parser.ParsedIntent;
import com.chess.parser.Parser;
import com.chess.pieces.GamePiece;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {
    private Board board;
    private Parser parser;
    private PieceColor currentPlayer;
    private GameStatus gameStatus;

    public Game() {
        this.board = new Board();
        this.parser = new Parser();
        this.currentPlayer = PieceColor.WHITE; // White always starts (racism aside)
        this.gameStatus = GameStatus.ACTIVE;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Java Chess!");
        printBoard();

        while (gameStatus == GameStatus.ACTIVE) {
            System.out.print("\n" + currentPlayer + "'s turn. Enter move (e.g., e4, Nf3, o-o): ");
            String input = scanner.nextLine().trim();

            try {
                // 1. Read input and parse it into a ParsedIntent object
                ParsedIntent intent = parser.parse(input);

                // 2. Find potential moves for the current player and validate the move
                GamePiece pieceToMove = processAndValidateMove(intent);

                if (pieceToMove != null) {
                    // 3. Execute the move on the board
                    executeMove(pieceToMove, intent);
                    
                    // 4. Update and print the board, then switch players
                    printBoard();
                    switchPlayer();

                    updateGameStatus();
                    
                    // 5. Check for checkmate or stalemate conditions
                    if (this.gameStatus == GameStatus.MATE) {
                        PieceColor winner = (currentPlayer == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
                        System.out.println("Mate! Congratulations to " + winner);
                    }
                    if (this.gameStatus == GameStatus.STALEMATE) {
                        System.out.println("Stalemate! Game ends in a draw");
                    }
                } else {
                    System.out.println("Illegal move! Either geometrically impossible or leaves King in check. Try again.");
                }

            } catch (IllegalArgumentException e) {
                // Catch input errors from the Parser, inform the user, repeat player turn
                System.out.println("Invalid input: " + e.getMessage() + ". Please try again.");
            }
        }
        scanner.close();
    }

    private GamePiece processAndValidateMove(ParsedIntent intent) {
        List<GamePiece> candidatePieces = new ArrayList<>();

        // 1. Traverse the board to find possible pieces to make the move
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                GamePiece piece = board.getPieceAt(new Position(row, col));
                
                // Check piece type
                if (piece != null && piece.getColor() == currentPlayer && piece.getPieceType() == intent.pieceType()) {
                    
                    // 2. Check if piece can geometrically reach the intended position
                    List<Position> validMoves = board.getValidMovesForPiece(piece);
                    if (validMoves.contains(intent.destination())) {
                        
                        // 3. Disambiguation
                        if (intent.fileDisambiguation() != null) {
                            char pieceFile = (char) ('a' + piece.getPosition().col());
                            if (pieceFile != intent.fileDisambiguation()) continue; // not the right col, skip
                        }
                        if (intent.rankDisambiguation() != null) {
                            int pieceRank = 8 - piece.getPosition().row();
                            if (pieceRank != intent.rankDisambiguation()) continue; // not the right row, skip
                        }

                        // 4. Does this move leave my King checked?
                        if (!leavesKingInCheck(piece, intent.destination())) {
                            candidatePieces.add(piece);
                        }
                    }
                }
            }
        }

        // 5. Verify legal move: can only be one legal move
        if (candidatePieces.isEmpty()) {
            return null; // Illegal move
        }
        if (candidatePieces.size() > 1) {
            // Ambiguous - need new disambiguous input
            throw new IllegalArgumentException("Ambiguous move. Please specify file or rank (e.g., Ndf3).");
        }

        return candidatePieces.get(0); // Success: return the only valid piece to make the move
}

    private void executeMove(GamePiece piece, ParsedIntent intent) {
    Position originalPos = piece.getPosition();
    Position destPos = intent.destination();

    // 1. Move piece on the board
    board.setPieceAt(originalPos, null);
    board.setPieceAt(destPos, piece);
    
    // 2. Update the piece's inner position record
    piece.setPosition(destPos);

    // 3. Update hasMoved for valid pieces
    if (piece.getPieceType() == PieceType.PAWN || 
        piece.getPieceType() == PieceType.ROOK || 
        piece.getPieceType() == PieceType.KING) {
        piece.setHasMoved(true);
    }
    
    // NEED TO ADD EN PASSANT, PROMOTION AND CASTLING LOGIC HERE
}

    private void switchPlayer() {
        currentPlayer = (currentPlayer == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }

    private void updateGameStatus() {
        boolean hasAnyLegalMove = false;

        // Iterate through the board to find enemy pieces.
        // used labe; searchloop to break both loops at once.
        searchLoop: 
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                GamePiece piece = board.getPieceAt(new Position(row, col));
                
                if (piece != null && piece.getColor() == currentPlayer) {
                    List<Position> potentialMoves = board.getValidMovesForPiece(piece);
                
                    for (Position dest : potentialMoves) {
                        if (!leavesKingInCheck(piece, dest)) {
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
        PieceColor enemyColor = (kingColor == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                GamePiece piece = board.getPieceAt(new Position(row, col));
                
                if (piece != null && piece.getColor() == enemyColor) {
                    List<Position> validMoves = board.getValidMovesForPiece(piece);
                    
                    if (validMoves.contains(kingPos)) {
                        return true; // Check!
                    }
                }
            }
        }

        return false; // No check!
    }

    private boolean leavesKingInCheck(GamePiece piece, Position destination) {
        Position originalPos = piece.getPosition();
        GamePiece capturedPiece = board.getPieceAt(destination); // Save the game piece at destination (can be null!)

        // Simulate move temporarily
        board.setPieceAt(originalPos, null);
        board.setPieceAt(destination, piece);
        piece.setPosition(destination);

        // Check if checked
        boolean inCheck = isKingInCheck(currentPlayer);

        // Undo the last move (if King is checked, we don't want to make the move)
        // We have to rollback because in the processAndvalidateMove main loop we might have more than one piece (ERROR that we catch later)
        board.setPieceAt(destination, capturedPiece);
        board.setPieceAt(originalPos, piece);
        piece.setPosition(originalPos);

        return inCheck;
    }

    // --------------------------------------------------------
    // Print board logic
    // --------------------------------------------------------
    
    private void printBoard() {
        // ANSI - Color-coding for terminal output
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_WHITE_PIECE = "\u001B[97m"; // // Bright white
        final String ANSI_BLACK_PIECE = "\u001B[34m"; // Blue (Because Black won't stand out)
        
        // קודי צבע לרקע (משבצות)
        final String ANSI_BG_WHITE_SQUARE = "\u001B[47m"; // background - white (bright gray)
        final String ANSI_BG_BLACK_SQUARE = "\u001B[100m"; // background - black (dark gray)

        System.out.println("\n   a  b  c  d  e  f  g  h"); // Column headings

        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + " "); // Row numbers on the left side

            for (int col = 0; col < 8; col++) {
                GamePiece piece = board.getPieceAt(new Position(row, col));

                // even index = white cell, else - black cell
                boolean isWhiteSquare = (row + col) % 2 == 0; 
                
                // choose background color for cell
                String bgCode = isWhiteSquare ? ANSI_BG_WHITE_SQUARE : ANSI_BG_BLACK_SQUARE;

                if (piece == null) {
                    // Print with 3 spaces to create the image of a "cell"
                    System.out.print(bgCode + "   " + ANSI_RESET);
                } else {
                    // Choose piece color
                    String colorCode = (piece.getColor() == PieceColor.WHITE) ? ANSI_WHITE_PIECE : ANSI_BLACK_PIECE; 
                    String pieceSymbol = getPieceSymbol(piece);
                    
                    // Cell occupied
                    System.out.print(bgCode + colorCode + " " + pieceSymbol + " " + ANSI_RESET);
                }
            }
            System.out.println(" " + (8 - row)); // Row numbers on the right
        }
        System.out.println("   a  b  c  d  e  f  g  h\n"); // Column headings for bottom of board
    }

    private String getPieceSymbol(GamePiece piece) {
        return switch (piece.getPieceType()) {
            case PAWN -> "P";
            case KNIGHT -> "N";
            case BISHOP -> "B";
            case ROOK -> "R";
            case QUEEN -> "Q";
            case KING -> "K";
        };
    }
}