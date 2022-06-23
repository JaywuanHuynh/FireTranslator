package com.project.firetranslator.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName="Translation", indices = {@Index(value = {"inputText"}, unique = true)})
public class Translation {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name="inputText")
    private String inputText;
    @ColumnInfo(name="outputText")
    private String outputText;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    public Translation(String inputText, String outputText) {
        this.inputText = inputText;
        this.outputText = outputText;
    }

    @Override
    public String toString() {
        return "Translation{" +
                "inputText='" + inputText + '\'' +
                ", outputText='" + outputText + '\'' +
                '}';
    }
}
