package ua.kpi.mobiledev.domain.orderStatusManagement;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.testCategories.UnitTest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static ua.kpi.mobiledev.domain.Order.OrderStatus.ACCEPTED;
import static ua.kpi.mobiledev.domain.Order.OrderStatus.CANCELLED;
import static ua.kpi.mobiledev.domain.Order.OrderStatus.NEW;
import static ua.kpi.mobiledev.domain.Order.OrderStatus.PROCESSING;
import static ua.kpi.mobiledev.domain.Order.OrderStatus.WAITING;

@Category(UnitTest.class)
public class OrderStatusTransitionTest {

    @Test
    public void acceptOrderServicing() {
        Order order = new Order();
        order.setOrderStatus(NEW);
        TaxiDriver driver = new TaxiDriver();
        OrderStatusTransition orderStatusTransition = new AcceptOrderServicing();
        Order actual = orderStatusTransition.changeOrderStatus(order, driver);
        assertEquals(actual.getTaxiDriver(), driver);
        assertEquals(ACCEPTED, actual.getOrderStatus());
    }

    @Test
    public void closeOrder() {
        Order order = new Order();
        order.setOrderStatus(NEW);
        OrderStatusTransition orderStatusTransition = new CloseOrder();
        assertEquals(CANCELLED, orderStatusTransition.changeOrderStatus(order, null).getOrderStatus());
    }

    @Test
    public void refuseOrderServicing() {
        Order order = new Order();
        order.setOrderStatus(NEW);
        OrderStatusTransition orderStatusTransition = new RefuseOrderServicing();
        Order actual = orderStatusTransition.changeOrderStatus(order, mock(User.class));
        assertEquals(NEW, actual.getOrderStatus());
        assertNull(actual.getTaxiDriver());
    }

    @Test
    public void markAsDone() {
        Order order = new Order();
        order.setOrderStatus(ACCEPTED);
        order.setTaxiDriver(new TaxiDriver());
        OrderStatusTransition orderStatusTransition = new MarkOrderAsDone();
        assertEquals(Order.OrderStatus.DONE, orderStatusTransition.changeOrderStatus(order, null).getOrderStatus());
    }

    @Test
    public void markAsWaiting() {
        Order order = new Order();
        order.setOrderStatus(ACCEPTED);
        order.setTaxiDriver(new TaxiDriver());
        OrderStatusTransition orderStatusTransition = new MarkAsWaiting();
        assertThat(orderStatusTransition.changeOrderStatus(order, null).getOrderStatus(), is(WAITING));
    }

    @Test
    public void markAsProcessing() {
        Order order = new Order();
        order.setOrderStatus(WAITING);
        order.setTaxiDriver(new TaxiDriver());
        OrderStatusTransition orderStatusTransition = new MarkAsProcessing();
        assertThat(orderStatusTransition.changeOrderStatus(order, null).getOrderStatus(), is(PROCESSING));
    }

}