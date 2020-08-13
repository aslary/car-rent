package at.htlstp.aslan.carrent.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Rental implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @FutureOrPresent(message = "{futOrPres}")
    @NotNull(message = "{notNull}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate rentalDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "{futOrPres}")
    private LocalDate returnDate;

    private Integer km;

    @ManyToOne
    @NotNull(message = "{notNull}")
    private Customer driver;

    @ManyToOne
    @NotNull(message = "{notNull}")
    private Car car;

    @ManyToOne
    @NotNull(message = "{notNull}")
    private Station rentalStation;

    @ManyToOne
    private Station returnStation;

}
