package com.athena.mis.accounting.utility

import com.athena.mis.utility.DateUtility

class WrapAccBankStatement {

    private static final int IDX_TRANSACTION_DATE = 0
    private static final int IDX_TRANSACTION_TYPE = 1
    private static final int IDX_COMPANY_REF = 2
    private static final int IDX_NARRATIVE = 3
    private static final int IDX_BANK_REF = 4
    private static final int IDX_TRANSACTION_REF = 5
    private static final int IDX_DEBIT = 6
    private static final int IDX_CREDIT = 7
    private static final int IDX_BALANCE = 8

    long serial

    Date transactionDate
    String strTransactionDate
    String transactionType
    String companyRef
    String narrative
    String bankRef
    String transactionRef
    String strDebit
    double debit
    String strCredit
    double credit
    String strBalance
    double balance
    String chequeNo             // get cheque no from company ref
    double amount               // debit or credit


    List errors = []


    private static final String ERROR_PARSING_CSV = "Error occurred to parse the CSV file"
    private static final String TRANS_DATE_REQUIRED = "Transaction Date required"
    private static final String TRANS_TYPE_REQUIRED = "Transaction Type required"
    private static final String TRANS_REF_REQUIRED = "Transaction Ref required"
    private static final String CREDIT_DEBIT_REQUIRED = "Credit or Debit amount required"
    private static final String INWARD_CHEQUE_DR = "Inward Cheque - Dr"
    private static final String CHEQUE_WITHDRAWAL = "Cheque Withdrawal"
    private static final String COMPANY_REF_REQUIRED = "Company Ref(Other Ref) required"

    WrapAccBankStatement(def tokens, long rowCount) {

        try {
            this.serial = rowCount

            this.strTransactionDate = tokens[this.IDX_TRANSACTION_DATE]
            this.transactionType = tokens[this.IDX_TRANSACTION_TYPE]
            this.transactionType.trim()
            this.companyRef = tokens[this.IDX_COMPANY_REF]
            this.companyRef.trim()
            this.narrative = tokens[this.IDX_NARRATIVE]
            this.narrative.trim()
            this.bankRef = tokens[this.IDX_BANK_REF]
            this.bankRef.trim()
            this.transactionRef = tokens[this.IDX_TRANSACTION_REF]
            this.transactionRef.trim()
            this.strDebit = tokens[this.IDX_DEBIT]
            this.strDebit.trim()
            this.strCredit = tokens[this.IDX_CREDIT]
            this.strCredit.trim()
            this.strBalance = tokens[this.IDX_BALANCE]
            this.strBalance.trim()
            formatEmptyProperties()

        } catch (Exception ex) {
            errors << ERROR_PARSING_CSV
        }
    }

    private formatEmptyProperties() {

        if (this.strTransactionDate.length() == 0) {
            this.transactionDate = null
        } else {
            try {
                this.transactionDate = DateUtility.parseDateFromBankStatement(this.strTransactionDate)
            } catch (Exception e) {
                this.transactionDate = null
            }
        }
        if (this.transactionType.length() == 0) {
            this.transactionType = null
        }
        if (this.companyRef.length() == 0) {
            this.companyRef = null
        }
        if (this.narrative.length() == 0) {
            this.narrative = null
        }
        if (this.bankRef.length() == 0) {
            this.bankRef = null
        }
        if (this.transactionRef.length() == 0) {
            this.transactionRef = null
        }
        if (this.strDebit.length() == 0) {
            this.debit = 0d
        } else {
            try {
                this.debit = Double.parseDouble(this.strDebit)
            } catch (Exception ex) {
                this.debit = 0d
            }
        }
        if (this.strCredit.length() == 0) {
            this.credit = 0d
        } else {
            try {
                this.credit = Double.parseDouble(this.strCredit)
            } catch (Exception ex) {
                this.credit = 0d
            }
        }
        if (this.strBalance.length() == 0) {
            this.balance = 0d
        } else {
            try {
                this.balance = Double.parseDouble(this.strBalance)
            } catch (Exception ex) {
                this.balance = 0d
            }
        }

        if ((this.transactionType.equalsIgnoreCase(INWARD_CHEQUE_DR)) || (this.transactionType.equalsIgnoreCase(CHEQUE_WITHDRAWAL))) {
            this.chequeNo = null
            int companyRefLength = this.companyRef.length()
            if (companyRefLength > 0) {
                this.chequeNo = this.companyRef.substring(3, companyRefLength)
            }
        }

        this.amount = this.debit > 0 ? this.debit : this.credit
    }

    boolean validateFile() {

        // Validate date
        if (!this.transactionDate) errors << TRANS_DATE_REQUIRED
        // Validate trans type
        if (!this.transactionType) errors << TRANS_TYPE_REQUIRED
        // Validate transaction ref
        if (!this.transactionRef) errors << TRANS_REF_REQUIRED
        // Validate company ref
        if (((this.transactionType.equalsIgnoreCase(INWARD_CHEQUE_DR))
                || (this.transactionType.equalsIgnoreCase(CHEQUE_WITHDRAWAL)))
                && (!this.chequeNo)) {
            errors << COMPANY_REF_REQUIRED
        }
        // Validate debit
        if (this.debit == 0 && this.credit == 0) errors << CREDIT_DEBIT_REQUIRED

        //*********************** Now all csv file validation check is done!


        return (errors.size() == 0)
    }
}
