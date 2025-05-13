package TraineeshipApplication.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name="students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne()
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    // The student's full name
    @Column(name = "student_name")
    private String studentName;
    
    @Column(name = "student_phoneNumber")
    private String studentNumber;
    
    @Column(name = "student_university")
    private String studentUniversity;
    
    // University ID number (AM)
    @Column(name = "am")
    private String am;

    // Average grade (0 if not used)
    @Column(name = "average_grade")
    private double averageGrade;
    
    @Column(name = "student_biography")
    private String studentBiography;
    
    @Lob
    @Column(name = "profile_image", columnDefinition = "LONGBLOB")
    private byte[] profileImage;
    
    // Whether they're looking for a traineeship
    @Column(name = "looking_for_traineeship")
    private boolean lookingForTraineeship;

    // The student's preferred location
    @Column(name = "preferred_location")
    private String preferredLocation;
    
    // Actual list of skills in a separate table
    @ElementCollection
    @CollectionTable(name="student_skills", joinColumns=@JoinColumn(name="student_id"))
    @Column(name="skill")
    private List<String> skills = new ArrayList<>();

    // Actual list of interests in a separate table
    @ElementCollection
    @CollectionTable(name="student_interests", joinColumns=@JoinColumn(name="student_id"))
    @Column(name="interest")
    private List<String> interests = new ArrayList<>();

    // If you still want assignedTraineeship
    @OneToOne
    @JoinColumn(name="assigned_position_id", unique=true)
    private TraineeshipPosition assignedTraineeship;
    
    @ManyToMany
    @JoinTable(
       name = "student_positions_applied",           // όνομα του join table
       joinColumns = @JoinColumn(name = "student_id"),
       inverseJoinColumns = @JoinColumn(name = "position_id")
    )
    private List<TraineeshipPosition> appliedPositions = new ArrayList<>();
    
    // The student's preferred location
    @Column(name = "traineeship_completed")
    private String traineeshipCompleted;
    // --- Transient fields for comma-separated form input ---

    @Transient
    private String skillsAsString;

    @Transient
    private String interestsAsString;

    // Convert the "skills" list into a comma-separated string
    public String getSkillsAsString() {
        if (skills == null || skills.isEmpty()) {
            return "";
        }
        return String.join(", ", skills);
    }

    // Parse a comma-separated string into the "skills" list
    public void setSkillsAsString(String input) {
        if (input == null || input.trim().isEmpty()) {
            this.skills = new ArrayList<>();
        } else {
            this.skills = Arrays.stream(input.split(","))
                                .map(String::trim)
                                .collect(Collectors.toList());
        }
    }

    // Convert "interests" to comma-separated
    public String getInterestsAsString() {
        if (interests == null || interests.isEmpty()) {
            return "";
        }
        return String.join(", ", interests);
    }

    // Parse a comma-separated string into the "interests" list
    public void setInterestsAsString(String input) {
        if (input == null || input.trim().isEmpty()) {
            this.interests = new ArrayList<>();
        } else {
            this.interests = Arrays.stream(input.split(","))
                                   .map(String::trim)
                                   .collect(Collectors.toList());
        }
    }
}
