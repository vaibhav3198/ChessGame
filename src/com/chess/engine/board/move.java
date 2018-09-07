package com.chess.engine.board;
import com.chess.engine.pieces.pawn;
import com.chess.engine.pieces.piece;
import com.chess.engine.pieces.rook;

import static com.chess.engine.board.board.*;

public abstract class move {
    protected final board b;
    protected final piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    public static final move NULL_MOVE = new NullMove();

    private move(final board b1, final piece mP, final int dC) {
        this.destinationCoordinate = dC;
        this.b = b1;
        this.movedPiece = mP;
        this.isFirstMove=movedPiece.isFirstMove();
    }

    private move(final board b, final int destinationCoordinate)  //to allocate null/illegal moves
    {
        this.b=b;
        this.destinationCoordinate=destinationCoordinate;
        this.movedPiece=null;
        this.isFirstMove=false;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        //result = prime * result + this.movedPiece.getPiecePosition();
        result = prime * result + this.destinationCoordinate;
        result = prime * result + this.movedPiece.hashCode();
        result = prime * result + this.movedPiece.getPiecePosition();
        return result;
    }

    @Override
    public boolean equals(final Object other)
    {
        if(this == other)
        {
            return true;
        }
        if(!(other instanceof move))
        {
            return false;
        }
        final move otherMove = (move) other;
        return  getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
                getDestinationCoordinate()==otherMove.getDestinationCoordinate()&&
                getMovedPiece().equals(otherMove.getMovedPiece());
    }

    public board getBoard(){
        return this.b;
    }

    public int getCurrentCoordinate() {
        return this.getMovedPiece().getPiecePosition();
    }

    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public piece getMovedPiece() {
        return this.movedPiece;
    }

    public boolean isAttack()
    {
        return false;
    }

    public boolean isCastlingMove()
    {
        return false;
    }

    public piece getAttackedPiece()
    {
        return null;
    }


    public board execute() {
        final Builder builder = new Builder();

        for(final piece p: this.b.currentPlayer().getActivePieces())
        {
             if(!this.movedPiece.equals(p))
             {
                 builder.setPiece(p);
             }
        }

        for(final piece p:this.b.currentPlayer().getOpponent().getActivePieces())
        {
            builder.setPiece(p);
        }
        //move the moved piece
        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(this.b.currentPlayer().getOpponent().getAlliance());

        return builder.build();
    }

    //public abstract  board execute() ;

    public static class MajorAttackMove extends AttackMove{  //v41:1400
        public MajorAttackMove(final board b, final piece pieceMoved, final int destinationCoordinate, final piece pieceAttacked){
            super(b,pieceMoved,destinationCoordinate,pieceAttacked);

        }

        @Override
        public boolean equals(final Object other){
            return this==other || other instanceof MajorAttackMove && super.equals(other);
        }

        @Override
        public String toString(){
            return movedPiece.getPieceType() + "x"  + boardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }



    public static final class MajorMove extends move  //non attacking move by pieces
    {

        public MajorMove(final board b1, final piece mP, final int dC) {
            super(b1, mP, dC);
        }

