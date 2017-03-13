package ua.kpi.mobiledev.domain.orderStatusManagement;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.exception.RequestException;
import ua.kpi.mobiledev.testCategories.UnitTest;

import java.text.MessageFormat;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
@Category(UnitTest.class)
public class OrderStatusTransitionManagerTest {

    private static OrderStatusManager orderStatusManager;

    @Mock
    private User customerBeforeChangingStatus;
    @Mock
    private User customerAfterChangingStatus;
    @Mock
    private TaxiDriver driverBeforeChangingStatus;
    @Mock
    private TaxiDriver driverAfterChangingStatus;
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @BeforeClass
    public static void beforeClass() throws Exception {
        orderStatusManager = new OrderStatusTransitionManager();
    }

    @Before
    public void beforeTest() {
        when(customerBeforeChangingStatus.getUserType()).thenReturn(User.UserType.CUSTOMER);
        when(customerAfterChangingStatus.getUserType()).thenReturn(User.UserType.CUSTOMER);
        when(driverBeforeChangingStatus.getUserType()).thenReturn(User.UserType.TAXI_DRIVER);
        when(driverAfterChangingStatus.getUserType()).thenReturn(User.UserType.TAXI_DRIVER);
    }

    @Test
    @Parameters({
            //id = 1 - customer's id
            //id = 2 - driver's id
            //id = -1 - should be null
            "NEW,CANCELLED,1,1,-1,-1,1",              //user cancelled new order
            "ACCEPTED,CANCELLED,1,1,2,2,1",           //user cancelled accepted order by driver
            "NEW,ACCEPTED,1,1,-1,2,2",                //driver accepted order for servicing
            "ACCEPTED,NEW,1,1,2,-1,2",                //driver declined order processing
            "ACCEPTED,DONE,1,1,2,2,2"                 //driver finished order processing
    })
    public void testValidTransitions(String currentStatus, String nextStatus,
                                     Integer customerIdBefore, Integer customerIdAfter,
                                     Integer driverIdBefore, Integer driverIdAfter,
                                     Integer userIdChangingStatus) throws Exception {
        //given
        customerBeforeChangingStatus = updateUserId(customerBeforeChangingStatus, processId(customerIdBefore));
        customerAfterChangingStatus = updateUserId(customerAfterChangingStatus, processId(customerIdAfter));
        driverBeforeChangingStatus = (TaxiDriver) updateUserId(driverBeforeChangingStatus, processId(driverIdBefore));
        driverAfterChangingStatus = (TaxiDriver) updateUserId(driverAfterChangingStatus, processId(driverIdAfter));
        Order order = initOrder(currentStatus, customerBeforeChangingStatus, driverBeforeChangingStatus);
        Order.OrderStatus targetStatus = orderStatusOf(nextStatus);

        //when
        Order changedOrder = orderStatusManager.changeOrderStatus(order, getChangingStatusUser(userIdChangingStatus), targetStatus);

        //then
        assertEquals(processMessage(targetStatus, changedOrder), targetStatus, changedOrder.getOrderStatus());
        checkUser(customerAfterChangingStatus, changedOrder.getCustomer());
        checkUser(driverAfterChangingStatus, changedOrder.getTaxiDriver());
    }

    private void checkUser(User expectedUser, User actualUser) {
        if (Objects.isNull(expectedUser)) {
            assertNull(actualUser);
        } else {
            assertEquals(expectedUser.getId(), actualUser.getId());
        }
    }

    @Parameters({
            "NEW,ACCEPTED",
            "NEW,DONE",
            "NEW,NEW",
            "ACCEPTED,NEW",
            "ACCEPTED,DONE",
            "DONE,NEW",
            "DONE,ACCEPTED",
            "DONE,CANCELLED",
            "CANCELLED,NEW",
            "CANCELLED,ACCEPTED",
            "CANCELLED,DONE",
            "CANCELLED,CANCELLED",
    })
    @Test(expected = RequestException.class)
    public void testCustomerIllegalTransitions(String currentStatus, String nextStatus) throws Exception {
        //given
        customerBeforeChangingStatus = updateUserId(customerBeforeChangingStatus, 1);
        Order order = initOrder(currentStatus, customerBeforeChangingStatus, driverBeforeChangingStatus);
        Order.OrderStatus targetOrderStatus = orderStatusOf(nextStatus);

        //when
        Order changedOrder = orderStatusManager.changeOrderStatus(order, customerBeforeChangingStatus, targetOrderStatus);

        //then IllegalStateException is thrown
    }

