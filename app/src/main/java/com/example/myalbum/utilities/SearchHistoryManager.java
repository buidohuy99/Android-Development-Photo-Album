package com.example.myalbum.utilities;

import java.util.Stack;

public class SearchHistoryManager {
    private static SearchHistoryManager trueObj = null;
    private Stack<String> tableSearch;

    public static SearchHistoryManager getInstance() {
        if(trueObj == null)
            trueObj = new SearchHistoryManager();
        return trueObj;
    }

    private SearchHistoryManager() {
        tableSearch = new Stack<>();
    }

    public void pushSearch(String search) {
        tableSearch.push(search);
    }

    public String popSearch(){
        return tableSearch.pop();
    }

    public String peekSearch() {
        return tableSearch.peek();
    }
}
