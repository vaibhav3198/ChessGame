package com.chess.engine.player;

import com.chess.engine.alliance;
import com.chess.engine.board.board;
import com.chess.engine.board.move;
import com.chess.engine.pieces.king;
import com.chess.engine.pieces.piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class Player {

    protected final board gameBoard;
    protected final king playerKing;
    protected final Collection<move> legalMoves;
    private final boolean isInCheck;

    Player(final board b, final Collection<move> legalMoves, final Collection<move> opponentMoves){
        this.gameBoard = b;
        this.playerKing = establishKing();
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves,calculateKingCastles(legalMoves,opponentMoves)));
        this.isInCheck=!Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(),opponentMoves).isEmpty();
    }

    public king getPlayerKing()
    {
        return this.playerKing;
    }

    public Collection<move> getLegalMoves()
    {
        return this.legalMoves;
    }

    protected static Collection<move> calculateAttacksOnTile(int piecePosition, Collection<move> moves) {
        final List<move> attackMoves = new ArrayList<>();
        for(final move m: moves)
        {
            if(piecePosition == m.getDestinationCoordinate())
            {
                attackMoves.add(m);
            }
        }
        return Collections.unmodifiableList(attackMoves);
    }

    private king establishKing(){
        for(final piece p1 :getActivePieces()){
            if(p1.getPieceType().isKing())
            {
                return (king)p1;
            }

        }
        throw new RuntimeException("Not a valid board, No king!");
    }

    public boolean isMoveLegal(final move m)  //checks if the move passed in is in the legal moves collection
    {
        return this.legalMoves.contains(m);
    }



    public boolean isInCheck()
    {
        return this.isInCheck;
    }


    public boolean isInCheckMate()
    {
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStaleMate()
    {
        return !this.isInCheck && !hasEscapeMoves();
    }

    protected boolean hasEscapeMoves() {
        for(final move m : this.legalMoves)
        {
            final MoveTransition transition = makeMove(m);  //make move on imaginary board
            if(transition.getMoveStatus().isDone())  //if after the move, the king is not in check
            {
                return true;
            }
        }
        return false;
    }

    //TODO implement methods below!
    public boolean isCastled()
    {
        return false;
    }

    public MoveTransition makeMove(final move m)
    {
        if(!isMoveLegal(m))
        {
            return new MoveTransition(this.gameBoard,m,MoveStatus.ILLEGAL_MOVE);
        }


            final board transitionBoard = m.execute();  //makes a move on virtual board
            final Collection<move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().
                            getPlayerKing().getPiecePosition(),
                    transitionBoard.currentPlayer().getLegalMoves());//after move is made, we are no longer current player, we are opponenet!
            if(!kingAttacks.isEmpty())
            {
                return new MoveTransition(this.gameBoard,m,MoveStatus.LEAVES_PLAYER_IN_CHECK);
            }

            return new MoveTransition(transitionBoard,m,MoveStatus.DONE);
    }
    public abstract Collection<piece> getActivePieces();
    public abstract alliance getAlliance();
    public abstract Player getOpponent();

    protected abstract Collection<move> calculateKingCastles(Collection<move> playerLegals,Collection<move> opponentsLegals);
}
