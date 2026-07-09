package com.chess.game;

import com.chess.parser.Parser;
import com.chess.view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        ConsoleView view = new ConsoleView();
        Parser parser = new Parser();

        GameController controller = new GameController(game, view, parser);
        controller.start();
    }
}