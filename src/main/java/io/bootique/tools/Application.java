package io.bootique.tools;

import io.bootique.Bootique;

public class Application {

    public static void main(String[] args) {
        Bootique
                .app(args)
//                .args("-n", "--gradle-hello-tpl")
                .args("--create-module", "--config=classpath:templates/module-tpl.yml")
                .autoLoadModules()
                .exec()
                .exit();

    }
}