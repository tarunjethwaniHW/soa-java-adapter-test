package com.soa.ejb;

import javax.ejb.Remote;

/**
 * Compliance Check EJB Remote Interface
 * JNDI: ejb/ComplianceCheck
 * Referenced from: composite.xml binding.ejb
 *
 * Call Chain:
 *   SOA_REFERENCE (ComplianceCheckEJB)
 *       → REFERENCE_TO_EJB (binding.ejb jndi-name="ejb/ComplianceCheck")
 *       → IComplianceCheckRemote.java (@Remote interface)
 *       → ComplianceCheckEJB.java (@Stateless implementation)
 *
 * This interface is invoked from SOA Suite via:
 *   <binding.ejb uri="ComplianceCheckEJB#com.soa.ejb.IComplianceCheckRemote"
 *                jndi-name="ejb/ComplianceCheck"/>
 */
@Remote
public interface IComplianceCheckRemote {

    /**
     * Check regulatory compliance for a loan application.
     * @param loanAmount Requested loan amount
     * @param dti Debt-to-income ratio
     * @param ltv Loan-to-value ratio
     * @return Compliance check result
     */
    ComplianceResult checkCompliance(double loanAmount, double dti, double ltv);

    /**
     * Check if loan meets QM (Qualified Mortgage) requirements.
     * @param dti Debt-to-income ratio
     * @return true if meets QM requirements (DTI <= 43%)
     */
    boolean isQualifiedMortgage(double dti);

    /**
     * Check for TRID compliance (TILA-RESPA Integrated Disclosure).
     * @param disclosureDate Date disclosures were provided
     * @param closingDate Scheduled closing date
     * @return true if timing requirements are met
     */
    boolean meetsTridTiming(java.util.Date disclosureDate, java.util.Date closingDate);
}
