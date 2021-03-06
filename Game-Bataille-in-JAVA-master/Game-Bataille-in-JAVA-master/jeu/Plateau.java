package jeu;


import java.util.*;
import java.util.Map.Entry;

/**
 * Classe de gestion du plateau du jeu
 */
public class Plateau extends Observable {



	/**
	 * Liste des cartes des cartes de chaque joueurs dans une bataille
	 */
	private HashMap<Joueur,Carte> levee;

	/**
	 * Liste totale des cartes posÃ©es par l'ensemble des joueurs au cours d'un tour
	 */
	private HashSet<Carte> leveeAdditionne;

	/**
	 * Liste des joueurs actifs au sein du tour
	 */
	private HashSet<Joueur> listeJoueurBataille ;

	/**
	 *Constructeur de la Classe Plateau
	 */
	public Plateau() {
		this.levee=new HashMap<Joueur,Carte>();
		this.leveeAdditionne = new HashSet<Carte>();
		this.listeJoueurBataille=new HashSet<Joueur>();
	}
	/**
	 * Getter de la levee des carte posee pour une bataille d'un tour
	 * @return HashMap<Joueur,Carte> Contient toute les cartes posÃ©es pour une bataille
	 */
	public HashMap<Joueur,Carte> getLevee(){
		return this.levee;
	}
	/**
	 * Getters de la liste de Joueur en bataille
	 * @return HashSet<Joueur> la liste des joueurs en pleine bataille
	 */

	public HashSet<Joueur> getListeJoueurBataille(){
		return this.listeJoueurBataille;
	}
	/**
	 * Getter de la  levÃ©e globale
	 * @return HashSet <Carte> Contient toute les carte posÃ©es par tous les joueurs au cours d'un tour
	 */
	public HashSet <Carte> getLeveeAdditionne(){
		return this.leveeAdditionne;
	}

	/**
	 * MÃ©thode permettant de dÄ™clencher une bataille et de la gérer.
	 * @param p partie en cours
	 */

