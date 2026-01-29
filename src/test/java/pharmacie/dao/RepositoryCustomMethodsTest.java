package pharmacie.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pharmacie.entity.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class RepositoryCustomMethodsTest {




    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private MedicamentRepository medicamentRepository;


    @Test // Ce test se base uniquement sur les données définies dans data.sql
    public void testMedicamentCustomMethods() {
        Medicament indisponible = medicamentRepository.findByNom("Lévofloxacine 500mg").orElseThrow();
        Medicament disponible   = medicamentRepository.findByNom("Doliprane Effervescent 1g").orElseThrow();

        // Trouve tous les médicaments disponibles
        List<Medicament> disponibles = medicamentRepository.findByIndisponibleFalse();

        assertTrue(disponibles.contains(disponible));
        assertFalse(disponibles.contains(indisponible));
        assertFalse(disponibles.isEmpty());
    }

    @Test // Ce test crée les enregistrements nécessaires
    public void testCategorieCustomMethods() {
        Categorie c1 = new Categorie();
        c1.setLibelle("AnalgesiquesTest");
        categorieRepository.save(c1);

        Categorie c2 = new Categorie();
        c2.setLibelle("AntibiotiquesTest");
        categorieRepository.save(c2);

        // findByLibelle
        Categorie found = categorieRepository.findByLibelle("AnalgesiquesTest");
        assertNotNull(found);
        assertEquals("AnalgesiquesTest", found.getLibelle());

        // findByLibelleContaining
        List<Categorie> list = categorieRepository.findByLibelleContaining("iquesTest");
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(cat -> cat.getLibelle().equals("AntibiotiquesTest")));
        assertTrue(list.stream().anyMatch(cat -> cat.getLibelle().equals("AnalgesiquesTest")));
    }


    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private DispensaireRepository dispensaireRepository;

    @Test
    public void testCommandeCustomMethods() {
        // Test basé sur data.sql
        // On cherche les commandes après le 1er Février 2025 (devrait trouver la commande 2, mais pas la 1)
        List<Commande> commandesRecentes = commandeRepository.findBySaisieleAfter(LocalDate.of(2025, 2, 1));

        assertFalse(commandesRecentes.isEmpty());
        assertEquals(1, commandesRecentes.size());
        assertEquals("Marseille", commandesRecentes.get(0).getAdresseLivraison().getVille());
    }

    @Test
    public void testDispensaireCustomMethods() {
        // Test findByAdresseVille
        List<Dispensaire> dispensairesParis = dispensaireRepository.findByAdresseVille("Paris");
        assertEquals(1, dispensairesParis.size());
        assertEquals("Dispensaire du Centre", dispensairesParis.get(0).getNom());
    }
}
