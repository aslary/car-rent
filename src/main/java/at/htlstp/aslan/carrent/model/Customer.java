package at.htlstp.aslan.carrent.model;

import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Customer implements Serializable {

    @Id
    @Range(min = 111_111, max = 999_999, message = "{customerNumberRange}")
    @EqualsAndHashCode.Include
    @NotNull(message = "{notNull}")
    private Integer customerNumber;

    @Size(min = 1, max = 255, message = "{nameRange}")
    @NotNull(message = "{notNull}")
    private String lastName;

    @Size(min = 1, max = 255, message = "{nameRange}")
    @NotNull(message = "{notNull}")
    private String firstName;

    public void setLastName(String lastName) {
        this.lastName = lastName.strip();
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName.strip();
    }

    @Override
    public String toString() {
        return "(" + customerNumber + ") " + lastName.toUpperCase() + ' ' + firstName;
    }
}
