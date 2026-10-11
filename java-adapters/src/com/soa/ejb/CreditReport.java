package com.soa.ejb;

import java.io.Serializable;
import java.util.Date;

/**
 * Credit Report Data Transfer Object
 * Returned by ICreditCheckRemote.checkCredit()
 */
public class CreditReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ssn;
    private int score;
    private String status;
    private Date reportDate;
    private String bureauName;
    private int accountCount;
    private double totalDebt;
    private int latePayments;

    public CreditReport() {
    }

    public CreditReport(String ssn, int score, String status) {
        this.ssn = ssn;
        this.score = score;
        this.status = status;
        this.reportDate = new Date();
        this.bureauName = "TestBureau";
    }

    // Getters and Setters
    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { this.ssn = ssn; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getReportDate() { return reportDate; }
    public void setReportDate(Date reportDate) { this.reportDate = reportDate; }

    public String getBureauName() { return bureauName; }
    public void setBureauName(String bureauName) { this.bureauName = bureauName; }

    public int getAccountCount() { return accountCount; }
    public void setAccountCount(int accountCount) { this.accountCount = accountCount; }

    public double getTotalDebt() { return totalDebt; }
    public void setTotalDebt(double totalDebt) { this.totalDebt = totalDebt; }

    public int getLatePayments() { return latePayments; }
    public void setLatePayments(int latePayments) { this.latePayments = latePayments; }

    @Override
    public String toString() {
        return "CreditReport{ssn='" + ssn + "', score=" + score +
               ", status='" + status + "', bureau='" + bureauName + "'}";
    }
}
