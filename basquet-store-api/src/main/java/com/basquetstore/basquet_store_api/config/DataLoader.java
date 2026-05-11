package com.basquetstore.basquet_store_api.config;

import com.basquetstore.basquet_store_api.entity.Role;
import com.basquetstore.basquet_store_api.entity.Shoe;
import com.basquetstore.basquet_store_api.entity.SizeVariant;
import com.basquetstore.basquet_store_api.entity.User;
import com.basquetstore.basquet_store_api.repository.ShoeRepository;
import com.basquetstore.basquet_store_api.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@AllArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ShoeRepository shoeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (shoeRepository.count() == 0) {
            loadShoes();
        }
        if (userRepository.count() == 0) {
            loadUsers();
        }
        System.out.println("Verificando datos...");
        System.out.println("Cantidad de shoes: " + shoeRepository.count());
    }

    private void loadShoes() {
        String[][] marcasModelos = {
                // Nike
                {"Nike", "Kyrie Flytrap 6"},
                {"Nike", "LeBron Witness V"},
                {"Nike", "GT Cut Academy"},
                {"Nike", "Precision 7"},
                // Adidas
                {"Adidas", "Harden Vol.9"},
                {"Adidas", "Dame Certified 9"},
                {"Adidas", "D.O.N. Issue 7"},
                {"Adidas", "Edwards AE 1"},
                // Peak
                {"Peak", "Big Triangle Pro 3"},
                {"Peak", "Taichi Cavalry 4"},
                {"Peak", "Wiggins Taichi Talent 3"},
                {"Peak", "Malik Monk Big Triangle 4"},
                // 361°
                {"361°", "Jokic Light Up"},
                {"361°", "Aaron Gordon AG4"},
                {"361°", "Quick Attack II"},
                {"361°", "Goey II"}
        };

        // Precios entre 80 y 200 USD (BigDecimal)
        BigDecimal[] precios = {
                new BigDecimal("199.99"), new BigDecimal("119.99"), new BigDecimal("109.99"), new BigDecimal("119.99"),
                new BigDecimal("119.99"), new BigDecimal("104.99"), new BigDecimal("109.99"), new BigDecimal("119.99"),
                new BigDecimal("129.99"), new BigDecimal("124.99"), new BigDecimal("109.99"), new BigDecimal("99.99"),
                new BigDecimal("119.99"), new BigDecimal("109.99"), new BigDecimal("104.99"), new BigDecimal("89.99")
        };

        for (int i = 0; i < marcasModelos.length; i++) {
            String brand = marcasModelos[i][0];
            String model = marcasModelos[i][1];
            BigDecimal price = precios[i];

            // Variantes de talles 39 al 42 con stock aleatorio
            List<SizeVariant> variants = new ArrayList<>();
            for (int size = 39; size <= 42; size++) {
                int stock = ThreadLocalRandom.current().nextInt(0, 11); // 0 a 10
                variants.add(new SizeVariant(size, stock));
            }

            Shoe shoe = new Shoe(brand, model, "Zapatilla " + model + " - " + brand, price, "https://placehold.co/400x400?text=" + model.replace(" ", "+"), variants);
            shoeRepository.save(shoe);
        }
    }

    private void loadUsers() {
        // Admin
        User admin = new User(
                "admin",
                "admin@basquetstore.com",
                passwordEncoder.encode("admin123"),
                "Admin",
                "Av. Admin 67",
                "+54 11 5555-0001",
                Role.ADMIN,
                true
        );
        userRepository.save(admin);

        // Usuario de prueba
        User user = new User(
                "adlfvrr",
                "adlfvrr@email.com",
                passwordEncoder.encode("adlfvrr67"),
                "Adolfo Vera",
                "Santa Fe 202",
                "+54 11 5555-0002",
                Role.USUARIO,
                true
        );
        userRepository.save(user);
    }

}
