
package graphique;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import javax.print.DocFlavor.STRING;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

import jeu.BatailleControleur;
import jeu.Carte;
import jeu.Joueur;
import jeu.Partie;


/** A ajouter**/

public class PlateauGraphique extends JFrame{
	private JPanel plateau;
	private JPanel plateauSud;
	private JPanel plateauNord;
	private LogBataille log;
	private CartesBataille cartes;
	private JoueursBataille joueurs;
	private BatailleControleur bc;
	private ArrayList<String> listeDesJoueurs;
	private  int nbJoueur;
	private int  nbCartes;
	private int tailleJeuCarte;

	public PlateauGraphique(String titre, int w, int h)	{
	 	super(titre);
	 	SplashScreen splash = new SplashScreen(10000);
		splash.showSplash();
		this.lancerJeu();
		/*Parametre dialog = new Parametre();
		dialog.pack();
		dialog.setVisible(true);
		this.bc=new BatailleControleur(Partie.initialiseLeJeu(this.getNbJoueur(),this.getNbCartes(),this.getTailleJeuCarte(),this.getJoueurs()));**/
		this.initPlateau();
		this.initPlateauSud();
		this.initPlateauNord();
		this.initComposants();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setSize(w, h);
	   	this.setVisible(true);
	}
	public int getNbJoueur(){
		return this.nbJoueur;
	}
	public int getNbCartes(){
		return this.nbCartes;
	}

	public int getTailleJeuCarte(){
		return this.tailleJeuCarte;
	}

	public ArrayList<String> getJoueurs(){
		return this.listeDesJoueurs;
	}
	/** A ajouter**/
	public void initComposants()  {
		this.setLayout(new BorderLayout());
		this.add(plateau, BorderLayout.CENTER);
		this.add(plateauSud, BorderLayout.SOUTH);
		this.add(plateauNord, BorderLayout.NORTH);
	}

	public void initPlateau(){
		this.plateau=new JPanel();
		//GridLayout g =new GridLayout(1,1);
		this.plateau.setLayout(new FlowLayout());
		this.cartes=new CartesBataille();

		this.cartes.setSize((int)this.cartes.getSize().getWidth()/2,(int)this.cartes.getSize().getHeight()/2);
		this.plateau.add(this.cartes,SwingConstants.CENTER);


	}

	public void initPlateauNord(){
		this.plateauNord=new JPanel();
		this.plateauNord.setLayout(new FlowLayout());
		this.log=new LogBataille();
		this.plateauNord.add(this.log);
	}

	public void initPlateauSud(){
		this.plateauSud=new JPanel();
		BorderLayout g = new BorderLayout() ;
		this.plateauSud.setLayout(g);
		this.joueurs=new JoueursBataille();
		this.plateauSud.add(this.joueurs,BorderLayout.CENTER);
		JButton bExit=new JButton("Quitter");
		JButton bNext=new JButton("Tour Suivant");
		this.plateauSud.add(bExit,BorderLayout.WEST);
		this.plateauSud.add(bNext,BorderLayout.EAST);
		BoutonPartie exit=new BoutonPartie(0);
		BoutonPartie next=new BoutonPartie(1);
		bExit.addActionListener(exit);
		bNext.addActionListener(next);
	}

	public static void main(String[] args) {

		new PlateauGraphique("Bataille THE GAME",1500,1500);
	}

	/************* inner classe pour le Graphique	****************/
	class BoutonPartie implements ActionListener{
		int type;
		int compteur=0;

		public BoutonPartie(int t){
			this.type=t;
		}

		public void actionPerformed(ActionEvent arg0) {
			if(this.type==0){
				PlateauGraphique.this.dispose();
			}
			else{
				if(this.compteur==0){
					PlateauGraphique.this.bc.prepBataille();
					playSound("Game-Bataille-in-JAVA-master/sons/cardPlace1.wav");
					this.compteur++;
				}
				else{
					if(this.compteur==1){
						PlateauGraphique.this.bc.nextTurn();
                        playSound("Game-Bataille-in-JAVA-master/sons/cardSlide1.wav");
                        this.compteur=0;
					}
				}
			}

		}

	}

