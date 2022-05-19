package com.ex.emailapi.services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import com.ex.emailapi.EmailApiApplication;
import com.ex.emailapi.entities.DailyRecipeTracker;
import com.ex.emailapi.entities.Subscription;
import com.ex.emailapi.repositories.DailyRecipeTrackerRepository;
import com.ex.emailapi.repositories.SubscriptionRepository;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import javax.mail.Message;

@Configuration
@EnableScheduling
public class EmailServiceImpl implements EmailService{

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    DailyRecipeTrackerRepository dailyRecipeTrackerRepository;


    final Logger logger = LoggerFactory.getLogger(EmailApiApplication.class);

    public EmailServiceImpl(SubscriptionRepository subscriptionRepository, DailyRecipeTrackerRepository dailyRecipeTrackerRepository) {
    this.subscriptionRepository = subscriptionRepository;
    this.dailyRecipeTrackerRepository = dailyRecipeTrackerRepository;
    }

    public EmailServiceImpl() {

    }

    /**
     *
     * @param email - email id of the customer
     * @param recipeId - customer's favorite recipe id
     * @return
     * @throws MessagingException
     */
    @Override
    public String sendmail(String email, int recipeId) throws MessagingException{
        if(email == null || recipeId == 0){
            throw new IllegalStateException("Email id or Recipe id can't be null");
        }

        // Host url
        String host = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/"+recipeId+"/information";
        //String host = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/random?tags=dessert&number=1";

        String charset = "application/json";

       // String x_rapidapi_host = "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com";
       // String x_rapidapi_key = "46c9581dbcmsh496a852afc52dadp18d0c6jsn88d3b880b345";

        String x_rapidapi_host = "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com";
        String x_rapidapi_key = "8225095afcmsh9855bc73d24b31cp145800jsn918dfa7503e8";

        HttpResponse <JsonNode> response = null;
        try {
            response = Unirest.get(host)
                    .header("x-rapidapi-host", x_rapidapi_host)
                    .header("x-rapidapi-key", x_rapidapi_key)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

       
        //String message=(response.getBody().getObject().getString("instructions"));

        logger.info("recipe id : "+response.getBody().getObject().getInt("id"));
        logger.info("recipe title : "+response.getBody().getObject().getString("title"));

        String message=("<html><body>"+
                "Hello,<br><br>"
                +"Here is your recipe from <em>'What Can I Make to Eat'</em>. <br><br>"
                +"<h1>"+response.getBody().getObject().getString("title")+"</h1>"
                +response.getBody().getObject().getString("summary")
                +"<br><br><br><br><img src=\'"+response.getBody().getObject().getString("image")+"\'/><br><br><br>"
                +"<h2>Instructions </h2>"+response.getBody().getObject().getString("instructions")+"<br><h2>servings </h2><h3>"+response.getBody().getObject().getInt("servings")+"</h3>"
                +"<h3>Total Time </h3><h4>"+response.getBody().getObject().getInt("readyInMinutes")+" "+" minutes</h4>"
                +"<br>For more recipe ideas be sure to visit our website - <a href=\"https://www.google.com\"><em>What Can I Make To Eat</em></a> "
        +"</body></html>");

        String subject = response.getBody().getObject().getString("title");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");


        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("AneeshRevatureProject1@gmail.com", "RevatureBank2022");
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("AneeshRevatureProject1@gmail.com", false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        msg.setSubject("Recipe For You - "+subject);
        msg.setContent(message, "text/html");
        msg.setSentDate(new Date());

        Transport.send(msg);

        //Commented for testing
        //return (response.getBody().toString());
        return "Email sent successfully";
    }

    /**
     *
     * @param emailAddressOfCurrentSubscriber This is the email address of the customer for whom a new recipe has to be emailed today
     * @param preferencesOfCurrentSubscriber This is the preference chosen by this customer for daily recipe suggestion
     * @return Sends back a recipe id of a new recipe which is not send the customer with the given email id in the past week
     */
    @Override
    public int getNewDailyRecipeForCurrentCustomer(String emailAddressOfCurrentSubscriber, String preferencesOfCurrentSubscriber) {
        logger.debug("Starting the process to find a random recipe for the subscriber");
        if (emailAddressOfCurrentSubscriber == null || preferencesOfCurrentSubscriber == null) {
            throw new IllegalStateException("Email or preferences can't be null");
        }

        List<DailyRecipeTracker> allRecipesSendToCurrentSubscriberPreviously = dailyRecipeTrackerRepository.findAllByEmail(emailAddressOfCurrentSubscriber);
        int totalNumberOfRecipesSendToCurrentSubscriberPreviously = allRecipesSendToCurrentSubscriberPreviously.size();

        int finalRecipeToBeSend = 0;
        logger.debug("Api call is being made for a random recipe");
        String host = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/random?number=1";
        if(preferencesOfCurrentSubscriber.equals("NO")){
            host = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/random?number=1";
        }else {
            host = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/random?tags=" + preferencesOfCurrentSubscriber + "&number=1";
        }
        String charset = "application/json";
        String x_rapidapi_host = "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com";
        String x_rapidapi_key = "46c9581dbcmsh496a852afc52dadp18d0c6jsn88d3b880b345";
        HttpResponse <JsonNode> response = null;
        do {

            try {
                response = Unirest.get(host)
                        .header("x-rapidapi-host", x_rapidapi_host)
                        .header("x-rapidapi-key", x_rapidapi_key)
                        .asJson();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            String jsonString = response.getBody().getObject().getJSONArray("recipes").get(0).toString();
            JSONObject obj = new JSONObject(jsonString);
            int generatedRecipeId = obj.getInt("id");//This is the id of the random generated recipe
            System.out.println("Generated id"+ generatedRecipeId);
            logger.debug("Random recipe id is received from external API");

            //This loop make sure that the generated recipe was not send to customer within last 7 days
            for (int j = 1; j <= 7; j++) {
                if ((totalNumberOfRecipesSendToCurrentSubscriberPreviously - j) <= 0) {
                    finalRecipeToBeSend = generatedRecipeId;
                    break;
                }

                if (allRecipesSendToCurrentSubscriberPreviously.get(totalNumberOfRecipesSendToCurrentSubscriberPreviously - j).getRecipeId() == generatedRecipeId) {
                    finalRecipeToBeSend = 0;
                    break;
                } else {
                    finalRecipeToBeSend = generatedRecipeId;
                }

            }
        }while (finalRecipeToBeSend == 0);
        logger.debug("Random recipe id which is not send in the past week is received and is now returned");
        return finalRecipeToBeSend;
    }

    /**
     *
     * @param subscriberToSendEmail This will have details of the subscriber to whom daily email should be sent
     * @return A string which says email was sent successfully
     */
    @Override
    public String sendDailyEmailToSubscriber(Subscription subscriberToSendEmail) {
        String emailAddressOfCurrentSubscriber = subscriberToSendEmail.getEmail();
        String preferencesOfCurrentSubscriber = subscriberToSendEmail.getPreferences();
        int todaysRecipeToBeSend = 0;
        if(preferencesOfCurrentSubscriber == null){
            todaysRecipeToBeSend = getNewDailyRecipeForCurrentCustomer(emailAddressOfCurrentSubscriber, "NO");
        }else {
            todaysRecipeToBeSend = getNewDailyRecipeForCurrentCustomer(emailAddressOfCurrentSubscriber, preferencesOfCurrentSubscriber);
        }
        System.out.println("Emailing recipe id "+todaysRecipeToBeSend+ "to " +emailAddressOfCurrentSubscriber);

        DailyRecipeTracker todaysRecipe = new DailyRecipeTracker();
        todaysRecipe.setEmail(emailAddressOfCurrentSubscriber);
        todaysRecipe.setRecipeId(todaysRecipeToBeSend);
        logger.debug("Trying to send an email to customer with a new recipe");
        try {
            sendmail(emailAddressOfCurrentSubscriber,todaysRecipeToBeSend);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        dailyRecipeTrackerRepository.save(todaysRecipe);
        return ("Email Send");
    }


    /**
     * This will send emails to all subscribers daily(for testing it i sending emails every 6 minutes) with a unique recipe,
     * which depends on subscribers preference if he has chosen one or else a random recipe
     */
    @Override
    @Scheduled(fixedRate = 1000000)//This will send daily emails after every six minutes
    public void dailyEmailSender() {
        logger.debug("Daily email sender started");

        List<Subscription> allSubscribedUsers = subscriptionRepository.findAll();
        //Finds total number of subscribers
        int numberOfSubscribers = subscriptionRepository.findAll().size();

        //For loop to iterate through the entire list of subscribers in subscription table
        for(int i=0;i<numberOfSubscribers; i++){
            //System.out.println(allSubscribedUsers.get(i));
            logger.debug("Starting to traverse through the entire list of subscribers");

            //Selecting each individual subscriber for sending email and separately accessing the specific details
            Subscription subscriberToSendEmail = allSubscribedUsers.get(i);
            String statusOfDailyEmailSendToSubscriber = sendDailyEmailToSubscriber(subscriberToSendEmail);
            logger.debug("Updated recipe tracker table with the recipe sent today");

        }


    }
}
