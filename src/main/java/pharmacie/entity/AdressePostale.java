package pharmacie.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data @NoArgsConstructor @AllArgsConstructor
public class AdressePostale {

    @Size(max = 255)
    private String rue;

    @Size(max = 10)
    private String codePostal; // Attention au P majuscule !

    @NotBlank
    @Size(max = 255)
    private String ville;
}
