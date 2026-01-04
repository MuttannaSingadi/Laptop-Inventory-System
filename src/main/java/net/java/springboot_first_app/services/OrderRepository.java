package net.java.springboot_first_app.services;

import java.util.List;
import net.java.springboot_first_app.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByCustomerName(String customerName);
}
