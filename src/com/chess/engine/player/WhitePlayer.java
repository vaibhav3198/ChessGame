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

public class WhitePlayer extends Player{
    public WhitePlayer(final board board,final Collection<move> whiteStandardLegalMoves,final Collection<move> blackStandardLegalMoves) {
        super(board,whiteStandardLegalMoves,blackStandardLegalMoves);
    }

    @Override
    public Collection<piece> getActivePieces(){
        return this.gameBoard.getWhitePieces();
    }

    @Override
    public alliance getAlliance() {
        return alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.gameBoard.blackPlayer();
    }

    @Override
    protected Collection<move> calculateKingCastles(final Collection<move> playerLegals,
                                                    final Collection<move> opponentsLegals) {
        final List<move> kingCastles = new ArrayList<>();
        if(this.playerKing.isFirstMove() && (!this.isInCheck())) {
            //whites king side castle
            if(!this.gameBoard.getTile(61).isTileOccupied() &&
                    !this.gameBoard.getTile(62).isTileOccupied())
            {
                final tile rookTile = this.gameBoard.getTile(63);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove())
                {
                    if(Player.calculateAttacksOnTile(61,opponentsLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(62,opponentsLegals).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook())
                    {
                        kingCastles.add(new KingSideCastleMove(this.gameBoard,this.playerKing,
                                                                    62,(rook)rookTile.getPiece(),rookTile.getTileCoordinate(),
                                                                    61));
                    }

                }
            }
            //white queen side castle
            if(!this.gameBoard.getTile(59).isTileOccupied() &&
                    !this.gameBoard.getTile(58).isTileOccupied() &&
                    !this.gameBoard.getTile(57).isTileOccupied())
            {
                final tile rookTile = this.gameBoard.getTile(56);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() &&
                        Player.calculateAttacksOnTile(58,opponentsLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(59,opponentsLegals).isEmpty() &&
                        rookTile.getPiece().getPieceType().isRook())
                {
                    kingCastles.add(new QueenSideCastleMove(this.gameBoard,
                                                            this.playerKing,58,(rook)rookTile.getPiece(),
                                                            rookTile.getTileCoordinate(),59));
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }
}
