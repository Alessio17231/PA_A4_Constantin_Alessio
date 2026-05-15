package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.model.Question;

import java.util.List;

public class QuestionRepository {
    private static final String PERSISTENCE_UNIT_NAME = "quizGamePersistenceUnit";

    private EntityManagerFactory entityManagerFactory;

    public QuestionRepository() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    public void saveQuestion(Question questionToSave) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(questionToSave);
            entityManager.getTransaction().commit();
        } catch (RuntimeException exception) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }

    public void saveAllQuestions(List<Question> questionsToSave) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            for (Question questionToSave : questionsToSave) {
                entityManager.persist(questionToSave);
            }
            entityManager.getTransaction().commit();
        } catch (RuntimeException exception) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }

    public Question findQuestionById(Long questionId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(Question.class, questionId);
        } finally {
            entityManager.close();
        }
    }

    public List<Question> findAllQuestions() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            TypedQuery<Question> selectAllQuery = entityManager.createQuery(
                    "SELECT question FROM Question question", Question.class);
            return selectAllQuery.getResultList();
        } finally {
            entityManager.close();
        }
    }

    public long countQuestions() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            TypedQuery<Long> countQuery = entityManager.createQuery(
                    "SELECT COUNT(question) FROM Question question", Long.class);
            return countQuery.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    public void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}