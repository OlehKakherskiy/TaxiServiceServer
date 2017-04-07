package ua.kpi.mobiledev.domain.orderStatusManagement;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.testCategories.UnitTest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ua.kpi.mobiledev.domain.Order.OrderStatus.ACCEPTED;
import static ua.kpi.mobiledev.domain.Order.OrderStatus.NEW;

@Category(UnitTest.class)
@Ignore
public class OrderStatusTransitionTest {

    @Test
    public void acceptOrderServicing() throws Exception {
        Order order = mock(Order.class);
        when(order.getOrderStatus()).thenReturn(NEW);
        User mockUser = mock(TaxiDriver.class);
        OrderStatusTransition orderStatusTransition = new AcceptOrderServicing();
        Order actual = orderStatusTransition.changeOrderStatus(order, mockUser);
        Assert.assertEquals(actual.getTaxiDriver(), mockUser);
        Assert.assertEquals(Order.OrderStatus.ACCEPTED, actual.getOrderStatus());
    }

    @Test
    public void closeOrder() throws Exception {
        Order order = mock(Order.class);
        when(order.getOrderStatus()).thenReturn(NEW);
        OrderStatusTransition orderStatusTransition = new CloseOrder();
        Assert.assertEquals(Order.OrderStatus.CANCELLED, orderStatusTransition.changeOrderStatus(order, null).getOrderStatus());
    }

    @Test
    public void refuseOrderServicing() throws Exception {
        Order order = mock(Order.class);
        when(order.getOrderStatus()).thenReturn(NEW);
        OrderStatusTransition orderStatusTransition = new RefuseOrderServicing();
        Order actual = orderStatusTransition.changeOrderStatus(order, mock(User.class));
        Assert.assertEquals(NEW, actual.getOrderStatus());
        Assert.assertNull(actual.getTaxiDriver());
    }

    @Test
    public void markAsDone() throws Exception {
        Order order = mock(Order.class);
        when(order.getOrderStatus()).thenReturn(ACCEPTED);
        when(order.getTaxiDriver()).thenReturn(any(TaxiDriver.class));
        OrderStatusTransition orderStatusTransition = new MarkOrderAsDone();
        Assert.assertEquals(Order.OrderStatus.DONE, orderStatusTransition.changeOrderStatus(order, null).getOrderStatus());
    }

}