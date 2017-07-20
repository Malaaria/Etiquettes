package lu.mullerwegener.etiquettes;

public class Etiquettes {
	
	private String compteur;
	private int ncompteur;
	
	Etiquettes(String initCompteur){
		System.out.println("initCompteur : " + initCompteur);
		compteur = initCompteur;
		ncompteur = Integer.valueOf(compteur).intValue();
	}
	
	protected void incrementCompteur(){
		this.ncompteur++;
		//this.compteur = Integer.toString(this.ncompteur);
		
		this.compteur = String.format("%1$04d", this.ncompteur);
		
		System.out.println("Compteur : " + this.compteur + ", ncompteur : " + this.ncompteur);
	}
	
	protected String getCompteur(){
		return this.compteur;
	}
	
	
}
