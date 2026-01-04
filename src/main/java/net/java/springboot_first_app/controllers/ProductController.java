package net.java.springboot_first_app.controllers;

import net.java.springboot_first_app.models.Product;
import net.java.springboot_first_app.models.Order;
import net.java.springboot_first_app.services.ProductRepository;
import net.java.springboot_first_app.services.OrderRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads";

    public ProductController(ProductRepository productRepo, OrderRepository orderRepo) {
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
    }

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("productCount", productRepo.count());
        model.addAttribute("orderCount", orderRepo.count());
        return "products/dashboard";
    }

    // ================= ADMIN PRODUCT LIST =================
    @GetMapping
    public String adminProducts(Model model) {
        model.addAttribute("products", productRepo.findAll());
        return "products/list";
    }

    // ================= USER PRODUCT LIST =================
    @GetMapping("/shop")
    public String userProducts(Model model) {
        model.addAttribute("products", productRepo.findAll());
        return "products/user-list";
    }

    // ================= ADD PRODUCT PAGE =================
    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("product", new Product());
        return "products/add";
    }

    // ================= SAVE PRODUCT =================
    @PostMapping
    public String saveProduct(
            @ModelAttribute Product product,
            @RequestParam("image") MultipartFile image
    ) throws IOException {

        if (!image.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(
                    image.getInputStream(),
                    uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
            );

            product.setImageFileName(fileName);
        }

        productRepo.save(product);
        return "redirect:/products";
    }

    // ================= EDIT PRODUCT =================
    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        model.addAttribute("product", productRepo.findById(id).orElseThrow());
        return "products/edit";
    }

    // ================= UPDATE PRODUCT =================
    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute Product product,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {

        Product existing = productRepo.findById(id).orElseThrow();

        existing.setName(product.getName());
        existing.setBrand(product.getBrand());
        existing.setCategory(product.getCategory());
        existing.setPrice(product.getPrice());

        if (image != null && !image.isEmpty()) {

            if (existing.getImageFileName() != null) {
                Files.deleteIfExists(
                        Paths.get(UPLOAD_DIR).resolve(existing.getImageFileName())
                );
            }

            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Files.copy(
                    image.getInputStream(),
                    Paths.get(UPLOAD_DIR).resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
            );

            existing.setImageFileName(fileName);
        }

        productRepo.save(existing);
        return "redirect:/products";
    }

    // ================= DELETE PRODUCT =================
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) throws IOException {
        Product product = productRepo.findById(id).orElseThrow();

        if (product.getImageFileName() != null) {
            Files.deleteIfExists(
                    Paths.get(UPLOAD_DIR).resolve(product.getImageFileName())
            );
        }

        productRepo.deleteById(id);
        return "redirect:/products";
    }

    // ================= BUY PAGE =================
    @GetMapping("/buy/{id}")
    public String buyPage(@PathVariable Long id, Model model) {
        model.addAttribute("product", productRepo.findById(id).orElseThrow());
        return "products/buy";
    }

    // ================= CONFIRM BUY =================
    @PostMapping("/buy/{id}")
    public String confirmBuy(
            @PathVariable Long id,
            @RequestParam int quantity,
            @RequestParam String customerName,
            Model model
    ) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Order order = new Order();
        order.setProductName(product.getName());
        order.setUnitPrice(product.getPrice());
        order.setQuantity(quantity);
        order.setTotalPrice(product.getPrice() * quantity);
        order.setCustomerName(customerName);
        order.setStatus("PENDING");

        orderRepo.save(order);

        model.addAttribute("product", product);
        model.addAttribute("quantity", quantity);
        model.addAttribute("total", order.getTotalPrice());
        model.addAttribute("customer", customerName);

        return "order-success";
    }
    
    // ================= VIEW ALL ORDERS (ADMIN) =================
    @GetMapping("/orders")
    public String viewOrders(Model model) {
        model.addAttribute("orders", orderRepo.findAll());
        return "products/orders";
    }

    // ================= UPDATE ORDER STATUS =================
    @PostMapping("/orders/update/{id}")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        orderRepo.save(order);

        return "redirect:/products/orders";
    }

    // ================= USER ORDER TRACKING =================
    @GetMapping("/my-orders")
    public String userOrders(@RequestParam String name, Model model) {
        model.addAttribute("orders", orderRepo.findByCustomerName(name));
        model.addAttribute("customer", name);
        return "products/my-orders";
    }
}
