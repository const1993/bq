package io.bootique.tools.template.services.cli;

import io.bootique.jopt.JoptCli;
import joptsimple.OptionSet;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class InteractiveCli extends JoptCli {

    private Map<String, Boolean> interactiveMap;

    public InteractiveCli(OptionSet parsed, String commandName,  Map<String, Boolean> interactiveMap) {
        super(parsed, commandName);
        this.interactiveMap = interactiveMap;
    }


    @Override
    public List<String> optionStrings(String name) {
        List<String> strings = super.optionStrings(name);
        if (strings.isEmpty() && interactiveMap.get(name)) {
            System.out.println("Please mention not recognized option " + name + ":");
            Scanner scanner = new Scanner(System.in);
            strings.add(String.valueOf(scanner.nextLine()));
        }
        return strings;
    }
}
