package com.neptunedreams.heroes;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = {
    "com.neptunedreams.heroes", 
    "com.neptunedreams.heroes.api", 
    "com.neptunedreams.heroes.config",
    "com.neptunedreams.heroes.data"
})
public class HeroServer implements CommandLineRunner {

    private static final String EXITCODE = "exitcode";

    @Override
    public void run(String... arg0) {
        if ((arg0.length > 0) && EXITCODE.equals(arg0[0])) {
            throw new ExitException();
        }
    }

    public static void main(String[] args) {
        final SpringApplication springApplication = new SpringApplication(HeroServer.class);
        //noinspection resource
        springApplication.run(args);
    }

    class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return 10;
        }

    }
}
