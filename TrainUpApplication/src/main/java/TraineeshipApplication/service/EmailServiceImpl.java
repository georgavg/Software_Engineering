package TraineeshipApplication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import TraineeshipApplication.dao.StudentDAORepository;
import TraineeshipApplication.model.Student;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;  // auto-configured by Spring Boot
    @Autowired
    private StudentDAORepository studentDAORepository;
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();
    
    @Override
    public void sendVerificationCode(String recipientEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(new InternetAddress("sandbox.user.avg@gmail.com", "TRAINUP"));
            helper.setTo(recipientEmail);
            helper.setSubject("Verify Your TRAINUP Account");

            // HTML Content with CID for the image
            String htmlContent = "<div style='font-family: Arial, sans-serif; text-align: center;'>"
                    + "<img src='cid:trainupLogo' alt='TRAINUP Logo' style='width:150px; margin-bottom: 10px;'/>"
                    + "<h2 style='color: #007bff;'>Welcome to TRAINUP!</h2>"
                    + "<p>You're just one step away from accessing amazing traineeship opportunities.</p>"
                    + "<p>Your verification code is:</p>"
                    + "<h3 style='color: #ff6600; font-weight: bold;'>" + code + "</h3>"
                    + "<p>Please enter this code on the verification page.</p>"
                    + "<p style='font-style: italic; color: #666;'>Bridging Students & Careers</p>"
                    + "<p>Best Regards,<br><b>TRAINUP Team</b></p>"
                    + "</div>";

            helper.setText(htmlContent, true);

            // ** Load logo image correctly**
            ClassPathResource resource = new ClassPathResource("static/images/logo.png");
            helper.addInline("trainupLogo", resource);

            // Send email
            mailSender.send(message);
            
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public  String generateVerificationCode() {
    	StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
        	code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
    
    @Override
    public void sendEmailToStudents(String Location) {
    	List<Student> students =  studentDAORepository.findAll()
                .stream()
                .filter(stu -> stu.getPreferredLocation().matches(Location))
                .collect(Collectors.toList());
    	try {
	    	MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(new InternetAddress("sandbox.user.avg@gmail.com", "TRAINUP"));
			helper.setTo(students.get(0).getUser().getEmail());
			helper.setSubject("Notification for New Traineeship Position");
	        String htmlContent = "<div style='font-family: Arial, sans-serif; text-align: center;'>"
	                + "<img src='cid:trainupLogo' alt='TRAINUP Logo' style='width:150px; margin-bottom: 10px;'/>"
	                + "<h2 style='color: #007bff;'>New Position!</h2>"
	                + "<p>Go to check new possitions in your Profile.</p>"
	                + "<p>The location is:</p>"
	                + "<h3 style='color: #ff6600; font-weight: bold;'>" + Location + "</h3>"
	                + "<p>Please check your profile for more informations and try to apply!</p>"
	                + "<p style='font-style: italic; color: #666;'>Bridging Students & Careers</p>"
	                + "<p>Best Regards,<br><b>TRAINUP Team</b></p>"
	                + "</div>";
	        helper.setText(htmlContent, true);
	
	        // ** Load logo image correctly**
	        ClassPathResource resource = new ClassPathResource("static/images/logo.png");
	        helper.addInline("trainupLogo", resource);
	        mailSender.send(message);
    	 } catch (MessagingException | UnsupportedEncodingException e) {
             e.printStackTrace();
         }
    }
    
}

