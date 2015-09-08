package com.macedo.moneymanager.models;

/**
 * Created by Beatriz on 07/09/2015.
 */
public class MonthExpense {

    private String month;
    private Float amount;
    private Float percentage;
    private Float winAmount;
    private Float lossAmount;

    public MonthExpense(String month, Float amount, Float percentage, Float winAmount, Float lossAmount) {
        this.month = month;
        this.amount = amount;
        this.percentage = percentage;
        this.winAmount = winAmount;
        this.lossAmount = lossAmount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }

    public Float getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(Float winAmount) {
        this.winAmount = winAmount;
    }

    public Float getLossAmount() {
        return lossAmount;
    }

    public void setLossAmount(Float lossAmount) {
        this.lossAmount = lossAmount;
    }
}
