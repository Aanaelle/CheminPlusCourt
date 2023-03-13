package ihm;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import controleur.Controleur;
import metier.Arete;
import metier.Noeud;


public class PanelGraphe extends JPanel 
{
    private Controleur ctrl;
    private FrameCreerGraphe frame;

    private List<Noeud> lstNoeuds;
    private List<Arete> lstAretes;

    private int xSelectionne, ySelectionne;
    private Noeud noeudSelectionne;
    public PanelGraphe(FrameCreerGraphe frame, Controleur ctrl)
    {
        this.ctrl = ctrl;
        this.frame = frame;

        this.setBackground(new Color(216,216,216));
        
        this.initComponent();
        this.add(new JLabel("Graphe"));

        addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                
                for (Noeud noeud : lstNoeuds) {
                    if (e.getX() >= noeud.getX() - 10 && e.getX() <= noeud.getX() + 10
                            && e.getY() >= noeud.getY() - 10 && e.getY() <= noeud.getY() + 10) {
                        // Le clic de souris est sur un noeud
                        noeudSelectionne = noeud;
                        xSelectionne = e.getX() - noeud.getX();
                        ySelectionne = e.getY() - noeud.getY();
                        break;
                    }
                }
            }
        
            @Override
            public void mouseReleased(MouseEvent e) {
                
                noeudSelectionne = null;
            }
        
            @Override
            public void mouseDragged(MouseEvent e) {
                if (noeudSelectionne != null) {
                    noeudSelectionne.setX(e.getX() - xSelectionne);
                    noeudSelectionne.setY(e.getY() - ySelectionne);
                    repaint();
                }
            }

        });

    }

    private void initComponent() 
    {
        this.lstNoeuds = ctrl.getAlNoeuds();
        this.lstAretes = ctrl.getAlAretes();
    }

    public void paint(Graphics g)
    {
        super.paint(g);
        this.dessinerAretes(g);
        this.dessinerNoeuds(g);
    }

    private void dessinerAretes(Graphics g) 
    {
        super.paintComponent(g);

        for (Arete arete : lstAretes) 
        {
            g.setColor(Color.BLACK);

            Noeud noeud1 = arete.getNoeud1();
            Noeud noeud2 = arete.getNoeud2();
            
            int x1 = noeud1.getX() + 10;
            int y1 = noeud1.getY() + 10;
            int x2 = noeud2.getX() + 10;
            int y2 = noeud2.getY() + 10;
            
            int arrowSize = 10;
            double dx = x2 - x1;
            double dy = y2 - y1;
            double angle = Math.atan2(dy, dx);
            int lineX = (int) (x1 + (10 + arrowSize) * Math.cos(angle));
            int lineY = (int) (y1 + (10 + arrowSize) * Math.sin(angle));
            int arrowX = (int) (x2 - (10 + arrowSize) * Math.cos(angle));
            int arrowY = (int) (y2 - (10 + arrowSize) * Math.sin(angle));

            g.drawLine(lineX, lineY, arrowX, arrowY);
            
            int[] arrowHeadX = new int[3];
            int[] arrowHeadY = new int[3];
            arrowHeadX[0] = arrowX;
            arrowHeadY[0] = arrowY;
            arrowHeadX[1] = (int) (arrowX - arrowSize * Math.cos(angle - Math.PI / 6));
            arrowHeadY[1] = (int) (arrowY - arrowSize * Math.sin(angle - Math.PI / 6));
            arrowHeadX[2] = (int) (arrowX - arrowSize * Math.cos(angle + Math.PI / 6));
            arrowHeadY[2] = (int) (arrowY - arrowSize * Math.sin(angle + Math.PI / 6));
            
            g.fillPolygon(arrowHeadX, arrowHeadY, 3);

            int distance = 10;
            int costX = (int) (lineX + ((arrowX - lineX) / 2) + distance * Math.sin(angle));
            int costY = (int) (lineY + ((arrowY - lineY) / 2) - distance * Math.cos(angle));
        
            g.drawString(String.valueOf(arete.getCout()), costX, costY);
      
        }

    }
    
    private void dessinerNoeuds(Graphics g) 
    {
        for (Noeud n : lstNoeuds)
        {
            g.setColor(new Color(0, 151, 178));
            g.fillOval(n.getX(), n.getY(), 20, 20);
            g.setColor(Color.BLACK);
            g.drawString(n.getNom()+"", n.getX()+7, n.getY()+15);
        }   
    }   
 
    public void majIHM() 
    {
        this.repaint();
        this.revalidate();
        this.repaint();
    }

    public Noeud getNoeudSelec() {
        return noeudSelectionne;
    } 

}