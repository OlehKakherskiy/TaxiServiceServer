package ua.kpi.mobiledev.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ua.kpi.mobiledev.domain.Address;
import ua.kpi.mobiledev.domain.Car;
import ua.kpi.mobiledev.domain.DriverLicense;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.Order.OrderStatus;
import ua.kpi.mobiledev.domain.RoutePoint;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.domain.orderStatusManagement.OrderStatusTransitionManager;
import ua.kpi.mobiledev.domain.priceCalculationManagement.PriceCalculationManager;
import ua.kpi.mobiledev.exception.ForbiddenOperationException;
import ua.kpi.mobiledev.exception.ResourceNotFoundException;
import ua.kpi.mobiledev.repository.OrderRepository;
import ua.kpi.mobiledev.service.googlemaps.GoogleMapsClientService;
import ua.kpi.mobiledev.service.googlemaps.GoogleMapsRouteResponse;
import ua.kpi.mobiledev.testCategories.UnitTest;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.MIN;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ua.kpi.mobiledev.domain.Car.CarType.PASSENGER_CAR;
import static ua.kpi.mobiledev.domain.Order.OrderStatus.ACCEPTED;
import static ua.kpi.mobiledev.domain.Order.OrderStatus.NEW;
import static ua.kpi.mobiledev.domain.Order.PaymentMethod.CASH;
import static ua.kpi.mobiledev.domain.User.UserType.CUSTOMER;

