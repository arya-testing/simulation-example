package dev.testment.core.fixtures.testmentmaintests;

import dev.testment.core.application.TestmentApplication;

public class NamespaceA {

    @TestmentApplication
    public static class Application {
        public static boolean calledMain;

        public static void main(String[] args) {
            calledMain = true;
        }
    }

}
