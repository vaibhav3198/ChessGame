package com.chess.engine.player.AI;

import com.chess.engine.board.board;
import com.chess.engine.board.move;
import com.chess.engine.player.MoveTransition;

public class Minimax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;

    public Minimax(final int searchDepth)
    {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString()
    {
        return "Minimax";
    }

    @Override
    public move execute(board b)   //returns the best move
    {
        final long startTime = System.currentTimeMillis();
        move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;

        System.out.println(b.currentPlayer() + "Thinking with depth = " + this.searchDepth);

        int newMoves = b.currentPlayer().getLegalMoves().size();
        for(final move m: b.currentPlayer().getLegalMoves())
        {
            final MoveTransition moveTransition = b.currentPlayer().makeMove(m);
            if(moveTransition.getMoveStatus().isDone())
            {
                currentValue=b.currentPlayer().getAlliance().isWhite() ?
                        min(moveTransition.getTransitionBoard(),this.searchDepth-1) :
                        max(moveTransition.getTransitionBoard(),this.searchDepth-1);
                if(b.currentPlayer().getAlliance().isWhite())
                {
                    if(currentValue>=highestSeenValue)
                    {
                        highestSeenValue=currentValue;
                        bestMove=m;
                    }
                }
                else if(b.currentPlayer().getAlliance().isBlack())
                {
                    if(currentValue<=lowestSeenValue)
                    {
                        lowestSeenValue=currentValue;
                        bestMove=m;

                    }
                }
            }
        }
        final long executionTime = System.currentTimeMillis() - startTime;
        System.out.println("Execution Time: " + executionTime);
        System.out.println("Number of trees: " + newMoves);
        return bestMove;
    }

    public int min(final board b,final int depth)
    {
        if(depth==0 || isEndGameScenerio(b)) //or gameover
        {
            return this.boardEvaluator.evaluate(b,depth);
        }
        else
        {
            int lowestSeenValue = Integer.MAX_VALUE;
            for (final move m : b.currentPlayer().getLegalMoves())
            {
                final MoveTransition moveTransition = b.currentPlayer().makeMove(m);
                if(moveTransition.getMoveStatus().isDone())
                {
                    final int currentValue = max(moveTransition.getTransitionBoard(),depth-1);
                    if(currentValue <= lowestSeenValue)
                    {
                        lowestSeenValue=currentValue;
                    }
                }
            }
            return lowestSeenValue;
        }
    }

    public int max(final board b, final int depth)
    {
        if(depth==0 || isEndGameScenerio(b)) //or gameover
        {
            return this.boardEvaluator.evaluate(b,depth);
        }
        else
        {
            int highestSeenValue = Integer.MIN_VALUE;
            for (final move m : b.currentPlayer().getLegalMoves())
            {
                final MoveTransition moveTransition = b.currentPlayer().makeMove(m);
                if(moveTransition.getMoveStatus().isDone())
                {
                    final int currentValue = min(moveTransition.getTransitionBoard(),depth-1);
                    if(currentValue >= highestSeenValue)
                    {
                        highestSeenValue=currentValue;
                    }
                }
            }
            return highestSeenValue;
        }
    }

    private static boolean isEndGameScenerio(final board b)
    {
        return b.currentPlayer().isInCheckMate() || b.currentPlayer().isInStaleMate();
    }
}
