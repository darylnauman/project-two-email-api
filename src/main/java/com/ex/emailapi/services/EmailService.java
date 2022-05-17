package com.ex.emailapi.services;

import com.ex.emailapi.entities.Subscription;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.io.IOException;

public interface EmailService {
    String sendmail(String email, int recipeId) throws AddressException, MessagingException, IOException;
    int getNewDailyRecipeForCurrentCustomer(String emailAddressOfCurrentSubscriber, String preferencesOfCurrentSubscriber);
    String sendDailyEmailToSubscriber(Subscription subscriberToSendEmail);
    void dailyEmailSender();

}
