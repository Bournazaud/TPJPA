package pharmacie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Ligne {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Positive
    private int quantite;

    @ManyToOne (optional = false)
    private Medicament medicament;
}