	public void bataille(Partie p) {
		for(Joueur j : p.getListeJoueur() ){
			this.listeJoueurBataille.add(j);
		}
		this.setChanged();
		this.notifyObservers("debut_bataille");

		boolean tourGagne = false;

		while(this.getListeJoueurBataille().size()>1){
			this.preparationBataille(); //PrÃ©paration a la bataille, pioche une carte de la main pour la mettre sur le plateau
			//VERIFIER SI LITERATEUR FONCTIONNE DANS LETAT ACTUEL SINON DANS LA BOUCLE FOR REMPLACER this.levee.entrySet par une variable contenant cette liste
			System.out.println("Preparation Bataille FINI !");

			this.getLevee().forEach((k,v) -> System.out.println("Joueur: "+k.getPseudo()+" Carte:"+v));

			/*
			Iterator<Entry<Joueur, Carte>> it=this.levee.entrySet().iterator();
			boolean removeJoueurPerduBataille=true;
			*/
			/*
			while(it.hasNext()){ //controle si un joueur a une carte plus puissante que les autres
				Iterator<Entry<Joueur, Carte>> itComparaison=this.levee.entrySet().iterator(); //iterateur permettant de supprimer les joueurs ayant perdu la bataille de la liste
				while(itComparaison.hasNext()){
					if(it.next().getValue().compareTo(itComparaison.next().getValue())==1){
						this.leveeAdditionne.add(itComparaison.next().getValue()); //ajoute la carte du joueur Ã  la levee globale du tour, qui sera gagne par le gagnant du tour
						this.removeJoueurBataille(itComparaison.next().getKey()); //supprime le joueur de la liste des joueurs encore actif ce tour
						itComparaison.remove(); //supprime la carte de levee en cours
						it=itComparaison;
					}
				}
			}
			*/
			for (Map.Entry<Joueur, Carte> entry1 : this.getLevee().entrySet()) {
				for (Map.Entry<Joueur, Carte> entry2 : this.getLevee().entrySet()){
					if(entry1.getValue().compareTo(entry2.getValue())==1){
						this.removeJoueurBataille(entry2.getKey());
					}
				}
			}

			/*
			while(removeJoueurPerduBataille==true){
				removeJoueurPerduBataille=false;
				while(it.hasNext()){
					if(it.next().getValue().compareTo(it.next().getValue())==1){
						this.removeJoueurBataille(j);
						removeJoueurPerduBataille=true;
					}
				}
			}
			*/

			System.out.println();
			System.out.println("Liste Joueur en Bataille : \n");
			for(Joueur j : this.getListeJoueurBataille()){
				System.out.println(j.getPseudo());
			}
			System.out.println();

			if(this.getListeJoueurBataille().size()==1){ //nous avons un gagnant

				tourGagne=true;

			}

			if(this.getLevee().size()>0){ //rajoute toute les cartes de la levÃ©e Ã  la levÃ©e gloabale
				Iterator <Entry<Joueur,Carte>> itCarteLevee= this.getLevee().entrySet().iterator();
				while(itCarteLevee.hasNext()){
					this.getLeveeAdditionne().add(itCarteLevee.next().getValue()); //ajoute la carte Ã  la levÃ©e globale
					itCarteLevee.remove();// retire la carte de la levÃ©e de la bataille
				}
			}

			if(tourGagne==false){ //vÃ©rifie quels sont les joueurs ayant des cartes Ã©gales et prÃ©pare la nouvelle bataille
				Iterator <Joueur> itJoueurPerdu=this.getListeJoueurBataille().iterator();
				while(itJoueurPerdu.hasNext()){
					Joueur j = itJoueurPerdu.next();
					System.out.println("Joueur en bataille :"+j.getPseudo() + " " + j.getMain());

					if(!j.partiePerdue()){
						j.poserCarteVersLeveeAdditionne(p);//chaque joueur doit poser une carte dans la levee globale (regle du jeu)
					}
					else{
						itJoueurPerdu.remove();
					}

				}
				this.setChanged();
				this.notifyObservers("bataille");
			}

		}


		if(tourGagne==true){
			for(Joueur j : this.getListeJoueurBataille()){
				//ajouter les cartes des leveeAdditionne au joueur gagnant en principe il n'y a plus de carte dans la levee(tous Ã  Ã©tÃ© transferrÃ© Ã  la levÃ©eAdditionnÃ©e)
				j.recupererCartes(this.getLeveeAdditionne());
			}
			this.getLeveeAdditionne().clear(); //supprime les cartes de la leveeAdditionne
			this.setChanged();
			this.notifyObservers("gagnant");
		}

		for(Joueur j : this.getListeJoueurBataille()){
			System.out.println(j.getPseudo() + " gagne la partie avec " + j.getMain().getLotDeCarte());
			System.out.println("Ce qui fait "+ j.getMain().getLotDeCarte().size()+" cartes dans sa main.");

		}
		//Supprimer la liste de joueur bataille (la remettre Ã  0), vide
		this.getListeJoueurBataille().clear();
		System.out.println("FIN TOUR");
	}

