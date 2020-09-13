package com.wojto.importing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CsvFileImporter implements Importer {

    private static final String STOCK_TRANSACTION_REGEXP = "[0-9: \\.]+;[\\w\\s-]+;[\\w-]+;[K|S]{1};[\\d ]+;[\\d ,]+;[\\w]+;[\\d ,]+;[\\w]+";

    //TODO Logically this class should be using the Transaction parser, not the other way around

    @Override
    public List<String> importTransactionsFromFile(File file) {
        List<String> transactionStrings = new ArrayList<>();

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            Scanner scanner = new Scanner(fileInputStream);
            String currentLine = "";

            while (scanner.hasNextLine()){
                currentLine = scanner.nextLine();
                if (currentLine.matches(STOCK_TRANSACTION_REGEXP)) transactionStrings.add(currentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactionStrings;
    }
}
