package com.chess.view;

import com.chess.board.Board;
import com.chess.board.Position;
import com.chess.enums.GameStatus;
import com.chess.enums.PieceColor;
import com.chess.enums.PieceType;
import com.chess.pieces.GamePiece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SwingView implements ChessView {
    private JFrame frame;
    private JPanel boardPanel;
    private JButton[][] squares;
    private JLabel statusLabel;
    
    private Consumer<String> moveConsumer;
    private Board currentBoard;
    private Position selectedPosition = null;
    private PieceColor currentPlayer;

    // Fixed square size for calculation and rendering
    private static final int SQUARE_SIZE = 70;

    // Cache map to store pre-loaded and pre-scaled ImageIcons
    private Map<String, ImageIcon> pieceIcons;

    public SwingView() {
        initializeUI();
        loadPieceIcons();
    }

    private void initializeUI() {
        frame = new JFrame("Java Chess Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(SQUARE_SIZE * 8 + 20, SQUARE_SIZE * 8 + 100);
        frame.setLayout(new BorderLayout());

        // Status bar setup
        statusLabel = new JLabel("Welcome to Java Chess!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(statusLabel, BorderLayout.NORTH);

        // Grid container setup for the 8x8 board layout
        boardPanel = new JPanel(new GridLayout(8, 8));
        squares = new JButton[8][8];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton button = new JButton();
                button.setFocusPainted(false);
                button.setOpaque(true);
                button.setBorderPainted(false);
                button.setRolloverEnabled(false); // Disables native OS hover animations to prevent rendering artifacts
                
                // Classic wooden board color themes
                if ((row + col) % 2 == 0) {
                    button.setBackground(new Color(240, 217, 181)); // Light square
                } else {
                    button.setBackground(new Color(181, 136, 99));  // Dark square
                }

                final int r = row;
                final int c = col;
                button.addActionListener(e -> handleSquareClick(new Position(r, c)));

                squares[row][col] = button;
                boardPanel.add(button);
            }
        }

        frame.add(boardPanel, BorderLayout.CENTER);
    }

    /**
     * Loads image assets from resources and stores them as scaled ImageIcons in the cache map.
     */
    private void loadPieceIcons() {
        pieceIcons = new HashMap<>();
        String[] colors = {"w", "b"};
        String[] types = {"king", "queen", "rook", "bishop", "knight", "pawn"};

        for (String color : colors) {
            for (String type : types) {
                String filename = color + "_" + type + ".png";
                String path = "/com/chess/resources/images/" + filename;
                
                try (InputStream is = getClass().getResourceAsStream(path)) {
                    if (is == null) {
                        System.err.println("Could not find resource: " + path);
                        continue;
                    }
                    
                    BufferedImage originalImage = ImageIO.read(is);
                    
                    // Smooth scaling to fit the grid buttons neatly
                    Image scaledImage = originalImage.getScaledInstance(
                            SQUARE_SIZE - 10, SQUARE_SIZE - 10, Image.SCALE_SMOOTH);
                    
                    String key = color + "_" + type;
                    pieceIcons.put(key, new ImageIcon(scaledImage));
                    
                } catch (IOException | NullPointerException e) {
                    System.err.println("Error loading image: " + filename + ". " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void onMoveSubmitted(Consumer<String> moveConsumer) {
        this.moveConsumer = moveConsumer;
    }

    @Override
    public void startInputLoop() {
        // Event-driven GUI triggers window visibility instead of running a blocking loop
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    @Override
    public void displayBoard(Board board, PieceColor currentPlayer) {
        this.currentBoard = board;
        this.currentPlayer = currentPlayer;
        this.selectedPosition = null; // Clear selections at the beginning of each turn

        statusLabel.setText(currentPlayer + "'s Turn");

        // The board rotates dynamically if it is Black's turn
        boolean rotate = (currentPlayer == PieceColor.BLACK);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // Invert array indices when rendering for the black perspective
                int displayRow = rotate ? (7 - row) : row;
                int displayCol = rotate ? (7 - col) : col;

                GamePiece piece = board.getPieceAt(new Position(displayRow, displayCol));
                JButton button = squares[row][col]; 
                
                // Reset to standard background patterns
                if ((row + col) % 2 == 0) {
                    button.setBackground(new Color(240, 217, 181));
                } else {
                    button.setBackground(new Color(181, 136, 99));
                }

                if (piece != null) {
                    String colorKey = piece.getColor() == PieceColor.WHITE ? "w" : "b";
                    String typeKey = piece.getPieceType().toString().toLowerCase();
                    String key = colorKey + "_" + typeKey;

                    ImageIcon icon = pieceIcons.get(key);
                    if (icon != null) {
                        button.setText(""); 
                        button.setIcon(icon);
                    } else {
                        // Fallback text rendering if asset files are missing
                        button.setIcon(null);
                        button.setText(getFallbackLetter(piece));
                    }
                } else {
                    button.setText(""); 
                    button.setIcon(null);
                }
            }
        }
    }

    @Override
    public void showMessage(String message) {
        statusLabel.setText(message);
    }

    @Override
    public void showError(String error) {
        statusLabel.setText("<html><font color='red'>Invalid: " + error + "</font></html>");
    }

    @Override
    public void showGameOver(GameStatus status, PieceColor winner) {
        String msg = status == GameStatus.MATE ? "CHECKMATE! " + winner + " wins!" : status + "! Draw.";
        statusLabel.setText(msg);
        JOptionPane.showMessageDialog(frame, msg, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Translates grid clicks to game-state positions, supporting board-rotation transformations.
     */
    private void handleSquareClick(Position pos) {
        if (currentBoard == null || moveConsumer == null) return;

        // Convert the structural view click coordinate into the underlying logical board coordinate
        boolean rotate = (currentPlayer == PieceColor.BLACK);
        int realRow = rotate ? (7 - pos.row()) : pos.row();
        int realCol = rotate ? (7 - pos.col()) : pos.col();
        Position realPos = new Position(realRow, realCol);

        // State 1: Selecting a source piece
        if (selectedPosition == null) {
            GamePiece piece = currentBoard.getPieceAt(realPos);
            if (piece != null && piece.getColor() == currentPlayer) {
                selectedPosition = realPos;
                
                // Solid color highlighting handles light vs dark squares perfectly without alpha ghosting
                boolean isLightSquare = (pos.row() + pos.col()) % 2 == 0;
                if (isLightSquare) {
                    squares[pos.row()][pos.col()].setBackground(new Color(247, 247, 105)); 
                } else {
                    squares[pos.row()][pos.col()].setBackground(new Color(186, 202, 68)); 
                }
            }
        } 
        // State 2: Submitting target destination or deselecting
        else {
            if (realPos.equals(selectedPosition)) {
                // Clicked the same piece twice -> deselect and redraw
                displayBoard(currentBoard, currentPlayer); 
            } else {
                // Translate click positions to SAN format string and pass asynchronously via callback
                String sanMove = convertClicksToSan(selectedPosition, realPos);
                if (!sanMove.isEmpty()) {
                    moveConsumer.accept(sanMove); 
                }
            }
        }
    }

    /**
     * Helper logic constructing accurate Standard Algebraic Notation string from coordinates.
     */
    private String convertClicksToSan(Position from, Position to) {
        GamePiece piece = currentBoard.getPieceAt(from);
        if (piece == null) return "";

        // Check castling intents explicitly
        if (piece.getPieceType() == PieceType.KING) {
            if (from.col() == 4 && to.col() == 6) return "O-O";
            if (from.col() == 4 && to.col() == 2) return "O-O-O";
        }

        StringBuilder san = new StringBuilder();
        char fromFile = (char) ('a' + from.col());
        boolean isCapture = currentBoard.getPieceAt(to) != null || to.equals(currentBoard.getEnPassantTarget());

        if (piece.getPieceType() != PieceType.PAWN) {
            san.append(getPieceLetter(piece.getPieceType()));
            san.append(fromFile); // Inject origin file token by default to prevent notation ambiguity
        } else if (isCapture) {
            san.append(fromFile); 
        }

        if (isCapture) {
            san.append('x');
        }

        san.append((char) ('a' + to.col()));
        san.append(8 - to.row());

        // Default pawn promotion maps automatically to a Queen
        if (piece.getPieceType() == PieceType.PAWN && (to.row() == 0 || to.row() == 7)) {
            san.append("=Q");
        }

        return san.toString();
    }

    private String getPieceLetter(PieceType type) {
        return switch (type) {
            case KNIGHT -> "N";
            case BISHOP -> "B";
            case ROOK -> "R";
            case QUEEN -> "Q";
            case KING -> "K";
            default -> "";
        };
    }

    private String getFallbackLetter(GamePiece piece) {
        return (piece.getColor() == PieceColor.WHITE ? "W" : "B") + getPieceLetter(piece.getPieceType());
    }
}