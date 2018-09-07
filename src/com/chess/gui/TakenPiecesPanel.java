package com.chess.gui;

import com.chess.engine.board.move;
import com.chess.engine.pieces.piece;
import com.chess.gui.Table.MoveLog;
import com.google.common.primitives.Ints;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TakenPiecesPanel extends JPanel{

    private final JPanel northPanel;
    private final JPanel southPanel;

    private static final Color PANEL_COLOR = Color.decode("0xffffff");
    private static final Dimension TAKEN_PIECES_DIMENSIONS = new Dimension(40,80);
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);

    public TakenPiecesPanel()
    {
        super(new BorderLayout());
        this.setBackground(PANEL_COLOR);  //method of JPanel
        this.setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8,2));  //8 rows 2 cols
        this.southPanel = new JPanel(new GridLayout(8,2));
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel.setBackground(PANEL_COLOR);
        add(this.northPanel,BorderLayout.NORTH);
        add(this.southPanel,BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSIONS);
    }

    public void redo(final MoveLog moveLog){
        southPanel.removeAll();
        northPanel.removeAll();

        final List<piece> whiteTakenPieces = new ArrayList<>();
        final List<piece> blackTakenPieces = new ArrayList<>();

        for(final move m : moveLog.getMoves())  //in list moves
        {
            if(m.isAttack())
            {
                final piece takenPiece = m.getAttackedPiece();
                if(takenPiece.getPieceAlliance().isWhite())
                {
                    whiteTakenPieces.add(takenPiece);
                }
                else if(takenPiece.getPieceAlliance().isBlack())
                {
                    blackTakenPieces.add(takenPiece);
                }
                else
                {
                    throw new RuntimeException("Game is in wrong state");
                }
            }
        }

        Collections.sort(whiteTakenPieces, new Comparator<piece>() {
            @Override
            public int compare(piece o1, piece o2) {
                return Ints.compare(o1.getPieceValue(),o2.getPieceValue());  //sorts as per strongness for visualality
            }
        });
        Collections.sort(blackTakenPieces, new Comparator<piece>() {
            @Override
            public int compare(piece o1, piece o2) {
                return Ints.compare(o1.getPieceValue(),o2.getPieceValue());  //sorts as per strongness for visualality
            }
        });

        for(final piece takenPiece : whiteTakenPieces)
        {
            try{
                System.out.println("art/fancy/" +
                        takenPiece.getPieceAlliance().toString().substring(0,1)+""+takenPiece.toString()+".gif");

                final BufferedImage image = ImageIO.read(new File("art/fancy/" +
                        takenPiece.getPieceAlliance().toString().substring(0,1)+""+takenPiece.toString()+".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(
                        icon.getIconWidth() - 15, icon.getIconWidth() - 15, Image.SCALE_SMOOTH)));
                this.northPanel.add(imageLabel);
            }
            catch (final IOException e){
                e.printStackTrace();
            }
        }
        for(final piece takenPiece : blackTakenPieces)
        {
            try{
                System.out.println("art/fancy/" +
                        takenPiece.getPieceAlliance().toString().substring(0,1)+""+takenPiece.toString()+".gif");
                final BufferedImage image = ImageIO.read(new File("art/fancy/" +
                        takenPiece.getPieceAlliance().toString().substring(0,1)+""+takenPiece.toString()+".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(
                        icon.getIconWidth() - 15, icon.getIconWidth() - 15, Image.SCALE_SMOOTH)));
                this.southPanel.add(imageLabel);
            }
            catch (final IOException e){
                e.printStackTrace();
            }
        }

        validate();
    }
}
