package pharmacie.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pharmacie.entity.Commande;
import java.time.LocalDate;
import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Integer> {

    // Trouve les commandes saisies après une Date donnée
    List<Commande> findBySaisieleAfter(LocalDate date);

    // Trouve toute les commandes en cours pour un dispensaire
    List<Commande> findByDispensaireCodeAndEnvoyeeleIsNull(String codeDispensaire);

    // Calculer le nombre d'articles commandés envoyés pour un dispensaire
    // On somme les quantités des lignes des commandes de ce dispensaire qui ont une date d'envoi
    @Query("SELECT SUM(l.quantite) FROM Commande c JOIN c.lignes l WHERE c.dispensaire.code = :code AND c.envoyeele IS NOT NULL")
    Long countArticlesEnvoyesByDispensaire(@Param("code") String codeDispensaire);
}
