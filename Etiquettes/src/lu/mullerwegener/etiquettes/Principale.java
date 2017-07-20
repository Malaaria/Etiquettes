package lu.mullerwegener.etiquettes;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dialog.ModalExclusionType;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Principale {

	private JFrame frmPrincipale;
	private JTable table;
	private JScrollPane scrollPane;
	private JButton btnImprimer;
	private ArrayList<String> alConfig;
	private String[][] myConfig;
	private JDialog frmAide;
	private JDialog frmAProposDe;
	private JMenuBar menuBar;
	private JMenu mnFichier;
	private JMenu mnAide;
	private JMenuItem mntmQuitter;
	private JMenuItem mntmAide;
	private JMenuItem mntmAPropos;
	private JTextField txtAnnee;
	private JPanel panelNorth;
	private EtiquettesTableModel tableModel;
	private JPanel panelAnnee;
	private JLabel lblAnnee;
	private JPanel panelPage;
	private JLabel lblPage;
	private JTextField txtNbPages;
	private JPanel panelAnneePage;	
	private JLabel textGeneration;	
	String sService;
	String sCompteur;
	EtiquettesCreatePDF ecp;	
	boolean table_manuel = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Principale window = new Principale();
					window.frmPrincipale.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Principale() {
		initialize();		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {		
		frmPrincipale = new JFrame();
		frmPrincipale.setTitle("Etiquettes");
		frmPrincipale.setBounds(100, 100, 780, 380);
		frmPrincipale.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		

		/*Frame Pour avertir de la génération*/

		textGeneration = new JLabel("Génération en cours, patientez svp.");							
		textGeneration.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		textGeneration.setHorizontalAlignment(SwingConstants.CENTER);

		panelNorth = new JPanel();
		frmPrincipale.getContentPane().add(panelNorth, BorderLayout.NORTH);
		panelNorth.setLayout(new BorderLayout(0, 0));		

		JLabel lblEtiquettesByMw = new JLabel("Etiquettes by MW");
		panelNorth.add(lblEtiquettesByMw, BorderLayout.NORTH);
		lblEtiquettesByMw.setHorizontalAlignment(SwingConstants.CENTER);
		lblEtiquettesByMw.setFont(new Font("Segoe UI", Font.PLAIN, 24));

		panelAnneePage = new JPanel();
		panelNorth.add(panelAnneePage, BorderLayout.WEST);

		panelAnnee = new JPanel();
		panelAnneePage.add(panelAnnee);

		lblAnnee = new JLabel("Ann\u00E9e:");
		panelAnnee.add(lblAnnee);

		txtAnnee = new JTextField();
		panelAnnee.add(txtAnnee);
		Calendar cal = Calendar.getInstance();
		String sAnnee = Integer.toString(cal.get(cal.YEAR));
		txtAnnee.setText(sAnnee);
		txtAnnee.setColumns(4);

		panelPage = new JPanel();
		panelAnneePage.add(panelPage);

		lblPage = new JLabel("Nombre de pages :");
		panelPage.add(lblPage);

		txtNbPages = new JTextField();
		txtNbPages.setText("1");
		panelPage.add(txtNbPages);
		txtNbPages.setColumns(4);

		DocumentListener documentListener = new DocumentListener() {
			public void changedUpdate(DocumentEvent documentEvent) {				
				refreshTable(true);
			}
			public void insertUpdate(DocumentEvent documentEvent) {				
				refreshTable(true);
			}
			public void removeUpdate(DocumentEvent documentEvent) {				
				refreshTable(true);
			}		    
		};
		txtAnnee.getDocument().addDocumentListener(documentListener);

		btnImprimer = new JButton("Imprimer");
		btnImprimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectedRow()>=0){														
					sService = (String)table.getValueAt(table.getSelectedRow(), 1);
					sCompteur = (String)table.getValueAt(table.getSelectedRow(), 2);
					String sAnnee = txtAnnee.getText();
					try {								
						frmPrincipale.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						ecp = new EtiquettesCreatePDF();
						ecp.trait(sService, sCompteur, txtNbPages.getText(), sAnnee);		
						frmPrincipale.setCursor(null);
						if(ecp.print()){
							setConfig(sService, ecp.getCompteur(), sAnnee);
							refreshTable(false);
						}						
					} catch (IOException | PrinterException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}					
				}else{
					JOptionPane.showMessageDialog(null, "Vous devez sélectionner une ligne.");
				}
			}
		});
		frmPrincipale.getContentPane().add(btnImprimer, BorderLayout.SOUTH);


		tableModel = new EtiquettesTableModel(myConfig);		
		table = new JTable();						
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(174);
		table.getColumnModel().getColumn(2).setPreferredWidth(105);
		scrollPane = new JScrollPane(table);
		frmPrincipale.getContentPane().remove(scrollPane);
		frmPrincipale.getContentPane().add(scrollPane, BorderLayout.CENTER);	
		refreshTable(false);				
		
		menuBar = new JMenuBar();
		frmPrincipale.setJMenuBar(menuBar);

		mnFichier = new JMenu("Fichier");
		menuBar.add(mnFichier);

		mntmQuitter = new JMenuItem("Quitter");
		mntmQuitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmPrincipale.dispose();
			}
		});
		mnFichier.add(mntmQuitter);

		mnAide = new JMenu("Aide");
		menuBar.add(mnAide);

		mntmAide = new JMenuItem("Aide");
		mntmAide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmAide = new JDialog(frmPrincipale, "Aide", true);
				frmAide.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				JTextArea txtAide = new JTextArea();
				txtAide.setText("Ce programme a été créé pour imprimer les étiquettes de Fiche Suivi Qualité (ref. 4607 chez Herma)"
						+ "\n\nSélectionnez une ligne en cliquant dessus"
						+ "\nChoisissez l'année"
						+ "\nChoisissez le nombre de pages"
						+ "\nCliquer sur le bouton imprimer, génèrera en mémoire un PDF, l'application sera indisponible pendant ce temps."
						+ "\nUne fois la génération terminée, une boite de dialogue avec le choix de l'imprimante s'ouvrira :"
						+ "\n\n -Si vous cliquez sur Annuler, il ne se passera rien, et le fichier de configuration ne sera pas mis à jour."
						+ "\n -Si vous cliquez sur Ok, le PDF sera envoyé à l'imprimante et le fichier de configuration sera mis à jour."
						);				
				txtAide.setEditable(false);
				txtAide.setWrapStyleWord(true);
				txtAide.setLineWrap(true);
				frmAide.getContentPane().add(txtAide);				
				frmAide.setSize(500, 300);
				frmAide.setLocationRelativeTo(frmPrincipale);
				frmAide.setVisible(true);		
			}
		});
		mnAide.add(mntmAide);

		mntmAPropos = new JMenuItem("A propos de...");
		mntmAPropos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmAProposDe = new JDialog(frmPrincipale, "A propos de", true);
				frmAProposDe.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				JTextArea txtAProposDe = new JTextArea();
				txtAProposDe.setText("Etiquettes by MW\n\n"
						+ "Créé par Paquet Fabien du 05/12/2016 au 08/12/2016.\n\n"
						+ "Visitez notre site sur https://www.mullerwegener.lu");				
				txtAProposDe.setEditable(false);
				frmAProposDe.getContentPane().add(txtAProposDe);				
				frmAProposDe.setSize(500, 300);
				frmAProposDe.setLocationRelativeTo(frmPrincipale);
				frmAProposDe.setVisible(true);				
			}
		});
		mnAide.add(mntmAPropos);	
	}

	private String[][] getConfig() throws IOException{
		ArrayList<String[]> listConfig = new ArrayList<String[]>();		
		String cheminconfig = "./config.txt";
		File configfile = new File(cheminconfig);
		String[][] returnConfig;
		boolean isCreated = configfile.createNewFile();		
		boolean isExisting = configfile.exists();
		if(isCreated || isExisting){
			FileReader fileReader = new FileReader(cheminconfig);
			BufferedReader br = new BufferedReader(fileReader);
			String line = br.readLine();
			alConfig = new ArrayList<String>();
			while(line != null){				
				alConfig.add(line);
				String[] lineSplit = line.split(";");
				if(txtAnnee.getText().equals(lineSplit[0])){
					listConfig.add(lineSplit);
				}
				line = br.readLine();
			}
			br.close();
			fileReader.close();
			if(listConfig.size()==0){
				returnConfig = new String[1][4];
			}else{
				returnConfig = new String[listConfig.size()][3];
			}
			for(int _i0=0;_i0<listConfig.size();_i0++){						
				returnConfig[_i0][0] = listConfig.get(_i0)[1];
				returnConfig[_i0][1] = listConfig.get(_i0)[2];
				returnConfig[_i0][2] = listConfig.get(_i0)[3];				
			}
		}else{
			returnConfig = new String[1][4];
		}

		return returnConfig;
	}

	private void setConfig(String service, String compteur, String annee){
		System.out.println("Writing config : " + annee + ", " + service + ", " + compteur);
		String cheminconfig = "./config.txt";
		File configfile = new File(cheminconfig);
		try {
			FileWriter fw = new FileWriter(configfile);
			BufferedWriter bw = new BufferedWriter(fw);
			String lineToWrite = "";
			String[] lineSplit;
			for(int _i0=0;_i0<alConfig.size();_i0++){
				lineToWrite = alConfig.get(_i0);
				if(lineToWrite.contains(";" + service +";") && lineToWrite.contains(annee + ";")){
					lineSplit = lineToWrite.split(";");
					lineToWrite = lineSplit[0] + ";" + lineSplit[1] + ";" + lineSplit[2] + ";" + compteur;
				}
				bw.write(lineToWrite);
				bw.newLine();
			}
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private void refreshTable(boolean table_manuel){
		try {
			myConfig = getConfig();			
		} catch (IOException e) {
			myConfig = new String[][]{{"", ""}};
		}		
		if(table.getSelectedRow()!= -1 && table.getSelectedColumn() != -1){
			String table_value = (String)table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()); 
			if(table_manuel && !table_value.equals(myConfig[table.getSelectedRow()][table.getSelectedColumn()])){
				myConfig[table.getSelectedRow()][table.getSelectedColumn()] = table_value;
				table_manuel = false;
			}
		}
		tableModel.setData(myConfig);
		table.repaint();
	}
}
