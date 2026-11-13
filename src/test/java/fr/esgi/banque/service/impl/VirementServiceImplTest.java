package fr.esgi.banque.service.impl;

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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

	// ---------------------------------------------------------------- V1

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

}
