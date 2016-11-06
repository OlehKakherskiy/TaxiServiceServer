package ua.kpi.mobiledev.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import ua.kpi.mobiledev.domain.*;
import ua.kpi.mobiledev.domain.dto.OrderDto;
import ua.kpi.mobiledev.repository.OrderRepository;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * Created by Oleg on 06.11.2016.
 */
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
        OrderDto orderDto = new OrderDto(1, now, "start", "end", Collections.emptyMap());
        User mockUser = mock(User.class);
        Order targetOrder = new Order(null, mockUser, null, now, "start", "end", 0.0, Order.OrderStatus.NEW, Collections.emptyMap());
        Order expectedOrder = new Order(1L, mockUser, null, now, "start", "end", 0.0, Order.OrderStatus.NEW, Collections.emptyMap());

        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.save(targetOrder)).thenReturn(expectedOrder);
        UserService userService = mock(UserService.class);
        when(userService.getUser(1)).thenReturn(mockUser);

        OrderService orderService = new TransactionalOrderService(orderRepository, userService, Collections.emptySet(), mock(OrderStatusTransitionManager.class));
        Assert.assertEquals(expectedOrder, orderService.addOrder(orderDto));
        verify(userService).getUser(1);
        verify(orderRepository).save(targetOrder);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addOrder_TaxiDriverIsOwner() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        OrderDto orderDto = new OrderDto(1, now, "start", "end", Collections.emptyMap());

        UserService userService = mock(UserService.class);
        when(userService.getUser(1)).thenReturn(new TaxiDriver(1, "", "", Collections.emptyList(), mock(Car.class)));

        OrderService orderService = new TransactionalOrderService(mock(OrderRepository.class), userService, Collections.emptySet(), mock(OrderStatusTransitionManager.class));
        orderService.addOrder(orderDto);
    }


    @Test
    public void changeOrderStatus() throws Exception {
        User mockUser = mock(User.class);
        Order mockOrder = mock(Order.class);
        Order.OrderStatus mockStatus = Order.OrderStatus.NEW;

        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.findOne(1L)).thenReturn(mockOrder);

        UserService userService = mock(UserService.class);
        when(userService.getUser(1)).thenReturn(mockUser);
        OrderStatusTransitionManager transitionManager = mock(OrderStatusTransitionManager.class);
        when(transitionManager.changeOrderStatus(mockOrder, mockUser, mockStatus)).thenReturn(mock(Order.class));

        OrderService orderService = new TransactionalOrderService(orderRepository, userService, Collections.emptySet(), transitionManager);
        Assert.assertNotNull(orderService.changeOrderStatus(1L, 1, mockStatus));

        verify(orderRepository).findOne(1L);
        verify(userService).getUser(1);
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
        when(userService.getUser(1)).thenReturn(mockUser);
        OrderStatusTransitionManager transitionManager = mock(OrderStatusTransitionManager.class);
        when(transitionManager.changeOrderStatus(mockOrder, mockUser, mockStatus)).thenThrow(IllegalStateException.class);

        OrderService orderService = new TransactionalOrderService(orderRepository, userService, Collections.emptySet(), transitionManager);
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
        when(userService.getUser(1)).thenReturn(mockUser);
        OrderStatusTransitionManager transitionManager = mock(OrderStatusTransitionManager.class);
        when(transitionManager.changeOrderStatus(mockOrder, mockUser, mockStatus)).thenThrow(IllegalArgumentException.class);

        OrderService orderService = new TransactionalOrderService(orderRepository, userService, Collections.emptySet(), transitionManager);
        orderService.changeOrderStatus(1L, 1, mockStatus);
    }

    @Test
    public void getOrderList() throws Exception {
        Order.OrderStatus mockStatus = Order.OrderStatus.CANCELLED;
        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.getAllByOrderStatus(mockStatus)).thenReturn(Arrays.asList(mock(Order.class), mock(Order.class)));
        OrderService orderService = new TransactionalOrderService(orderRepository, mock(UserService.class), Collections.emptySet(), mock(OrderStatusTransitionManager.class));
        Assert.assertEquals(2, orderService.getOrderList(mockStatus).size());
        verify(orderRepository).getAllByOrderStatus(mockStatus);
    }

    @Test
    public void getOrder() throws Exception {
        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.findOne(1L)).thenReturn(mock(Order.class));

        OrderService orderService = new TransactionalOrderService(orderRepository, mock(UserService.class), Collections.emptySet(), mock(OrderStatusTransitionManager.class));
        Assert.assertNotNull(orderService.getOrder(1L));
        verify(orderRepository).findOne(1L);
    }

    @Test
    @Ignore
    public void deleteOrder() throws Exception {
    }

    @Test
    public void updateOrder_nothingToChange() throws Exception {
        OrderDto orderDto = new OrderDto(null, null, null, null, null);
        Order mockOrder = new Order(1L, MOCK_USER, null, NOW, "", "", 1.0, Order.OrderStatus.NEW, Collections.emptyMap());
        Order expectedOrder = new Order(1L, MOCK_USER, null, NOW, "", "", 1.0, Order.OrderStatus.NEW, Collections.emptyMap());

        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.findOne(1L)).thenReturn(mockOrder);
        when(orderRepository.save(mockOrder)).thenReturn(mockOrder);

        OrderService orderService = new TransactionalOrderService(orderRepository, mock(UserService.class), Collections.emptySet(), mock(OrderStatusManager.class));
        Assert.assertEquals(expectedOrder, orderService.updateOrder(1L, orderDto));
        verify(orderRepository).findOne(1L);
        verify(orderRepository).save(mockOrder);
    }

    @Test
    public void updateOrder_willBeChanged() throws Exception {
        OrderDto orderDto = new OrderDto(null, NOW, "start", "end", null);
        Order mockOrder = new Order(1L, MOCK_USER, null, NOW, "", "", 1.0, Order.OrderStatus.NEW, Collections.emptyMap());
        Order expectedOrder = new Order(1L, MOCK_USER, null, NOW, "start", "end", 1.0, Order.OrderStatus.NEW, Collections.emptyMap());

        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.findOne(1L)).thenReturn(mockOrder);
        when(orderRepository.save(mockOrder)).thenReturn(mockOrder);

        OrderService orderService = new TransactionalOrderService(orderRepository, mock(UserService.class), Collections.emptySet(), mock(OrderStatusManager.class));
        Assert.assertEquals(expectedOrder, orderService.updateOrder(1L, orderDto));
        verify(orderRepository).findOne(1L);
        verify(orderRepository).save(mockOrder);
    }

}