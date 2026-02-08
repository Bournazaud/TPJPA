package pharmacie.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.dao.DataIntegrityViolationException;
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

    // --- TESTS DES CONTRAINTES ---

    @Test
    public void testIntegrityConstraints() {
        // 1. On ne peut pas supprimer une catégorie qui a des médicaments
        // La catégorie 1 (Antalgiques) a des médicaments dans data.sql
        Categorie catAvecMeds = categorieRepository.findById(1).orElseThrow();

        assertThrows(DataIntegrityViolationException.class, () -> {
            categorieRepository.delete(catAvecMeds);
            categorieRepository.flush(); // Force l'exécution SQL immédiate pour voir l'erreur
        });

        // 2. On peut supprimer une catégorie sans médicament
        Categorie vide = new Categorie();
        vide.setLibelle("Vide");
        vide = categorieRepository.save(vide);

        categorieRepository.delete(vide); // Ne doit pas planter
        assertTrue(categorieRepository.findById(vide.getCode()).isEmpty());
    }

    @Test
    public void testCascadeDispensaire() {
        // 3. Quand on supprime un dispensaire, on supprime ses commandes
        // Créons un dispensaire et une commande pour le test
        Dispensaire d = new Dispensaire("D_TEST", "TestDisp");
        d.setAdresse(new AdressePostale("Rue", "00000", "Ville"));
        dispensaireRepository.save(d);

        Commande c = new Commande();
        c.setDispensaire(d);
        c.setSaisiele(LocalDate.now());
        c.setAdresseLivraison(d.getAdresse());
        commandeRepository.save(c);

        Integer idCommande = c.getNumero();
        assertNotNull(commandeRepository.findById(idCommande).orElse(null));

        // Suppression du dispensaire
        dispensaireRepository.delete(d);

        // La commande doit avoir disparu
        assertTrue(commandeRepository.findById(idCommande).isEmpty());
    }

    // --- TESTS DES REQUETES ---

    @Test
    public void testNewQueries() {
        // Utilisons les données de data.sql
        // D01 a une commande (N°1) saisie le 10/01 mais pas de date d'envoi (donc "en cours")
        // D02 a une commande (N°2) saisie le 20/02... (ajoutons une date d'envoi pour tester le count)

        Commande c2 = commandeRepository.findById(2).orElseThrow();
        c2.setEnvoyeele(LocalDate.now()); // On dit qu'elle est envoyée
        // Ajoutons des lignes à la commande 2 pour avoir quelque chose à compter
        Ligne l = new Ligne();
        l.setQuantite(5);
        l.setMedicament(medicamentRepository.findById(1).orElseThrow()); // Morphine
        c2.getLignes().add(l);
        commandeRepository.save(c2);

        // Test : Commandes en cours pour D01
        List<Commande> enCoursD01 = commandeRepository.findByDispensaireCodeAndEnvoyeeleIsNull("D01");
        assertEquals(1, enCoursD01.size()); // La commande 1 est en cours

        // Test : Commandes en cours pour D02 (La commande 2 est envoyée, donc 0 en cours)
        List<Commande> enCoursD02 = commandeRepository.findByDispensaireCodeAndEnvoyeeleIsNull("D02");
        assertEquals(0, enCoursD02.size());

        // Test : Nombre d'articles envoyés pour D02
        Long nbArticles = commandeRepository.countArticlesEnvoyesByDispensaire("D02");
        assertEquals(5L, nbArticles); // La ligne qu'on vient d'ajouter
    }





}