	class LogBataille extends JPanel implements Observer{
		JLabel l;
		public LogBataille(){
			this.l=new JLabel("Nouvelle Partie :",SwingConstants.CENTER);
			PlateauGraphique.this.bc.relieLog(this);
			this.add(this.l);
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			//Affichage Texte
			//JLabel l=new JLabel(s,SwingConstants.CENTER);
			//this.add(l);
			//String text="Joueurs en Bataille :\n";
	    	/*for(Joueur j:PlateauGraphique.this.bc.getListeJoueurBataille()){
	    		text+=j.getPseudo()+" ";
	    	}*/
			//this.l.setText(text);
			//this.validate();
			//PlateauGraphique.this.log.l.setText("test");
		}

		@Override
		public void update(Observable arg0, Object arg1) {
			// appelle méthode controleur

			//retourne la modification de la vue
			//this.repaint();

			//PlateauGraphique.this.log.l.setText("test");
			this.logText((String) arg1);
		}

		public void logText(String s){
			if(s instanceof String){
				if(s.equals("bataille")){
					String t=new String("Bataille avec les Joueurs : \n");
					for(Joueur j : PlateauGraphique.this.bc.getListeJoueurBataille()){
						t+=j.getPseudo()+" ";
					}
					this.l.setText(t);

				}

				if(s.equals("debut_bataille")){
					String t=new String("Nouveau tour avec les Joueurs : \n");
					for(Joueur j : PlateauGraphique.this.bc.getListeJoueurBataille()){
						t+=j.getPseudo()+" ";
					}
					this.l.setText(t);
				}

				if(s.equals("gagnant")){
					String t=new String("Gagnant du tour : \n");
					for(Joueur j : PlateauGraphique.this.bc.getListeJoueurBataille()){
						t+=j.getPseudo()+" ";
					}
					this.l.setText(t);
				}

			}

		}
	}

	class CartesBataille extends JPanel implements Observer {
		public CartesBataille(){
			this.setLayout(new FlowLayout());
			PlateauGraphique.this.bc.relieCarteBataille(this);
			//this.modifCartePlateau();
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			//Boucle qui récupére les cartes en Bataille
			//this.setBackground(Color.BLUE);

		}

		@Override
		public void update(Observable arg0, Object arg1) {
			if(arg1 instanceof String){
				this.modifCartePlateau((String) arg1);
			}
		}

		public void modifCartePlateau(String s){
			if(s.equals("carte")){
				this.removeAll();
				String chemin="Game-Bataille-in-JAVA-master/cartes/";
				for (Map.Entry<Joueur, Carte> entry : PlateauGraphique.this.bc.getLevee().entrySet()) {
					JPanel gridCartes=new JPanel();
					gridCartes.setLayout(new FlowLayout());
					LabelImage labCentre=new LabelImage(chemin+entry.getValue().getNomFichier());
					labCentre.setSize((int)PlateauGraphique.this.plateau.getSize().getWidth()/2,(int) PlateauGraphique.this.getSize().getHeight()/2);
					System.out.println(chemin+entry.getValue().getNomFichier()+"\n"+entry.getKey().getPseudo());
			/*labCentre.setHorizontalAlignment(JLabel.CENTER);
		    labCentre.setVerticalAlignment(JLabel.CENTER);*/
					gridCartes.add(new JLabel(entry.getKey().getPseudo()));
					gridCartes.add(labCentre);

					System.out.println("§§§§§§§§§ TEST RECUP NOM JOUEUR §§§§§§§"+entry.getKey().getPseudo());
					this.add(gridCartes);
				}
				this.validate();
				//this.repaint();
			}
			//this.getLevee().forEach((k,v) -> System.out.println("Joueur: "+k.getPseudo()+" Carte:"+v));
		}

	}

	class JoueursBataille extends JPanel implements Observer {
		//JPanel gridJoueurs;

