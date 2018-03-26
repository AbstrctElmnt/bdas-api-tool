package com.bdas;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public interface RestActions {
    void sendRequest();

    default Set<String> loadData(String filename) {
        Set<String> data = new HashSet<>();
        try {
            String idLine = null;
            FileInputStream fis = new FileInputStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            while ((idLine = reader.readLine()) != null) {
                data.add(idLine.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
