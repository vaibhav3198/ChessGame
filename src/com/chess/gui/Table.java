package com.chess.gui;

import com.chess.engine.board.board;
import com.chess.engine.board.boardUtils;
import com.chess.engine.board.move;
import com.chess.engine.board.tile;
import com.chess.engine.pieces.piece;
import com.chess.engine.player.AI.Minimax;
import com.chess.engine.player.AI.MoveStrategy;
import com.chess.engine.player.MoveTransition;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table extends Observable{
    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;  //v40:0651
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;
    private board chessBoard;
    private tile sourceTile;
    private tile destinationTile;
    private piece humanMovedPiece;
    private BoardDirection boardDirection;

    private move computerMove;

    private boolean highlightLegalMoves;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400,350);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);
    private static String defaultPieceImagesPath = "art/fancy/";

    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");

    private static final Table INSTANCE = new Table();

    private Table()
    {
        this.gameFrame = new JFrame("Chess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = CreateTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = board.createStandardBoard();
        this.gameHistoryPanel=new GameHistoryPanel();
        this.takenPiecesPanel= new TakenPiecesPanel();
        this.boardPanel=new BoardPanel();
        this.moveLog=new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(this.gameFrame,true);
        this.boardDirection=BoardDirection.NORMAL;
        this.highlightLegalMoves=true;
        this.gameFrame.add(this.takenPiecesPanel,BorderLayout.WEST);
        this.gameFrame.add(this.gameHistoryPanel,BorderLayout.EAST);
        this.gameFrame.add(this.boardPanel,BorderLayout.CENTER);
        this.gameFrame.setVisible(true);
    }

    public static Table get()
    {
        return INSTANCE;
    }

    public void show()
    {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    private GameSetup getGameSetup()
    {
        return this.gameSetup;
    }

    private board getGameBoard()
    {
        return this.chessBoard;
    }

    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    private JMenuBar CreateTableMenuBar() {  //v29:0635
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());  //adds the file menu
        tableMenuBar.add(createPreferencesMenu()); //adds the preferences menu
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open up that PGN file");
            }
        });
        fileMenu.add(openPGN);

        final JMenuItem exitMenu = new JMenuItem("Exit");
        exitMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenu);
        return fileMenu;
    }

    private JMenu createPreferencesMenu(){  //v36:0222
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoard = new JMenuItem("Flip Board");
        flipBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoard);
        preferencesMenu.addSeparator();
        final JCheckBoxMenuItem legalMovesHighlighter = new JCheckBoxMenuItem("Highlight Legal Moves",false);
        legalMovesHighlighter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMovesHighlighter.isSelected();
            }
        });
        preferencesMenu.add(legalMovesHighlighter);
        return preferencesMenu;
    }

    private JMenu createOptionsMenu()
    {
        final JMenu optionsMenu = new JMenu("Options");
        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });

        optionsMenu.add(setupGameMenuItem);
        return optionsMenu;
    }

    private void setupUpdate(final GameSetup gameSetup)
    {
        setChanged();
        notifyObservers(gameSetup);
    }

    private static class TableGameAIWatcher implements Observer
    {

        @Override
        public void update(final Observable o, final Object arg)
        {
            if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
                    !Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
                    !Table.get().getGameBoard().currentPlayer().isInStaleMate())
            {
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }

            if(Table.get().getGameBoard().currentPlayer().isInCheckMate())
            {
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: Player " + Table.get().getGameBoard().currentPlayer().getOpponent() + " Wins", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            if(Table.get().getGameBoard().currentPlayer().isInStaleMate())
            {
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in stalemate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void updateGameBoard(final board b)
    {
        this.chessBoard = b;
    }

    public void updateComputerMove(final move m)
    {
        this.computerMove = m;
    }

    private MoveLog getMoveLog()
    {
        return this.moveLog;
    }

    private GameHistoryPanel getGameHistoryPanel()
    {
        return this.gameHistoryPanel;
    }

    private TakenPiecesPanel getTakenPiecesPanel()
    {
        return this.takenPiecesPanel;
    }


    final void moveMadeUpdate(final PlayerType playerType)
    {
        setChanged();
        notifyObservers(playerType);
    }

    private static class AIThinkTank extends SwingWorker<move,String>
    {
        private AIThinkTank()
        {

        }

        @Override
        protected move doInBackground() throws Exception
        {
            final MoveStrategy minimax = new Minimax(4);
            final move bestMove = minimax.execute(Table.get().getGameBoard());
            return bestMove;
        }

        @Override
        public void done()
        {
            try
            {
                final move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(),Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            catch (ExecutionException e)
            {
                e.printStackTrace();
            }

        }
    }

    public enum BoardDirection{    //v36:0222
        NORMAL{
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED{
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);  //method of guava
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };
        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }

    private class BoardPanel extends JPanel{ //v30:0340  maps to board visually
        final List<TilePanel> boardTiles;

        BoardPanel(){
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            for(int i=0;i< boardUtils.numTiles;i++){
                final TilePanel tilePanel = new TilePanel(this,i);   //creating 64 tiles in 8x8 grid
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final board b)
        {
            removeAll();
            for(final TilePanel tilePanel : boardDirection.traverse(boardTiles))
            {
                tilePanel.drawTile(b);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog{//keeps track of moves v38:0232
        private final List<move> moves;

        MoveLog()
        {
            this.moves=new ArrayList<>();
        }
        public List<move> getMoves()
        {
            return this.moves;
        }
        public void addMove(final move m)
        {
            this.moves.add(m);
        }

        public int size()
        {
            return this.moves.size();
        }
        public void clear()
        {
            this.moves.clear();
        }
        public move removeMove(int index)
        {
            return this.moves.remove(index);
        }
        public boolean removeMove(final  move m)
        {
            return this.moves.remove(m);
        }

    }

    enum PlayerType
    {
        HUMAN,
        COMPUTER
    }

    private class TilePanel extends JPanel{ //v30:0340   maps to tile visually
        private final int tileId;

        TilePanel(final BoardPanel boardPanel,final int tileId){
            super(new GridBagLayout());
            this.tileId=tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);
            //highlightLegals(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if(isRightMouseButton(e))   //cancels the current selection
                    {
                        sourceTile=null;
                        destinationTile=null;
                        humanMovedPiece=null;
                    }
                    else if(isLeftMouseButton(e))  //selection of the tile
                    {
                        if(sourceTile == null)  //first click to the tile
                        {
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if(humanMovedPiece == null)
                            {
                                sourceTile = null;
                            }
                            System.out.println("Selected! ");
                            //highlightLegals(chessBoard);
                        }
                        else  //second click to the tile
                        {
                            destinationTile = chessBoard.getTile(tileId);
                            final move mov=move.MoveFactory.createMove(chessBoard,sourceTile.getTileCoordinate(),destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(mov);
                            if(transition.getMoveStatus().isDone())
                            {
                                chessBoard =transition.getTransitionBoard();  //v35:0054
                                moveLog.addMove(mov);
                                System.out.println("Move made!");
                            }
                            else
                            {
                                System.out.println("Move not made! ");
                            }
                            sourceTile=null;
                            destinationTile=null;
                            humanMovedPiece=null;
                            //System.out.println("Done! ");
                        }
                        SwingUtilities.invokeLater(new Runnable() {//to update gui
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard,moveLog);
                                takenPiecesPanel.redo(moveLog);
                                if(gameSetup.isAIPlayer(chessBoard.currentPlayer()))
                                {
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                }
                                boardPanel.drawBoard(chessBoard);

                            }
                        });
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });
            validate();
        }

        public void drawTile(final board b)
        {
            assignTileColor();
            assignTilePieceIcon(b);
            highlightLegals(chessBoard);
            validate();
            repaint();
        }

        private void assignTilePieceIcon(final board b)
        {
            this.removeAll();
            if(b.getTile(this.tileId).isTileOccupied())  //if there is a piece on the tile
            {
                try //whenever image is loaded try catch is necessary
                {
                    final BufferedImage image = ImageIO.read(new File(defaultPieceImagesPath + b.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0,1) +
                                                                b.getTile(this.tileId).getPiece().toString() + ".gif"));  // white bishop: WB.gif conversion
                    add(new JLabel(new ImageIcon(image)));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        private void highlightLegals(final board b)
        {
            if(highlightLegalMoves)  //if the user wants the highlighted moves
            {
                for(final move mov : pieceLegalMoves(b))
                {
                    if(mov.getDestinationCoordinate() == this.tileId)
                    {
                        try{
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        private Collection<move> pieceLegalMoves (final board b)
        {
            if(humanMovedPiece!=null && humanMovedPiece.getPieceAlliance()==b.currentPlayer().getAlliance())
            {
                return humanMovedPiece.calculateLegalMoves(b);
            }
            return Collections.emptyList();
        }

        private void assignTileColor() {//v31:0057
            if(boardUtils.EightRank[this.tileId] ||
                    boardUtils.SixthRank[this.tileId] ||
                    boardUtils.FourthRank[this.tileId] ||
                    boardUtils.SecondRank[this.tileId])
            {
                setBackground(this.tileId % 2 !=0 ? darkTileColor : lightTileColor);
            }
            else if(boardUtils.SeventhRank[this.tileId] ||
                    boardUtils.FifthRank[this.tileId] ||
                    boardUtils.ThirdRank[this.tileId] ||
                    boardUtils.FirstRank[this.tileId])
            {
                setBackground(this.tileId % 2 !=0 ? lightTileColor : darkTileColor);
            }
        }
    }
}