		public JoueursBataille(){
			this.setLayout(new FlowLayout(FlowLayout.CENTER,10,this.getHeight()/2));
			//Boucle qui récupére les joueurs en Bataille
			//this.gridJoueurs=new JPanel(new GridLayout(1,2,10,10));
			for(Joueur j : PlateauGraphique.this.bc.getListeJoueurActif()){
				JLabel nom =new JLabel(j.getPseudo());
				JLabel nombreCarte =new JLabel("Nombre de Cartes : "+j.getMain().getLotDeCarte().size());
				JPanel gridJoueur=new JPanel(new GridLayout(2,1,10,0));
				gridJoueur.add(nom,SwingConstants.CENTER);
				gridJoueur.add(nombreCarte);
				//this.gridJoueurs.add(gridJoueur);
				this.add(gridJoueur);
			}
			//this.add(this.gridJoueurs);
			this.setLayout(new FlowLayout(FlowLayout.CENTER,10,this.getHeight()/2));
			PlateauGraphique.this.bc.relieJoueursBataille(this);
		}
		protected void paintComponent(Graphics g) {
			//this.removeAll();
			//this.modifListeJoueurs();
			super.paintComponent(g);
			//this.gridJoueur.removeAll();

			//Grise les couleurs netant plus actif
		    	/*for(Joueur j : PlateauGraphique.this.bc.getListeJoueurActif()){
		    		JLabel nom =new JLabel(j.getPseudo());
		    		JLabel nombreCarte =new JLabel("Nombre de Cartes : "+j.getMain().getLotDeCarte().size());
					this.gridJoueur.add(nom);
					this.gridJoueur.add(nombreCarte);
					this.add(this.gridJoueur);
				}*/

			//this.setLayout(new FlowLayout(FlowLayout.CENTER,10,this.getHeight()/2));
		    	/*
		    	for(Joueur j : PlateauGraphique.this.bc.getListeJoueurActif()){
		    		JLabel nom =new JLabel(j.getPseudo());
		    		JLabel nombreCarte =new JLabel("Nombre de Cartes : "+j.getMain().getLotDeCarte().size());
		    		JPanel gridJoueur=new JPanel(new GridLayout(2,1,10,0));
		    		gridJoueur.add(nom,SwingConstants.CENTER);
		    		gridJoueur.add(nombreCarte);
					//this.gridJoueurs.add(gridJoueur);
		    		this.add(gridJoueur);
				}*/
			//this.setBackground(Color.RED);

		}
		public void modifListeJoueurs(){
			this.removeAll();
			//this.setLayout(new FlowLayout(FlowLayout.CENTER,10,this.getHeight()/2));
			for(Joueur j : PlateauGraphique.this.bc.getListeJoueurActif()){
				JLabel nom =new JLabel(j.getPseudo());
				JLabel nombreCarte =new JLabel("Nombre de Cartes : "+j.getMain().getLotDeCarte().size());
				JPanel gridJoueur=new JPanel(new GridLayout(2,1,10,0));
				gridJoueur.add(nom,SwingConstants.CENTER);
				gridJoueur.add(nombreCarte);
				//this.gridJoueurs.add(gridJoueur);
				this.add(gridJoueur);
			}
			this.validate();
			this.repaint();
			//PlateauGraphique.this.repaint();
			//this.setVisible(true);
		}

		public void update(Observable arg0, Object arg1) {
			this.modifListeJoueurs();
			//this.repaint();
		}
	}

	class Parametre extends JDialog {
		private JPanel contentPane;
		private JButton buttonOK;
		private JButton buttonCancel;
		private JRadioButton a32RadioButton;
		private JComboBox nbJeuDeCartes;
		private JComboBox nbJoueurs;
		private JRadioButton a52RadioButton;


