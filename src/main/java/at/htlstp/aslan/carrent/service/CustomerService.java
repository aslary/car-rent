package at.htlstp.aslan.carrent.service;

import at.htlstp.aslan.carrent.model.Customer;
import at.htlstp.aslan.carrent.repository.CustomerRepository;
import at.htlstp.aslan.carrent.util.MessagesBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private MessagesBean messages;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * @return a list of all customers
     */
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    /**
     * Saves the given customer.
     *
     * @param customer customer to be saved
     * @return the saved customer coming from the database (only if no exceptions occur)
     * @throws EntityExistsException if the given primary key already belongs to an existing entity
     */
    public Customer create(Customer customer) {
        if (customerRepository.existsById(customer.getCustomerNumber())) {
            throw new EntityExistsException(messages.get("customerAlreadyExists"));
        }
        return customerRepository.save(customer);
    }

    public boolean existsById(Integer id) {
        return customerRepository.existsById(id);
    }
}
