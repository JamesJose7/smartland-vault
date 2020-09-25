package com.jeeps.smartlandvault.merging;

public class JoinPair {
    private String leftContainerId;
    private String leftContainerProperty;
    private String rightContainerId;
    private String rightContainerProperty;

    public JoinPair() { }

    public JoinPair(String leftContainerId, String leftContainerProperty, String rightContainerId, String rightContainerProperty) {
        this.leftContainerId = leftContainerId;
        this.leftContainerProperty = leftContainerProperty;
        this.rightContainerId = rightContainerId;
        this.rightContainerProperty = rightContainerProperty;
    }

    public String getLeftContainerId() {
        return leftContainerId;
    }

    public void setLeftContainerId(String leftContainerId) {
        this.leftContainerId = leftContainerId;
    }

    public String getLeftContainerProperty() {
        return leftContainerProperty;
    }

    public void setLeftContainerProperty(String leftContainerProperty) {
        this.leftContainerProperty = leftContainerProperty;
    }

    public String getRightContainerId() {
        return rightContainerId;
    }

    public void setRightContainerId(String rightContainerId) {
        this.rightContainerId = rightContainerId;
    }

    public String getRightContainerProperty() {
        return rightContainerProperty;
    }

    public void setRightContainerProperty(String rightContainerProperty) {
        this.rightContainerProperty = rightContainerProperty;
    }
}
