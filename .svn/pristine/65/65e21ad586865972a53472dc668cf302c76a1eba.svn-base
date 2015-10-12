package com.athena.mis.exchangehouse.entity

/*
* Payment Response Notification is the response object from payPoint when transaction is successful
*/

class ExhPaymentResponseNotification {

    long id
    long taskId                 // ExhTask.id
    Date createdOn              // Object createdOn
    String key                  // key = Spring.encodePassword (Task.id + Customer.id + Beneficiary.id)
    //
    int intTransID                // MCPE unique identifier
    int intAccountID            // a/c of sfsl? (in test mode it was 315389)
    int intStatus                // 0 fail, 1 = success
    int intTime                    // The time at which this transaction was authorised (seconds from 1970)
    float fltAmount                // amount associated with the transaction
    String strCurrency            // 3 letter ISO e.g.GBP
    String strMessage            // any message returned by bank when this Transaction was processed(can be null)
    String strPaymentType        // purchaser's card type. e.g. VISA
    String strCustomer            // card holder's name

    String strAddress            // card holder's street address.
    String strCity                // card holder's city
    String strState                // card holder's state/county
    String strPostcode            // card holder's postcode.
    String strCountry            // 2 letter ISO code of customer country of origin

    String strTel                // card holder's telephone number.
    String strFax                // card holder's fax number.
    String strEmail                // card holder's email address.

    String strDesc                // previously submitted description. e.g. Payment for remittance SB12345
    String strCartID            // previously submitted cartId. e.g. task.id
    String strTransactionType    // one of these: Payment, Repeat Payment, Refund or Chargeback
    float fltFraudScore            // Likelihood of transaction fraudulent. 0 = most unlikely, 10=most likely
    float fltNotifyVersion        // version of the notification(currently 1.5)


    static constraints = {
        strAddress(nullable: true)
        strMessage(nullable: true)
        strCity(nullable: true)
        strState(nullable: true)
        strPostcode(nullable: true)
        strTel(nullable: true)
        strFax(nullable: true)
        strEmail(nullable: true)
    }

    static mapping = {
        version false
        id generator: 'sequence', params: [sequence: 'exh_payment_response_notification_id_seq']
        // indexing
        taskId index: 'exh_payment_response_notification_task_id_idx'
    }
}
