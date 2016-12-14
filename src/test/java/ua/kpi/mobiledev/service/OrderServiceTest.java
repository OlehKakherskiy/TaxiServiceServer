package ua.kpi.mobiledev.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.kpi.mobiledev.domain.Car;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.domain.dto.OrderDto;
import ua.kpi.mobiledev.domain.dto.OrderPriceDto;
import ua.kpi.mobiledev.domain.orderStatusManagement.OrderStatusManager;
import ua.kpi.mobiledev.domain.orderStatusManagement.OrderStatusTransitionManager;
import ua.kpi.mobiledev.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private static LocalDateTime NOW;

    private static User MOCK_USER;

    @BeforeClass
    public static void beforeClass() {
        NOW = LocalDateTime.now();
        MOCK_USER = mock(User.class);
    }

    @Test
    public void addOrder() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        OrderDto orderDto = new OrderDto(1, now, "start", "end", new OrderPriceDto(5.0, Collections.emptyList()), 0.0);
        User mockUser = mock(User.class);
        Order targetOrder = new Order(null, mockUser, null, now, "start", "end", 25.0, Order.OrderStatus.NEW, Collections.emptySet());
        Order expectedOrder = new Order(1L, mockUser, null, now, "start", "end", 25.0, Order.OrderStatus.NEW, Collections.emptySet());

        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.save(targetOrder)).thenReturn(expectedOrder);
        UserService userService = mock(UserService.class);
        when(userService.getById(1)).thenReturn(mockUser);

        TransactionalOrderService orderService = new TransactionalOrderService(orderRepository, userService, Collections.emptyMap(), mock(OrderStatusTransitionManager.class));
        orderService.setKmPrice(5);
        Assert.assertEquals(expectedOrder, orderService.addOrder(orderDto));
        verify(userService).getById(1);
        verify(orderRepository).save(targetOrder);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addOrder_TaxiDriverIsOwner() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        OrderDto orderDto = new OrderDto(1, now, "start", "end", mock(OrderPriceDto.class), 0.0);

        UserService userService = mock(UserService.class);
        when(userService.getById(1)).thenReturn(new TaxiDriver(1, "", "", Collections.emptySet(), mock(Car.class)));

        OrderService orderService = new TransactionalOrderService(mock(OrderRepository.class), userService, Collections.emptyMap(), mock(OrderStatusTransitionManager.class));
        orderService.addOrder(orderDto);
    }

    @Test
    public void changeOrderStatus() throws Exception {
        User mockUser = mock(User.class);
        Order mockOrder = mock(Order.class);
        Order updatedOrder = mock(Order.class);
        Order.OrderStatus mockStatus = Order.OrderStatus.NEW;

        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.findOne(1L)).thenReturn(mockOrder);
        when(orderRepository.save(updatedOrder)).thenReturn(updatedOrder);

        UserService userService = mock(UserService.class);
        when(userService.getById(1)).thenReturn(mockUser);
        OrderStatusTransitionManager transitionManager = mock(OrderStatusTransitionManager.class);
        when(transitionManager.changeOrderStatus(mockOrder, mockUser, mockStatus)).thenReturn(updatedOrder);

        OrderService orderService = new TransactionalOrderService(orderRepository, userService, Collections.emptyMap(), transitionManager);
        Assert.assertNotNull(orderService.changeOrderStatus(1L, 1, mockStatus));

        verify(orderRepository).findOne(1L);
        verify(orderRepository).save(updatedOrder);
        verify(userService).getById(1);
        verify(transitionManager).changeOrderStatus(mockOrder, mockUser, mockStatus);
    }

    @Test(expected = IllegalStateException.class)
    public void changeOrderStatus_incompatibleState() throws Exception {
        User mockUser = mock(User.class);
        Order mockOrder = mock(Order.class);
        Order.OrderStatus mockStatus = Order.OrderStatus.NEW;

        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.findOne(1L)).thenReturn(mockOrder);

        UserService userService = mock(UserService.class);
        when(userService.getById(1)).thenReturn(mockUser);
        OrderStatusTransitionManager transitionManager = mock(OrderStatusTransitionManager.class);
        when(transitionManager.changeOrderStatus(mockOrder, mockUser, mockStatus)).thenThrow(IllegalStateException.class);

        OrderService orderService = new TransactionalOrderService(orderRepository, userService, Collections.emptyMap(), transitionManager);
        orderService.changeOrderStatus(1L, 1, mockStatus);
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeOrderStatus_userHasNoRights() throws Exception {
        User mockUser = mock(User.class);
        Order mockOrder = mock(Order.class);
        Order.OrderStatus mockStatus = Order.OrderStatus.NEW;

        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.findOne(1L)).thenReturn(mockOrder);

        UserService userService = mock(UserService.class);
        when(userService.getById(1)).thenReturn(mockUser);
        OrderStatusTransitionManager transitionManager = mock(OrderStatusTransitionManager.class);
        when(transitionManager.changeOrderStatus(mockOrder, mockUser, mockStatus)).thenThrow(IllegalArgumentException.class);

        OrderService orderService = new TransactionalOrderService(orderRepository, userService, Collections.emptyMap(), transitionManager);
        orderService.changeOrderStatus(1L, 1, mockStatus);
    }

    @Test
    public void getOrderList_FilterByOrderStatus() throws Exception {
        Order.OrderStatus mockStatus = Order.OrderStatus.CANCELLED;
        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.getAllByOrderStatus(mockStatus)).thenReturn(Arrays.asList(mock(Order.class), mock(Order.class)));
        OrderService orderService = new TransactionalOrderService(orderRepository, mock(UserService.class), Collections.emptyMap(), mock(OrderStatusTransitionManager.class));
        Assert.assertEquals(2, orderService.getOrderList(mockStatus).size());
        verify(orderRepository).getAllByOrderStatus(mockStatus);
    }

    @Test
    public void getOrderList_ignoringOrderStatus() throws Exception {
        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.findAll()).thenReturn(Arrays.asList(mock(Order.class), mock(Order.class)));
        OrderService orderService = new TransactionalOrderService(orderRepository, mock(UserService.class), Collections.emptyMap(), mock(OrderStatusTransitionManager.class));
        Assert.assertEquals(2, orderService.getOrderList(null).size());
        verify(orderRepository).findAll();
    }

    @Test
    public void getOrder() throws Exception {
        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.findOne(1L)).thenReturn(mock(Order.class));

        OrderService orderService = new TransactionalOrderService(orderRepository, mock(UserService.class), Collections.emptyMap(), mock(OrderStatusTransitionManager.class));
        Assert.assertNotNull(orderService.getOrder(1L));
        verify(orderRepository).findOne(1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOrder_NoOrderWithId() throws Exception {
        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.findOne(1L)).thenReturn(null);
        OrderService orderService = new TransactionalOrderService(orderRepository, mock(UserService.class),
                Collections.emptyMap(), mock(OrderStatusTransitionManager.class));
        orderService.getOrder(1L);
    }

    @Test
    public void deleteOrder() throws Exception {
        Order order = mock(Order.class);
        User mockCustomer = mock(User.class);
        when(mockCustomer.getId()).thenReturn(1);
        when(order.getCustomer()).thenReturn(mockCustomer);

        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.findOne(1L)).thenReturn(order);
        OrderService orderService = new TransactionalOrderService(orderRepository, null, null, null);

        Assert.assertTrue(orderService.deleteOrder(1L, 1));
        verify(order).getCustomer();
        verify(mockCustomer).getId();
        verify(orderRepository).findOne(1L);
        verify(orderRepository).delete(order);
    }

    @Test
    public void deleteOrder_UserHasNoRights() throws Exception {
        Order order = mock(Order.class);
        User mockCustomer = mock(User.class);
        when(mockCustomer.getId()).thenReturn(2);
        when(order.getCustomer()).thenReturn(mockCustomer);

        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.findOne(1L)).thenReturn(order);
        OrderService orderService = new TransactionalOrderService(orderRepository, null, null, null);

        Assert.assertFalse(orderService.deleteOrder(1L, 1));
        verify(order).getCustomer();
        verify(mockCustomer).getId();
        verify(orderRepository).findOne(1L);
    }


    @Test
    public void updateOrder_willBeChanged() throws Exception {
        OrderDto orderDto = new OrderDto(1, NOW, "start", "end", new OrderPriceDto(5.0, null), null);
        Order mockOrder = new Order(1L, MOCK_USER, null, NOW, "start", "end", 10.0, Order.OrderStatus.NEW, Collections.emptySet());
        Order expectedOrder = new Order(1L, MOCK_USER, null, NOW, "start", "end", 10.0, Order.OrderStatus.NEW, Collections.emptySet());

        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.findOne(1L)).thenReturn(mockOrder);
        when(orderRepository.save(mockOrder)).thenReturn(mockOrder);

        TransactionalOrderService orderService = new TransactionalOrderService(orderRepository, mock(UserService.class), Collections.emptyMap(), mock(OrderStatusManager.class));
        orderService.setKmPrice(2);
        Assert.assertEquals(expectedOrder, orderService.updateOrder(1L, orderDto));
        verify(orderRepository).findOne(1L);
        verify(orderRepository).save(mockOrder);
    }

    @Test
    public void calculatePrice_calculateWithoutAddParams() throws Exception {
        OrderPriceDto orderPrice = new OrderPriceDto(5.0, Collections.emptyList());
        TransactionalOrderService orderService = new TransactionalOrderService(null, null, null, null);
        orderService.setKmPrice(5);
        Assert.assertEquals(25.0, orderService.calculatePrice(orderPrice), 1e-7);
    }
}