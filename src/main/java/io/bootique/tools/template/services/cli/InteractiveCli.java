package io.bootique.tools.template.services.cli;

import io.bootique.jopt.JoptCli;
import joptsimple.OptionSet;

import java.util.List;
import java.util.Scanner;

public class InteractiveCli extends JoptCli {

    private List<String> interactiveMap;

    public InteractiveCli(OptionSet parsed, String commandName,  List<String> interactiveMap) {
        super(parsed, commandName);
        this.interactiveMap = interactiveMap;
    }


    @Override
    public List<String> optionStrings(String name) {
        List<String> strings = super.optionStrings(name);
        if (strings.isEmpty() && interactiveMap.contains(name)) {
            System.out.println("Please mention not recognized option " + name + ":");
            Scanner scanner = new Scanner(System.in);
            strings.add(String.valueOf(scanner.nextLine()));
        }
        return strings;
    }
}
