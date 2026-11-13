package fr.esgi.banque.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests de la classe CalculFrais, règle par règle (F1 à F5).
 */
class CalculFraisTest {

	private final CalculFrais calculFrais = new CalculFrais();


	@ParameterizedTest
	@ValueSource(ints = { -1, 0, 10, 17 })
	void shouldRejectMinorHolder_F1(int age) {
		// Arrange
		BigDecimal soldeMoyen = new BigDecimal("500");

		// Act / Assert
		assertThatThrownBy(() -> calculFrais.fraisMensuels(age, soldeMoyen, false))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldAcceptHolderOfExactlyMajorityAge_F1() {
		// Arrange
		BigDecimal soldeMoyen = new BigDecimal("500");

		// Act
		BigDecimal frais = calculFrais.fraisMensuels(18, soldeMoyen, false);

		// Assert
		assertThat(frais).isNotNull();
	}

	@Test
	void shouldRejectNullAverageBalance_F1() {
		// Arrange / Act / Assert
		assertThatThrownBy(() -> calculFrais.fraisMensuels(40, null, false))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldChargeMonthlyFeeOfTwoEuros_F2() {
		// Arrange
		int age = 40;
		BigDecimal soldeMoyen = new BigDecimal("500");

		// Act
		BigDecimal frais = calculFrais.fraisMensuels(age, soldeMoyen, false);

		// Assert
		assertThat(frais).isEqualByComparingTo("2.00");
	}


	/** Frais offerts sous 26 ans à 26 ans exactement ils sont dus. */
	@ParameterizedTest
	@CsvSource({
			"18, 0.00",
			"25, 0.00",
			"26, 2.00",
			"27, 2.00",
			"60, 2.00"
	})
	void shouldWaiveFeeOnlyForHoldersUnder26_F3(int age, String fraisAttendus) {
		// Arrange
		BigDecimal soldeMoyen = new BigDecimal("500");

		// Act
		BigDecimal frais = calculFrais.fraisMensuels(age, soldeMoyen, false);

		// Assert
		assertThat(frais).isEqualByComparingTo(fraisAttendus);
	}


	/** Frais offerts à partir de 1 000 € de solde moyen (1 000 inclus). */
	@ParameterizedTest
	@CsvSource({
			"0, 2.00",
			"999.99, 2.00",
			"1000, 0.00",
			"1000.00, 0.00",
			"1000.01, 0.00",
			"5000, 0.00"
	})
	void shouldWaiveFeeWhenAverageBalanceReaches1000_F4(String soldeMoyen, String fraisAttendus) {
		// Arrange
		int age = 40;

		// Act
		BigDecimal frais = calculFrais.fraisMensuels(age, new BigDecimal(soldeMoyen), false);

		// Assert
		assertThat(frais).isEqualByComparingTo(fraisAttendus);
	}


	/** Avec carte premium, le supplément de 5 € est toujours dû, même si la tenue est offerte. */
	@ParameterizedTest
	@CsvSource({
			"40, 500, 7.00",   // tenue 2 + premium 5
			"20, 500, 5.00",   // tenue offerte (moins de 26 ans), premium dû
			"40, 1000, 5.00",  // tenue offerte (solde >= 1000), premium dû
			"20, 5000, 5.00"   // tenue offerte deux fois, premium dû
	})
	void shouldAlwaysChargePremiumSupplement_F5(int age, String soldeMoyen, String fraisAttendus) {
		// Arrange
		BigDecimal solde = new BigDecimal(soldeMoyen);

		// Act
		BigDecimal frais = calculFrais.fraisMensuels(age, solde, true);

		// Assert
		assertThat(frais).isEqualByComparingTo(fraisAttendus);
	}

	@Test
	void shouldNotChargePremiumSupplementWithoutPremiumCard_F5() {
		// Arrange
		BigDecimal soldeMoyen = new BigDecimal("5000");

		// Act
		BigDecimal frais = calculFrais.fraisMensuels(20, soldeMoyen, false);

		// Assert
		assertThat(frais).isEqualByComparingTo("0");
	}

}
