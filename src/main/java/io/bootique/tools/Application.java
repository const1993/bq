package io.bootique.tools;

import io.bootique.Bootique;

public class Application {

    public static void main(String[] args) {
        Bootique
                .app(args)
                .args("-n", "--hello-tpl")
                .autoLoadModules()
                .exec()
                .exit();

    }
}