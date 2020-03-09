package dev.testment.core.driver;

import dev.testment.core.Testment;
import dev.testment.core.application.TestmentApplication;

@TestmentApplication
public class Application {

    public static void main(String[] args) {
        Testment.runApplication(Application.class, args);
    }

}