package io.bootique.tools.template.services.cli;

import io.bootique.jopt.JoptCli;
import joptsimple.OptionSet;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class InteractiveCli extends JoptCli {

    private List<String> interactiveList;
    private Map<String, String> optionWithDefaultMap;

    public InteractiveCli(OptionSet parsed, String commandName,  List<String> interactiveList,
                          Map<String, String> optionWithDefaultMap) {
        super(parsed, commandName);
        this.interactiveList = interactiveList;
        this.optionWithDefaultMap = optionWithDefaultMap;
    }

    public InteractiveCli(OptionSet parsed, String commandName,  List<String> interactiveMap) {
        super(parsed, commandName);
        this.interactiveList = interactiveMap;
    }


    @Override
    public List<String> optionStrings(String name) {
        List<String> strings = super.optionStrings(name);
        //Should check for default value first.
        if (strings.isEmpty() && interactiveList.contains(name)) {
            System.out.println("Please mention not recognized option " + name + ":");
            Scanner scanner = new Scanner(System.in);
            strings.add(String.valueOf(scanner.nextLine()));
        }
        return strings;
    }
}
