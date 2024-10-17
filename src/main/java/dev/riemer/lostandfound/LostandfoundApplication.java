package dev.riemer.lostandfound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry class for our API application.
 */
@SpringBootApplication
public class LostandfoundApplication {

    /**
     * Main entry function for our API application.
     * @param args the arguments given to this application
     */
    public static void main(final String[] args) {
        SpringApplication.run(LostandfoundApplication.class, args);
    }
}
