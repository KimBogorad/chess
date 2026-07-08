package com.chess.game;

import com.chess.board.Board;
import com.chess.board.Position;
import com.chess.enums.PieceColor;
import com.chess.parser.ParsedIntent;
import com.chess.parser.Parser;
import com.chess.pieces.GamePiece;

import java.util.Scanner;

public class Game {
    private Board board;
    private Parser parser;
    private PieceColor currentPlayer;
    private boolean isGameOver;

    public Game() {
        this.board = new Board();
        this.parser = new Parser();
        this.currentPlayer = PieceColor.WHITE; // לבן תמיד מתחיל בשחמט!
        this.isGameOver = false;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Java Chess!");
        printBoard();

        while (!isGameOver) {
            System.out.print("\n" + currentPlayer + "'s turn. Enter move (e.g., e4, Nf3): ");
            String input = scanner.nextLine().trim();

            try {
                // 1. קריאת קלט ופירוק לכוונת משתמש
                ParsedIntent intent = parser.parse(input);

                // 2. מציאת הכלים הפוטנציאליים ובדיקת חוקיות (רמה 2 + 3)
                boolean isValid = processAndValidateMove(intent);

                if (isValid) {
                    // 3. ביצוע המהלך בפועל
                    executeMove(intent);
                    
                    // 4. עדכון והדפסה
                    printBoard();
                    switchPlayer();
                    
                    // TODO: בדיקה האם עכשיו יש שחמט או פט על השחקן הבא, ואם כן -> isGameOver = true
                } else {
                    System.out.println("Illegal move! Either geometrically impossible or leaves King in check. Try again.");
                }

            } catch (IllegalArgumentException e) {
                // תופס שגיאות תחביר מה-Parser (כמו "z9" או קלט לא הגיוני)
                System.out.println("Invalid input: " + e.getMessage() + ". Please try again.");
            }
        }
        scanner.close();
    }

    private boolean processAndValidateMove(ParsedIntent intent) {
        // TODO: כאן תבוא הלוגיקה המורכבת:
        // 1. למצוא את כל הכלים של ה-currentPlayer מסוג intent.pieceType()
        // 2. לסנן מתוכם את מי שה-Board מאשר לו להגיע ל-intent.destination()
        // 3. לפתור כפילויות (Disambiguation) אם מצאנו שניים כאלו
        // 4. לבצע סימולציה זמנית על הלוח
        // 5. לבדוק האם המלך שלנו נשאר בשח (רמה 3)
        // 6. לבטל את הסימולציה ולהחזיר true או false בהתאם

        return true; // כרגע תמיד מחזיר true כדי שהשלד ירוץ
    }

    private void executeMove(ParsedIntent intent) {
        // TODO: להזיז בפועל את הכלי במטריצה של ה-Board, לטפל באכילות, הצרחות וכו'
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }

    // --------------------------------------------------------
    // לוגיקת הדפסת הלוח (הצגה חזותית)
    // --------------------------------------------------------
    
    private void printBoard() {
        // קודי צבע להדפסה בטרמינל (ANSI)
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_WHITE_PIECE = "\u001B[97m"; // לבן בוהק
        final String ANSI_BLACK_PIECE = "\u001B[34m"; // כחול (שחור נבלע ברקע של הטרמינל, כחול או אדום בולטים יותר)
        
        System.out.println("\n  a b c d e f g h"); // כותרות העמודות

        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + " "); // מספר השורה (8 עד 1)

            for (int col = 0; col < 8; col++) {
                GamePiece piece = board.getPieceAt(new Position(row, col));

                // הטריק המתמטי למשבצות: אם סכום האינדקסים זוגי - המשבצת לבנה, אם אי-זוגי - שחורה
                boolean isWhiteSquare = (row + col) % 2 == 0;
                String squareBackground = isWhiteSquare ? "_" : " "; // '_' ללבן, ' ' לשחור

                if (piece == null) {
                    System.out.print(squareBackground + " ");
                } else {
                    String colorCode = (piece.getColor() == PieceColor.WHITE) ? ANSI_WHITE_PIECE : ANSI_BLACK_PIECE;
                    // אנחנו נצטרך פונקציה getSymbol() בכל כלי כדי להחזיר את האות שלו
                    String pieceSymbol = getPieceSymbol(piece);
                    System.out.print(colorCode + pieceSymbol + ANSI_RESET + squareBackground);
                }
            }
            System.out.println(" " + (8 - row)); // מספר השורה גם בצד השני
        }
        System.out.println("  a b c d e f g h\n");
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