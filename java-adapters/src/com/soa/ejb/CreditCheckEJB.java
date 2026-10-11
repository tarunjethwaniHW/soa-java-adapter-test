package com.soa.ejb;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * Credit Check Stateless Session Bean
 * Deployed to WebLogic, accessed via binding.ejb from SOA Composite
 *
 * JNDI Name: ejb/CreditCheck
 * EJB URI: CreditCheckEJB#com.soa.ejb.ICreditCheckRemote
 *
 * This EJB is invoked by SOA Suite via:
 *   <reference name="CreditCheckEJB">
 *       <interface.java interface="com.soa.ejb.ICreditCheckRemote"/>
 *       <binding.ejb uri="CreditCheckEJB#com.soa.ejb.ICreditCheckRemote"
 *                    jndi-name="ejb/CreditCheck">
 *           <property name="java.naming.factory.initial">
 *               weblogic.jndi.WLInitialContextFactory
 *           </property>
 *           <property name="java.naming.provider.url">
 *               t3://localhost:7001
 *           </property>
 *       </binding.ejb>
 *   </reference>
 */
@Stateless(name = "CreditCheckEJB", mappedName = "ejb/CreditCheck")
public class CreditCheckEJB implements ICreditCheckRemote {

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public CreditReport checkCredit(String ssn, String firstName, String lastName) {
        // Simulate credit bureau call
        CreditReport report = new CreditReport();
        report.setSsn(maskSsn(ssn));
        report.setScore(calculateScore(ssn));
        report.setStatus("VERIFIED");
        report.setReportDate(new java.util.Date());
        report.setBureauName("TransUnion");
        report.setAccountCount(5 + Math.abs(ssn.hashCode() % 10));
        report.setTotalDebt(10000 + Math.abs(ssn.hashCode() % 50000));
        report.setLatePayments(Math.abs(ssn.hashCode() % 3));

        return report;
    }

    @Override
    public int getCreditScore(String ssn) {
        return calculateScore(ssn);
    }

    @Override
    public boolean meetsMinimumScore(String ssn, int minScore) {
        return calculateScore(ssn) >= minScore;
    }

    /**
     * Calculate credit score based on SSN hash (demo only).
     */
    private int calculateScore(String ssn) {
        if (ssn == null || ssn.isEmpty()) {
            return 650; // Default score
        }
        // Generate score between 580-820 based on SSN hash
        int baseScore = 580;
        int variation = Math.abs(ssn.hashCode() % 240);
        return baseScore + variation;
    }

    /**
     * Mask SSN for security (show only last 4 digits).
     */
    private String maskSsn(String ssn) {
        if (ssn == null || ssn.length() < 4) {
            return "XXX-XX-XXXX";
        }
        return "XXX-XX-" + ssn.substring(ssn.length() - 4);
    }
}
