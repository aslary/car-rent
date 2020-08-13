package at.htlstp.aslan.carrent.bean;

import at.htlstp.aslan.carrent.model.Car;
import at.htlstp.aslan.carrent.model.Customer;
import at.htlstp.aslan.carrent.model.Rental;
import at.htlstp.aslan.carrent.model.Station;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * A class used as a form backing bean when finishing an rental.
 * This class has some defined validation constraints, because when finishing a rental, these must be validated.
 * When issuing a rental (creating it) these fields have to be set to null, thus, the entity class does not have this validation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FinishRentalBean {
    private Integer id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate rentalDate;
    private Customer driver;
    private Car car;
    private Station rentalStation;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "{notNull}")
    private LocalDate returnDate;

    @NotNull(message = "{notNull}")
    private Station returnStation;

    @NotNull(message = "{notNull}")
    @Range(min = 0, max = 500_000, message = "{kmError}")
    private Integer km;

    /**
     * Creates a {@link FinishRentalBean} object from a {@link Rental} object
     *
     * @param rental The rental to be turned into a FinishRentalBean
     * @return converted rental as FinishRentalBean
     */
    public static FinishRentalBean fromRental(Rental rental) {
        return new FinishRentalBean(
                rental.getId(),
                rental.getRentalDate(),
                rental.getDriver(),
                rental.getCar(),
                rental.getRentalStation(),
                LocalDate.now(),
                null,
                0
        );
    }
}
