package ua.kpi.mobiledev.domain.orderStatusManagement;

import org.junit.Assert;
import org.junit.Test;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;

import static org.mockito.Mockito.mock;

/**
 * Created by Oleg on 07.11.2016.
 */
public class OrderStatusTransitionTest {

    @Test
    public void acceptOrderServicing() throws Exception {
        Order order = new Order(1L, null, null, null, null, null, 0.0, Order.OrderStatus.NEW, null);
        User mockUser = mock(TaxiDriver.class);
        OrderStatusTransition orderStatusTransition = new AcceptOrderServicing();
        Order actual = orderStatusTransition.changeOrderStatus(order, mockUser, Order.OrderStatus.ACCEPTED);
        Assert.assertEquals(actual.getTaxiDriver(), mockUser);
        Assert.assertEquals(Order.OrderStatus.ACCEPTED, actual.getOrderStatus());
    }

    @Test
    public void closeOrder() throws Exception {
        Order order = new Order(1L, null, null, null, null, null, 0.0, Order.OrderStatus.NEW, null);
        OrderStatusTransition orderStatusTransition = new CloseOrder();
        Assert.assertEquals(Order.OrderStatus.CANCELLED, orderStatusTransition.changeOrderStatus(order, null, null).getOrderStatus());
    }

    @Test
    public void refuseOrderServicing() throws Exception {
        Order order = new Order(1L, null, mock(TaxiDriver.class), null, null, null, 0.0, Order.OrderStatus.ACCEPTED, null);
        OrderStatusTransition orderStatusTransition = new RefuseOrderServicing();
        Order actual = orderStatusTransition.changeOrderStatus(order, mock(User.class), Order.OrderStatus.NEW);
        Assert.assertEquals(Order.OrderStatus.NEW, actual.getOrderStatus());
        Assert.assertNull(actual.getTaxiDriver());
    }

    @Test
    public void markAsDone() throws Exception {
        Order order = new Order(1L, null, mock(TaxiDriver.class), null, null, null, 0.0, Order.OrderStatus.ACCEPTED, null);
        OrderStatusTransition orderStatusTransition = new MarkOrderAsDone();
        Assert.assertEquals(Order.OrderStatus.DONE, orderStatusTransition.changeOrderStatus(order, null, null).getOrderStatus());
    }

}