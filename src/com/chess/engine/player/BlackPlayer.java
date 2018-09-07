package com.chess.engine.player;

import com.chess.engine.alliance;
import com.chess.engine.board.board;
import com.chess.engine.board.move;
import com.chess.engine.board.tile;
import com.chess.engine.pieces.piece;
import com.chess.engine.pieces.rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.move.*;

public class BlackPlayer extends Player{
    public BlackPlayer(final board board,final  Collection<move> whiteStandardLegalMoves,final Collection<move> blackStandardLegalMoves) {
        super(board,blackStandardLegalMoves,whiteStandardLegalMoves);
    }

    @Override
    public Collection<piece> getActivePieces() {
        return this.gameBoard.getBlackPieces();
    }

    @Override
    public alliance getAlliance() {
        return alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.gameBoard.whitePlayer();
    }

    @Override
    protected Collection<move> calculateKingCastles(final Collection<move> playerLegals,
                                                    final Collection<move> opponentsLegals) {
        final List<move> kingCastles = new ArrayList<>();
        if(this.playerKing.isFirstMove() && (!this.isInCheck())) {
            //blacks king side castle
            if(!this.gameBoard.getTile(5).isTileOccupied() && !this.gameBoard.getTile(6).isTileOccupied())
            {
                final tile rookTile = this.gameBoard.getTile(7);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove())
                {
                    if(Player.calculateAttacksOnTile(5,opponentsLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(6,opponentsLegals).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook())
                    {
                        kingCastles.add(new KingSideCastleMove(this.gameBoard,this.playerKing,
                                                                6,(rook)rookTile.getPiece(),
                                                                rookTile.getTileCoordinate(),5));
                    }

                }
            }
            //black queen side castle
            if(!this.gameBoard.getTile(1).isTileOccupied() &&
                    !this.gameBoard.getTile(2).isTileOccupied() &&
                    !this.gameBoard.getTile(3).isTileOccupied())
            {
                final tile rookTile = this.gameBoard.getTile(0);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() &&
                        Player.calculateAttacksOnTile(2,opponentsLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(3,opponentsLegals).isEmpty() &&
                        rookTile.getPiece().getPieceType().isRook())
                {
                    kingCastles.add(new QueenSideCastleMove(this.gameBoard,this.playerKing,
                                                                2,(rook)rookTile.getPiece(),
                                                                rookTile.getTileCoordinate(),3));
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }
}
