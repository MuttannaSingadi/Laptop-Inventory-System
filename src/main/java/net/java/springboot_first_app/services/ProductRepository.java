package net.java.springboot_first_app.services;

import net.java.springboot_first_app.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
