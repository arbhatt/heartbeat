package com.adp.enets.heartbeat.commandline;

import org.apache.commons.cli.*;

public class Parser {

    private Options options = new Options();

    public void build() {
        options.addOption("h", "help", false, "Display Usage and Help");
        options.addOption("l", "list", false, "List all the processes monitored for this identity");
        options.addOption("h", "start", false, "Start Monitoring a process");
        options.addOption("h", "stop", false, "Stop Monitoring a process");
        options.addOption("h", "identity", true, "Provide the identity for this process");
    }

    public void parse(String[] command) throws ParseException {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = null;
        cmd = parser.parse(options, command);
        if (cmd.hasOption("h")) {
            help();
        }
    }

    private void help() {
        System.out.println("Displaying Help");
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Heartbeat SDK Initializer", options);
    }
}
