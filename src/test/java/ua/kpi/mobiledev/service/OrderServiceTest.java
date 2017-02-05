package ua.kpi.mobiledev.service;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ua.kpi.mobiledev.domain.*;
import ua.kpi.mobiledev.domain.additionalRequirements.CarTypeAdditionalRequirement;
import ua.kpi.mobiledev.domain.dto.AddReqSimpleDto;
import ua.kpi.mobiledev.domain.dto.OrderDto;
import ua.kpi.mobiledev.domain.dto.OrderPriceDto;
import ua.kpi.mobiledev.domain.orderStatusManagement.OrderStatusTransitionManager;
import ua.kpi.mobiledev.repository.OrderRepository;
import ua.kpi.mobiledev.testCategories.UnitTest;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    private static LocalDateTime NOW = LocalDateTime.now();
    private static final Map<Integer, AdditionalRequirement> additionalRequirementMap = new HashMap<>();

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserService userService;
    @Mock
    private OrderStatusTransitionManager transitionManager;
    @Mock
    private User customer;
    @Mock
    private TaxiDriver taxiDriver;

    private OrderService orderServiceUnderTest;

    @BeforeClass
    public static void initAddRequirements() {
        additionalRequirementMap.put(1, new CarTypeAdditionalRequirement("", "",
                getCarTypes(), getMultiplyCoefficient()));
    }

    private static Map<Integer, String> getCarTypes() {
        Map<Integer, String> carTypes = new HashMap<>();
        carTypes.put(1, "TRUCK");
        carTypes.put(2, "PASSENGER_CAR");
        carTypes.put(3, "MINIBUS");
        return carTypes;
    }

    private static Map<Car.CarType, Double> getMultiplyCoefficient() {
        Map<Car.CarType, Double> multiplyCoefficient = new HashMap<>();
        multiplyCoefficient.put(Car.CarType.TRUCK, 3.0);
        multiplyCoefficient.put(Car.CarType.PASSENGER_CAR, 1.0);
        multiplyCoefficient.put(Car.CarType.MINIBUS, 2.0);
        return multiplyCoefficient;
    }

    @Before
    public void initOrderService() {
        orderServiceUnderTest = new TransactionalOrderService(orderRepository, userService, transitionManager);
        ((TransactionalOrderService) orderServiceUnderTest).setKmPrice(5);
        ((TransactionalOrderService) orderServiceUnderTest).setAdditionalRequirements(additionalRequirementMap);
    }

    @Test
    public void addValidOrderByCustomer() {
        //given
        OrderDto orderDto = new OrderDto(1, NOW, "start", "end",
                new OrderPriceDto(5.0, Collections.emptyList()), 0.0);
        Order orderBeforeSaveOperation = new Order(null, customer, null, NOW,
                "start", "end", 25.0, Order.OrderStatus.NEW, Collections.emptySet());
        Order expectedOrder = new Order(1L, customer, null, NOW,
                "start", "end", 25.0, Order.OrderStatus.NEW, Collections.emptySet());

        //when
        when(orderRepository.save(orderBeforeSaveOperation)).thenReturn(expectedOrder);
        when(userService.getById(anyInt())).thenReturn(customer);
        Order actual = orderServiceUnderTest.addOrder(orderDto);

        //then
        assertEquals(expectedOrder, actual);
        verify(userService, times(1)).getById(anyInt());
        verify(orderRepository, times(1)).save(orderBeforeSaveOperation);
        verifyNoMoreInteractions(orderRepository, userService, transitionManager);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addOrderByTaxiDriver() throws Exception {
        //given
        OrderDto orderDto = new OrderDto(1, NOW, "start", "end",
                new OrderPriceDto(5.0, Collections.emptyList()), 0.0);
        when(taxiDriver.getUserType()).thenReturn(User.UserType.TAXI_DRIVER);
        when(userService.getById(1)).thenReturn(taxiDriver);

        //when
        orderServiceUnderTest.addOrder(orderDto);

        //then IllegalArgumentException is thrown
    }

    @Test(expected = NullPointerException.class)
    public void addOrderWithoutPriceInfo() throws Exception {
        //given
        OrderDto orderDto = new OrderDto(1, NOW, "start", "end",
                null, 0.0);

        //when
        orderServiceUnderTest.addOrder(orderDto);

        //then NPE is thrown
    }

    @Test
    public void changeOrderStatus() throws Exception {
        //given
        Order.OrderStatus mockStatus = Order.OrderStatus.NEW;
        initOrderRepositoryAndUserServiceForChangeOrderStatus();
        when(transitionManager.changeOrderStatus(any(), any(), eq(mockStatus))).thenReturn(mock(Order.class));

        //when
        Order updatedOrder = orderServiceUnderTest.changeOrderStatus(1L, 1, mockStatus);

        //then
        assertNotNull(updatedOrder);
        verify(orderRepository, times(1)).findOne(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(userService, times(1)).getById(anyInt());
        verify(transitionManager).changeOrderStatus(any(), any(), eq(mockStatus));
        verifyNoMoreInteractions(orderRepository, userService, transitionManager);
    }

    @Test(expected = IllegalStateException.class)
    public void changeOrderStatusToIncompatibleState() throws Exception {
        //given
        initOrderRepositoryAndUserServiceForChangeOrderStatus();

        //when
        doThrow(IllegalStateException.class).when(transitionManager).changeOrderStatus(any(), any(), any());
        orderServiceUnderTest.changeOrderStatus(1L, 1, Order.OrderStatus.ACCEPTED);

        //then IllegalStateException is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeOrderStatusWhileUserHasNoRights() throws Exception {
        //given
        initOrderRepositoryAndUserServiceForChangeOrderStatus();

        //when
        doThrow(IllegalArgumentException.class).when(transitionManager).changeOrderStatus(any(), any(), any());
        orderServiceUnderTest.changeOrderStatus(1L, 1, Order.OrderStatus.ACCEPTED);

        //then IllegalArgumentException is thrown
    }

    private void initOrderRepositoryAndUserServiceForChangeOrderStatus() {
        when(orderRepository.findOne(anyLong())).thenReturn(mock(Order.class));
        when(orderRepository.save(any(Order.class))).thenReturn(mock(Order.class));
        when(userService.getById(anyInt())).thenReturn(mock(User.class));
    }

    @Test
    public void getOrderListFilteredByOrderStatus() {
        //given
        Order.OrderStatus targetStatus = Order.OrderStatus.NEW;

        //when
        when(orderRepository.getAllByOrderStatus(targetStatus)).thenReturn(Arrays.asList(mock(Order.class), mock(Order.class)));
        List<Order> resultList = orderServiceUnderTest.getOrderList(targetStatus);

        //then
        assertEquals(2, resultList.size());
        verify(orderRepository, times(1)).getAllByOrderStatus(targetStatus);
        verifyNoMoreInteractions(orderRepository, userService, transitionManager);
    }

    @Test
    public void getOrderListIgnoringOrderStatus() {
        //given
        Order.OrderStatus targetStatus = null; //ALL orders should be returned

        //when
        when(orderRepository.findAll()).thenReturn(Arrays.asList(mock(Order.class), mock(Order.class)));
        List<Order> resultList = orderServiceUnderTest.getOrderList(targetStatus);

        //then
        assertEquals(2, resultList.size());
        verify(orderRepository, times(1)).findAll();
        verifyNoMoreInteractions(orderRepository, userService, transitionManager);
    }

    @Test
    public void getExistingOrderById() throws Exception {
        //given
        Long orderId = 1L;

        //when
        when(orderRepository.findOne(anyLong())).thenReturn(mock(Order.class));
        Order resultOrder = orderServiceUnderTest.getOrder(orderId);

        //then
        assertNotNull(resultOrder);
        verify(orderRepository, times(1)).findOne(orderId);
        verifyNoMoreInteractions(orderRepository, userService, transitionManager);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOrderWithNotExistingId() throws Exception {
        //given
        Long notExistingOrderId = 1L;

        //when
        when(orderRepository.findOne(anyLong())).thenReturn(null);
        Order resultOrder = orderServiceUnderTest.getOrder(notExistingOrderId);

        //then IllegalArgumentException is thrown
    }

    @Test
    public void deleteExistingOrderWithAppropriateRights() throws Exception {
        //given
        Order order = mock(Order.class);

        //when
        when(customer.getId()).thenReturn(1);
        when(order.getCustomer()).thenReturn(customer);
        when(orderRepository.findOne(anyLong())).thenReturn(order);
        boolean deleteResult = orderServiceUnderTest.deleteOrder(1L, 1);

        //then
        assertTrue(deleteResult);
        verify(order, times(1)).getCustomer();
        verify(customer, times(1)).getId();
        verify(orderRepository, times(1)).findOne(1L);
        verify(orderRepository, times(1)).delete(order);
        verifyNoMoreInteractions(customer, orderRepository, userService, transitionManager);
    }

    @Test
    public void deleteExistingOrderWithNoUserRights() throws Exception {
        //given
        Order order = mock(Order.class);

        //when
        when(customer.getId()).thenReturn(2);
        when(order.getCustomer()).thenReturn(customer);
        when(orderRepository.findOne(anyLong())).thenReturn(order);
        boolean deleteResult = orderServiceUnderTest.deleteOrder(1L, 1);

        //then
        assertFalse(deleteResult);
        verify(order).getCustomer();
        verify(customer).getId();
        verify(orderRepository).findOne(1L);
        verifyNoMoreInteractions(customer, orderRepository, userService, transitionManager);
    }


    @Test
    public void updateOrderFromValidDto() throws Exception {
        //given
        LocalDateTime updateTime = LocalDateTime.now();
        OrderDto orderDto = new OrderDto(1, updateTime, "start", "end",
                new OrderPriceDto(5.0, null), null);
        Order orderBeforeUpdate = new Order(1L, customer, null, NOW, "start_before", "end_before",
                20.0, Order.OrderStatus.NEW, Collections.emptySet());
        Order updatedOrder = new Order(1L, customer, null, updateTime, "start", "end",
                25.0, Order.OrderStatus.NEW, Collections.emptySet());
        Order expectedOrder = new Order(1L, customer, null, updateTime, "start", "end",
                25.0, Order.OrderStatus.NEW, Collections.emptySet());

        //when
        when(orderRepository.findOne(1L)).thenReturn(orderBeforeUpdate);
        when(orderRepository.save(updatedOrder)).thenReturn(updatedOrder);
        Order actualOrder = orderServiceUnderTest.updateOrder(1L, orderDto);

        //then
        assertEquals(expectedOrder, actualOrder);
        verify(orderRepository, times(1)).findOne(1L);
        verify(orderRepository, times(1)).save(updatedOrder);
        verifyNoMoreInteractions(orderRepository, userService, transitionManager);
    }

    @Test
    public void calculatePriceWithoutAdditionalParams() {
        //given
        OrderPriceDto orderPrice = new OrderPriceDto(5.0, Collections.emptyList());

        //when
        Double actualPrice = orderServiceUnderTest.calculatePrice(orderPrice);

        //then
        assertEquals(25.0, actualPrice, 1e-7);
        verifyNoMoreInteractions(orderRepository, userService, transitionManager);
    }

    @Test
    public void calculatePriceWithAdditionalParams() {
        //given
        OrderPriceDto orderPrice = new OrderPriceDto(5.0,
                Collections.singletonList(new AddReqSimpleDto(1, 1)));

        //when
        Double actualPrice = orderServiceUnderTest.calculatePrice(orderPrice);

        //then
        assertEquals(100.0, actualPrice, 1e-7);
        verifyNoMoreInteractions(orderRepository, userService, transitionManager);
    }

    @Test
    public void calculatePriceWhenDtoIsNull() {
        //given
        OrderPriceDto orderPriceDto = null;

        //when
        Double actualPrice = orderServiceUnderTest.calculatePrice(orderPriceDto);

        //then
        assertEquals(0.0, actualPrice, 1e-7);
    }
}