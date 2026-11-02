
package com.test.edc.taskmanager.task_manager_api.test;

import com.test.edc.taskmanager.task_manager_api.dto.TaskRequest;
import com.test.edc.taskmanager.task_manager_api.entity.Task;
import com.test.edc.taskmanager.task_manager_api.entity.TaskPriority;
import com.test.edc.taskmanager.task_manager_api.entity.TaskStatus;
import com.test.edc.taskmanager.task_manager_api.repository.TaskRepository;
import com.test.edc.taskmanager.task_manager_api.service.implementation.TaskService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires de la classe TaskService.
 *
 * Objectif :
 * Vérifier le bon fonctionnement des opérations CRUD
 * sans communiquer avec une véritable base de données.
 *
 * Technologies :
 * - JUnit 5 : exécution et organisation des tests.
 * - Mockito : simulation des dépendances.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    /**
     * Simulation du repository.
     *
     * @Mock permet de créer un faux objet TaskRepository.
     * Les méthodes du repository ne communiquent pas
     * réellement avec la base de données.
     */
    @Mock
    private TaskRepository repository;

    /**
     * Instance réelle du service à tester.
     *
     * @InjectMocks injecte automatiquement le mock repository
     * dans le constructeur de TaskService.
     */
    @InjectMocks
    private TaskService service;

    /**
     * Objet utilisé comme donnée d'entrée
     * dans les différents tests.
     */
    private TaskRequest request;

    /**
     * Méthode exécutée avant chaque test.
     *
     * @BeforeEach permet de préparer un environnement
     * indépendant pour chaque méthode de test.
     */
    @BeforeEach
    void setUp() {

        // Préparation d'une requête de création de tâche.
        request = new TaskRequest(
                "Développer le dashboard",
                "Interface de gestion",
                TaskStatus.TODO,
                TaskPriority.HIGH,
                null
        );
    }

    /**
     * TEST 1 : Vérifier la création d'une tâche.
     *
     * Scénario :
     * - Le service reçoit une requête.
     * - Il construit une nouvelle tâche.
     * - Il demande au repository de sauvegarder la tâche.
     * - Le repository retourne la tâche sauvegardée.
     */
    @Test
    void shouldCreateTask() {

        // ARRANGE : configuration du comportement du repository.
        // Mockito retourne l'objet reçu lors de l'appel à save().
        when(repository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT : exécution de la méthode à tester.
        Task result = service.create(request);

        // ASSERT : vérification du résultat obtenu.
        assertNotNull(result);
        assertEquals("Développer le dashboard", result.getTitle());
        assertEquals("Interface de gestion", result.getDescription());
        assertEquals(TaskStatus.TODO, result.getStatus());
        assertEquals(TaskPriority.HIGH, result.getPriority());

        // Vérification que save() a été appelé exactement une fois.
        verify(repository, times(1)).save(any(Task.class));
    }

    /**
     * TEST 2 : Vérifier la recherche d'une tâche par son identifiant.
     *
     * Scénario :
     * - Une tâche existe avec l'identifiant 1.
     * - Le repository retourne cette tâche.
     * - Le service doit retourner la tâche correspondante.
     */
    @Test
    void shouldFindTaskById() {

        // ARRANGE : création manuelle d'une tâche de test.
        // On ne passe pas par service.create(), car ce test
        // concerne uniquement la recherche par identifiant.
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Développer le dashboard");
        task.setDescription("Interface de gestion");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.HIGH);

        // Simulation du résultat de findById().
        // Le repository retourne une Optional contenant la tâche.
        when(repository.findById(1L))
                .thenReturn(Optional.of(task));

        // ACT : recherche de la tâche par son identifiant.
        Task result = service.findById(1L);

        // ASSERT : vérification de la tâche retournée.
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Développer le dashboard", result.getTitle());
        assertEquals(TaskStatus.TODO, result.getStatus());

        // Vérification de l'appel au repository.
        verify(repository, times(1)).findById(1L);
    }

    /**
     * TEST 3 : Vérifier la gestion d'une tâche inexistante.
     *
     * Scénario :
     * - L'utilisateur recherche une tâche avec l'identifiant 999.
     * - Le repository ne trouve aucune tâche.
     * - Le service doit déclencher une exception HTTP 404.
     */
    @Test
    void shouldThrow404WhenTaskDoesNotExist() {

        // ARRANGE : simulation d'une recherche sans résultat.
        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        // ACT + ASSERT :
        // Vérification que la recherche déclenche une exception.
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findById(999L)
        );

        // Vérification du statut HTTP retourné.
        assertEquals(404, exception.getStatusCode().value());

        // Vérification de l'appel au repository.
        verify(repository, times(1)).findById(999L);
    }

    /**
     * TEST 4 : Vérifier la modification d'une tâche existante.
     *
     * Scénario :
     * - Une tâche existe déjà avec l'identifiant 1.
     * - Le service récupère cette tâche.
     * - Il modifie ses propriétés avec les nouvelles données.
     * - Il sauvegarde la tâche modifiée.
     */
    @Test
    void shouldUpdateTask() {

        // ARRANGE : création d'une tâche existante.
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Ancien titre");
        task.setDescription("Ancienne description");

        // Simulation de la récupération de la tâche.
        when(repository.findById(1L))
                .thenReturn(Optional.of(task));

        // Simulation de la sauvegarde de la tâche modifiée.
        when(repository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT : modification de la tâche.
        Task result = service.update(1L, request);

        // ASSERT : vérification des nouvelles valeurs.
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Développer le dashboard", result.getTitle());
        assertEquals("Interface de gestion", result.getDescription());
        assertEquals(TaskStatus.TODO, result.getStatus());
        assertEquals(TaskPriority.HIGH, result.getPriority());

        // Vérification que le repository a sauvegardé la tâche.
        verify(repository, times(1)).save(task);

        // Vérification que la tâche a été recherchée une seule fois.
        verify(repository, times(1)).findById(1L);
    }

    /**
     * TEST 5 : Vérifier la suppression d'une tâche.
     *
     * Scénario :
     * - Une tâche existe avec l'identifiant 1.
     * - Le service récupère cette tâche.
     * - Le service demande au repository de la supprimer.
     */
    @Test
    void shouldDeleteTask() {

        // ARRANGE : création d'une tâche existante.
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Tâche à supprimer");

        // Simulation de la récupération de la tâche.
        when(repository.findById(1L))
                .thenReturn(Optional.of(task));

        // ACT : suppression de la tâche.
        service.delete(1L);

        // ASSERT : vérification de la suppression.
        verify(repository, times(1)).delete(task);

        // Vérification que la tâche a été recherchée.
        verify(repository, times(1)).findById(1L);
    }
}
