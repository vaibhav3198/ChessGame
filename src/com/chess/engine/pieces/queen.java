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

import static com.chess.engine.pieces.piece.pieceType.QUEEN;

public class queen extends piece {

    private final static int candidateMoveVectorCoordinates[]={-9,-8,-7,-1,1,7,8,9};  //union of bishop and rook

    public queen(final int pp, final alliance pa) {
        super(QUEEN,pp, pa,true);
    }
    public queen(final int pp,final alliance pa,final boolean isFirstMove)
    {
        super(QUEEN,pp,pa,isFirstMove);
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
    public queen movePiece(final move m) {
        return new queen(m.getDestinationCoordinate(),m.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString()
    {
        return QUEEN.toString();
    }

    private static boolean isFirstColumnExclusion (final int currentPosition, final int candidateOffset)
    {
        return boardUtils.FirstColumn[currentPosition] && (candidateOffset==-1 || candidateOffset == -9 || candidateOffset == 7);
    }
    private static boolean isEightColumnExclusion (final int currentPosition, final int candidateOffset)
    {
        return boardUtils.EightColumn[currentPosition] && (candidateOffset==-1 || candidateOffset == 9 || candidateOffset == -7);
    }
}
