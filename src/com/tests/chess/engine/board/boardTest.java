package com.tests.chess.engine.board;

import com.chess.engine.board.board;
import com.chess.engine.board.boardUtils;
import com.chess.engine.board.move;
import com.chess.engine.player.AI.Minimax;
import com.chess.engine.player.AI.MoveStrategy;
import com.chess.engine.player.MoveTransition;
import org.junit.Test;

import static junit.framework.Assert.*;

public class boardTest {

    @Test
    public void initialBoard() {

        final board b = board.createStandardBoard();
        assertEquals(b.currentPlayer().getLegalMoves().size(), 20);
        assertEquals(b.currentPlayer().getOpponent().getLegalMoves().size(), 20);
        assertFalse(b.currentPlayer().isInCheck());
        assertFalse(b.currentPlayer().isInCheckMate());
        assertFalse(b.currentPlayer().isCastled());
        assertEquals(b.currentPlayer(), b.whitePlayer());
        assertEquals(b.currentPlayer().getOpponent(), b.blackPlayer());
        assertFalse(b.currentPlayer().getOpponent().isInCheck());
        assertFalse(b.currentPlayer().getOpponent().isInCheckMate());
        assertFalse(b.currentPlayer().getOpponent().isCastled());

    }

    @Test
    public void testFoolsMate()
    {
        final board b = board.createStandardBoard();
        final MoveTransition t1= b.currentPlayer().makeMove(move.MoveFactory.createMove(b,
                                 boardUtils.getCoordinateAtPosition("f2"),
                                 boardUtils.getCoordinateAtPosition("f3")));
        assertTrue(t1.getMoveStatus().isDone());

        final MoveTransition t2= t1.getTransitionBoard().currentPlayer().makeMove(move.MoveFactory.createMove(t1.getTransitionBoard(),
                boardUtils.getCoordinateAtPosition("e7"),
                boardUtils.getCoordinateAtPosition("e5")));
        assertTrue(t2.getMoveStatus().isDone());

        final MoveTransition t3= t2.getTransitionBoard().currentPlayer().makeMove(move.MoveFactory.createMove(t2.getTransitionBoard(),
                boardUtils.getCoordinateAtPosition("g2"),
                boardUtils.getCoordinateAtPosition("g4")));
        assertTrue(t3.getMoveStatus().isDone());

        final MoveStrategy strategy = new Minimax(4);
        final move aiMove = strategy.execute(t3.getTransitionBoard());

        final move bestMove = move.MoveFactory.createMove(t3.getTransitionBoard(),
                boardUtils.getCoordinateAtPosition("d8"),
                boardUtils.getCoordinateAtPosition("h4"));

        assertEquals(aiMove,bestMove);
    }
}