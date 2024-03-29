package ihm;

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.Component;

import java.util.List;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import controleur.Controleur;
import metier.Arete;
import metier.Noeud;

public class DialogTabDistance extends JDialog
{
    private Controleur ctrl;
    private Noeud noeudDepart;

    private Object[] ligne;

    private List<Noeud> lstNoeuds;
    private List<Arete> lstAretes;

    private int[] tabDistance;
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane scrollPane;

    public DialogTabDistance(Controleur ctrl, Noeud noeudDepart)
    {
        this.setTitle("Tableau des distances de " + noeudDepart.getNom());
        this.setLayout(new BorderLayout());

        this.ctrl = ctrl;
        this.noeudDepart = noeudDepart;
        this.lstNoeuds = ctrl.getAlNoeuds();
        this.lstAretes = ctrl.getAlAretes();

        this.table = new JTable();
        this.table.setDefaultEditor(Object.class, null);
        this.model = new DefaultTableModel();
        this.scrollPane = new JScrollPane(table);
        
        this.initialisation();
        this.bellmanFord();

        table.setModel(model);
        this.add(scrollPane, BorderLayout.CENTER);

        this.setAlwaysOnTop(true);
        this.setSize(table.getPreferredSize().width + 80, table.getPreferredSize().height + 80);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setVisible(true);

    }

    public void initialisation()
    {
        //phase d'initialisation du tableau
        String[] entetes = new String[lstNoeuds.size()+1];
        entetes[0] = "";
        
        for (int i = 0; i < lstNoeuds.size(); i++)
        {
            entetes[i+1] = ""+lstNoeuds.get(i).getNom();
        }

        model.setColumnIdentifiers(entetes);
        
        //on initialise le tableau des distances
        this.tabDistance = new int[lstNoeuds.size()];

        tabDistance[noeudDepart.getId()] = 0;
        for (int i = 0; i < lstNoeuds.size(); i++)
        {
            if (i != noeudDepart.getId())
                tabDistance[i] = Integer.MAX_VALUE;
        }

        //on écrit dans la première ligne du tableau les distances initiales

        ligne = new Object[lstNoeuds.size() + 1];
        ligne[0] = "Initialisation";
        for (int i = 0; i < tabDistance.length; i++)
        {
            if (tabDistance[i] == Integer.MAX_VALUE)
                ligne[i + 1] = "+∞";
            else
                ligne[i + 1] = tabDistance[i];
        }

        model.addRow(ligne);
    }


    public void bellmanFord()
    {
        for (int i = 0; i < lstNoeuds.size() - 1; i++)
        {
            for (Arete a : lstAretes)
            {
                int indexDepart = this.lstNoeuds.indexOf(a.getNoeud1());
                int indexArrivee = this.lstNoeuds.indexOf(a.getNoeud2());
                int cout = a.getCout();

                if (tabDistance[indexDepart] != Integer.MAX_VALUE && 
                    tabDistance[indexDepart] + cout < tabDistance[indexArrivee])
                {
                    tabDistance[indexArrivee] = tabDistance[indexDepart] + cout;
                }
            }

            //on écrit dans la ligne suivante du tableau les nouvelles distances
            ligne = new Object[lstNoeuds.size() + 1];
            ligne[0] = "Iteration " + (i + 1);
            for (int j = 0; j < tabDistance.length; j++)
            {
                if (tabDistance[j] == Integer.MAX_VALUE)
                    ligne[j + 1] = "+∞";
                else
                    ligne[j + 1] = tabDistance[j];

            }

            //si la ligne est identique à la précédente, on arrête l'algorithme
            if (model.getRowCount() > 0)
            {
                //on récupère chaque élément de la ligne précédente
                Object[] tabLignePrecedente = new Object[model.getColumnCount() - 1];

                for (int j = 0; j < tabLignePrecedente.length; j++)
                {
                    tabLignePrecedente[j] = model.getValueAt(model.getRowCount() - 1, j + 1);
                }

                //on vérifie si la ligne actuelle est identique à la précédente
                int nbIdentiques = 0;
                for (int j = 0; j < tabLignePrecedente.length; j++)
                {
                    if (tabLignePrecedente[j].equals(ligne[j + 1]))
                        nbIdentiques++;
                }

                if (nbIdentiques == tabLignePrecedente.length)
                    break;
                else 
                    model.addRow(ligne);

            }

        }

        //circuit absorbant
        boolean circuitAbsorbant = false;
        for (Arete a : lstAretes)
        {
            int indexDepart = this.lstNoeuds.indexOf(a.getNoeud1());
            int indexArrivee = this.lstNoeuds.indexOf(a.getNoeud2());
            int cout = a.getCout();

            if (tabDistance[indexDepart] != Integer.MAX_VALUE && 
                tabDistance[indexDepart] + cout < tabDistance[indexArrivee])
            {
                circuitAbsorbant = true;
                break;
            }
        }

        if (circuitAbsorbant)
        {
            this.add(new JLabel("Il y a un circuit absorbant"), BorderLayout.SOUTH);
            System.out.println("Il y a un circuit absorbant");
        }
    }
}
