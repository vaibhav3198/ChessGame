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

import static com.chess.engine.pieces.piece.pieceType.KNIGHT;

public class knight extends piece {

    private final static int[] candidate_move_coordinates={-17,-15,-10,-6,6,10,15,17};
    public knight(final int pp, final alliance pa)
    {
        super(KNIGHT,pp,pa,true);
    }
    public knight(final int pp,final alliance pa,final boolean isFirstMove)
    {
        super(KNIGHT,pp,pa,isFirstMove);
    }

    @Override
    public Collection<move> calculateLegalMoves(final board b)
    {
        int candidateDestinationCoordinate;
        final List<move> legalMoves = new ArrayList<>();
        for(final int currentCandidateOffset : candidate_move_coordinates)
        {
            candidateDestinationCoordinate=this.piecePosition+currentCandidateOffset;

            if(boardUtils.isValidTileCoordinate(candidateDestinationCoordinate) /*valid tile coordinate*/)
            {
                if(isFirstColumnExclusion(this.piecePosition,currentCandidateOffset) || isSecondColumnExclusion(this.piecePosition,currentCandidateOffset) || isSeventhColumnExclusion(this.piecePosition,currentCandidateOffset) || isEightColumnExclusion(this.piecePosition,currentCandidateOffset))
                {
                    continue;
                }

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
        //return legalMoves;
        //return Collections.unmodifiableList(legalMoves);
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public knight movePiece(final move m) {
        return new knight(m.getDestinationCoordinate(),m.getMovedPiece().getPieceAlliance());
    }


    @Override
    public String toString()
    {
        return KNIGHT.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset)
    {
       return boardUtils.FirstColumn[currentPosition] && (candidateOffset == -17 || candidateOffset==-10 || candidateOffset==6 || candidateOffset==15);
    }

    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset)
    {
        return boardUtils.SecondColumn[currentPosition] && (candidateOffset == -10 || candidateOffset == 6);
    }

    private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset)
    {
        return boardUtils.SeventhColumn[currentPosition] && (candidateOffset == 10 || candidateOffset ==-6);
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset)
    {
        return boardUtils.EightColumn[currentPosition] && (candidateOffset == -15 || candidateOffset == -6 || candidateOffset==10 || candidateOffset == 17);
    }
}
