package fr.esgi.banque.business;

public class CompteIntrouvableException extends RuntimeException {

	public CompteIntrouvableException(String numeroCompte) {
		super("Compte introuvable : " + numeroCompte);
	}

}
