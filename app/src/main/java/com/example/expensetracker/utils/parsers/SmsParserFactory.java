package com.example.expensetracker.utils.parsers;


public class SmsParserFactory {
    public static SmsParser getParser(String sender) {
        if (sender == null) return null;
        else if (sender.equals("HDFC")) return new HDFCSmsParser();
        else if (sender.equals("ICICI")) return new ICICISmsParser();
        else if(sender.equals("IDFC")) return  new IDFCSmsParser();
        else if(sender.equals("SBI")) return  new SBISmsParser();
        // Add more banks here
        return null;
    }
}
