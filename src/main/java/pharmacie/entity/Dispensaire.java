package pharmacie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;
import java.util.LinkedList;

@Entity
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor @ToString
public class Dispensaire {
    @Id
    @NonNull // Génère l'argument 1 du constructeur
    @Column(length = 50)
    private String code;

    @NotBlank
    @NonNull
    private String nom;

    private String contact;
    private String fonction;
    private String telephone;
    private String fax;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "rue", column = @Column(name = "adresse_rue")),
        @AttributeOverride(name = "codePostal", column = @Column(name = "adresse_cp")),
        @AttributeOverride(name = "ville", column = @Column(name = "adresse_ville"))
    })
    private AdressePostale adresse;

    @OneToMany(mappedBy = "dispensaire", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Commande> commandes = new LinkedList<>();
}
