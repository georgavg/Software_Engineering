package TraineeshipApplication.model;

import lombok.*;
import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name="traineeship_positions")
public class TraineeshipPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")   // ⬅ ADD THIS
    private LocalDate fromDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")   // ⬅ ADD THIS
    private LocalDate toDate;
    
    @ElementCollection
    @CollectionTable(name="traineeship_position_topics", joinColumns=@JoinColumn(name="position_id"))
    @Column(name="topic")
    private List<String> topics = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name="traineeship_position_skills", joinColumns=@JoinColumn(name="position_id"))
    @Column(name="skill")
    private List<String> skills = new ArrayList<>();

    @Transient
    private String topicsAsString;

    @Transient
    private String skillsAsString;

    @Column(name="is_assigned")
    private boolean assigned;          // Flag for assigned vs. open

    @Column(name="student_logbook")
    private String studentLogbook;

    @Column(name="pass_fail_grade", nullable = true )
    private boolean passFailGrade;

    // If you prefer the Student to own the relationship, remove this 
    // and rely on Student.assignedTraineeship. Otherwise, you can 
    // make it bidirectional by referencing Student here:
    @OneToOne(mappedBy="assignedTraineeship")
    @ToString.Exclude
    private Student student;

    // Many positions per company
    @ManyToOne
    @JoinColumn(name="company_id")
    private Company company;

    // Many positions per professor
    @ManyToOne
    @JoinColumn(name="professor_id")
    private Professor supervisor;

    // One-to-many with Evaluation
    @OneToMany(mappedBy = "traineeshipPosition", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Evaluation> evaluations = new ArrayList<>();
    
    
    public String getTopicsAsString() {
        if (topics.isEmpty()) return "";
        return String.join(", ", topics);
    }

    public void setTopicsAsString(String input) {
        topics.clear();
        if (input != null && !input.trim().isEmpty()) {
            for (String t : input.split("\\s*,\\s*")) {
                topics.add(t.trim());
            }
        }
    }

    public String getSkillsAsString() {
        if (skills.isEmpty()) return "";
        return String.join(", ", skills);
    }

    public void setSkillsAsString(String input) {
        skills.clear();
        if (input != null && !input.trim().isEmpty()) {
            for (String s : input.split("\\s*,\\s*")) {
                skills.add(s.trim());
            }
        }
    }

}
