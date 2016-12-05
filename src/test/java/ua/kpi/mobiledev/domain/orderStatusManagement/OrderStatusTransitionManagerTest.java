package ua.kpi.mobiledev.domain.orderStatusManagement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Created by Oleg on 10.11.2016.
 */
public class OrderStatusTransitionManagerTest {

    private static OrderStatusManager orderStatusManager;

    private static User customerMock;

    private static TaxiDriver taxiDriverMock;

    private static Map<User.UserType, Map<Order.OrderStatus, Map<Order.OrderStatus, OrderStatusTransition>>> permittedTransitions;

    private Order mockOrder;

    @BeforeClass
    public static void beforeClass() throws Exception {
        permittedTransitions = new HashMap<>();
        permittedTransitions.put(User.UserType.CUSTOMER, customerTransitions());
        permittedTransitions.put(User.UserType.TAXI_DRIVER, taxiDriverTransition());
        orderStatusManager = new OrderStatusTransitionManager(permittedTransitions);
        customerMock = mockUserWithType(User.UserType.CUSTOMER);
        taxiDriverMock = (TaxiDriver) mockUserWithType(User.UserType.TAXI_DRIVER);
    }

    private static User mockUserWithType(User.UserType type) {
        User user = (type == User.UserType.CUSTOMER) ? mock(User.class) : mock(TaxiDriver.class);
        when(user.getUserType()).thenReturn(type);
        return user;
    }

    private static Map<Order.OrderStatus, Map<Order.OrderStatus, OrderStatusTransition>> customerTransitions() {
        Map<Order.OrderStatus, Map<Order.OrderStatus, OrderStatusTransition>> customerTransitions = new HashMap<>();
        Map<Order.OrderStatus, OrderStatusTransition> transitionFromStatusNew = new HashMap<>();
        transitionFromStatusNew.put(Order.OrderStatus.CANCELLED, mock(OrderStatusTransition.class));
        customerTransitions.put(Order.OrderStatus.NEW, transitionFromStatusNew);
        customerTransitions.put(Order.OrderStatus.ACCEPTED, transitionFromStatusNew); //the same transition strategy as with new
        return customerTransitions;
    }

    private static Map<Order.OrderStatus, Map<Order.OrderStatus, OrderStatusTransition>> taxiDriverTransition() {
        Map<Order.OrderStatus, Map<Order.OrderStatus, OrderStatusTransition>> res = new HashMap<>();

        Map<Order.OrderStatus, OrderStatusTransition> transitionFromStatusNew = new HashMap<>();
        transitionFromStatusNew.put(Order.OrderStatus.ACCEPTED, mock(OrderStatusTransition.class));

        Map<Order.OrderStatus, OrderStatusTransition> transitionFromAccepted = new HashMap<>();
        transitionFromAccepted.put(Order.OrderStatus.DONE, mock(OrderStatusTransition.class));
        transitionFromAccepted.put(Order.OrderStatus.NEW, mock(OrderStatusTransition.class));

        res.put(Order.OrderStatus.NEW, transitionFromStatusNew);
        res.put(Order.OrderStatus.ACCEPTED, transitionFromAccepted);
        return res;
    }

    @Before
    public void beforeTest() {
        mockOrder = mock(Order.class);
    }

    @Test
    public void changeOrderStatus_UserHasAccessAndChangesToPermittedStatus() throws Exception {
        when(mockOrder.getCustomer()).thenReturn(customerMock);
        when(mockOrder.getOrderStatus()).thenReturn(Order.OrderStatus.ACCEPTED);
        Order changedStatusOrder = mock(Order.class);
        when(changedStatusOrder.getOrderStatus()).thenReturn(Order.OrderStatus.CANCELLED);
        OrderStatusTransition toCancelTransition = permittedTransitions.get(User.UserType.CUSTOMER).get(Order.OrderStatus.ACCEPTED).get(Order.OrderStatus.CANCELLED);
        when(toCancelTransition.changeOrderStatus(mockOrder, customerMock))
                .thenReturn(changedStatusOrder);
        Assert.assertEquals(Order.OrderStatus.CANCELLED, orderStatusManager.changeOrderStatus(mockOrder, customerMock, Order.OrderStatus.CANCELLED).getOrderStatus());
        verify(mockOrder).getCustomer();
        verify(mockOrder).getOrderStatus();
        verify(toCancelTransition).changeOrderStatus(mockOrder, customerMock);

    }

    @Test(expected = IllegalStateException.class)
    public void changeOrderStatus_UserHasAccessAndChangesToProhibitedStatus() throws Exception {
        when(mockOrder.getOrderStatus()).thenReturn(Order.OrderStatus.ACCEPTED);
        when(mockOrder.getCustomer()).thenReturn(customerMock);
        orderStatusManager.changeOrderStatus(mockOrder, customerMock, Order.OrderStatus.NEW);
    }


    @Test
    public void changeOrderStatus_userIsOrderOwnerCheck() throws Exception {
        when(mockOrder.getOrderStatus()).thenReturn(Order.OrderStatus.NEW);
        when(mockOrder.getCustomer()).thenReturn(customerMock);
        //just check that won't throw exception, also need to be called with permitted transition status
        orderStatusManager.changeOrderStatus(mockOrder, customerMock, Order.OrderStatus.CANCELLED);
        verify(customerMock, atLeastOnce()).getUserType();
        verify(mockOrder).getCustomer();
        verify(mockOrder).getOrderStatus();
    }

    @Test
    public void changeOrderStatus_userIsDriverAndServicesOrder() throws Exception {
        when(mockOrder.getTaxiDriver()).thenReturn(taxiDriverMock);
        when(mockOrder.getOrderStatus()).thenReturn(Order.OrderStatus.ACCEPTED);
        orderStatusManager.changeOrderStatus(mockOrder, taxiDriverMock, Order.OrderStatus.DONE);
        verify(taxiDriverMock, atLeastOnce()).getUserType();
        verify(mockOrder).getTaxiDriver();
        verify(mockOrder).getOrderStatus();
    }

    @Test
    public void changeOrderStatus_userIsNewServicingDriver() throws Exception {
        when(mockOrder.getTaxiDriver()).thenReturn(null);
        when(mockOrder.getOrderStatus()).thenReturn(Order.OrderStatus.NEW);

        orderStatusManager.changeOrderStatus(mockOrder, taxiDriverMock, Order.OrderStatus.ACCEPTED);

        verify(taxiDriverMock, atLeastOnce()).getUserType();
        verify(mockOrder, atLeastOnce()).getTaxiDriver();
    }

    @Test
    public void changeOrderStatus_customerHasNoRights() throws Exception {
        when(mockOrder.getCustomer()).thenReturn(mock(User.class));
        try {
            orderStatusManager.changeOrderStatus(mockOrder, customerMock, Order.OrderStatus.CANCELLED);
        } catch (IllegalArgumentException e) {
            verify(customerMock, atLeastOnce()).getUserType();
            verify(mockOrder).getCustomer();
            return;
        }
        fail(); //TODO:change logic, remove try/catch
    }

    @Test
    public void changeOrderStatus_driverHasNoRights() throws Exception {
        when(mockOrder.getTaxiDriver()).thenReturn(mock(TaxiDriver.class));
        try {
            orderStatusManager.changeOrderStatus(mockOrder, taxiDriverMock, Order.OrderStatus.DONE);
        } catch (IllegalArgumentException e) {
            verify(taxiDriverMock, atLeastOnce()).getUserType();
            verify(mockOrder, atLeastOnce()).getTaxiDriver();
            return;
        }
        fail();
    }
}