    @Parameters({
            "NEW,NEW",
            "NEW,CANCELLED",
            "ACCEPTED,CANCELLED",
            "DONE,NEW",
            "DONE,ACCEPTED",
            "DONE,CANCELLED",
            "CANCELLED,NEW",
            "CANCELLED,ACCEPTED",
            "CANCELLED,DONE",
            "CANCELLED,CANCELLED",
    })
    @Test(expected = RequestException.class)
    public void testTaxiDriverIllegalTransitions(String currentStatus, String nextStatus) throws Exception {
        //given
        customerBeforeChangingStatus = updateUserId(customerBeforeChangingStatus, 1);
        driverBeforeChangingStatus = (TaxiDriver) updateUserId(driverBeforeChangingStatus, 2);
        Order order = initOrder(currentStatus, customerBeforeChangingStatus, driverBeforeChangingStatus);
        Order.OrderStatus targetOrderStatus = orderStatusOf(nextStatus);

        //when
        Order changedOrder = orderStatusManager.changeOrderStatus(order, driverBeforeChangingStatus, targetOrderStatus);

        //then IllegalStateException is thrown
    }

    @Test(expected = RequestException.class)
    public void testCustomerIsNotOrderOwner() throws Exception {
        //given
        Order order = initOrder("NEW", updateUserId(customerBeforeChangingStatus, 1),
                mock(TaxiDriver.class));
        User actualUser = updateUserId(mock(User.class), 2);

        //when
        orderStatusManager.changeOrderStatus(order, actualUser, Order.OrderStatus.ACCEPTED);

        //then throw IllegalArgumentException
    }


    @Test(expected = RequestException.class)
    public void testTaxiDriverCantServiceOrder() throws Exception {
        //given
        customerBeforeChangingStatus = updateUserId(customerBeforeChangingStatus, 1);
        driverBeforeChangingStatus = (TaxiDriver) updateUserId(driverBeforeChangingStatus, 2);
        Order order = initOrder("NEW", customerBeforeChangingStatus, driverBeforeChangingStatus);
        TaxiDriver actualTaxiDriver = (TaxiDriver) updateUserId(mock(TaxiDriver.class), 3);

        //when
        orderStatusManager.changeOrderStatus(order, actualTaxiDriver, Order.OrderStatus.ACCEPTED);

        //then throw IllegalArgumentException
    }

    private Integer processId(Integer id) {
        return (id == -1) ? null : id;
    }

    private String processMessage(Order.OrderStatus targetStatus, Order changedOrder) {
        return MessageFormat.format("status is not as expected, should be ''{0}'', but was ''{1}''",
                targetStatus, changedOrder.getOrderStatus());
    }

    private Order initOrder(String currentStatus, User customerBeforeChangingStatus, TaxiDriver driverBeforeChangingStatus) {
        Order order = new Order();
        order.setOrderStatus(orderStatusOf(currentStatus));
        order.setCustomer(customerBeforeChangingStatus);
        order.setTaxiDriver(driverBeforeChangingStatus);
        return order;
    }

    private Order.OrderStatus orderStatusOf(String orderStatusName) {
        return Order.OrderStatus.valueOf(orderStatusName);
    }

    private User updateUserId(User targetUser, Integer userId) {
        if (userId == null) {
            return null;
        }
        when(targetUser.getId()).thenReturn(userId);
        return targetUser;
    }

    private User getChangingStatusUser(Integer id) {
        if (id == null) {
            fail("there's no id of user that is changing status");
        }
        if (Objects.nonNull(customerBeforeChangingStatus) && Objects.equals(id, customerBeforeChangingStatus.getId())) {
            return customerBeforeChangingStatus;
        }
        if (Objects.nonNull(driverBeforeChangingStatus) && Objects.equals(id, driverBeforeChangingStatus.getId())) {
            return driverBeforeChangingStatus;
        }
        if (Objects.equals(driverAfterChangingStatus.getId(), id)) {
            return driverAfterChangingStatus;
        }
        fail("There's no user with id = " + id);
        return null;
    }

}