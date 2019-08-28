package com.nahuo.buyertool.Bean;

import java.io.Serializable;

/**
 * Created by jame on 2018/11/1.
 */

public class SearchBean implements Serializable {
    private static final long serialVersionUID = -6969726812450406996L;
    private String keyWord="";
    private String stall_word="";

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getStall_word() {
        return stall_word;
    }

    public void setStall_word(String stall_word) {
        this.stall_word = stall_word;
    }
}
