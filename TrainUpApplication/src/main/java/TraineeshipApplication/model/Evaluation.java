package TraineeshipApplication.model;

import lombok.*;
import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name="evaluations")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // For example, you might define different types of evaluations:
    @Enumerated(EnumType.STRING)
    private EvaluationType evaluationType;

    private int motivation;
    private int efficiency;
    private int effectiveness;

    // Who is being evaluated. Many evaluations belong to one TraineeshipPosition
    @ManyToOne
    @JoinColumn(name="traineeship_position_id")
    private TraineeshipPosition traineeshipPosition;
}
