package di;

import com.google.inject.Guice;

public class Injector {
    private static Injector ourInstance = new Injector();

    public static Injector getInstance() {
        return ourInstance;
    }

    private com.google.inject.Injector injector = Guice.createInjector();

    private Injector() {
    }

    public com.google.inject.Injector getInjector() {
        return injector;
    }
}
