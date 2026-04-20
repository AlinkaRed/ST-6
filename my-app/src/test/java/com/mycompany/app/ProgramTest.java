package com.mycompany.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GridLayout;
import java.lang.reflect.Field;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class ProgramTest {
  @Test
  void gameInitializesWithCleanBoardAndPlayers() {
    Game game = new Game();

    assertEquals(State.PLAYING, game.state);
    assertEquals('X', game.player1.symbol);
    assertEquals('O', game.player2.symbol);
    for (char c : game.board) {
      assertEquals(' ', c);
    }
  }

  @Test
  void checkStateDetectsXWin() {
    Game game = new Game();
    game.symbol = 'X';
    char[] board = {'X', 'X', 'X', ' ', 'O', ' ', ' ', ' ', 'O'};

    assertEquals(State.XWIN, game.checkState(board));
  }

  @Test
  void checkStateDetectsOWin() {
    Game game = new Game();
    game.symbol = 'O';
    char[] board = {'O', 'X', 'X', 'X', 'O', ' ', ' ', ' ', 'O'};

    assertEquals(State.OWIN, game.checkState(board));
  }

  @Test
  void checkStateDetectsDraw() {
    Game game = new Game();
    game.symbol = 'X';
    char[] board = {'X', 'O', 'X', 'X', 'O', 'O', 'O', 'X', 'X'};

    assertEquals(State.DRAW, game.checkState(board));
  }

  @Test
  void generateMovesCollectsAllEmptyCells() {
    Game game = new Game();
    char[] board = {'X', ' ', 'O', ' ', 'X', ' ', ' ', 'O', ' '};
    ArrayList<Integer> moves = new ArrayList<>();

    game.generateMoves(board, moves);

    assertEquals(5, moves.size());
    assertEquals(1, moves.get(0));
    assertEquals(8, moves.get(4));
  }

  @Test
  void evaluatePositionReturnsExpectedScoreForPlayer() {
    Game game = new Game();
    Player xPlayer = game.player1;
    Player oPlayer = game.player2;

    game.symbol = 'X';
    char[] xWinBoard = {'X', 'X', 'X', ' ', 'O', ' ', ' ', ' ', 'O'};
    assertEquals(Game.INF, game.evaluatePosition(xWinBoard, xPlayer));
    assertEquals(-Game.INF, game.evaluatePosition(xWinBoard, oPlayer));

    game.symbol = 'O';
    char[] oWinBoard = {'O', 'X', 'X', 'X', 'O', ' ', ' ', ' ', 'O'};
    assertEquals(Game.INF, game.evaluatePosition(oWinBoard, oPlayer));
    assertEquals(-Game.INF, game.evaluatePosition(oWinBoard, xPlayer));

    game.symbol = 'X';
    char[] drawBoard = {'X', 'O', 'X', 'X', 'O', 'O', 'O', 'X', 'X'};
    assertEquals(0, game.evaluatePosition(drawBoard, xPlayer));
  }

  @Test
  void minimaxFindsImmediateWinningMoveForO() {
    Game game = new Game();
    char[] board = {'O', 'O', ' ', 'X', 'X', ' ', ' ', ' ', ' '};

    int move = game.miniMax(board, game.player2);

    assertEquals(3, move);
  }

  @Test
  void minMoveReturnsFinalScoreWhenPositionIsTerminal() {
    Game game = new Game();
    game.symbol = 'X';
    char[] board = {'X', 'X', 'X', ' ', 'O', ' ', ' ', ' ', 'O'};

    assertEquals(Game.INF, game.minMove(board, game.player1));
  }

  @Test
  void maxMoveReturnsFinalScoreWhenPositionIsTerminal() {
    Game game = new Game();
    game.symbol = 'O';
    char[] board = {'O', 'X', 'X', 'X', 'O', ' ', ' ', ' ', 'O'};

    assertEquals(Game.INF, game.maxMove(board, game.player2));
  }

  @Test
  void ticTacToeCellStoresCoordinatesAndMarker() {
    TicTacToeCell cell = new TicTacToeCell(4, 1, 1);

    assertEquals(4, cell.getNum());
    assertEquals(1, cell.getRow());
    assertEquals(1, cell.getCol());
    assertEquals(' ', cell.getMarker());

    cell.setMarker("X");
    assertEquals('X', cell.getMarker());
    assertFalse(cell.isEnabled());
  }

  @Test
  void utilityPrintMethodsCanBeCalled() {
    Utility.print(new char[] {'X', 'O', 'X', ' ', ' ', ' ', ' ', ' ', ' '});
    Utility.print(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9});
    ArrayList<Integer> moves = new ArrayList<>();
    moves.add(0);
    moves.add(4);
    Utility.print(moves);
  }

  @Test
  void panelHandlesFirstMoveWithoutFinishingGame() throws Exception {
    System.setProperty("java.awt.headless", "true");
    TicTacToePanel panel = new TicTacToePanel(new GridLayout(3, 3));

    Field cellsField = TicTacToePanel.class.getDeclaredField("cells");
    cellsField.setAccessible(true);
    TicTacToeCell[] cells = (TicTacToeCell[]) cellsField.get(panel);

    cells[0].doClick();

    assertEquals('X', cells[0].getMarker());

    boolean hasOMove = false;
    for (int i = 1; i < cells.length; i++) {
      if (cells[i].getMarker() == 'O') {
        hasOMove = true;
        break;
      }
    }
    assertTrue(hasOMove);

    Field gameField = TicTacToePanel.class.getDeclaredField("game");
    gameField.setAccessible(true);
    Game game = (Game) gameField.get(panel);
    assertEquals(State.PLAYING, game.state);
  }
}
