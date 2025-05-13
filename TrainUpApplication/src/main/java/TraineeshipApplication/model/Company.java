package TraineeshipApplication.model;

import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name="companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Link back to the application's User (one-to-one)
    @OneToOne
    @JoinColumn(name="user_id", unique=true)
    private User user;

    @Column(name="company_name")
    private String companyName;

    @Column(name="company_location")
    private String companyLocation;
    
    @Column(name="company_description")
    private String companyDescription;
    
    @Column(name="company_phoneNumber")
    private String companyNumber;
    
    @Column(name="company_website")
    private String companyWebsite;
    
    @Lob
    @Column(name = "profile_image", columnDefinition = "LONGBLOB")
    private byte[] profileImage;
    
    // One-to-many with TraineeshipPosition
    @OneToMany(mappedBy="company", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<TraineeshipPosition> positions = new ArrayList<>();
}
