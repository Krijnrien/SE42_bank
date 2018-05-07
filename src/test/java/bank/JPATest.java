package bank;

import bank.domain.Account;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.Assert.*;

public class JPATest {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bankPU");
    EntityManager em;

    @Before
    public void initialize() {
        em = emf.createEntityManager();
    }

    @Test
    public void persistenceTest() {
        Account account = new Account(111L);
        em.getTransaction().begin();
        em.persist(account);

        //TODO: verklaar en pas eventueel aan
        assertNull(account.getId());
        em.getTransaction().commit();
        System.out.println("AccountId: " + account.getId());
//TODO: verklaar en pas eventueel aan
        assertTrue(account.getId() > 0L);

    }

}