package pharmacie.dao;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import pharmacie.entity.Medicament;

// Cette interface sera auto-implémentée par Spring
public interface MedicamentRepository extends JpaRepository<Medicament, Integer> {
    /**
     * Trouve un médicament à partir de son nom (unique dans Medicament)
     * @return un médicament "optionnel"
     */
    Optional<Medicament>findByNom(String nom);

    /**
     * Trouve les médicaments disponibles (indisponible = false)
     * @return la liste des médicaments disponibles
     */
    List<Medicament> findByIndisponibleFalse();

    // medicament diponibles pour une catégorie
    // les critères : catégorie qui est donnée + pas disponible + stock >= Commandé
    @Query("SELECT m FROM Medicament m WHERE m.categorie.code = :categorieCode AND m.indisponible = false AND m.unitesEnStock >= m.unitesCommandees")
    List<Medicament> findMedicamentByCategorie(Integer categorieCode);
}
