package fr.esgi.banque.service.impl;

import fr.esgi.banque.business.Compte;
import fr.esgi.banque.business.CompteIntrouvableException;
import fr.esgi.banque.service.CompteRepository;
import fr.esgi.banque.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Tests de VirementServiceImpl, développé en TDD règle par règle (V1 à V7).
 */
@ExtendWith(MockitoExtension.class)
class VirementServiceImplTest {

	@Mock
	private CompteRepository repository;

	@Mock
	private NotificationService notifications;

	/** Mercredi 15 janvier 2025 : un jour ouvré. */
	private final Clock horloge = Clock.fixed(Instant.parse("2025-01-15T10:00:00Z"), ZoneOffset.UTC);

	private VirementServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new VirementServiceImpl(repository, notifications, horloge);
	}


	@Test
	void shouldRejectNullAmount_V1() {
		// Arrange / Act / Assert
		assertThatThrownBy(() -> service.virer("FR001", "FR002", null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@ValueSource(strings = { "0", "0.00", "-0.01", "-100" })
	void shouldRejectZeroOrNegativeAmount_V1(String montant) {
		// Arrange / Act / Assert
		assertThatThrownBy(() -> service.virer("FR001", "FR002", new BigDecimal(montant)))
				.isInstanceOf(IllegalArgumentException.class);
	}


	@Test
	void shouldRejectTransferToSameAccount_V2() {
		// Arrange
		BigDecimal montant = new BigDecimal("100");

		// Act / Assert
		assertThatThrownBy(() -> service.virer("FR001", "FR001", montant))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldRejectTransferWhenSourceAccountDoesNotExist_V3() {
		// Arrange
		when(repository.trouver("FR001")).thenReturn(Optional.empty());

		// Act / Assert
		assertThatThrownBy(() -> service.virer("FR001", "FR002", new BigDecimal("100")))
				.isInstanceOf(CompteIntrouvableException.class);
	}

	@Test
	void shouldRejectTransferWhenDestinationAccountDoesNotExist_V3() {
		// Arrange
		Compte source = new Compte("FR001", "Alice", new BigDecimal("500"), BigDecimal.ZERO);
		when(repository.trouver("FR001")).thenReturn(Optional.of(source));
		when(repository.trouver("FR002")).thenReturn(Optional.empty());

		// Act / Assert
		assertThatThrownBy(() -> service.virer("FR001", "FR002", new BigDecimal("100")))
				.isInstanceOf(CompteIntrouvableException.class);
	}

}
