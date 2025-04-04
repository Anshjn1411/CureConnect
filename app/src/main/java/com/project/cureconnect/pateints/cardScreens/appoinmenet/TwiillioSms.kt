package com.project.cureconnect.pateints.cardScreens.appoinmenet

import com.project.cureconnect.pateints.Constant.TwilioCredential
import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message

object TwilioSMS {

     val ACCOUNT_SID = TwilioCredential.ACCOUNT_SID.toString()
    val AUTH_TOKEN = TwilioCredential.AUTH_TOKEN
     val FROM_PHONE_NUMBER = TwilioCredential.FROM_PHONE_NUMBER

    init {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN)
    }

    fun sendSMS(toPhoneNumber: String, messageBody: String) {
        try {
            val message = Message.creator(
                com.twilio.type.PhoneNumber(toPhoneNumber),
                com.twilio.type.PhoneNumber(FROM_PHONE_NUMBER),
                messageBody
            ).create()

            println("SMS sent successfully! Message SID: ${message.sid}")
        } catch (e: Exception) {
            println("Error sending SMS: ${e.message}")
        }
    }
}
