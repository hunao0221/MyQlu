package com.hugo.myqlu.bean;

/**
 * @author Hugo
 * Created on 2016/4/28 22:13.
 */
public class ZhangBean {
    //交易时间
    private String time;
    //消费终端
    private String terminal;
    //现有余额
    private String balance;
    //交易额
    private String turnover;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTurnover() {
        return turnover;
    }

    public void setTurnover(String turnover) {
        this.turnover = turnover;
    }

    @Override
    public String toString() {
        return "ZhangBean{" +
                "balance='" + balance + '\'' +
                ", time='" + time + '\'' +
                ", terminal='" + terminal + '\'' +
                ", turnover='" + turnover + '\'' +
                '}';
    }
}
