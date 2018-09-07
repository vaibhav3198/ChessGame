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

import static com.chess.engine.pieces.piece.pieceType.BISHOP;

public class bishop extends piece {

    private final static int candidateMoveVectorCoordinates[]={-9,-7,7,9};

    public bishop(int pp, alliance pa) {
        super(BISHOP,pp, pa,true);   //error get packet type i.e check piece class
    }

    public bishop(final int pp,final alliance pa,final boolean isFirstMove)
    {
        super(BISHOP,pp,pa,isFirstMove);
    }

    @Override
    public Collection<move> calculateLegalMoves(final board b)
    {
       final List<move> legalMoves = new ArrayList<>();

       for(final int candidateCoordinateOffset : candidateMoveVectorCoordinates)
       {
           int candidateDestinationCoordinate = this.piecePosition; //temporary initialization

           while(boardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
           {
               if(isFirstColumnExclusion(candidateDestinationCoordinate,candidateCoordinateOffset) || isEightColumnExclusion(candidateDestinationCoordinate,candidateCoordinateOffset))
               {
                   break;
               }
               candidateDestinationCoordinate+=candidateCoordinateOffset;
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
                       break;
                   }

               }

           }
       }
        //return Collections.unmodifiableList(legalMoves);
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public bishop movePiece(final move m) {
        return new bishop(m.getDestinationCoordinate(),m.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString()
    {
        return BISHOP.toString();
    }


    private static boolean isFirstColumnExclusion (final int currentPosition, final int candidateOffset)
    {
        return boardUtils.FirstColumn[currentPosition] && (candidateOffset == -9 || candidateOffset == 7);
    }
    private static boolean isEightColumnExclusion (final int currentPosition, final int candidateOffset)
    {
        return boardUtils.EightColumn[currentPosition] && (candidateOffset == 9 || candidateOffset == -7);
    }
}
