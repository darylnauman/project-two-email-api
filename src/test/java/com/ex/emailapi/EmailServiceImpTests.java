package com.ex.emailapi;

import com.ex.emailapi.entities.DailyRecipeTracker;
import com.ex.emailapi.entities.Subscription;
import com.ex.emailapi.repositories.DailyRecipeTrackerRepository;
import com.ex.emailapi.repositories.SubscriptionRepository;
import com.ex.emailapi.services.EmailServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class EmailServiceImpTests {


    private EmailServiceImpl emailService;
    private SubscriptionRepository subscriptionRepository;
    private DailyRecipeTrackerRepository dailyRecipeTrackerRepository;

    final Logger logger = LoggerFactory.getLogger(EmailApiApplication.class);

    @BeforeEach
    public void initEachTest(){
        System.out.println("Initializing before test");
        subscriptionRepository = mock(SubscriptionRepository.class);
        dailyRecipeTrackerRepository = mock(DailyRecipeTrackerRepository.class);
        emailService = new EmailServiceImpl(subscriptionRepository, dailyRecipeTrackerRepository);
        System.out.println("Done init");
    }

    /**
     * Test for sendmail- Email id or Recipe id can't be null
     */
    @Test
    public void shouldThrowIllegalStateException() {
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class, () -> {

            emailService.sendmail(null,0);
        });

        logger.info("Message shouldThrowIllegalStateException : " + e.getMessage());

        Assertions.assertEquals("Email id or Recipe id can't be null", e.getMessage(), "Method didn't throw with null parameter");
    }

    /**
     * Test for instant email
     * @throws MessagingException
     */
    @Test
    public void shouldSendMail() throws MessagingException {
        String message = emailService.sendmail("anju.naduth@gmail.com", 479701);
        //String message = emailService.sendmail("anju.naduth@gmail.com", 479102);

        Assertions.assertNotNull(message);

        logger.info("Message in shouldSendMail is: "+message);

        Assertions.assertEquals("Email sent successfully", message, "Email sent successfully");

    }



    //Just write test for daily email sender below

    /**
     * Throws an IllegalStateException when the values received as arguments for finding daily recipe id is NULL
     */
    @Test
    public void shouldThrowIllegalStateExceptionForNullDetailsForGettingDailyRecipe() {
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, () -> {

            emailService.getNewDailyRecipeForCurrentCustomer(null, null);
        });

        Assertions.assertEquals("Email or preferences can't be null", ex.getMessage(), "Method didn't throw with null values");

        ex = Assertions.assertThrows(IllegalStateException.class, () -> {

            emailService.getNewDailyRecipeForCurrentCustomer(null, "apple");
        });

        Assertions.assertEquals("Email or preferences can't be null", ex.getMessage(), "Method didn't throw with null values");

        ex = Assertions.assertThrows(IllegalStateException.class, () -> {

            emailService.getNewDailyRecipeForCurrentCustomer("aneeshcm18@gmail.com", null);
        });

        Assertions.assertEquals("Email or preferences can't be null", ex.getMessage(), "Method didn't throw with null values");

    }

    /**
     * Checks to make sure a valid ID will be received on happy path
     */
    @Test
    void shouldReturnRecipeIdForValidDetails() {
        String email = "aneeshcm18@gmail.com";
        String preferences = "fish";
        int mockTrackerId = 0;
        String mockEmail = "aneeshcm18@gmail.com";
        int mockRecipeId = 479701;
        DailyRecipeTracker mockDailyRecipeTracker = new DailyRecipeTracker();
        mockDailyRecipeTracker.setTrackerId(mockTrackerId);
        mockDailyRecipeTracker.setEmail(mockEmail);
        mockDailyRecipeTracker.setRecipeId(mockRecipeId);
        List<DailyRecipeTracker> mockDailyRecipeTrackerList = new ArrayList<DailyRecipeTracker>();
        mockDailyRecipeTrackerList.add(mockDailyRecipeTracker);

        when(dailyRecipeTrackerRepository.findAllByEmail(email)).thenReturn(mockDailyRecipeTrackerList);
        int returnedRecipeId = emailService.getNewDailyRecipeForCurrentCustomer(email, preferences);

        Assertions.assertNotNull(returnedRecipeId,"Recipe received is null");
    }

    /**
     * Test to check if daily emails are sent to customer
     */
    @Test
    void shouldReturnSuccessMessageForDailyEmailSend() {

        String expectedResponseOfDailyEmailSend = "Email Send";

        Subscription mockSubscriberToSendEmail =new Subscription();
        mockSubscriberToSendEmail.setSubscriptionId(0);
        mockSubscriberToSendEmail.setEmail("aneeshcm18@gmail.com");
        mockSubscriberToSendEmail.setPreferences("fish");

        DailyRecipeTracker mockTodaysRecipe = new DailyRecipeTracker();
        mockTodaysRecipe.setEmail("aneeshcm18@gmail.com");
        mockTodaysRecipe.setRecipeId(479701);

        when(dailyRecipeTrackerRepository.save(mockTodaysRecipe)).thenReturn(mockTodaysRecipe);
        String responseOfDailyEmailSend = emailService.sendDailyEmailToSubscriber(mockSubscriberToSendEmail);

        Assertions.assertEquals(responseOfDailyEmailSend, expectedResponseOfDailyEmailSend,"Daily email not sent");
    }

}
