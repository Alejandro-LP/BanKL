package co.edu.konradlorenz.config;

import co.edu.konradlorenz.model.Cliente;
import co.edu.konradlorenz.model.ClienteAdmin;
import co.edu.konradlorenz.repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "bankl.admin.seed.enabled", havingValue = "true")
public class AdminSeedConfiguration {
    private static final Logger log = LoggerFactory.getLogger(AdminSeedConfiguration.class);

    @Bean
    CommandLineRunner seedDemoAdmin(ClienteRepository clientes) {
        return args -> {
            Cliente existente = clientes.findByUsuarioIS("admin");
            if (existente instanceof ClienteAdmin) {
                log.info("README demo administrator already exists; no account changes made.");
                return;
            }
            if (existente != null) {
                throw new IllegalStateException("The username 'admin' already belongs to a non-admin account.");
            }

            ClienteAdmin admin = new ClienteAdmin(
                "Admin", "BanKL", "00000000", "Sede principal", "0000000000",
                "admin@bankl.com", "admin", "Admin123"
            );
            admin.setPinSeguridad(1234);
            clientes.save(admin);
            log.info("README demo administrator account created.");
        };
    }
}