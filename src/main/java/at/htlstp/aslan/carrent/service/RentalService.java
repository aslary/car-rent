package at.htlstp.aslan.carrent.service;

import at.htlstp.aslan.carrent.bean.FinishRentalBean;
import at.htlstp.aslan.carrent.model.Car;
import at.htlstp.aslan.carrent.model.Rental;
import at.htlstp.aslan.carrent.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CarService carService;

    /**
     * Saves the given rental.
     * Sets following fields to null: id, km, return date, return station und the station of the car
     *
     * @param rental the rental to be saved
     */
    public void create(Rental rental) {
        rental.setId(null);
        rental.setKm(null);
        rental.setReturnDate(null);
        rental.setReturnStation(null);
        rental.getCar().setStation(null);

        rentalRepository.save(rental);
    }

    /**
     * Finishes the given rental with the help of a {@link FinishRentalBean}.
     * Updates the record afterwards.
     *
     * @param rental           the rental to be updated
     * @param finishRentalBean the bean
     */
    public void finish(Rental rental, FinishRentalBean finishRentalBean) {
        rental.setReturnStation(finishRentalBean.getReturnStation());
        rental.setKm(finishRentalBean.getKm());
        rental.getCar().setStation(rental.getReturnStation());
        rental.getCar().setMileage(rental.getCar().getMileage() + rental.getKm());

        rentalRepository.save(rental);
    }

    /**
     * Returns a list of all running rentals.<br>
     * See {@link RentalRepository#findRunningRentals()}
     */
    public List<Rental> findRunningRentals() {
        return rentalRepository.findRunningRentals();
    }

    /**
     * Returns a list of all rentals that have a specific car.<br>
     * See {@link RentalRepository#findByCar(Car)}
     */
    public List<Rental> findByCar(Car car) {
        return rentalRepository.findByCar(car);
    }

    /**
     * Checks whether a rental with a given id exists and can be finished.
     * If the id is null or the rental was not found, an empty {@link Optional} is returned.
     * If the rental was found and can be finished (see {@link #canFinish(Rental)}), then an {@link Optional} with the found rental is returned.
     */
    public Optional<Rental> existsAndCanFinish(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<Rental> opt = rentalRepository.findById(id);
        Rental rental;
        if (opt.isPresent() && canFinish((rental = opt.get()))) {
            return Optional.of(rental);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Checks whether a given rental can be finished or not.
     * Returns true if return date, km and return station fields are null, and false otherwise.
     */
    public boolean canFinish(Rental rental) {
        return rental.getReturnDate() == null && rental.getKm() == null && rental.getReturnStation() == null;
    }

    /**
     * Checks whether a rental can be created ot not. A rental can be created, if the provided car is indeed a car of the provided station.
     * This method is used for security reasons, since users can manipulate the frontend values via F12.
     */
    public boolean canCreate(Rental rental) {
        return carService.findByStation(rental.getRentalStation()).contains(rental.getCar());
    }

    /**
     * Returns false if the rental date of rental is after the return date of finishRentalBean.
     * If that is not the case, then the return date of rental will be set to the return date of finishRentalBean and true will be returned.
     */
    public boolean cleanDates(Rental rental, FinishRentalBean finishRentalBean) {
        if (rental.getRentalDate().isAfter(finishRentalBean.getReturnDate())) {
            return false;
        } else {
            rental.setReturnDate(finishRentalBean.getReturnDate());
            return true;
        }
    }
}
