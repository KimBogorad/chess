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

    // גודל קבוע למשבצת (כדי שנדע לאיזה גודל לצבוע את התמונות)
    private static final int SQUARE_SIZE = 70;

    // מילון שיאחסן את התמונות המוכנות (ImageIcon) לכל סוג כלי וצבע
    // המפתח יהיה מחרוזת כמו "w_queen" או "b_knight"
    private Map<String, ImageIcon> pieceIcons;

    public SwingView() {
        initializeUI();
        loadPieceIcons(); // טעינת התמונות מיד עם יצירת ה-View
    }

    private void initializeUI() {
        frame = new JFrame("Java Chess Engine - Custom Figures");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // גודל מחושב לפי גודל המשבצת + קצת מרווח
        frame.setSize(SQUARE_SIZE * 8 + 20, SQUARE_SIZE * 8 + 100);
        frame.setLayout(new BorderLayout());

        // כותרת עליונה
        statusLabel = new JLabel("Welcome! Custom Figures Loaded.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(statusLabel, BorderLayout.NORTH);

        // לוח המשחק (Grid של 8x8)
        boardPanel = new JPanel(new GridLayout(8, 8));
        squares = new JButton[8][8];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton button = new JButton();
                // מורידים את הטקסט והגופן הגדול, לא צריך אותם יותר
                button.setFocusPainted(false);
                button.setOpaque(true);
                button.setBorderPainted(false);
                button.setRolloverEnabled(false);
                
                // צביעת המשבצות
                if ((row + col) % 2 == 0) {
                    button.setBackground(new Color(240, 217, 181));
                } else {
                    button.setBackground(new Color(181, 136, 99));
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

    // מתודה חדשה וקריטית: טעינה של 12 התמונות כמשאבים
    private void loadPieceIcons() {
        pieceIcons = new HashMap<>();
        String[] colors = {"w", "b"};
        String[] types = {"king", "queen", "rook", "bishop", "knight", "pawn"};

        for (String color : colors) {
            for (String type : types) {
                String filename = color + "_" + type + ".png"; // למשל: w_king.png
                
                // טעינת הקובץ כמשאב מהפרויקט (לא מהדיסק הקשיח האבסולוטי)
                // ה-path הוא יחסי לתיקיית ה-src שלך.
                String path = "/com/chess/resources/images/" + filename;
                
                try (InputStream is = getClass().getResourceAsStream(path)) {
                    if (is == null) {
                        System.err.println("Could not find resource: " + path);
                        continue;
                    }
                    
                    // קריאת התמונה המקורית
                    BufferedImage originalImage = ImageIO.read(is);
                    
                    // צביעה (Resize) של התמונה לגודל המשבצת
                    // SCALE_SMOOTH מבטיח איכות טובה של הקיטום
                    Image scaledImage = originalImage.getScaledInstance(
                            SQUARE_SIZE - 10, SQUARE_SIZE - 10, Image.SCALE_SMOOTH);
                    
                    // שמירת ה-ImageIcon המוכנה במילון
                    String key = color + "_" + type; // למשל: w_king
                    pieceIcons.put(key, new ImageIcon(scaledImage));
                    
                } catch (IOException | NullPointerException e) {
                    System.err.println("Error loading image: " + filename + ". " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void displayBoard(Board board, PieceColor currentPlayer) {
        this.currentBoard = board;
        this.currentPlayer = currentPlayer;
        this.selectedPosition = null;

        statusLabel.setText(currentPlayer + "'s Turn");

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                GamePiece piece = board.getPieceAt(new Position(row, col));
                JButton button = squares[row][col];
                
                // איפוס צבע הרקע
                if ((row + col) % 2 == 0) {
                    button.setBackground(new Color(240, 217, 181));
                } else {
                    button.setBackground(new Color(181, 136, 99));
                }

                // השינוי המרכזי: במקום setText, משתמשים ב-setIcon
                if (piece != null) {
                    // יצירת המפתח לפי צבע וסוג הכלי של המודל
                    String colorKey = piece.getColor() == PieceColor.WHITE ? "w" : "b";
                    String typeKey = piece.getPieceType().toString().toLowerCase(); // KNIGHT -> knight
                    String key = colorKey + "_" + typeKey;

                    // שליפת ה-ImageIcon מהמילון
                    ImageIcon icon = pieceIcons.get(key);
                    if (icon != null) {
                        button.setText(""); // מוודאים שאין טקסט
                        button.setIcon(icon); // קביעת התמונה
                    } else {
                        // אם אין תמונה (fallback), נשתמש באות הראשונה של הכלי
                        button.setIcon(null);
                        button.setText(getFallbackLetter(piece));
                    }
                } else {
                    button.setText(""); // משבצת ריקה - מנקים הכל
                    button.setIcon(null);
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
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    @Override
    public void showMessage(String message) {
        statusLabel.setText(message);
    }

    @Override
    public void showError(String error) {
        // מציג את השגיאה באדום
        statusLabel.setText("<html><font color='red'>Invalid: " + error + "</font></html>");
    }

    @Override
    public void showGameOver(GameStatus status, PieceColor winner) {
        String msg = status == GameStatus.MATE ? "CHECKMATE! " + winner + " wins!" : status + "! Draw.";
        statusLabel.setText(msg);
        // מציג חלון קופץ נחמד בסיום
        JOptionPane.showMessageDialog(frame, msg, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- לוגיקת הלחיצות והתרגום (נשארת אותו דבר) ---

    private void handleSquareClick(Position pos) {
        if (currentBoard == null || moveConsumer == null) return;

        if (selectedPosition == null) {
            GamePiece piece = currentBoard.getPieceAt(pos);
            if (piece != null && piece.getColor() == currentPlayer) {
                selectedPosition = pos;
                // סימון הכלי הנבחר (עובד מעולה גם עם תמונות שקופות)
                // בדיקה האם המשבצת שעליה לחצנו היא בהירה או כהה
                boolean isLightSquare = (pos.row() + pos.col()) % 2 == 0;

                if (isLightSquare) {
                    // צבע אטום למשבצת בהירה מודגשת
                    squares[pos.row()][pos.col()].setBackground(new Color(247, 247, 105)); 
                } else {
                    // צבע אטום למשבצת כהה מודגשת
                    squares[pos.row()][pos.col()].setBackground(new Color(186, 202, 68)); 
                }
            }
        } 
        else {
            if (pos.equals(selectedPosition)) {
                // לחיצה חוזרת - איפוס הלוח (מנקה את הסימון הצהוב)
                displayBoard(currentBoard, currentPlayer); 
            } else {
                String sanMove = convertClicksToSan(selectedPosition, pos);
                if (!sanMove.isEmpty()) {
                    moveConsumer.accept(sanMove); // קריאה חזרה (Callback) לקונטרולר!
                }
            }
        }
    }

    private String convertClicksToSan(Position from, Position to) {
        GamePiece piece = currentBoard.getPieceAt(from);
        if (piece == null) return "";

        // הצרחה
        if (piece.getPieceType() == PieceType.KING) {
            if (from.col() == 4 && to.col() == 6) return "O-O";
            if (from.col() == 4 && to.col() == 2) return "O-O-O";
        }

        StringBuilder san = new StringBuilder();
        char fromFile = (char) ('a' + from.col());
        boolean isCapture = currentBoard.getPieceAt(to) != null || to.equals(currentBoard.getEnPassantTarget());

        if (piece.getPieceType() != PieceType.PAWN) {
            san.append(getPieceLetter(piece.getPieceType()));
            san.append(fromFile); 
        } else if (isCapture) {
            san.append(fromFile); // exd5
        }

        if (isCapture) {
            san.append('x');
        }

        san.append((char) ('a' + to.col()));
        san.append(8 - to.row());

        // הכתרה (ברירת מחדל למלכה)
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

    // פונקציית עזר למקרה שאין תמונה
    private String getFallbackLetter(GamePiece piece) {
        return (piece.getColor() == PieceColor.WHITE ? "W" : "B") + getPieceLetter(piece.getPieceType());
    }
}