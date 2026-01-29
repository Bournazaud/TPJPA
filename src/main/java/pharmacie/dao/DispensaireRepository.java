package pharmacie.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pharmacie.entity.Dispensaire;
import java.util.List;

public interface DispensaireRepository extends JpaRepository<Dispensaire, String> {
    // Trouve tout les dispensaires d'une ville donnée
    List<Dispensaire> findByAdresseVille(String Ville);
}
