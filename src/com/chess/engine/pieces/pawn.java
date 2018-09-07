package com.chess.engine.pieces;

import com.chess.engine.alliance;
import com.chess.engine.board.board;
import com.chess.engine.board.boardUtils;
import com.chess.engine.board.move;
import com.chess.engine.board.move.PawnAttackMove;
import com.chess.engine.board.move.PawnMove;
import com.chess.engine.board.move.PawnPromotion;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.pieces.piece.pieceType.PAWN;

public class pawn extends piece {
    private final static int[]  candidateMoveCoordinate={8,16,7,9};  //subtract for white, add for black

    public pawn(final int pp, final alliance pa) {
        super(PAWN,pp, pa,true);
    }
    public pawn(final int pp,final alliance pa,final boolean isFirstMove)
    {
        super(PAWN,pp,pa,isFirstMove);
    }

    @Override
    public Collection<move> calculateLegalMoves(final board b) {
        final List<move> legalMoves = new ArrayList<>();

        for(final int currentCandidateOffset : candidateMoveCoordinate)
        {
            int candidateDestinationCoordinate = this.piecePosition + (currentCandidateOffset) * (this.getPieceAlliance().getDirection());

            if(!boardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
            {
                continue;
            }

            //one move ahead
            if(currentCandidateOffset == 8 && !b.getTile(candidateDestinationCoordinate).isTileOccupied())  //fwd by one and not occupied
            {
                if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){  //promotion
                    legalMoves.add(new PawnPromotion(new PawnMove(b,this,candidateDestinationCoordinate)));
                }
                else  //simple pawn move
                {
                    legalMoves.add(new PawnMove(b,this, candidateDestinationCoordinate));
                }
            }

            //two move ahead i.e jump
            else if(currentCandidateOffset == 16 && this.isFirstMove() &&
                    ((boardUtils.SeventhRank[this.piecePosition] && this.getPieceAlliance().isBlack()) ||
                    (boardUtils.SecondRank[this.piecePosition]) && this.getPieceAlliance().isWhite()))
            {
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if(!b.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                        !b.getTile(candidateDestinationCoordinate).isTileOccupied())
                {
                    legalMoves.add(new move.PawnJump(b,this, candidateDestinationCoordinate));
                }
            }

            //attacking moves
            else if(currentCandidateOffset ==7 &&
                    (! ((boardUtils.EightColumn[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                    boardUtils.FirstColumn[this.piecePosition] && this.pieceAlliance.isBlack())))
            {
                if(b.getTile(candidateDestinationCoordinate).isTileOccupied())
                {
                    final piece pieceOnDestination = b.getTile(candidateDestinationCoordinate).getPiece();
                    if(this.pieceAlliance != pieceOnDestination.pieceAlliance)
                    {
                        if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) //promotion with attack
                        {
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(b,this, candidateDestinationCoordinate,pieceOnDestination)));
                        }
                        else //simple attack
                        {
                            legalMoves.add(new PawnAttackMove(b,this, candidateDestinationCoordinate,pieceOnDestination));
                        }
                    }
                }
                //the logic for enpassant might be incorrect
                else if(b.getEnPassantPawn() != null){
                    if(b.getEnPassantPawn().getPiecePosition()==this.piecePosition+(this.getPieceAlliance().getOppositeDirection())){
                        final piece pieceToBeAttacked = b.getEnPassantPawn();
                        if(this.pieceAlliance != pieceToBeAttacked.getPieceAlliance()){
                            legalMoves.add(new move.PawnEnPassantAttackMove(b,this,candidateDestinationCoordinate,pieceToBeAttacked));
                        }
                    }
                }
            }

            else if(currentCandidateOffset ==9 &&
                    (! ((boardUtils.FirstColumn[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                            boardUtils.EightColumn[this.piecePosition] && this.pieceAlliance.isBlack())))
            {
                if(b.getTile(candidateDestinationCoordinate).isTileOccupied())
                {
                    final piece pieceOnDestination = b.getTile(candidateDestinationCoordinate).getPiece();
                    if(this.pieceAlliance != pieceOnDestination.pieceAlliance)
                    {
                        if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) //promotion with attack
                        {
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(b,this, candidateDestinationCoordinate,pieceOnDestination)));
                        }
                        else
                        {
                            legalMoves.add(new PawnAttackMove(b,this, candidateDestinationCoordinate,pieceOnDestination));
                        }

                    }
                }
                else if(b.getEnPassantPawn() != null){
                    if(b.getEnPassantPawn().getPiecePosition()==this.piecePosition-(this.getPieceAlliance().getOppositeDirection())){
                        final piece pieceToBeAttacked = b.getEnPassantPawn();
                        if(this.pieceAlliance != pieceToBeAttacked.getPieceAlliance()){
                            legalMoves.add(new move.PawnEnPassantAttackMove(b,this,candidateDestinationCoordinate,pieceToBeAttacked));
                        }
                    }
                }
            }
        }

        //return legalMoves;
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public pawn movePiece(final move m) {
        return new pawn(m.getDestinationCoordinate(),m.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString()
    {
        return PAWN.toString();
    }

    public piece getPromotionPiece()   //TODO : Give the human player an option of other pieces
    {
        return new queen(this.piecePosition,this.pieceAlliance,false);
    }
}
