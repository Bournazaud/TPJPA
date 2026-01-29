package pharmacie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer numero;

    @NotNull
    private LocalDate saisiele;

    // Correction de la faute de frappe : envoyeele (avec 2 'e')
    private LocalDate envoyeele;

    private BigDecimal port;
    private String destinataire;
    private BigDecimal remise;

    // Relation vers Dispensaire (Qui passe la commande)
    @ManyToOne(optional = false)
    private Dispensaire dispensaire;

    // Composition avec Ligne
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "commande_id") // Crée une clé étrangère dans la table LIGNE
    private List<Ligne> lignes = new LinkedList<>();

    // C'EST ICI QU'IL MANQUAIT LES LIGNES IMPORTANTES :
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "rue", column = @Column(name = "livraison_rue")),
        @AttributeOverride(name = "codePostal", column = @Column(name = "livraison_cp")),
        @AttributeOverride(name = "ville", column = @Column(name = "livraison_ville"))
    })
    private AdressePostale adresseLivraison;
}
