package com.macedo.moneymanager.models;

/**
 * Created by Beatriz on 07/09/2015.
 */
public class MonthExpense {

    private String month;
    private Double amount;
    private Double percentage;
    private Double winAmount;
    private Double lossAmount;

    public MonthExpense(String month, Double amount, Double percentage, Double winAmount, Double lossAmount) {
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Double getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(Double winAmount) {
        this.winAmount = winAmount;
    }

    public Double getLossAmount() {
        return lossAmount;
    }

    public void setLossAmount(Double lossAmount) {
        this.lossAmount = lossAmount;
    }
}
