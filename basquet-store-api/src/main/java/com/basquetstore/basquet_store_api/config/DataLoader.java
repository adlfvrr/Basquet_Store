package com.basquetstore.basquet_store_api.config;

import com.basquetstore.basquet_store_api.entity.Role;
import com.basquetstore.basquet_store_api.entity.Shoe;
import com.basquetstore.basquet_store_api.entity.ShoeVariant;
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

    //Cargamos datos (en caso que no existan) dentro de la bdd. Creamos un usuario normal, un admin e insertamos las zapatillas de forma predeterminada

    private final ShoeRepository shoeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (shoeRepository.count() == 0) {
            loadShoesGeneral();
            loadShoesKids();
        }
        if (userRepository.count() == 0) {
            loadUsers();
        }
        System.out.println("Verificando datos...");
        System.out.println("Cantidad de shoes: " + shoeRepository.count());
    }

    private void loadShoesGeneral() {
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
            List<ShoeVariant> variants = new ArrayList<>();
            for (int size = 39; size <= 42; size++) {
                int stock = ThreadLocalRandom.current().nextInt(0, 11); // 0 a 10
                variants.add(new ShoeVariant(size, stock));
            }

            Shoe shoe = new Shoe(brand, model, "Zapatilla " + model + " - " + brand, "GENERAL", price, "https://placehold.co/400x400?text=" + model.replace(" ", "+"), variants);
            shoeRepository.save(shoe);
        }
    }

    private void loadShoesKids(){
        String[][] kidsModels = {
                // Nike
                {"Nike", "Giannis Immortality 4"},
                {"Nike", "Ja 3"},
                {"Nike", "Sabrina 3"},
                {"Nike", "LeBron Witness 9"},
                // Adidas
                {"Adidas", "Dame 10"},
                {"Adidas", "Dame X"},
                {"Adidas", "Own The Game 3.0"},
                {"Adidas", "Initiation PS"},
                // Puma
                {"Puma", "MB.05 Voltage"},
                {"Puma", "Scoot Zeros II GS"},
                {"Puma", "MB.04 Lo Team"},
                {"Puma", "Rebound V6 Lo"},
                // Under Armour
                {"Under Armour", "UA Jet '25"},
                {"Under Armour", "Lockdown 7"},
                {"Under Armour", "Curry 3Z 25"},
                {"Under Armour", "Curry 12"}
        };

        // Precios entre 80 y 200 USD (BigDecimal)
        BigDecimal[] kidsPrices = {
                new BigDecimal("69.99"), new BigDecimal("72.99"), new BigDecimal("74.99"), new BigDecimal("69.99"),
                new BigDecimal("70.00"), new BigDecimal("65.99"), new BigDecimal("54.99"), new BigDecimal("59.99"),
                new BigDecimal("69.99"), new BigDecimal("64.99"), new BigDecimal("74.99"), new BigDecimal("49.99"),
                new BigDecimal("54.99"), new BigDecimal("59.99"), new BigDecimal("69.99"), new BigDecimal("79.99")
        };

        for (int i = 0; i < kidsModels.length; i++) {
            String brand = kidsModels[i][0];
            String model = kidsModels[i][1];
            BigDecimal price = kidsPrices[i];

            // Variantes de talles 39 al 42 con stock aleatorio
            List<ShoeVariant> variants = new ArrayList<>();
            for (int size = 36; size <= 39; size++) {
                int stock = ThreadLocalRandom.current().nextInt(0, 11); // 0 a 10
                variants.add(new ShoeVariant(size, stock));
            }

            Shoe shoe = new Shoe(brand, model, "Zapatilla " + model + " - " + brand, "KIDS", price, "https://placehold.co/400x400?text=" + model.replace(" ", "+"), variants);
            shoeRepository.save(shoe);
        }
    }

    private void loadClothing(){
        String[][] clothingBrandModelsSection = {

                {"Nike", "Black Short Swoosh Man", "M"},
                {"Nike", "Ja 3"},
                {"Nike", "Sabrina 3"},
                {"Nike", "LeBron Witness 9"},

                {"Adidas", "Dame 10"},
                {"Adidas", "Dame X"},
                {"Adidas", "Own The Game 3.0"},
                {"Adidas", "Initiation PS"},

                {"Puma", "MB.05 Voltage"},
                {"Puma", "Scoot Zeros II GS"},
                {"Puma", "MB.04 Lo Team"},
                {"Puma", "Rebound V6 Lo"},
                
                {"Under Armour", "UA Jet '25"},
                {"Under Armour", "Lockdown 7"},
                {"Under Armour", "Curry 3Z 25"},
                {"Under Armour", "Curry 12"}
        };

        // Precios entre 80 y 200 USD (BigDecimal)
        BigDecimal[] kidsPrices = {
                new BigDecimal("69.99"), new BigDecimal("72.99"), new BigDecimal("74.99"), new BigDecimal("69.99"),
                new BigDecimal("70.00"), new BigDecimal("65.99"), new BigDecimal("54.99"), new BigDecimal("59.99"),
                new BigDecimal("69.99"), new BigDecimal("64.99"), new BigDecimal("74.99"), new BigDecimal("49.99"),
                new BigDecimal("54.99"), new BigDecimal("59.99"), new BigDecimal("69.99"), new BigDecimal("79.99")
        };

        for (int i = 0; i < kidsModels.length; i++) {
            String brand = kidsModels[i][0];
            String model = kidsModels[i][1];
            BigDecimal price = kidsPrices[i];

            // Variantes de talles 39 al 42 con stock aleatorio
            List<ShoeVariant> variants = new ArrayList<>();
            for (int size = 36; size <= 39; size++) {
                int stock = ThreadLocalRandom.current().nextInt(0, 11); // 0 a 10
                variants.add(new ShoeVariant(size, stock));
            }

            Shoe shoe = new Shoe(brand, model, "Zapatilla " + model + " - " + brand, "KIDS", price, "https://placehold.co/400x400?text=" + model.replace(" ", "+"), variants);
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
