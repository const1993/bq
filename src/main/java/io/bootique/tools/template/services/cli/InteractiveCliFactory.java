package io.bootique.tools.template.services.cli;

import com.google.inject.Provider;
import io.bootique.BootiqueException;
import io.bootique.cli.Cli;
import io.bootique.cli.NoArgsCli;
import io.bootique.command.CommandManager;
import io.bootique.command.ManagedCommand;
import io.bootique.jopt.JoptCliFactory;
import io.bootique.meta.application.ApplicationMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.tools.template.services.options.InteractiveOptionMetadata;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InteractiveCliFactory extends JoptCliFactory {

    private volatile OptionParser optionParser;
    private final Object optionParserLock;
    private Provider<CommandManager> commandManagerProvider;

    public InteractiveCliFactory(Provider<CommandManager> commandManagerProvider, ApplicationMetadata application) {
        super(commandManagerProvider, application);

        this.commandManagerProvider = commandManagerProvider;
        this.optionParserLock = new Object();
    }

    @Override
    public Cli createCli(String[] args) {
        if (args.length == 0) {
            return NoArgsCli.getInstance();
        }

        OptionSet parsed;
        if(Arrays.binarySearch(args, "--tpl") < 0 || Arrays.binarySearch(args, "-t") < 0){
            String[] extendedArgs = Arrays.copyOf(args, args.length + 1);
            extendedArgs[extendedArgs.length - 1] = "--tpl";

            parsed = parse(extendedArgs);
        }
        else {
            parsed = parse(args);
        }

        String commandName = commandName(parsed);
        ManagedCommand managedCommand = commandManagerProvider.get().lookupByName(commandName);
        List<String> interactiveList = managedCommand.getCommand().getMetadata().getOptions()
                .stream().filter(o -> o instanceof InteractiveOptionMetadata && ((InteractiveOptionMetadata) o).isInteractive())
                .map(OptionMetadata::getName)
                .collect(Collectors.toList());
        return new InteractiveCli(parsed, commandName, interactiveList);
    }

    private OptionSet parse(String[] args) {
        try {
            return getParser().parse(args);
        } catch (OptionException e) {
            throw new BootiqueException(1, e.getMessage(), e);
        }
    }

    private OptionParser getParser() {
        if (optionParser == null) {
            synchronized (optionParserLock) {
                if (optionParser == null) {
                    optionParser = createParser();
                }
            }
        }
        return optionParser;
    }
}
