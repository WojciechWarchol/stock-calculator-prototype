package com.wojto.importing;

import java.io.File;
import java.util.List;

public interface Importer {

    public List<String> importTransactionsFromFile(File file);

}
