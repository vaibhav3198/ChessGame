package com.chess.engine.pieces;

import com.chess.engine.alliance;
import com.chess.engine.board.board;
import com.chess.engine.board.boardUtils;
import com.chess.engine.board.move;
import com.chess.engine.board.tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.chess.engine.pieces.piece.pieceType.KING;

public class king extends piece {

    private final static int[]  candidateMoveCoordinate={-9,-8,-7,-1,1,7,8,9};  //actual moves

    public king(final int pp,final alliance pa) {
        super(KING,pp, pa,true);
    }
    public king(final int pp,final alliance pa,final boolean isFirstMove)
    {
        super(KING,pp,pa,isFirstMove);
    }

    @Override
    public Collection<move> calculateLegalMoves(board b)
    {
        final List<move> legalMoves = new ArrayList<>();

        for(final int currentCandidateOffset : candidateMoveCoordinate)
        {
           final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;

           if(isFirstColumnExclusion(this.piecePosition,currentCandidateOffset) || isEightColumnExclusion(this.piecePosition,currentCandidateOffset))
           {
               continue;
           }

           if(boardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
           {
               final tile candidateDestinationTile = b.getTile(candidateDestinationCoordinate);
               if(!candidateDestinationTile.isTileOccupied())
               {
                   legalMoves.add(new move.MajorMove(b,this,candidateDestinationCoordinate));
               }
               else  //if the destination tile is occupied check which alliance piece is on it
               {
                   final piece pieceAtDestination = candidateDestinationTile.getPiece();
                   final alliance pieceAllianceOfDestination= pieceAtDestination.getPieceAlliance();
                   if(this.pieceAlliance != pieceAllianceOfDestination)
                   {
                       legalMoves.add(new move.MajorAttackMove(b,this,candidateDestinationCoordinate,pieceAtDestination));
                   }
               }
           }
        }
        //return Collections.unmodifiableList(legalMoves);
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public king movePiece(final move m) {
        return new king(m.getDestinationCoordinate(),m.getMovedPiece().getPieceAlliance());
    }


    @Override
    public String toString()
    {
        return KING.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset)
    {
        return boardUtils.FirstColumn[currentPosition] && (candidateOffset == -9 || candidateOffset==-1 || candidateOffset==7 );
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset)
    {
        return boardUtils.EightColumn[currentPosition] && (candidateOffset == 9 || candidateOffset == 1 || candidateOffset==-7);
    }
}
