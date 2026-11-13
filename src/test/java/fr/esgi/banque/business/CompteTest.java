package fr.esgi.banque.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test d'exemple fourni pour vérifier que le projet est bien configuré.
 * À compléter (partie A).
 */
class CompteTest {

	@Test
	void shouldIncreaseBalanceWhenCrediting() {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100.00"), new BigDecimal("200"));

		// Act
		compte.crediter(new BigDecimal("50.00"));

		// Assert
		assertThat(compte.getSolde()).isEqualByComparingTo("150.00");
	}


	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   " })
	void shouldRejectInvalidNumero_C1(String numero) {
		// Arrange / Act / Assert
		assertThatThrownBy(() -> new Compte(numero, "Alice", BigDecimal.TEN, BigDecimal.ZERO))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   " })
	void shouldRejectInvalidTitulaire_C1(String titulaire) {
		// Arrange / Act / Assert
		assertThatThrownBy(() -> new Compte("FR001", titulaire, BigDecimal.TEN, BigDecimal.ZERO))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldCreateCompteWhenNumeroAndTitulaireAreValid_C1() {
		// Arrange / Act
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("10"), new BigDecimal("5"));

		// Assert
		assertThat(compte.getNumero()).isEqualTo("FR001");
		assertThat(compte.getTitulaire()).isEqualTo("Alice");
	}

}
