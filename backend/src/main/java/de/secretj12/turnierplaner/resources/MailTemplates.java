package de.secretj12.turnierplaner.resources;

import io.quarkus.mailer.Mail;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.ws.rs.core.UriBuilder;
import java.io.File;

public class MailTemplates {
//    TODO internationalization
    String url =  "http://localhost:3000";

    public Mail verificationMail(String recipient,String verificationcode){
        return Mail.withHtml(recipient,"Please verify your email",
                "<p>Welcome!,</p>"+
                        "<p>Please verify your email with the following link:</p>" +
                        "<a href="+UriBuilder.fromUri(url+"/#/player/verification").queryParam("code",verificationcode).build().toString()+">link</a>"+
                     "<p>Best regards</p><img src=\"cid:logo@quarkus.io\"/></p>"
                )
                .addInlineAttachment("Turnierplaner.png",new File("src/main/resources/tennis-ball.png"),"image/png","<logo@quarkus.io>");
    }
}