		public Parametre() {
			this.initComponents();
			setContentPane(contentPane);
			setModal(true);
			getRootPane().setDefaultButton(buttonOK);
			PlateauGraphique.this.listeDesJoueurs = new ArrayList<String>();
			this.buttonOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					PlateauGraphique.this.nbJoueur = Integer.parseInt(Parametre.this.nbJoueurs.getSelectedItem().toString());
					PlateauGraphique.this.tailleJeuCarte = Integer.parseInt(Parametre.this.nbJeuDeCartes.getSelectedItem().toString());
					boolean si32 = Parametre.this.a32RadioButton.isSelected();
					if (si32) {
						PlateauGraphique.this.nbCartes = 8;
					} else {

						PlateauGraphique.this.nbCartes = 13;
					}
					Object[] inputFields = new Object[PlateauGraphique.this.nbJoueur*2];
					for(int i=0; i<(PlateauGraphique.this.nbJoueur*2); i++){
						if(i%2==0){
							inputFields[i]="Joueur " + i/2;
						}
						else {
							inputFields[i]=new JTextField();

						}
					}

					int liste = JOptionPane.showConfirmDialog(Parametre.this, inputFields, "Entrer les noms des Joueurs", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

					if (liste == JOptionPane.OK_OPTION) {
						for(int i=0; i<PlateauGraphique.this.nbJoueur*2; i++){
							if(i%2!=0){
								JTextField jtt=(JTextField) inputFields[i];
								PlateauGraphique.this.listeDesJoueurs.add(jtt.getText());
							}
						}
					}
					onOK();
				}
			});

