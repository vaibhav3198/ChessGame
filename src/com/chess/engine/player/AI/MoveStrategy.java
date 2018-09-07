package com.chess.engine.player.AI;

import com.chess.engine.board.board;
import com.chess.engine.board.move;

public interface MoveStrategy {

    move execute(board b);
}
