package TraineeshipApplication.service;

import javax.mail.MessagingException;

public interface EmailService {

    /**
     * Sends a verification email to the given recipient.
     * @param recipientEmail The email address to send to.
     * @param verificationLink The link with the verification token.
     */

	void sendVerificationCode(String recipientEmail, String code);
    
	public String generateVerificationCode();
	
	void sendEmailToStudents(String Location);
}