        @Override
        public boolean equals(final Object other)  //v37:1705
        {
            return this==other || other instanceof MajorMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return movedPiece.getPieceType().toString() + boardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    public static class AttackMove extends move
    {
        final piece attackedPiece;
        public AttackMove(final board b1, final piece mP, final int dC, final piece aP) {
            super(b1, mP, dC);
            this.attackedPiece=aP;
        }

        @Override
        public int hashCode()
        {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other)
        {
            if(this == other)
            {
                return true;
            }
            if(!(other instanceof AttackMove))
            {
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece() == otherAttackMove.getAttackedPiece();
        }

        @Override
        public boolean isAttack()
        {
            return true;
        }

        @Override
        public piece getAttackedPiece()
        {
            return this.attackedPiece;
        }


    }

    public static final class PawnMove extends move  //non attacking move by pawns
    {

        public PawnMove(final board b1,final piece mP,final int dC) {
            super(b1, mP, dC);
        }

        @Override
        public boolean equals(final Object other){
            return this==other || other instanceof PawnMove && super.equals(other);
        }

        @Override
        public String toString(){
            return boardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    public static class PawnAttackMove extends AttackMove  // attacking move by pawns
    {

        public PawnAttackMove(final board b1,final piece mP,final int dC,final piece aP) {
            super(b1, mP, dC,aP);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString(){
            return boardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0,1) + "x" +
                    boardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    public static final class PawnEnPassantAttackMove extends AttackMove
    {

        public PawnEnPassantAttackMove(final board b1,final piece mP,final int dC,final piece aP) {
            super(b1, mP, dC,aP);
        }

        @Override
        public boolean equals(final Object other){
            return this==other || other instanceof PawnEnPassantAttackMove && super.equals(other);
        }

        @Override
        public board execute(){
            final Builder builder =new Builder();
            for(final piece p : this.b.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(p)){
                    builder.setPiece(p);
                }
            }
            for (final piece p : this.b.currentPlayer().getOpponent().getActivePieces()){
                if(!p.equals(this.getAttackedPiece())){
                    builder.setPiece(p);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.b.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

    }

    public static class PawnPromotion extends move{

        final move decoratedMove;
        final pawn promotedPawn;
        public PawnPromotion(final move decoratedMove) {
            super(decoratedMove.getBoard(),decoratedMove.getMovedPiece(),decoratedMove.getDestinationCoordinate());
            this.decoratedMove=decoratedMove;
            this.promotedPawn=(pawn)decoratedMove.getMovedPiece();
        }

        @Override
        public int hashCode()
        {
            return decoratedMove.hashCode() + (31*promotedPawn.hashCode());
        }

        @Override
        public boolean equals(final Object other)
        {
            return this==other || other instanceof PawnPromotion && (super.equals(other));
        }

        @Override
        public board execute()
        {
            final board pawnMovedBoard = this.decoratedMove.execute();  //first execute either normal one step or capture
            final board.Builder builder = new Builder();

            for(final piece p : pawnMovedBoard.currentPlayer().getActivePieces())
            {
                if(!this.promotedPawn.equals(p))
                {
                    builder.setPiece(p);
                }
            }

            for(final piece p : pawnMovedBoard.currentPlayer().getOpponent().getActivePieces())
            {
                builder.setPiece(p);
            }

            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isAttack()
        {
            return this.decoratedMove.isAttack();
        }

        @Override
        public piece getAttackedPiece()
        {
            return this.decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString()
        {
            return "";  //todo use =. Eg: "e8=Q"
        }


    }

    public static final class PawnJump extends move
    {

        public PawnJump(final board b1,final piece mP,final int dC) {
            super(b1, mP, dC);
        }

        @Override
        public board execute()
        {
            final Builder builder = new Builder();
            for(final piece p: this.b.currentPlayer().getActivePieces())
            {
                if(!this.movedPiece.equals(p))
                {
                    builder.setPiece(p);
                }
            }
            for(final piece p:this.b.currentPlayer().getOpponent().getActivePieces())
            {
                builder.setPiece(p);
            }
            final pawn movedPawn = (pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.b.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public String toString()
        {
            return boardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    static abstract class CastleMove extends move  //non attacking move by pieces
    {
        protected final rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        public CastleMove(final board b1,final piece mP,final int dC,
                          final rook castleRook, final int castleRookStart, final int castleRookDestination) {

            super(b1, mP, dC);
            this.castleRook=castleRook;
            this.castleRookStart=castleRookStart;
            this.castleRookDestination=castleRookDestination;
        }

        public rook getCastleRook()
        {
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove()
        {
            return true;
        }

        @Override
        public board execute()
        {
            final Builder builder = new Builder();
            for(final piece p: this.b.currentPlayer().getActivePieces())
            {
                if(!this.movedPiece.equals(p) && !this.castleRook.equals(p))
                {
                    builder.setPiece(p);
                }
            }
            for(final piece p:this.b.currentPlayer().getOpponent().getActivePieces())
            {
                builder.setPiece(p);
            }
            builder.setPiece(this.movedPiece.movePiece(this)); //king
            //TODO look into first moves of pieces
            builder.setPiece(new rook(this.castleRookDestination,this.castleRook.getPieceAlliance()));
            builder.setMoveMaker(this.b.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public int hashCode(){ //v41:2025
            final int prime = 31;
            int result = super.hashCode();
            result= prime*result + this.castleRook.hashCode();
            result= prime*result + this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(final Object other){
            if(this==other){
                return true;
            }
            if(!(other instanceof CastleMove)){
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }

    }

    public static final class KingSideCastleMove extends CastleMove
    {

        public KingSideCastleMove(final board b1,final piece mP,final int dC,
                                  final rook castleRook, final int castleRookStart, final int castleRookDestination) {

            super(b1, mP, dC,castleRook,castleRookStart,castleRookDestination);
        }

        @Override
        public boolean equals(final Object other){
          return this==other || other instanceof KingSideCastleMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return "0-0";
        }

    }

    public static final class QueenSideCastleMove extends CastleMove
    {

        public QueenSideCastleMove(final board b1,final piece mP,final int dC,
        final rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(b1, mP, dC,castleRook,castleRookStart,castleRookDestination);
        }

        @Override
        public boolean equals(final Object other){
            return this==other || other instanceof QueenSideCastleMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return "0-0-0";
        }
    }

    public static final class NullMove extends move
    {

        public NullMove() {
            super(null, 65);//v41:0416
        }

        @Override
        public board execute()
        {
            throw new RuntimeException("Cannot Execute Null Move!");
        }

        @Override
        public int getCurrentCoordinate(){
            return -1;
        }

    }

    public static class MoveFactory
    {
        private MoveFactory()
        {
            throw new RuntimeException("Non Instantiable!");
        }

        public static move createMove(final board b,final int currentCoordinate, final int destinationCoordinate)
        {
          for(final move m:b.getAllLegalMoves())
          {
              if(m.getCurrentCoordinate() == currentCoordinate && m.getDestinationCoordinate() == destinationCoordinate)
              {
                  return m;
              }
          }
          return NULL_MOVE;
        }
    }

}
