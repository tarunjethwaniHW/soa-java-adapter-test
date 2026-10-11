package com.soa.spring;

import java.util.Map;

/**
 * Risk Scorer Implementation - Spring Bean
 * Bean ID: riskScorerBean
 * Exposed as SCA service: RiskScorerService
 *
 * This class is instantiated by Spring and exposed to SOA Suite via:
 *   <implementation.spring src="spring/risk-scorer-context.xml"/>
 */
public class RiskScorerImpl implements IRiskScorer {

    private int baseRiskScore = 500;
    private int maxRiskScore = 850;

    @Override
    public int calculateRiskScore(Map<String, Object> applicationData) {
        int score = baseRiskScore;

        // Credit score factor (major impact)
        Integer creditScore = (Integer) applicationData.get("creditScore");
        if (creditScore != null) {
            if (creditScore >= 760) score += 100;
            else if (creditScore >= 720) score += 75;
            else if (creditScore >= 680) score += 50;
            else if (creditScore >= 640) score += 25;
            else score -= 50;
        }

        // DTI factor (debt-to-income ratio)
        Double dti = (Double) applicationData.get("dti");
        if (dti != null) {
            if (dti < 28.0) score += 75;
            else if (dti < 36.0) score += 50;
            else if (dti < 43.0) score += 25;
            else score -= 25;
        }

        // LTV factor (loan-to-value ratio)
        Double ltv = (Double) applicationData.get("ltv");
        if (ltv != null) {
            if (ltv < 80.0) score += 50;
            else if (ltv < 90.0) score += 25;
            else if (ltv > 95.0) score -= 25;
        }

        // Loan amount factor
        Double loanAmount = (Double) applicationData.get("loanAmount");
        if (loanAmount != null && loanAmount > 500000) {
            score -= 15; // Higher risk for jumbo loans
        }

        return Math.max(300, Math.min(score, maxRiskScore));
    }

    @Override
    public String getRiskCategory(int score) {
        if (score >= 750) return "EXCELLENT";
        if (score >= 700) return "GOOD";
        if (score >= 650) return "FAIR";
        return "POOR";
    }

    @Override
    public boolean meetsThreshold(int score, int threshold) {
        return score >= threshold;
    }

    // Setters for Spring property injection
    public void setBaseRiskScore(int baseRiskScore) {
        this.baseRiskScore = baseRiskScore;
    }

    public void setMaxRiskScore(int maxRiskScore) {
        this.maxRiskScore = maxRiskScore;
    }
}
