package ua.kpi.mobiledev.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.Order.OrderStatus;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.domain.dto.AddReqSimpleDto;
import ua.kpi.mobiledev.domain.dto.OrderDto;
import ua.kpi.mobiledev.domain.dto.OrderPriceDto;
import ua.kpi.mobiledev.domain.orderStatusManagement.OrderStatusTransitionManager;
import ua.kpi.mobiledev.exception.ForbiddenOperationException;
import ua.kpi.mobiledev.exception.ResourceNotFoundException;
import ua.kpi.mobiledev.repository.OrderRepository;
import ua.kpi.mobiledev.testCategories.UnitTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static ua.kpi.mobiledev.domain.Order.OrderStatus.ACCEPTED;
import static ua.kpi.mobiledev.domain.Order.OrderStatus.NEW;
import static ua.kpi.mobiledev.domain.User.UserType.CUSTOMER;

@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class TransactionalOrderServiceTest {

    private static final int CUSTOMER_ID = 1;
    private static final int DRIVER_ID = 2;
    private static LocalDateTime NOW = LocalDateTime.now();
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserService userService;
    @Mock
    private OrderStatusTransitionManager transitionManager;
    private User customer;
    private TaxiDriver taxiDriver;

    private TransactionalOrderService orderService;

//    @BeforeClass
//    public static void initAddRequirements() {
//        additionalRequirementMap.put(1, new CarTypeAdditionalRequirement("", "",
//                getCarTypes(), getMultiplyCoefficient()));
//    }
//
//    private static Map<Integer, String> getCarTypes() {
//        Map<Integer, String> carTypes = new HashMap<>();
//        carTypes.put(1, "TRUCK");
//        carTypes.put(2, "PASSENGER_CAR");
//        carTypes.put(3, "MINIBUS");
//        return carTypes;
//    }
//
//    private static Map<CarType, Double> getMultiplyCoefficient() {
//        Map<CarType, Double> multiplyCoefficient = new HashMap<>();
//        multiplyCoefficient.put(TRUCK, 3.0);
//        multiplyCoefficient.put(PASSENGER_CAR, 1.0);
//        multiplyCoefficient.put(MINIBUS, 2.0);
//        return multiplyCoefficient;
//    }

    @Before
    public void initOrderService() {
        orderService = new TransactionalOrderService(orderRepository, userService, transitionManager);
        orderService.setKmPrice(5);
//        orderService.setAdditionalRequirements(additionalRequirementMap);

        customer = new User(CUSTOMER_ID, null, null, CUSTOMER, null);
        taxiDriver = new TaxiDriver(DRIVER_ID, null, null, null, null, null);
    }

//    @Test
//    public void addValidOrderByCustomer() {
//        //given
//        OrderDto orderDto = new OrderDto(1, NOW, "start", "end",
//                new OrderPriceDto(5.0, Collections.emptyList()), 0.0);
//        Order orderBeforeSaveOperation = new Order(null, customer, null, NOW,
//                "start", "end", 25.0, NEW, Collections.emptySet());
//        Order expectedOrder = new Order(1L, customer, null, NOW,
//                "start", "end", 25.0, NEW, Collections.emptySet());
//
//        //when
//        when(orderRepository.save(orderBeforeSaveOperation)).thenReturn(expectedOrder);
//        when(userService.getById(anyInt())).thenReturn(customer);
//        Order actual = orderService.addOrder(orderDto);
//
//        //then
//        assertEquals(expectedOrder, actual);
//        verify(userService).getById(anyInt());
//        verify(orderRepository).save(orderBeforeSaveOperation);
//        verifyNoMoreInteractions(orderRepository, userService, transitionManager);
//    }

    @Test(expected = ForbiddenOperationException.class)
    public void addOrderByTaxiDriver() throws Exception {
        //given
        OrderDto orderDto = new OrderDto(1, NOW, "start", "end",
                new OrderPriceDto(5.0, Collections.emptyList()), 0.0);
        when(userService.getById(1)).thenReturn(taxiDriver);

        //when
        orderService.addOrder(orderDto);

        //then ForbiddenOperationException is thrown
    }

    @Test(expected = NullPointerException.class)
    public void addOrderWithoutPriceInfo() throws Exception {
        //given
        OrderDto orderDto = new OrderDto(1, NOW, "start", "end",
                null, 0.0);

        //when
        orderService.addOrder(orderDto);

        //then NPE is thrown
    }

    @Test
    public void changeOrderStatus() throws Exception {
        //given
        OrderStatus mockStatus = NEW;
        initOrderRepositoryAndUserServiceForChangeOrderStatus();
        when(transitionManager.changeOrderStatus(any(), any(), eq(mockStatus))).thenReturn(mock(Order.class));

        //when
        Order updatedOrder = orderService.changeOrderStatus(1L, 1, mockStatus);

        //then
        assertNotNull(updatedOrder);
        verify(orderRepository).findOne(anyLong());
        verify(orderRepository).save(any(Order.class));
        verify(userService).getById(anyInt());
        verify(transitionManager).changeOrderStatus(any(), any(), eq(mockStatus));
        verifyNoMoreInteractions(orderRepository, userService, transitionManager);
    }

    @Test(expected = IllegalStateException.class)
    public void changeOrderStatusToIncompatibleState() throws Exception {
        //given
        initOrderRepositoryAndUserServiceForChangeOrderStatus();

        //when
        doThrow(IllegalStateException.class).when(transitionManager).changeOrderStatus(any(), any(), any());
        orderService.changeOrderStatus(1L, 1, ACCEPTED);

        //then IllegalStateException is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeOrderStatusWhileUserHasNoRights() throws Exception {
        //given
        initOrderRepositoryAndUserServiceForChangeOrderStatus();

        //when
        doThrow(IllegalArgumentException.class).when(transitionManager).changeOrderStatus(any(), any(), any());
        orderService.changeOrderStatus(1L, 1, ACCEPTED);

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
        OrderStatus targetStatus = NEW;

        //when
        when(orderRepository.getAllByOrderStatus(targetStatus)).thenReturn(Arrays.asList(mock(Order.class), mock(Order.class)));
        List<Order> resultList = orderService.getOrderList(targetStatus);

        //then
        assertEquals(2, resultList.size());
        verify(orderRepository).getAllByOrderStatus(targetStatus);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void shouldReturnOrdersOfAllStatusesWhenTargetStatusIsNull() {
        //given
        OrderStatus targetStatus = null; //ALL orders should be returned

        //when
        when(orderRepository.findAll()).thenReturn(Arrays.asList(mock(Order.class), mock(Order.class)));
        List<Order> resultList = orderService.getOrderList(targetStatus);

        //then
        assertEquals(2, resultList.size());
        verify(orderRepository).findAll();
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void getExistingOrderById() throws Exception {
        //given
        Long orderId = 1L;

        //when
        when(orderRepository.findOne(anyLong())).thenReturn(mock(Order.class));
        Order resultOrder = orderService.getOrder(orderId);

        //then
        assertNotNull(resultOrder);
        verify(orderRepository).findOne(orderId);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getOrderWithNotExistingId() throws Exception {
        //given
        Long notExistingOrderId = 1L;

        //when
        when(orderRepository.findOne(anyLong())).thenReturn(null);
        orderService.getOrder(notExistingOrderId);

        //then ResourceNotFoundException is thrown
    }

    @Test
    public void deleteExistingOrderWithAppropriateRights() throws Exception {
        //given
        Order order = mock(Order.class);

        //when
        when(userService.getById(CUSTOMER_ID)).thenReturn(customer);
        when(order.getCustomer()).thenReturn(customer);
        when(orderRepository.findOne(anyLong())).thenReturn(order);
        orderService.deleteOrder(1L, CUSTOMER_ID);

        //then
        verify(userService).getById(CUSTOMER_ID);
        verify(orderRepository).findOne(1L);
        verify(orderRepository).delete(order);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test(expected = ForbiddenOperationException.class)
    public void shouldThrowForbiddenExceptionWhenTaxiDriverDeletesOrder() {
        //given
        Order order = mock(Order.class);

        //when
        when(userService.getById(anyInt())).thenReturn(taxiDriver);

        orderService.deleteOrder(1L, 2);
    }

    @Test(expected = ForbiddenOperationException.class)
    public void shouldThrowForbiddenExceptionWhenUserIsNotOrderOwner() throws Exception {
        //given
        Order order = mock(Order.class);
        User user = mock(User.class);

        //when
        when(user.getUserType()).thenReturn(CUSTOMER);
        when(user.getId()).thenReturn(3);
        when(userService.getById(3)).thenReturn(user);
        when(orderRepository.findOne(1L)).thenReturn(order);
        when(order.getCustomer()).thenReturn(customer);
        orderService.deleteOrder(1L, 3);

        //then
        verify(orderRepository).findOne(1L);
        verify(userService).getById(2);
        verifyNoMoreInteractions(orderRepository);
    }


//    @Test
//    public void updateOrderFromValidDto() throws Exception {
//        //given
//        LocalDateTime updateTime = LocalDateTime.now();
//        OrderDto orderDto = new OrderDto(1, updateTime, "start", "end",
//                new OrderPriceDto(5.0, null), null);
//        Order orderBeforeUpdate = new Order(1L, customer, null, NOW, "start_before", "end_before",
//                20.0, NEW, Collections.emptySet());
//        Order updatedOrder = new Order(1L, customer, null, updateTime, "start", "end",
//                25.0, NEW, Collections.emptySet());
//        Order expectedOrder = new Order(1L, customer, null, updateTime, "start", "end",
//                25.0, NEW, Collections.emptySet());
//
//        //when
//        when(orderRepository.findOne(1L)).thenReturn(orderBeforeUpdate);
//        when(orderRepository.save(updatedOrder)).thenReturn(updatedOrder);
//        Order actualOrder = orderService.updateOrder(1L, 1, orderDto);
//
//        //then
//        assertEquals(expectedOrder, actualOrder);
//        verify(orderRepository).findOne(1L);
//        verify(orderRepository).save(updatedOrder);
//        verifyNoMoreInteractions(orderRepository, userService, transitionManager);
//    }

    @Test
    public void calculatePriceWithoutAdditionalParams() {
        //given
        OrderPriceDto orderPrice = new OrderPriceDto(5.0, Collections.emptyList());

        //when
        Double actualPrice = orderService.calculatePrice(orderPrice);

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
        Double actualPrice = orderService.calculatePrice(orderPrice);

        //then
        assertEquals(100.0, actualPrice, 1e-7);
        verifyNoMoreInteractions(orderRepository, userService, transitionManager);
    }

    @Test
    public void calculatePriceWhenDtoIsNull() {
        //given
        OrderPriceDto orderPriceDto = null;

        //when
        Double actualPrice = orderService.calculatePrice(orderPriceDto);

        //then
        assertEquals(0.0, actualPrice, 1e-7);
    }
}