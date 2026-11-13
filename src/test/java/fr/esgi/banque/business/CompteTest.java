package fr.esgi.banque.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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


	@Test
	void shouldRejectNullInitialBalance_C2() {
		// Arrange / Act / Assert
		assertThatThrownBy(() -> new Compte("FR001", "Alice", null, BigDecimal.ZERO))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldRejectNullOverdraft_C2() {
		// Arrange / Act / Assert
		assertThatThrownBy(() -> new Compte("FR001", "Alice", BigDecimal.TEN, null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@ValueSource(strings = { "-0.01", "-1", "-500" })
	void shouldRejectNegativeOverdraft_C2(String decouvert) {
		// Arrange / Act / Assert
		assertThatThrownBy(() -> new Compte("FR001", "Alice", BigDecimal.TEN, new BigDecimal(decouvert)))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@ValueSource(strings = { "0", "0.00", "0.01", "200" })
	void shouldAcceptZeroOrPositiveOverdraft_C2(String decouvert) {
		// Arrange / Act
		Compte compte = new Compte("FR001", "Alice", BigDecimal.TEN, new BigDecimal(decouvert));

		// Assert
		assertThat(compte.getDecouvertAutorise()).isEqualByComparingTo(decouvert);
	}


	@ParameterizedTest
	@ValueSource(strings = { "0", "0.00", "-0.01", "-50" })
	void shouldRejectNonPositiveAmountWhenCrediting_C3(String montant) {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100"), BigDecimal.ZERO);

		// Act / Assert
		assertThatThrownBy(() -> compte.crediter(new BigDecimal(montant)))
				.isInstanceOf(IllegalArgumentException.class);
		assertThat(compte.getSolde()).isEqualByComparingTo("100");
	}

	@Test
	void shouldRejectNullAmountWhenCrediting_C3() {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100"), BigDecimal.ZERO);

		// Act / Assert
		assertThatThrownBy(() -> compte.crediter(null)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldAcceptSmallestPositiveAmountWhenCrediting_C3() {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100"), BigDecimal.ZERO);

		// Act
		compte.crediter(new BigDecimal("0.01"));

		// Assert
		assertThat(compte.getSolde()).isEqualByComparingTo("100.01");
	}


	@ParameterizedTest
	@ValueSource(strings = { "0", "0.00", "-0.01", "-50" })
	void shouldRejectNonPositiveAmountWhenDebiting_C4(String montant) {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100"), new BigDecimal("200"));

		// Act / Assert
		assertThatThrownBy(() -> compte.debiter(new BigDecimal(montant)))
				.isInstanceOf(IllegalArgumentException.class);
		assertThat(compte.getSolde()).isEqualByComparingTo("100");
	}

	@Test
	void shouldRejectNullAmountWhenDebiting_C4() {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100"), new BigDecimal("200"));

		// Act / Assert
		assertThatThrownBy(() -> compte.debiter(null)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldDecreaseBalanceWhenDebiting_C4() {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100"), BigDecimal.ZERO);

		// Act
		compte.debiter(new BigDecimal("30.50"));

		// Assert
		assertThat(compte.getSolde()).isEqualByComparingTo("69.50");
	}


	/** Solde 100, découvert 200 : limite = -200, soit un débit maximal de 300. */
	@ParameterizedTest
	@CsvSource({
			"299.99, -199.99",
			"300.00, -200.00"
	})
	void shouldAcceptDebitUpToOverdraftLimit_C5(String montant, String soldeAttendu) {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100"), new BigDecimal("200"));

		// Act
		compte.debiter(new BigDecimal(montant));

		// Assert
		assertThat(compte.getSolde()).isEqualByComparingTo(soldeAttendu);
	}

	@ParameterizedTest
	@ValueSource(strings = { "300.01", "301", "1000" })
	void shouldRejectDebitBeyondOverdraftLimitAndKeepBalance_C5(String montant) {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100"), new BigDecimal("200"));

		// Act / Assert
		assertThatThrownBy(() -> compte.debiter(new BigDecimal(montant)))
				.isInstanceOf(SoldeInsuffisantException.class);
		assertThat(compte.getSolde()).isEqualByComparingTo("100");
	}

	@Test
	void shouldAllowEmptyingAccountWhenNoOverdraft_C5() {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100"), BigDecimal.ZERO);

		// Act
		compte.debiter(new BigDecimal("100"));

		// Assert
		assertThat(compte.getSolde()).isEqualByComparingTo("0");
	}


	@ParameterizedTest
	@ValueSource(strings = { "0", "0.0", "0.00", "0.000" })
	void shouldBeEmptyWhenBalanceIsZeroWhateverTheScale_C6(String solde) {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal(solde), BigDecimal.ZERO);

		// Act
		boolean vide = compte.estVide();

		// Assert
		assertThat(vide).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = { "0.01", "-0.01", "100" })
	void shouldNotBeEmptyWhenBalanceIsNotZero_C6(String solde) {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal(solde), new BigDecimal("200"));

		// Act
		boolean vide = compte.estVide();

		// Assert
		assertThat(vide).isFalse();
	}

}
