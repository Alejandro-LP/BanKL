package co.edu.konradlorenz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BanklApplication {

    public static void main(String[] args) {
        SpringApplication.run(BanklApplication.class, args);
    }

    @Bean
    public CommandLineRunner abrirNavegador() {
        return args -> {
            try {

               
                Thread.sleep(3000);

                
                new ProcessBuilder("cmd", "/c", "start http://localhost:8080/html/index.html")
                        .start();

            } catch (Exception e) {
                System.out.println("No se pudo abrir el navegador automáticamente");
            }
        };
    }
}