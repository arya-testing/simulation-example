package com.arya.util;

import org.apache.commons.cli.*;

public class Args {

    public static boolean exists(String[] args, String name) {
        Options options = new Options();
        options.addOption(new Option(null, name, false, null));
        CommandLineParser parser = new ExtendedParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args, false);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return cmd.hasOption(name);
    }

    public static String getValue(String[] args, String name) {
        String value = getValueHelper(args, name);
        if(value == null) {
            throw new RuntimeException("Missing value for argument " + name);
        }
        return value;
    }

    public static String getValue(String[] args, String name, String defaultValue) {
        String value = getValueHelper(args, name);
        return value != null ? value : defaultValue;
    }

    public static int getIntValue(String[] args, String name) {
        String value = getValueHelper(args, name);
        if(value == null) {
            throw new RuntimeException("Missing value for argument " + name);
        }
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException e) {
            throw new RuntimeException("Argument " + name + " must be an integer");
        }
    }

    public static int getIntValue(String[] args, String name, int defaultValue) {
        String value = getValueHelper(args, name);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch(NumberFormatException e) {
            throw new RuntimeException("Argument " + name + " must be an integer");
        }
    }

    private static String getValueHelper(String[] args, String name) {
        Options options = new Options();
        options.addOption(new Option(null, name, true, null));
        CommandLineParser parser = new ExtendedParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args, false);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return cmd.getOptionValue(name);
    }

}
