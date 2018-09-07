package com.chess.engine.player.AI;

import com.chess.engine.board.board;

public interface BoardEvaluator {

    int evaluate(board b,int depth);
}
