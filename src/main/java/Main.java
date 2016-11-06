import ua.kpi.mobiledev.domain.Car;
import ua.kpi.mobiledev.domain.MobileNumber;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Oleg on 06.11.2016.
 */
public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        User user = new User(null, "name", "email", new ArrayList<>());
        user.getMobileNumbers().addAll(Arrays.asList(new MobileNumber(null, "+380935705681", user), new MobileNumber(null, "+380975106619", user)));
        System.out.println("user before persist = " + user);
        em.persist(user);
        em.flush();
        System.out.println("user after persist = " + user);
        System.out.println("user after find = " + em.find(User.class, user.getId()));
        transaction.commit();

        EntityTransaction transaction1 = em.getTransaction();
        transaction1.begin();
        TaxiDriver taxiDriver = new TaxiDriver(null, "name2", "email2", new ArrayList<>(), new Car(null, "model", "manufacturer", "plateNum", 6, Car.CarType.MINIBUS));
        taxiDriver.getMobileNumbers().addAll(Arrays.asList(new MobileNumber(null, "+380935705681(1)", user), new MobileNumber(null, "+380975106619(1)", taxiDriver)));
        System.out.println("taxiDriver before persist= " + taxiDriver);
        em.persist(taxiDriver);
        em.flush();
        System.out.println("taxiDriver after persist= " + taxiDriver);
        System.out.println("em.find(T) = " + em.find(TaxiDriver.class, taxiDriver.getId()));
        transaction1.commit();

        em.close();
        emf.close();
    }
}
