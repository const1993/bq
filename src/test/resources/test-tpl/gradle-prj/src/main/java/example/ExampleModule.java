package example;

import com.google.inject.Module;
import io.bootique.Bootique;

public class ExampleModule implements Module {

    public static void main(String[] args) throws Exception {
        Bootique.app(args).module(ExampleModule.class).autoLoadModules().exec().exit();
    }

    @Override
    public void configure(Binder binder) {

        System.out.println("hello");

    }
}