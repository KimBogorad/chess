package com.chess.parser;

import com.chess.board.Position;
import com.chess.enums.MoveFlag;
import com.chess.enums.PieceType;
import com.chess.enums.CastlingType;
import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    // Regex to analyze chess moves in standard algebraic notation (SAN)
    // [NBRQK]? = piece type (if not specified, it's a pawn)
    // [a-h]?[1-8]? = piece disambiguation (file or rank)
    // (x)? = capture indicator
    // ([a-h][1-8]) = destination square
    // (=[NBRQ])? = corronation piece (for pawn promotion)
    // ([+#])? = check/mate indicator
    private static final Pattern MOVE_PATTERN = 
        Pattern.compile("([NBRQK])?([a-h]|[1-8])?(x)?([a-h][1-8])(=[NBRQ])?([+#])?");

    public ParsedIntent parse(String moveStr) {
        // handle castling moves separately
        if (moveStr.toLowerCase().equals("o-o")) return new ParsedIntent(CastlingType.KINGSIDE);
        if (moveStr.toLowerCase().equals("o-o-o")) return new ParsedIntent(CastlingType.QUEENSIDE);

        Matcher matcher = MOVE_PATTERN.matcher(moveStr);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid move format: " + moveStr);
        }

        // 1. extract piece type (if not specified, it's a pawn)
        PieceType pieceType = parsePieceType(matcher.group(1));

        // 2. extract destination square
        Position destination = parsePosition(matcher.group(4));

        // 3. build the flags enum set - isCaptue, isCheck, isMate
        Set<MoveFlag> flags = EnumSet.noneOf(MoveFlag.class);
        if (matcher.group(3) != null) flags.add(MoveFlag.CAPTURE);
        if (matcher.group(6) != null) {
            String checkOrMate = matcher.group(6);
            if (checkOrMate.equals("+")) flags.add(MoveFlag.CHECK);
            if (checkOrMate.equals("#")) flags.add(MoveFlag.MATE);
        }

        // 4. disambiguation notation if exists (file or rank)
        Character fileDisambiguation = null;
        Integer rankDisambiguation = null;
        if (matcher.group(2) != null) {
            String dis = matcher.group(2);
            if (Character.isLetter(dis.charAt(0))) fileDisambiguation = dis.charAt(0);
            else rankDisambiguation = Character.getNumericValue(dis.charAt(0));
        }

        return new ParsedIntent(pieceType, destination, flags, null, fileDisambiguation, rankDisambiguation, CastlingType.NONE);
    }

    private PieceType parsePieceType(String s) {
        if (s == null) return PieceType.PAWN; // if no piece type is specified, it's a pawn
        return switch (s) {
            case "N" -> PieceType.KNIGHT;
            case "B" -> PieceType.BISHOP;
            case "R" -> PieceType.ROOK;
            case "Q" -> PieceType.QUEEN;
            case "K" -> PieceType.KING;
            default -> PieceType.PAWN;
        };
    }

    private Position parsePosition(String pos) {
        int col = pos.charAt(0) - 'a'; // 'a' -> 0, 'b' -> 1, ..., 'h' -> 7
        int row = 8 - Character.getNumericValue(pos.charAt(1)); // '1' -> 7, '2' -> 6, ..., '8' -> 0
        
        Position pos_intent = new Position(row, col);
        if (!pos_intent.isWithinBounds()) {
            throw new IllegalArgumentException("Invalid position input: " + pos);
        }
        return pos_intent;
    }
}