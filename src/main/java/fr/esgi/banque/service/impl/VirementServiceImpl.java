package fr.esgi.banque.service.impl;

import fr.esgi.banque.business.Compte;
import fr.esgi.banque.business.CompteIntrouvableException;
import fr.esgi.banque.business.Virement;
import fr.esgi.banque.service.CompteRepository;
import fr.esgi.banque.service.NotificationService;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

/**
 * Service de virement entre deux comptes.
 */
public class VirementServiceImpl {

	private final CompteRepository repository;
	private final NotificationService notifications;
	private final Clock horloge;

	public VirementServiceImpl(CompteRepository repository, NotificationService notifications, Clock horloge) {
		this.repository = repository;
		this.notifications = notifications;
		this.horloge = horloge;
	}

	public Virement virer(String numeroSource, String numeroDestination, BigDecimal montant) {
		if (montant == null || montant.signum() <= 0) {
			throw new IllegalArgumentException("Le montant du virement doit être strictement positif");
		}
		if (numeroSource.equals(numeroDestination)) {
			throw new IllegalArgumentException("Le virement vers le même compte est interdit");
		}
		Compte source = trouverCompte(numeroSource);
		Compte destination = trouverCompte(numeroDestination);
		source.debiter(montant);
		destination.crediter(montant);
		repository.sauvegarder(source);
		repository.sauvegarder(destination);
		return new Virement(numeroSource, numeroDestination, montant, LocalDate.now(horloge));
	}

	private Compte trouverCompte(String numero) {
		return repository.trouver(numero).orElseThrow(() -> new CompteIntrouvableException(numero));
	}

}
