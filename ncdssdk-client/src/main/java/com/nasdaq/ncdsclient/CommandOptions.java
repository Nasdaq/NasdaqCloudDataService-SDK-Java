package com.nasdaq.ncdsclient;

import java.util.ArrayList;
@SuppressWarnings("unchecked")
public class CommandOptions {
    protected ArrayList arguments;

    public CommandOptions(String[] args) {
        parse(args);
    }

    public void parse(String[] args) {
        arguments = new ArrayList();
        for (int i = 0; i < args.length; i++) {
            arguments.add(args[i]);
        }
    }

    public int size() {
        return arguments.size();
    }

    public boolean hasOption(String option) {
        boolean hasValue = false;
        String str;
        for (int i = 0; i < arguments.size(); i++) {
            str = (String) arguments.get(i);
            if (true == str.equalsIgnoreCase(option)) {
                hasValue = true;
                break;
            }
        }
        return hasValue;
    }

    public String valueOf(String option)
    {
        String value = null;
        String str;
        for ( int i = 0; i < arguments.size(); i++ ) {
            str = (String)arguments.get(i);
            if ( true == str.equalsIgnoreCase(option) ) {
                value = (String)arguments.get(i+1);
                break;
            }
        }
        return value;
    }
}