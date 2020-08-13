package at.htlstp.aslan.carrent.controller.rest;

import at.htlstp.aslan.carrent.model.Car;
import at.htlstp.aslan.carrent.model.Customer;
import at.htlstp.aslan.carrent.model.Rental;
import at.htlstp.aslan.carrent.service.CarService;
import at.htlstp.aslan.carrent.service.CustomerService;
import at.htlstp.aslan.carrent.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private CarService carService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RentalService rentalService;

    @GetMapping("running-rentals")
    public List<Rental> findRunningRentals() {
        return rentalService.findRunningRentals();
    }

    @GetMapping("mileage-greater-than/{mileage}")
    public List<Car> findByMileageGreaterThan(@PathVariable Integer mileage) {
        return carService.findByMileageGreaterThan(mileage);
    }

    @PostMapping("create-car")
    public ResponseEntity<Car> createCar(@RequestBody @Valid Car car) {
        return ResponseEntity.created(URI.create("")).body(carService.create(car));
    }

    @PostMapping("create-customer")
    public ResponseEntity<Customer> createCustomer(@RequestBody @Valid Customer customer) {
        return ResponseEntity.created(URI.create("")).body(customerService.create(customer));
    }

    @DeleteMapping("delete-car/{registrationNr}")
    public void deleteCar(@PathVariable String registrationNr) {
        carService.deleteById(registrationNr);
    }

}
