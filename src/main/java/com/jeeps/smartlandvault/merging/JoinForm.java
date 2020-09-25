package com.jeeps.smartlandvault.merging;

import java.util.List;

public class JoinForm {
    private String name;
    private List<JoinPair> joinPairs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JoinPair> getJoinPairs() {
        return joinPairs;
    }

    public void setJoinPairs(List<JoinPair> joinPairs) {
        this.joinPairs = joinPairs;
    }
}
