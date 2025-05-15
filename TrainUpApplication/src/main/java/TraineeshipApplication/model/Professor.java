package TraineeshipApplication.model;

import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name="professors")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Link back to the application's User (one-to-one)
    @OneToOne
    @JoinColumn(name="user_id", unique=true, nullable=false)
    private User user;
   
    @Column(name="professor_name")
    private String professorName;
    
    @Column (name = "professor_number")
    private String professorNumber;
    
    @Column (name = "professor_location")
    private String professorLocation;
    
    @Column(name = "professor_university")
    private String professorUniversity;
    
    @Column(name = "professor_biography")
    private String professorBiography;
    
    @Column(name = "professor_website")
    private String professorWebsite;
    
    @Lob
    @Column(name = "profile_image", columnDefinition = "LONGBLOB")
    private byte[] profileImage;
    
    // Store interests as a list
    @ElementCollection
    @CollectionTable(name="professor_interests", joinColumns=@JoinColumn(name="professor_id"))
    @Column(name="interest")
    private List<String> interests = new ArrayList<>();

    // One-to-many with TraineeshipPosition
    @OneToMany(mappedBy="supervisor", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<TraineeshipPosition> supervisedPositions = new ArrayList<>();
    
    @Transient
    private String interestsAsString;
    
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
