package pharmacie.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pharmacie.entity.Commande;
import java.time.LocalDate;
import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Integer> {
    // Trouve les commandes saisies apres une Date donnée
    List<Commande> findBySaisieleAfter(LocalDate date);
}
