package bank;

import bank.domain.Account;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.DatabaseCleaner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class JPATest {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bankPU");
    private EntityManager em;
    private DatabaseCleaner databaseCleaner;

    @BeforeClass
    public void init() {
        em = emf.createEntityManager();
        databaseCleaner = new DatabaseCleaner(em);
    }

    @Before
    public void initClean() {
        try {
            databaseCleaner.clean();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    @Test
    public void rollbackTest() {
        Account account = new Account(111L);
        em.getTransaction().begin();
        em.persist(account);
        assertNull(account.getId());
        em.getTransaction().rollback();
        // TODO code om te testen dat table account geen records bevat. Hint: bestudeer/gebruik AccountDAOJPAImpl

    }

    @Test
    public void flushTest() {
        Long expected = -100L;
        Account account = new Account(111L);
        account.setId(expected);
        em.getTransaction().begin();
        em.persist(account);
        //TODO: verklaar en pas eventueel aan
        //assertNotEquals(expected, account.getId();
        em.flush();
        //TODO: verklaar en pas eventueel aan
        //assertEquals(expected, account.getId();
        em.getTransaction().commit();
        //TODO: verklaar en pas eventueel aan

    }

    @Test
    public void editCorrectAfterPersistTest() {
        Long expectedBalance = 400L;
        Account account = new Account(114L);
        em.getTransaction().begin();
        em.persist(account);
        account.setBalance(expectedBalance);
        em.getTransaction().commit();
        assertEquals(expectedBalance, account.getBalance());
        //TODO: verklaar de waarde van account.getBalance
        Long cid = account.getId();
        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
        Account found = em2.find(Account.class, cid);
        //TODO: verklaar de waarde van found.getBalance
        assertEquals(expectedBalance, found.getBalance());

    }

    @Test
    public void editWrongAfterPersistTest() {
        //TODO Do the same as editCorrectAfterPersistTest() but now create incorrect object and test its incorrect equals
    }

    @Test
    public void mergeTest() {
        Account acc = new Account(1L);
        //noinspection UnusedAssignment
        Account acc2 = new Account(2L);
        //noinspection UnusedAssignment
        Account acc9 = new Account(9L);

        // scenario 1
        Long balance1 = 100L;
        em.getTransaction().begin();
        em.persist(acc);
        acc.setBalance(balance1);
        em.getTransaction().commit();
        //TODO: voeg asserties toe om je verwachte waarde van de attributen te verifieren.
        //TODO: doe dit zowel voor de bovenstaande java objecten als voor opnieuw bij de entitymanager opgevraagde objecten met overeenkomstig Id.


        // scenario 2
        Long balance2a = 211L;
        acc = new Account(2L);
        em.getTransaction().begin();
        acc9 = em.merge(acc);
        acc.setBalance(balance2a);
        acc9.setBalance(balance2a + balance2a);
        em.getTransaction().commit();
        //TODO: voeg asserties toe om je verwachte waarde van de attributen te verifiëren.
        //TODO: doe dit zowel voor de bovenstaande java objecten als voor opnieuw bij de entitymanager opgevraagde objecten met overeenkomstig Id.
        // HINT: gebruik acccountDAO.findByAccountNr


        // scenario 3
        Long balance3b = 322L;
        Long balance3c = 333L;
        acc = new Account(3L);
        em.getTransaction().begin();
        acc2 = em.merge(acc);
        assertTrue(em.contains(acc)); // verklaar
        assertTrue(em.contains(acc2)); // verklaar
        assertEquals(acc, acc2);  //verklaar
        acc2.setBalance(balance3b);
        acc.setBalance(balance3c);
        em.getTransaction().commit();
        //TODO: voeg asserties toe om je verwachte waarde van de attributen te verifiëren.
        //TODO: doe dit zowel voor de bovenstaande java objecten als voor opnieuw bij de entitymanager opgevraagde objecten met overeenkomstig Id.


        // scenario 4
        Account account = new Account(114L);
        account.setBalance(450L);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(account);
        em.getTransaction().commit();

        Account account2 = new Account(114L);
        Account tweedeAccountObject = account2;
        tweedeAccountObject.setBalance(650L);
        assertEquals((Long) 650L, account2.getBalance());  //verklaar
        account2.setId(account.getId());
        em.getTransaction().begin();
        account2 = em.merge(account2);
        assertSame(account, account2);  //verklaar
        assertTrue(em.contains(account2));  //verklaar
        assertFalse(em.contains(tweedeAccountObject));  //verklaar
        tweedeAccountObject.setBalance(850L);
        assertEquals((Long) 650L, account.getBalance());  //verklaar
        assertEquals((Long) 650L, account2.getBalance());  //verklaar
        em.getTransaction().commit();
        em.close();

    }

    @Test
    public void findAndClearTest() {
        Account acc1 = new Account(77L);
        em.getTransaction().begin();
        em.persist(acc1);
        em.getTransaction().commit();
        //Database bevat nu een account.

        // scenario 1
        Account accF1;
        Account accF2;
        accF1 = em.find(Account.class, acc1.getId());
        accF2 = em.find(Account.class, acc1.getId());
        assertSame(accF1, accF2);

        // scenario 2
        accF1 = em.find(Account.class, acc1.getId());
        em.clear();
        accF2 = em.find(Account.class, acc1.getId());
        assertSame(accF1, accF2);
        //TODO verklaar verschil tussen beide scenario’s

    }

    @Test
    public void removeTest() {
        Account acc1 = new Account(88L);
        em.getTransaction().begin();
        em.persist(acc1);
        em.getTransaction().commit();
        Long id = acc1.getId();
        //Database bevat nu een account.

        em.remove(acc1);
        assertEquals(id, acc1.getId());
        Account accFound = em.find(Account.class, id);
        assertNull(accFound);
        //TODO: verklaar bovenstaande asserts
    }


}