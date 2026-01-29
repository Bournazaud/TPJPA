package pharmacie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor @ToString
public class Dispensaire {
    @Id
    @NonNull
    @Column(length = 50)
    private String code;

    @NotBlank
    private String nom;

    private String contact;
    private String fonction;
    private String telephone;
    private String fax;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "rue", column = @Column(name = "adresse_rue")),
        // ICI : "name" doit correspondre exactement à la variable dans AdressePostale
        @AttributeOverride(name = "codePostal", column = @Column(name = "adresse_cp")),
        @AttributeOverride(name = "ville", column = @Column(name = "adresse_ville"))
    })
    private AdressePostale adresse;
}
