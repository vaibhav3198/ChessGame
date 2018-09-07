package com.chess.engine.board;

import com.chess.engine.alliance;
import com.chess.engine.pieces.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;

public class board {

    private final List<tile> gameBoard;
    private final Collection<piece> whitePieces;
    private final Collection<piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;

    private final Player currentPlayer;

    private final pawn enPassantPawn;

    private  board(final Builder builder)
    {

        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard,alliance.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard,alliance.BLACK);

        this.enPassantPawn=builder.enPassantPawn;

        final Collection<move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<move> blackStandardLegalMoves  = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this,whiteStandardLegalMoves,blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this,whiteStandardLegalMoves,blackStandardLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer,this.blackPlayer);
        //TODO: some error in the above call
        //this.currentPlayer=this.whitePlayer;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        for(int i=0;i<boardUtils.numTiles;i++)
        {
            final String tileText = this.gameBoard.get(i).toString();
            builder.append(String.format("%3s",tileText));
            if((i+1)%boardUtils.NUM_TILES_PER_ROW ==0)
            {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public Player whitePlayer()
    {
        return this.whitePlayer;
    }

    public Player blackPlayer()
    {
        return this.blackPlayer;
    }

    public Player currentPlayer()
    {
        return this.currentPlayer;
    }

    public pawn getEnPassantPawn(){return this.enPassantPawn;}

    public Collection<piece> getBlackPieces() {
        return this.blackPieces;
    }

    public Collection<piece> getWhitePieces() {
        return this.whitePieces;
    }

    private Collection<move> calculateLegalMoves(final Collection<piece> piecesOfAlliance) {
        final List<move> legalMoves = new ArrayList<>();
        for(final piece loopPiece : piecesOfAlliance)
        {
            legalMoves.addAll(loopPiece.calculateLegalMoves(this));
        }
        return Collections.unmodifiableList(legalMoves);
    }

    private static Collection<piece> calculateActivePieces(final List<tile> gameBoard,final alliance allianceOfPiece) {
        final List<piece> activePieces = new ArrayList<>();

        for(final tile t: gameBoard)
        {
            if(t.isTileOccupied())
            {
                final piece pieceOnTile = t.getPiece();
                if(pieceOnTile.getPieceAlliance()== allianceOfPiece)
                {
                    activePieces.add(pieceOnTile);
                }
            }
        }
        return Collections.unmodifiableList(activePieces);
    }

    public tile getTile(final int tileCoordinate)
    {
        return gameBoard.get(tileCoordinate);
    }

   /* private static List<tile> createGameBoard(final Builder builder)
    {
        final List<tile> tiles = new ArrayList<>();
        for(int i=0;i<boardUtils.numTiles;i++)
        {
            tiles.add(tile.createTile(i,builder.boardConfig.get(i)));
            //tiles.set(i, tile.createTile(i, builder.boardConfig.get(i)));
        }
        return Collections.unmodifiableList(tiles);   //Check v14 around 8

    }*/
   private static List<tile> createGameBoard(final Builder builder)
   {
       final tile[] tiles = new tile[boardUtils.numTiles];
       for(int i=0;i<boardUtils.numTiles;i++)
       {
           tiles[i]=tile.createTile(i,builder.boardConfig.get(i));
       }
       return ImmutableList.copyOf(tiles);
   }

    public static board createStandardBoard()
    {
        final Builder builder = new Builder();

        // Black Layout
        builder.setPiece(new rook(0,alliance.BLACK));
        builder.setPiece(new knight(1,alliance.BLACK));
        builder.setPiece(new bishop(2,alliance.BLACK));
        builder.setPiece(new queen(3,alliance.BLACK));
        builder.setPiece(new king(4,alliance.BLACK));
        builder.setPiece(new bishop(5,alliance.BLACK));
        builder.setPiece(new knight(6,alliance.BLACK));
        builder.setPiece(new rook(7,alliance.BLACK));
        builder.setPiece(new pawn(8,alliance.BLACK));
        builder.setPiece(new pawn(9,alliance.BLACK));
        builder.setPiece(new pawn(10,alliance.BLACK));
        builder.setPiece(new pawn(11,alliance.BLACK));
        builder.setPiece(new pawn(12,alliance.BLACK));
        builder.setPiece(new pawn(13,alliance.BLACK));
        builder.setPiece(new pawn(14,alliance.BLACK));
        builder.setPiece(new pawn(15,alliance.BLACK));

        //White Layout
        builder.setPiece(new rook(56,alliance.WHITE));
        builder.setPiece(new knight(57,alliance.WHITE));
        builder.setPiece(new bishop(58,alliance.WHITE));
        builder.setPiece(new queen(59,alliance.WHITE));
        builder.setPiece(new king(60,alliance.WHITE));
        builder.setPiece(new bishop(61,alliance.WHITE));
        builder.setPiece(new knight(62,alliance.WHITE));
        builder.setPiece(new rook(63,alliance.WHITE));
        builder.setPiece(new pawn(48,alliance.WHITE));
        builder.setPiece(new pawn(49,alliance.WHITE));
        builder.setPiece(new pawn(50,alliance.WHITE));
        builder.setPiece(new pawn(51,alliance.WHITE));
        builder.setPiece(new pawn(52,alliance.WHITE));
        builder.setPiece(new pawn(53,alliance.WHITE));
        builder.setPiece(new pawn(54,alliance.WHITE));
        builder.setPiece(new pawn(55,alliance.WHITE));

        return builder.build();
    }

    public Iterable<move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(),this.blackPlayer.getLegalMoves()));
    }

    public static class Builder  //predefined class to build instance of board
    {
        Map<Integer,piece> boardConfig;
        alliance nextMoveMaker;
        pawn enPassantPawn;

        public Builder()
        {
            this.boardConfig = new HashMap<>();
            nextMoveMaker=alliance.WHITE;
        }

        public Builder setPiece(final piece pieceToSet)
        {
            this.boardConfig.put(pieceToSet.getPiecePosition(),pieceToSet);
            return this;
        }

        public Builder setMoveMaker (final alliance nextMoveMaker)
        {
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        public board build()
        {
            return new board(this);
        }

        public void setEnPassantPawn(pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }
    }
}
