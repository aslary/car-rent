package at.htlstp.aslan.carrent.controller.view;

import at.htlstp.aslan.carrent.bean.FinishRentalBean;
import at.htlstp.aslan.carrent.model.Car;
import at.htlstp.aslan.carrent.model.Rental;
import at.htlstp.aslan.carrent.model.Station;
import at.htlstp.aslan.carrent.service.CarService;
import at.htlstp.aslan.carrent.service.CustomerService;
import at.htlstp.aslan.carrent.service.RentalService;
import at.htlstp.aslan.carrent.service.StationService;
import at.htlstp.aslan.carrent.util.MessagesBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A class used to serve server-side rendered views.<br>
 * As defined in the security configuration, only users with the role of EMPLOYEE can use these endpoints.<br>
 * This is achieved by setting the root route of this controller to {@code /employee/**}.
 */
@Controller
@RequestMapping("employee")
@SessionAttributes({"finishRentalBean"})
public class EmployeeController {

    @Autowired
    private MessagesBean messages;

    @Autowired
    private StationService stationService;

    @Autowired
    private CarService carService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RentalService rentalService;

    @GetMapping("/create-rental")
    public String showCreateRentalForm(Model model, @ModelAttribute("rental") Rental rental) {
        List<Station> stations = stationService.findAll();
        List<Car> cars;

        if (stations.isEmpty()) {
            cars = new ArrayList<>();
        } else {
            cars = carService.findByStation(rental.getRentalStation() == null ? stations.get(0) : rental.getRentalStation());
        }

        rental.setRentalDate(LocalDate.now());
        model.addAttribute("rental", rental);
        model.addAttribute("cars", cars);
        model.addAttribute("stations", stations);
        model.addAttribute("customers", customerService.findAll());

        return "fragments/create-rental";
    }

    @PostMapping("/create-rental/refresh")
    public String refreshCreateRentalForm(@ModelAttribute("rental") Rental rental, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("rental", rental);
        return "redirect:/employee/create-rental";
    }

    @PostMapping("/create-rental/process")
    public ModelAndView processCreateRentalForm(@Valid @ModelAttribute("rental") Rental rental, BindingResult bindingResult) {
        ModelAndView createRentalForm = new ModelAndView("fragments/create-rental");
        List<Station> stations = stationService.findAll();
        createRentalForm.addObject("cars", carService.findByStation(stations.get(0)));
        createRentalForm.addObject("stations", stations);
        createRentalForm.addObject("customers", customerService.findAll());

        if (bindingResult.hasErrors()) {
            return createRentalForm;
        }

        if (!rentalService.canCreate(rental)) {
            return createRentalForm.addObject("carMismatchError", messages.get("carMismatchError"));
        }

        rentalService.create(rental);
        return new ModelAndView("redirect:/employee/running-rentals")
                .addObject("success", messages.get("createRentalSuccess"));
    }

    @GetMapping("/running-rentals")
    public String showRunningRentalsForm(Model model, @ModelAttribute("error") String error, @ModelAttribute("success") String success) {
        model.addAttribute("rentals", rentalService.findRunningRentals());
        model.addAttribute("error", error);
        model.addAttribute("success", success);
        return "fragments/running-rentals";
    }

    @GetMapping("/finish-rental/{id}")
    public ModelAndView showFinishForm(@PathVariable("id") Integer id) {
        Optional<Rental> opt = rentalService.existsAndCanFinish(id);
        if (opt.isEmpty()) {
            return new ModelAndView("redirect:/employee/running-rentals")
                    .addObject("error", messages.get("rentalNotFound"));
        }

        return new ModelAndView("fragments/finish-rental")
                .addObject("stations", stationService.findAll())
                .addObject("finishRentalBean", FinishRentalBean.fromRental(opt.get()));
    }

    @PostMapping("/finish-rental/{id}")
    public ModelAndView processFinishForm(@PathVariable("id") Integer id, @Valid FinishRentalBean finishRentalBean, BindingResult bindingResult) {
        ModelAndView redirectRunningRentals = new ModelAndView("redirect:/employee/running-rentals");
        ModelAndView finishRentalForm = new ModelAndView("/fragments/finish-rental");

        Optional<Rental> opt = rentalService.existsAndCanFinish(id);
        if (opt.isEmpty()) {
            return redirectRunningRentals.addObject("error", messages.get("rentalNotFound"));
        }

        Rental rental = opt.get();
        finishRentalForm.addObject("stations", stationService.findAll());

        if (bindingResult.hasErrors()) {
            return finishRentalForm;
        }

        if (!rentalService.cleanDates(rental, finishRentalBean)) {
            return finishRentalForm.addObject("returnDateError", messages.get("returnDateError"));
        }

        rentalService.finish(rental, finishRentalBean);
        return redirectRunningRentals.addObject("success", messages.get("finishRentalSuccess"));
    }

}