	public void batailleGraphique(Partie p) {

		boolean tourGagne = false;


		//VERIFIER SI LITERATEUR FONCTIONNE DANS LETAT ACTUEL SINON DANS LA BOUCLE FOR REMPLACER this.levee.entrySet par une variable contenant cette liste
		System.out.println("Preparation Bataille FINI !");

		this.getLevee().forEach((k,v) -> System.out.println("Joueur: "+k.getPseudo()+" Carte:"+v));


		for (Map.Entry<Joueur, Carte> entry1 : this.getLevee().entrySet()) {
			for (Map.Entry<Joueur, Carte> entry2 : this.getLevee().entrySet()){
				if(entry1.getValue().compareTo(entry2.getValue())==1){
					this.removeJoueurBataille(entry2.getKey());
				}
			}
		}


		System.out.println();
		System.out.println("Liste Joueur en Bataille : \n");
		for(Joueur j : this.getListeJoueurBataille()){
			System.out.println(j.getPseudo());
		}
		System.out.println();

		if(this.getListeJoueurBataille().size()==1){ //nous avons un gagnant

			tourGagne=true;

		}

		if(this.getLevee().size()>0){ //rajoute toute les cartes de la levÃ©e Ã  la levÃ©e gloabale
			Iterator <Entry<Joueur,Carte>> itCarteLevee= this.getLevee().entrySet().iterator();
			while(itCarteLevee.hasNext()){
				this.getLeveeAdditionne().add(itCarteLevee.next().getValue()); //ajoute la carte Ã  la levÃ©e globale
				itCarteLevee.remove();// retire la carte de la levÃ©e de la bataille
			}
		}

		if(tourGagne==false){ //vÃ©rifie quels sont les joueurs ayant des cartes Ã©gales et prÃ©pare la nouvelle bataille
			Iterator <Joueur> itJoueurPerdu=this.getListeJoueurBataille().iterator();
			while(itJoueurPerdu.hasNext()){
				Joueur j = itJoueurPerdu.next();
				System.out.println("Joueur en bataille :"+j.getPseudo() + " " + j.getMain());

				if(!j.partiePerdue()){
					j.poserCarteVersLeveeAdditionne(p);//chaque joueur doit poser une carte dans la levee globale (regle du jeu)
				}
				else{
					itJoueurPerdu.remove();
				}

			}
			this.setChanged();
			this.notifyObservers("bataille");
		}




		if(tourGagne==true){
			for(Joueur j : this.getListeJoueurBataille()){
				//ajouter les cartes des leveeAdditionne au joueur gagnant en principe il n'y a plus de carte dans la levee(tous Ã  Ã©tÃ© transferrÃ© Ã  la levÃ©eAdditionnÃ©e)
				j.recupererCartes(this.getLeveeAdditionne());
			}
			this.getLeveeAdditionne().clear(); //supprime les cartes de la leveeAdditionne
			this.setChanged();
			this.notifyObservers("gagnant");
		}

		for(Joueur j : this.getListeJoueurBataille()){
			System.out.println(j.getPseudo() + " gagne la partie avec " + j.getMain().getLotDeCarte());
			System.out.println("Ce qui fait "+ j.getMain().getLotDeCarte().size()+" cartes dans sa main.");

		}
		//Supprimer la liste de joueur bataille (la remettre Ã  0), vide
		this.getListeJoueurBataille().clear();
		System.out.println("FIN TOUR");
	}
	/**
	 * MÃ©thode permettant de prÃ©parer la bataille, rÃ©cupÃ©ration des cartes de la main de chaque joueur pour l'ajouter a chaque plateau individuel
	 */
	public void preparationBataille() {
		for(Joueur j : this.getListeJoueurBataille()){
			j.poserCarte(); //prend une carte de la main et la pose sur le plateau individuel
			if(j.getMonPlateau().getCartePose().getHauteur()!=0){ //
				this.levee.put(j, j.getMonPlateau().getCartePose()); //transfert la carte du plateau indivduel au plateau collectif
				//!!! PENSER A SUPPRIMER LA CARTE SI ELLE NA PAS ETE ENLEVE DAS LA METHODE poserCarte()
				j.getMonPlateau().setCartePose(new Carte());//VERIFIER PERTINENCE !!! Une fois la carte mis dans le Plateau de la partie, la carte dans le plateau Individuel est remplacÃƒÂ© par une carte nulle
			}

		}
		this.setChanged();
		this.notifyObservers("carte");
	}

	public void preparationBatailleGraphique(Partie p){
		for(Joueur j : p.getListeJoueur() ){
			this.listeJoueurBataille.add(j);
		}
		this.setChanged();
		this.notifyObservers("debut_bataille");

		for(Joueur j : this.getListeJoueurBataille()){
			j.poserCarte(); //prend une carte de la main et la pose sur le plateau individuel
			if(j.getMonPlateau().getCartePose().getHauteur()!=0){ //
				this.levee.put(j, j.getMonPlateau().getCartePose()); //transfert la carte du plateau indivduel au plateau collectif
				//!!! PENSER A SUPPRIMER LA CARTE SI ELLE NA PAS ETE ENLEVE DAS LA METHODE poserCarte()
				j.getMonPlateau().setCartePose(new Carte());//VERIFIER PERTINENCE !!! Une fois la carte mis dans le Plateau de la partie, la carte dans le plateau Individuel est remplacÃƒÂ© par une carte nulle
			}

		}
		this.setChanged();
		this.notifyObservers("carte");
	}
	/**
	 * MÃ©thode permettant de supprimer un joueur de la liste des joueurs du tour
	 * @param j joueur a supprimer
	 */
	public void removeJoueurBataille(Joueur j){
		Iterator <Joueur> ite =this.getListeJoueurBataille().iterator();
		while(ite.hasNext()){
			if(j.compareIdJoueur(ite.next())==1){
				ite.remove();
			}
		}
		this.setChanged();
		this.notifyObservers("bataille");
	}


}