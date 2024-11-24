package md.mirrerror.prlab3.utils;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import org.springframework.stereotype.Component;

@Component
public class EmailManager {

    public void sendEmail() {
        Email email = new Email();

        email.setFrom("mirrerror", "mirrerror@trial-z3m5jgrwjq0ldpyo.mlsender.net");
        email.addRecipient("mirrerror", "killnobb@gmail.com");

        email.setSubject("Email subject");

        String htmlContent = "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                "        .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }" +
                "        .header { text-align: center; padding: 10px 0; }" +
                "        .header h1 { margin: 0; color: #333333; }" +
                "        .content { margin: 20px 0; }" +
                "        .content p { line-height: 1.6; color: #666666; }" +
                "        .footer { text-align: center; padding: 10px 0; color: #999999; font-size: 12px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">" +
                "            <h1>Email Subject</h1>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <p>Hello,</p>" +
                "            <p>This is the task for the mark 10 on the third lab.</p>" +
                "            <p>Best regards,<br>mirrerror</p>" +
                "        </div>" +
                "        <div class=\"footer\">" +
                "            <p>&copy; 2024 mirrerror. All rights reserved.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";

        email.setHtml(htmlContent);

        MailerSend ms = new MailerSend();

        ms.setToken(System.getenv("MAILERSEND_API_KEY"));

        try {
            MailerSendResponse response = ms.emails().send(email);
            System.out.println(response.messageId);
        } catch (MailerSendException e) {
            e.printStackTrace();
        }
    }
}