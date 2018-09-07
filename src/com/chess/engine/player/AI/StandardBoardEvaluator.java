package com.chess.engine.player.AI;

import com.chess.engine.board.board;
import com.chess.engine.pieces.piece;
import com.chess.engine.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator
{
    private static final int CHECK_BONUS = 50;
    private static final int CHECK_MATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;

    @Override
    public int evaluate(final board b,final int depth)
    {
        return scorePlayer(b,b.whitePlayer(),depth) - scorePlayer(b,b.blackPlayer(),depth);
    }

    private int scorePlayer(final board b, final Player player, final int depth)
    {
        return pieceValue(player) +
                mobility(player) +
                check(player) +
                checkmate(player,depth) +
                castled(player);
    }

    private static int castled(final Player player)
    {
        return player.isCastled() ? CASTLE_BONUS :0;
    }

    private static int checkmate(final Player player, final int depth)
    {
        return player.getOpponent().isInCheckMate() ? CHECK_MATE_BONUS * depthBonus(depth) : 0;
    }

    private static int depthBonus(final int depth)
    {
        return depth==0 ? 1 : DEPTH_BONUS*depth;
    }

    private static int check(final Player player)
    {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private static int mobility(final Player player)  //TODO: can be improve based on contol of squares
    {
        return player.getLegalMoves().size();
    }

    private static int pieceValue(final Player player)
    {
        int pieceValueScore = 0;
        for (final piece p: player.getActivePieces())
        {
            pieceValueScore += p.getPieceValue();
        }
        return pieceValueScore;
    }
}