			this.buttonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onCancel();
				}
			});

			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					onCancel();
				}
			});

			contentPane.registerKeyboardAction(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onCancel();
				}
			}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		}

		public void initComponents() {
			this.contentPane = new JPanel();
			this.contentPane.setLayout(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();

			JPanel haut = JPanelHaut();
			JPanel bas = JPanelBas();
			this.contentPane.add(haut, gc);
			gc.weighty = 1;
			gc.weightx = 1;
			gc.gridx = 0;
			gc.gridy = 1;
			this.contentPane.add(bas, gc);
			gc.weightx = 1;
			gc.weighty = 0;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.fill = GridBagConstraints.LAST_LINE_END;

		}

		private JPanel JPanelHaut() {
			JPanel jPanelhaut = new JPanel();
			jPanelhaut.setLayout(new GridBagLayout());
        /* Le gridBagConstraints va définir la position et la taille des éléments */
			GridBagConstraints gc = new GridBagConstraints();
        /* le parametre fill sert à définir comment le composant sera rempli GridBagConstraints.BOTH permet d'occuper tout l'espace disponible
		 * horizontalement et verticalement GridBagConstraints.HORIZONTAL maximise horizontalement GridBagConstraints.VERTICAL maximise verticalement
		 */
			gc.fill = GridBagConstraints.BOTH;

        /* insets définir la marge entre les composant new Insets(margeSupérieure, margeGauche, margeInférieur, margeDroite) */
			gc.insets = new Insets(0, 0, 0, 0);

		/* ipady permet de savoir où on place le composant s'il n'occupe pas la totalité de l'espace disponnible */
			gc.ipady = gc.anchor = GridBagConstraints.CENTER;

		/* weightx définit le nombre de cases en abscisse */
			gc.weightx = 0;

		/* weightx définit le nombre de cases en ordonnée */
			gc.weighty = 0;

		/* pour dire qu'on ajoute un composant en position (i, j), on définit gridx=i et gridy=j */
			gc.gridx = 0;
			gc.gridy = 0;

			JPanel titre = new JPanel();
			JLabel labataille = new JLabel("CHOISISSEZ VOS OPTIONS");
			titre.add(labataille);
			JPanel vide = JPanelSelection();
			jPanelhaut.add(titre, gc);
			gc.gridx = 0;
			gc.gridy = 1;
			gc.fill = GridBagConstraints.BOTH;
			gc.gridwidth = 1;
			gc.weightx = 1;
			gc.weighty = 1;


			jPanelhaut.add(vide, gc);
			gc.gridx = 1;
			gc.gridy = 1;
			gc.weightx = 1;
			gc.weighty = 1;

			return jPanelhaut;
		}

		private JPanel JPanelSelection() {
			JPanel jPanelSelection = new JPanel();
			jPanelSelection.setLayout(new GridLayout(1, 1));
			JPanel labels = new JPanel();
			labels.setLayout(new GridLayout(0, 1));
			JLabel joueurs = new JLabel("Nombre de Joueurs");
			JLabel cartes = new JLabel("Nombre de Cartes");
			JLabel jeux = new JLabel("Nombre de Jeu de cartes");
			labels.add(joueurs);
			labels.add(cartes);
			labels.add(jeux);


			JPanel options = new JPanel();
			options.setLayout(new GridLayout(0, 1));
			JPanel nombreDeJoueurs = new JPanel();
			String[] nbj = {"2", "4", "6", "8", "10"};
			this.nbJoueurs = new JComboBox(nbj);
			this.nbJoueurs.setSelectedIndex(0);
			nombreDeJoueurs.add(this.nbJoueurs);
			options.add(nombreDeJoueurs);


			JPanel nombreDeCartes = new JPanel();
			ButtonGroup btg = new ButtonGroup();
			this.a32RadioButton = new JRadioButton("32");
			this.a52RadioButton = new JRadioButton("52");
			this.a32RadioButton.setSelected(true);
			btg.add(this.a32RadioButton);
			btg.add(this.a52RadioButton);
			nombreDeCartes.add(this.a32RadioButton);
			nombreDeCartes.add(this.a52RadioButton);
			options.add(nombreDeCartes);

			JPanel nombreDeJeux = new JPanel();
			String[] nbjc = {"1", "2", "3", "4", "5"};
			this.nbJeuDeCartes = new JComboBox(nbjc);
			this.nbJeuDeCartes.setSelectedIndex(0);
			nombreDeJeux.add(this.nbJeuDeCartes);
			options.add(nombreDeJeux);


			jPanelSelection.add(labels);
			jPanelSelection.add(options);

			return jPanelSelection;
		}

		private JPanel JPanelBas() {
			JPanel jPanelbas = new JPanel();
			jPanelbas.setLayout(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			JPanel action = new JPanel();
			this.buttonOK = new JButton("OK");
			this.buttonOK.setBackground(Color.cyan);
			this.buttonCancel = new JButton("Annuler");
			this.buttonCancel.setBackground(Color.red);
			action.add(this.buttonOK);
			action.add(this.buttonCancel);
			jPanelbas.add(action, gc);
			gc.gridx = 0;
			gc.gridy = 1;
			gc.fill = GridBagConstraints.LAST_LINE_END;
			return jPanelbas;
		}

		private void onOK() {
			dispose();
		}

		private void onCancel() {
			dispose();
		}


	}
	/** A ajouter**/


	public void lancerJeu(){
		String[] lesPartie = {"nouvelle partie", "test 1", "test 2", "test 3"};
		JComboBox listeDePartie = new JComboBox(lesPartie);


		Object[] choixDePartie = {"Choisir une partie",listeDePartie };

		int liste = JOptionPane.showConfirmDialog(PlateauGraphique.this, choixDePartie, "Choisir une partie", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
		int lecChoix = listeDePartie.getSelectedIndex();
		if (liste == JOptionPane.OK_OPTION) {
			switch (lecChoix){
				case 0: Parametre dialog = new Parametre();
						dialog.pack();
						dialog.setVisible(true);
						this.bc=new BatailleControleur(Partie.initialiseLeJeu(this.getNbJoueur(),this.getNbCartes(),this.getTailleJeuCarte(),this.getJoueurs()));
					break;
				case 1: try{
								this.bc = new BatailleControleur(Partie.intialiseTest1());
							}
						catch (Exception e){
								System.out.println("Erreur d'initialisation au niveau du jeu d'essai 1");
							}
					break;
				case 2:try{
								this.bc = new BatailleControleur(Partie.initialiseJeuEssai2());
							}
						catch (Exception e){
								System.out.println("Erreur d'initialisation au niveau du jeu d'essai 2");
							}
					break;
				case 3: try{
								this.bc = new BatailleControleur(Partie.initialiseJeuEssai3());
							}
						catch (Exception e){
								System.out.println("Erreur d'initialisation au niveau du jeu d'essai 3");
							}
					break;
			}
		}
	}



	public void playSound(String soundName)
	{
		try
		{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile( ));
			Clip clip = AudioSystem.getClip( );
			clip.open(audioInputStream);
			clip.start( );
		}
		catch(Exception ex)
		{
			System.out.println("Error with playing sound.");
			ex.printStackTrace( );
		}
	}
}