@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class TransactionalOrderServiceTest {

    private static final int CUSTOMER_ID = 1;
    private static final int DRIVER_ID = 2;
    private static final LocalDateTime NOW = now();
    private static final long ORDER_ID = 1L;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserService userService;
    @Mock
    private OrderStatusTransitionManager transitionManager;
    @Mock
    private PriceCalculationManager priceCalculationManager;
    @Mock
    private GoogleMapsClientService googleMapsClientService;
    @Mock
    private Address startPointAddressMock;
    @Mock
    private Address endPointAddressMock;
    @Mock
    private Address addressMock;
    private RoutePoint startPoint;
    private RoutePoint middlePoint;
    private RoutePoint endPoint;

    private User customer;
    private TaxiDriver taxiDriver;

    private TransactionalOrderService orderService;
    private Order originalOrder;

    @Before
    public void initOrderService() {
        when(userService.getById(CUSTOMER_ID)).thenReturn(customer);
        when(googleMapsClientService.calculateDistance(anyList())).thenReturn(new GoogleMapsRouteResponse(100000, 3600));
        orderService = new TransactionalOrderService();
        orderService.setOrderRepository(orderRepository);
        orderService.setOrderStatusManager(transitionManager);
        orderService.setPriceCalculationManager(priceCalculationManager);
        orderService.setUserService(userService);
        orderService.setGoogleMapsService(googleMapsClientService);
        customer = new User(CUSTOMER_ID, "name", "email", CUSTOMER, emptyList());
        taxiDriver = new TaxiDriver(DRIVER_ID, "name", "email", emptyList(), mock(Car.class), mock(DriverLicense.class));
        startPoint = new RoutePoint(1L, startPointAddressMock, 0, 40.0, 40.0);
        middlePoint = new RoutePoint(3L, addressMock, 1, 40.0, 40.0);
        endPoint = new RoutePoint(2L, endPointAddressMock, 2, 41.0, 41.0);
        originalOrder = createOriginalOrder();
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

    @Ignore
    @Test(expected = ForbiddenOperationException.class)
    public void addOrderByTaxiDriver() throws Exception {
//        //given
//        OrderDto orderDto = new OrderDto(1, NOW, "start", "end",
//                new OrderPriceDto(5.0, Collections.emptyList()), 0.0);
//        when(userService.getById(1)).thenReturn(taxiDriver);
//
//        //when
//        orderService.addOrder(orderDto);
//
//        //then ForbiddenOperationException is thrown
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
        when(orderRepository.getAllByOrderStatus(targetStatus)).thenReturn(asList(mock(Order.class), mock(Order.class)));
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
        when(orderRepository.findAll()).thenReturn(asList(mock(Order.class), mock(Order.class)));
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
    public void shouldThrowForbiddenExceptionWhenUserIsNotOrderOwnerToDelete() throws Exception {
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

    @Test
    public void shouldDefaultValuesWhenCalculateOrderRouteInfoOfNull() {
        assertTrue(nonNull(orderService.getOrderRouteParams(null)));
    }

    @Test
    public void shouldCalculateRouteInfo() {
        Order orderWithRouteInfo = new Order();
        orderWithRouteInfo.setRoutePoints(emptyList());
        Order withPriceSet = new Order();
        withPriceSet.setPrice(100.556);
        when(priceCalculationManager.calculateOrderPrice(any())).thenReturn(withPriceSet);

        Order actual = orderService.getOrderRouteParams(orderWithRouteInfo);
        assertThat(actual.getDistance(), is(100.0));
        assertThat(actual.getDuration(), is(LocalTime.ofSecondOfDay(3600)));
        assertThat(actual.getPrice(), is(100.56));
    }

    @Test
    public void shouldNotUpdateOrderWhenParamsAreNull() {
        Order originalOrder = createOriginalOrder();
        Order toUpdate = new Order();
        toUpdate.setOrderId(ORDER_ID);

        checkOrderUnderTest(originalOrder, toUpdate, originalOrder);
    }

    @Test(expected = ForbiddenOperationException.class)
    public void shouldThrowExceptionWhenDriverUpdatesOrder() {
        when(userService.getById(DRIVER_ID)).thenReturn(taxiDriver);
        when(orderRepository.findOne(ORDER_ID)).thenReturn(originalOrder);

        Order orderToUpdate = new Order();
        orderToUpdate.setOrderId(ORDER_ID);

        orderService.updateOrder(orderToUpdate, DRIVER_ID);
    }

    @Test(expected = ForbiddenOperationException.class)
    public void shouldThrowExceptionWhenNonOwnerUpdatesOrder() {
        Integer nonOrderOwnerId = 3;
        User nonOrderOwner = new User();
        nonOrderOwner.setId(nonOrderOwnerId);

        when(userService.getById(nonOrderOwnerId)).thenReturn(nonOrderOwner);
        when(orderRepository.findOne(ORDER_ID)).thenReturn(originalOrder);

        Order orderToUpdate = new Order();
        orderToUpdate.setOrderId(ORDER_ID);

        orderService.updateOrder(orderToUpdate, nonOrderOwnerId);
    }

    @Test
    public void shouldResetStartTimeWhenOrderMarkedAsQuick() {
        Order toUpdate = createOrderWithTimeUnderTest(MIN);
        Order afterUpdate = createOriginalOrder();
        afterUpdate.setStartTime(null);

        checkOrderUnderTest(originalOrder, toUpdate, afterUpdate);
    }

    @Test
    public void shouldUpdateStartTime() {
        LocalDateTime now = now();
        Order toUpdate = createOrderWithTimeUnderTest(now);
        Order afterUpdate = createOriginalOrder();
        afterUpdate.setStartTime(now);

        checkOrderUnderTest(originalOrder, toUpdate, afterUpdate);
    }

    private Order createOrderWithTimeUnderTest(LocalDateTime time) {
        Order orderToUpdate = new Order();
        orderToUpdate.setOrderId(ORDER_ID);
        orderToUpdate.setStartTime(time);
        return orderToUpdate;
    }

    @Test
    public void shouldNotUpdateCustomer() {
        Order toUpdate = new Order();
        toUpdate.setOrderId(ORDER_ID);
        toUpdate.setCustomer(new User());
        Order afterUpdate = createOriginalOrder();

        checkOrderUnderTest(originalOrder, toUpdate, afterUpdate);
    }

    @Test
    public void shouldNotUpdateDistance() {
        Order toUpdate = new Order();
        toUpdate.setOrderId(ORDER_ID);
        toUpdate.setDistance(6.0);
        Order afterUpdate = createOriginalOrder();

        checkOrderUnderTest(originalOrder, toUpdate, afterUpdate);
    }

    @Test
    public void shouldNotUpdatePrice() {
        Order toUpdate = new Order();
        toUpdate.setOrderId(ORDER_ID);
        toUpdate.setPrice(6.0);
        Order afterUpdate = createOriginalOrder();

        checkOrderUnderTest(originalOrder, toUpdate, afterUpdate);
    }

    @Test
    public void shouldUpdateExtraPrice() {
        Order toUpdate = new Order();
        toUpdate.setOrderId(ORDER_ID);
        toUpdate.setExtraPrice(6.0);
        Order afterUpdate = createOriginalOrder();
        afterUpdate.setExtraPrice(6.0);

        checkOrderUnderTest(originalOrder, toUpdate, afterUpdate);
    }

    @Test
    public void shouldUpdateWithPet() {
        Order toUpdate = new Order();
        toUpdate.setOrderId(ORDER_ID);
        toUpdate.setWithPet(true);
        Order afterUpdate = createOriginalOrder();
        afterUpdate.setWithPet(true);

        checkOrderUnderTest(originalOrder, toUpdate, afterUpdate);
    }

    @Test
    public void shouldUpdateWithLuggage() {
        Order toUpdate = new Order();
        toUpdate.setOrderId(ORDER_ID);
        toUpdate.setWithLuggage(true);
        Order afterUpdate = createOriginalOrder();
        afterUpdate.setWithLuggage(true);

        checkOrderUnderTest(originalOrder, toUpdate, afterUpdate);
    }

    @Test
    public void shouldUpdateDriveMyCar() {
        Order toUpdate = new Order();
        toUpdate.setOrderId(ORDER_ID);
        toUpdate.setDriveMyCar(true);
        Order afterUpdate = createOriginalOrder();
        afterUpdate.setDriveMyCar(true);

        checkOrderUnderTest(originalOrder, toUpdate, afterUpdate);
    }

    @Test
    public void shouldUpdatePassengerCount() {
        Order toUpdate = new Order();
        toUpdate.setOrderId(ORDER_ID);
        int passengerCount = 7;
        toUpdate.setPassengerCount(passengerCount);
        Order afterUpdate = createOriginalOrder();
        afterUpdate.setPassengerCount(passengerCount);

        checkOrderUnderTest(originalOrder, toUpdate, afterUpdate);
    }

    @Test
    public void shouldUpdateComment() {
        Order toUpdate = new Order();
        toUpdate.setOrderId(ORDER_ID);
        String comment = "comment";
        toUpdate.setComment(comment);
        Order afterUpdate = createOriginalOrder();
        afterUpdate.setComment(comment);

        checkOrderUnderTest(originalOrder, toUpdate, afterUpdate);
    }

    @Test
    public void shouldUpdatePaymentMethod() {
        Order toUpdate = new Order();
        toUpdate.setOrderId(ORDER_ID);
        toUpdate.setPaymentMethod(CASH);
        Order afterUpdate = createOriginalOrder();
        afterUpdate.setPaymentMethod(CASH);

        checkOrderUnderTest(originalOrder, toUpdate, afterUpdate);
    }

    @Test
    public void shouldUpdateCarType() {
        Order toUpdate = new Order();
        toUpdate.setOrderId(ORDER_ID);
        toUpdate.setCarType(PASSENGER_CAR);
        Order afterUpdate = createOriginalOrder();
        afterUpdate.setCarType(PASSENGER_CAR);

        checkOrderUnderTest(originalOrder, toUpdate, afterUpdate);
    }

    @Test
    public void shouldAppendRoutePoint() {
        RoutePoint beforeUpdate = new RoutePoint(null, addressMock, null,
                44.4, 44.4);
        RoutePoint afterUpdate = new RoutePoint(null, addressMock, 3,
                44.4, 44.4);
        Order expected = createExpectedOrder();
        expected.getRoutePoints().add(afterUpdate);
        expected.setDistance(100.0);

        checkOrderUnderTest(originalOrder, createOrderWithUpdatedRoutePoint(beforeUpdate), expected);
    }

    @Test
    public void shouldAddRoutePointToSpecificPosition() {
        RoutePoint routePoint = new RoutePoint(null, addressMock, 1,
                44.4, 44.4);

        Order expectedOrder = createExpectedOrder();
        expectedOrder.getRoutePoints().add(1, routePoint);
        expectedOrder.getRoutePoints().get(2).setRoutePointPosition(2);
        expectedOrder.getRoutePoints().get(3).setRoutePointPosition(3);
        expectedOrder.setDistance(100.0);

        checkOrderUnderTest(originalOrder, createOrderWithUpdatedRoutePoint(routePoint), expectedOrder);
    }

    @Test
    public void shouldDeleteRoutePointWhenItRoutePointPositionIsNull() {
        RoutePoint routePoint = new RoutePoint(1L, mock(Address.class),
                null, 0.0, null);

        Order expected = createExpectedOrder();
        expected.getRoutePoints().remove(0);
        expected.getRoutePoints().forEach(point -> point.setRoutePointPosition(point.getRoutePointPosition() - 1));
        expected.setDistance(100.0);

        checkOrderUnderTest(originalOrder, createOrderWithUpdatedRoutePoint(routePoint), expected);
    }

    @Test
    public void shouldChangeRoutePointPosition() {
        int newPosition = 1;

        Order expected = createExpectedOrder();
        List<RoutePoint> expectedPoints = expected.getRoutePoints();
        expectedPoints.get(newPosition).setRoutePointPosition(0);
        RoutePoint removed = expectedPoints.remove(0);
        removed.setRoutePointPosition(newPosition);
        expectedPoints.add(removed.getRoutePointPosition(), removed);
        expected.setDistance(100.0);

        long changePointPositionId = 1L;
        checkOrderUnderTest(originalOrder, createOrderWithUpdatedRoutePoint(new RoutePoint(changePointPositionId, null,
                newPosition, null, null)), expected);
    }

    @Test
    public void shouldUpdateRoutePoint() {
        RoutePoint routePoint = new RoutePoint(1L, addressMock, 0, 0.0, 0.0);

        Order expected = createExpectedOrder();
        expected.getRoutePoints().set(routePoint.getRoutePointPosition(), routePoint);
        expected.setDistance(100.0);
        expected.setDuration(LocalTime.ofSecondOfDay(3600));

        checkOrderUnderTest(originalOrder, createOrderWithUpdatedRoutePoint(routePoint), expected);
    }

    @Test
    public void shouldRecalculateDistaneAndDurationWhenRoutePointsWereChanged() {
        RoutePoint routePoint = new RoutePoint(null, addressMock, null, 0.0, 0.0);
        Order expected = createExpectedOrder();
        expected.getRoutePoints().add(routePoint);
        expected.setDistance(100.0);
        expected.setDuration(LocalTime.ofSecondOfDay(3600));

        checkOrderUnderTest(originalOrder, createOrderWithUpdatedRoutePoint(routePoint), expected);
        verify(googleMapsClientService).calculateDistance(anyList());
    }

    private Order createOriginalOrder() {
        Order order = new Order();
        order.setOrderId(ORDER_ID);
        order.setCustomer(customer);
        order.setStartTime(NOW);
        order.setOrderStatus(NEW);
        order.setPrice(20.0);
        order.fillDefaultAdditionalParameters();
        order.setDistance(100.0);
        order.setDuration(LocalTime.ofSecondOfDay(3600));
        order.setComment("");

        order.setRoutePoints(new ArrayList<>(asList(startPoint, middlePoint, endPoint)));
        return order;
    }

    private Order createExpectedOrder() {
        Order order = createOriginalOrder();
        order.setRoutePoints(createBasicExpectedRoutePointList());
        return order;
    }

    private List<RoutePoint> createBasicExpectedRoutePointList() {
        RoutePoint start = new RoutePoint(1L, startPointAddressMock, 0, 40.0, 40.0);
        RoutePoint middle = new RoutePoint(3L, addressMock, 1, 40.0, 40.0);
        RoutePoint end = new RoutePoint(2L, endPointAddressMock, 2, 41.0, 41.0);
        return new ArrayList<>(asList(start, middle, end));
    }

    private Order createOrderWithUpdatedRoutePoint(RoutePoint routePointToUpdate) {
        Order toUpdate = new Order();
        toUpdate.setOrderId(ORDER_ID);
        toUpdate.setRoutePoints(asList(routePointToUpdate));
        return toUpdate;
    }

    private void checkOrderUnderTest(Order original, Order toUpdate, Order expected) {
        when(orderRepository.findOne(ORDER_ID)).thenReturn(original);
        when(priceCalculationManager.calculateOrderPrice(any())).thenReturn(original);
        when(userService.getById(CUSTOMER_ID)).thenReturn(customer);
        when(orderRepository.save(expected)).thenReturn(expected);
        assertThat(orderService.updateOrder(toUpdate, CUSTOMER_ID), is(expected));
        verify(orderRepository).findOne(ORDER_ID);
        verify(orderRepository).save(expected);
    }
}