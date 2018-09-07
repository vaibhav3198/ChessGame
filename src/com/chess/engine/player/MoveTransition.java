package com.chess.engine.player;

import com.chess.engine.board.board;
import com.chess.engine.board.move;

public class MoveTransition {

    private final board transitionBoard;  //the board after move is made
    private final move move;
    private final MoveStatus moveStatus;

    public MoveTransition(board transitionBoard,final move m,final MoveStatus moveStatus) {
        this.transitionBoard = transitionBoard;
        this.move=m;
        this.moveStatus=moveStatus;
    }

    public MoveStatus getMoveStatus() {
        return this.moveStatus;
    }

    public board getTransitionBoard()
    {
        return this.transitionBoard;
    }
}
