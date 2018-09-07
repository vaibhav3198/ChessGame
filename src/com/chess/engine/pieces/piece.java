package com.chess.engine.pieces;
import com.chess.engine.alliance;
import com.chess.engine.board.board;
import com.chess.engine.board.move;


import java.util.Collection;

public abstract class piece
{
    protected final int piecePosition;
    protected final alliance pieceAlliance; //Alliance is either white or black to which the piece belongs
    protected final boolean isFirstMove;
    protected final pieceType pieceType;
    private  final int cachedHashCode;


    public int getPiecePosition()
    {
        return this.piecePosition;
    }

    piece(final pieceType pt,final int pp, final alliance pa,final boolean isFirstMove)
    {
        this.piecePosition=pp;
        this.pieceAlliance=pa;
        this.isFirstMove =isFirstMove;
        this.pieceType=pt;
        this.cachedHashCode = computeHashCode();
    }

    protected  int computeHashCode(){
        int result = pieceType.hashCode();  //v23 5:30
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object other)
    {
        if(this == other)
        {
            return true;
        }
        if(!(other instanceof piece))
        {
            return false;
        }
        final piece otherPiece = (piece) other;
        return piecePosition == otherPiece.getPiecePosition() && pieceType == otherPiece.getPieceType() &&
                pieceAlliance == otherPiece.getPieceAlliance() && isFirstMove == otherPiece.isFirstMove();
    }

    @Override
    public int hashCode()
    {
        return this.cachedHashCode;
    }

    public alliance getPieceAlliance() {
        return pieceAlliance;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    public abstract Collection<move> calculateLegalMoves(final board b);  //retuens linked list of possible moves of the piece

    public pieceType getPieceType()
    {
        return this.pieceType;
    }

    public int getPieceValue()
    {
        return this.pieceType.getPieceValue();
    }

    public abstract piece movePiece(move m);


    public enum pieceType
    {
        PAWN("P",100) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KNIGHT("N",300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        BISHOP("B",300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        ROOK("R",500) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN("Q",900) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KING("K",10000) {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        };

        private String pieceName;
        private int pieceValue;
        pieceType(final String pieceName,final int pieceValue)
        {
            this.pieceName = pieceName;
            this.pieceValue= pieceValue;
        }

        @Override
        public String toString()
        {
            return this.pieceName;
        }

        public int getPieceValue(){
            return this.pieceValue;
        }
        public abstract boolean isKing();

        public abstract  boolean isRook();

    }

}
