package com.soa.ejb;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Compliance Check Stateless Session Bean
 * Deployed to WebLogic, accessed via binding.ejb from SOA Composite
 *
 * JNDI Name: ejb/ComplianceCheck
 * EJB URI: ComplianceCheckEJB#com.soa.ejb.IComplianceCheckRemote
 *
 * This EJB is invoked by SOA Suite via:
 *   <reference name="ComplianceCheckEJB">
 *       <interface.java interface="com.soa.ejb.IComplianceCheckRemote"/>
 *       <binding.ejb uri="ComplianceCheckEJB#com.soa.ejb.IComplianceCheckRemote"
 *                    jndi-name="ejb/ComplianceCheck">
 *           <property name="java.naming.factory.initial">
 *               weblogic.jndi.WLInitialContextFactory
 *           </property>
 *           <property name="java.naming.provider.url">
 *               t3://compliance-server:7001
 *           </property>
 *       </binding.ejb>
 *   </reference>
 */
@Stateless(name = "ComplianceCheckEJB", mappedName = "ejb/ComplianceCheck")
public class ComplianceCheckEJB implements IComplianceCheckRemote {

    private static final double QM_DTI_THRESHOLD = 43.0;
    private static final double HIGH_COST_LTV_THRESHOLD = 80.0;
    private static final double JUMBO_LOAN_THRESHOLD = 726200.0; // 2023 conforming limit
    private static final int TRID_WAITING_PERIOD_DAYS = 3;

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ComplianceResult checkCompliance(double loanAmount, double dti, double ltv) {
        ComplianceResult result = new ComplianceResult();
        result.setCompliant(true);

        // QM eligibility check
        result.setQmEligible(dti <= QM_DTI_THRESHOLD);
        if (!result.isQmEligible()) {
            result.addWarning("DTI exceeds QM threshold of " + QM_DTI_THRESHOLD + "%");
        }

        // High-cost loan check (HPML rules)
        if (ltv > HIGH_COST_LTV_THRESHOLD) {
            result.addWarning("LTV > 80% - may require PMI or additional documentation");
        }

        // Jumbo loan check
        if (loanAmount > JUMBO_LOAN_THRESHOLD) {
            result.addWarning("Loan exceeds conforming limit - jumbo loan rules apply");
        }

        // Extreme DTI violation
        if (dti > 50.0) {
            result.addViolation("DTI exceeds maximum allowable limit of 50%");
        }

        // Extreme LTV violation
        if (ltv > 97.0) {
            result.addViolation("LTV exceeds maximum allowable limit of 97%");
        }

        // Set recommendation
        if (result.isCompliant() && result.isQmEligible()) {
            result.setRecommendation("APPROVE - All compliance checks passed");
        } else if (result.isCompliant()) {
            result.setRecommendation("APPROVE WITH CONDITIONS - Review warnings");
        } else {
            result.setRecommendation("DECLINE - Compliance violations found");
        }

        return result;
    }

    @Override
    public boolean isQualifiedMortgage(double dti) {
        return dti <= QM_DTI_THRESHOLD;
    }

    @Override
    public boolean meetsTridTiming(Date disclosureDate, Date closingDate) {
        if (disclosureDate == null || closingDate == null) {
            return false;
        }

        long diffInMillis = closingDate.getTime() - disclosureDate.getTime();
        long diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

        // TRID requires at least 3 business days between disclosure and closing
        return diffInDays >= TRID_WAITING_PERIOD_DAYS;
    }
}
