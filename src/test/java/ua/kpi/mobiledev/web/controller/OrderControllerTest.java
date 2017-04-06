package ua.kpi.mobiledev.web.controller;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ua.kpi.mobiledev.domain.Car;
import ua.kpi.mobiledev.domain.DriverLicense;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.domain.dto.AddReqSimpleDto;
import ua.kpi.mobiledev.domain.dto.OrderPriceDto;
import ua.kpi.mobiledev.domain.dto.OrderStatusDto;
import ua.kpi.mobiledev.service.OrderService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration({"classpath:RESTContext.xml", "classpath:testContext.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

public class OrderControllerTest {

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static User mockUser;
    private static TaxiDriver taxiDriver;
    private static Order mockNewOrder;
    private static Order mockOrderWithoutDriverAndAddReqs;
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Autowired
    private OrderService orderService;

    @BeforeClass
    public static void beforeClass() throws Exception {
        mockUser = new User(1, "oleh", "ol@gmail.com", User.UserType.CUSTOMER, Collections.emptySet());
        taxiDriver = new TaxiDriver(2, "taxist", "taxist@gmail.com", Collections.emptySet(), mock(Car.class), mock(DriverLicense.class));
//        AdditionalRequirement additionalRequirement = mock(AdditionalRequirement.class);
//        when(additionalRequirement.getId()).thenReturn(1);
//        AdditionalRequirement additionalRequirement1 = mock(AdditionalRequirement.class);
//        when(additionalRequirement1.getId()).thenReturn(2);
//        Set<AdditionalRequirementValue> additionalRequirementValues = new HashSet<>(Arrays.asList(
//                new AdditionalRequirementValue(null, additionalRequirement, 1),
//                new AdditionalRequirementValue(null, additionalRequirement1, 3)
//        ));
//        mockNewOrder = new Order(1L, mockUser, taxiDriver, NOW,
//                "Start", "End", 100.0, Order.OrderStatus.ACCEPTED, additionalRequirementValues);
//
//        mockOrderWithoutDriverAndAddReqs = new Order(1L, mockUser, null, NOW,
//                "Start", "End", 100.0, Order.OrderStatus.NEW, Collections.emptySet());
    }

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    @Ignore

    public void getOrder_allFieldsExists() throws Exception {
        when(orderService.getOrder(1L)).thenReturn(mockNewOrder);
        mockMvc.perform(get("/order/1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.orderId").value("1"))
                .andExpect(jsonPath("$.startTime").value(NOW.toString()))
                .andExpect(jsonPath("$.startPoint").value("Start"))
                .andExpect(jsonPath("$.endPoint").value("End"))
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.customer.customerId").value(1))
                .andExpect(jsonPath("$.customer.name").value("oleh"))
                .andExpect(jsonPath("$.taxiDriver.taxiDriverId").value(2))
                .andExpect(jsonPath("$.taxiDriver.name").value("taxist"))
                .andExpect(jsonPath("$.price").value("100.0"))
                .andExpect(jsonPath("$.additionalRequirements[0].reqId").value(1))
                .andExpect(jsonPath("$.additionalRequirements[0].reqValueId").value(1))
                .andExpect(jsonPath("$.additionalRequirements[1].reqId").value(2))
                .andExpect(jsonPath("$.additionalRequirements[1].reqValueId").value(3));
        verify(orderService, times(1)).getOrder(1L);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore

    public void getOrder_illegalOrderId() throws Exception {
        when(orderService.getOrder(5L)).thenThrow(new IllegalArgumentException("test message"));
        mockMvc.perform(get("/order/5")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("message").value(Matchers.notNullValue()));
        verify(orderService, times(1)).getOrder(5L);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore

    public void getOrder_hasNoTaxiDriverAndNoAddRequirements() throws Exception {
        when(orderService.getOrder(1L)).thenReturn(mockOrderWithoutDriverAndAddReqs);
        mockMvc.perform(get("/order/1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.taxiDriver").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.additionalRequirements").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.additionalRequirements.length()").value(0));
        verify(orderService, times(1)).getOrder(1L);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore
    public void readAllOrders_validStatusLowerCased() throws Exception {
//        Order additionalOrder = new Order(1L, mockUser, taxiDriver, NOW,
//                "Start1", "End1", 115.0, Order.OrderStatus.ACCEPTED, Collections.emptySet());
        Order additionalOrder = null;
        when(orderService.getOrderList(Order.OrderStatus.ACCEPTED)).thenReturn(Arrays.asList(mockNewOrder, additionalOrder));
        mockMvc.perform(get("/order?orderStatus=accepted")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].orderId").value(1))
                .andExpect(jsonPath("$.[0].startTime").value(NOW.toString()))
                .andExpect(jsonPath("$.[0].startPoint").value("Start"))
                .andExpect(jsonPath("$.[0].endPoint").value("End"))
                .andExpect(jsonPath("$.[0].price").value(100.0))
                .andExpect(jsonPath("$.[0].status").value("ACCEPTED"))

                .andExpect(jsonPath("$.[1].orderId").value(1))
                .andExpect(jsonPath("$.[1].startTime").value(NOW.toString()))
                .andExpect(jsonPath("$.[1].startPoint").value("Start1"))
                .andExpect(jsonPath("$.[1].endPoint").value("End1"))
                .andExpect(jsonPath("$.[1].price").value(115.0))
                .andExpect(jsonPath("$.[0].status").value("ACCEPTED"));
        verify(orderService, times(1)).getOrderList(Order.OrderStatus.ACCEPTED);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore
    public void readAllOrders_statusIsAll() throws Exception {
        when(orderService.getOrderList(null)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/order?orderStatus=accepted")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.length()").value(0));
        verify(orderService, times(1)).getOrderList(Order.OrderStatus.ACCEPTED);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore
    public void readAllOrders_invalidStatus() throws Exception {
        mockMvc.perform(get("/order?orderStatus=ertretertervfddfgd")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.message").value(Matchers.notNullValue()));
        verifyNoMoreInteractions(orderService);
    }


    @Test
    @Ignore
    public void calculateOrderPrice_allIsOk() throws Exception {
//        OrderPriceDto priceDto = new OrderPriceDto(5.0, Arrays.asList(
//                new AddReqSimpleDto(1, 2),
//                new AddReqSimpleDto(2, 3),
//                new AddReqSimpleDto(3, 3)));
//        when(orderService.calculatePrice(priceDto)).thenReturn(100.0);
//        mockMvc.perform(post("/order/price")
//                .content(JsonMapper.toJson(priceDto))
//                .contentType(MediaType.APPLICATION_JSON_UTF8))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.price").value(100.0));
//        verify(orderService).calculatePrice(priceDto);
//        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore
    public void calculateOrderPrice_invalidDistance() throws Exception {
        OrderPriceDto priceDto = new OrderPriceDto(-100.0, Arrays.asList(
                new AddReqSimpleDto(1, 2),
                new AddReqSimpleDto(2, 3),
                new AddReqSimpleDto(3, 3)));
        mockMvc.perform(post("/order/price")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonMapper.toJson(priceDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.[0].field").value("distance"))
                .andExpect(jsonPath("$.[0].code").value("distance.invalidValue"))
                .andExpect(jsonPath("$.[0].message").value(Matchers.notNullValue()));
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore
    public void calculateOrderPrice_nullDistance() throws Exception {
//        OrderPriceDto priceDto = new OrderPriceDto(null, Arrays.asList(
//                new AddReqSimpleDto(1, 2),
//                new AddReqSimpleDto(2, 3),
//                new AddReqSimpleDto(3, 3)));
//        when(orderService.calculatePrice(priceDto)).thenReturn(100.0);
//        mockMvc.perform(post("/order/price")
//                .content(JsonMapper.toJson(priceDto))
//                .contentType(MediaType.APPLICATION_JSON_UTF8))
//                .andExpect(status().isOk());
//        verify(orderService).calculatePrice(priceDto);
//        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore
    public void calculateOrderPrice_invalidParameterMap() throws Exception {
//        OrderPriceDto priceDto = new OrderPriceDto(3.0, Arrays.asList(
//                new AddReqSimpleDto(-1, 2),
//                new AddReqSimpleDto(2, 3),
//                new AddReqSimpleDto(3, 3)));
//        when(orderService.calculatePrice(priceDto)).thenReturn(100.0);
//        mockMvc.perform(post("/order/price")
//                .content(JsonMapper.toJson(priceDto))
//                .contentType(MediaType.APPLICATION_JSON_UTF8))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.[0].field").value("additionalRequirements"))
//                .andExpect(jsonPath("$.[0].code").value("additionalRequirements.invalidKey"))
//                .andExpect(jsonPath("$.[0].message").value(Matchers.notNullValue()));
//        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore
    public void calculateOrderPrice_nullParameterMap() throws Exception {
//        OrderPriceDto priceDto = new OrderPriceDto(3.0, null);
//        when(orderService.calculatePrice(priceDto)).thenReturn(100.0);
//        mockMvc.perform(post("/order/price")
//                .content(JsonMapper.toJson(priceDto))
//                .contentType(MediaType.APPLICATION_JSON_UTF8))
//                .andExpect(status().isOk());
//        verify(orderService).calculatePrice(priceDto);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore
    public void calculateOrderPrice_illegalParameterIdOrParamValueId() throws Exception {
//        OrderPriceDto priceDto = new OrderPriceDto(3.0, Arrays.asList(
//                new AddReqSimpleDto(1, 2),
//                new AddReqSimpleDto(2, 3),
//                new AddReqSimpleDto(3, 3)));
//        when(orderService.calculatePrice(priceDto)).thenThrow(new NullPointerException("exMessage"));
//        mockMvc.perform(post("/order/price")
//                .content(JsonMapper.toJson(priceDto))
//                .contentType(MediaType.APPLICATION_JSON_UTF8))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("exMessage"));
//        verify(orderService).calculatePrice(priceDto);
//        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore
    public void changeOrderStatus_changeWithValidData() throws Exception {
        when(orderService.changeOrderStatus(1L, 1, Order.OrderStatus.ACCEPTED)).thenReturn(new Order());

        mockMvc.perform(put("/order/1/status")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonMapper.toJson(new OrderStatusDto(1, Order.OrderStatus.ACCEPTED))))
                .andExpect(status().isOk());

        verify(orderService).changeOrderStatus(1L, 1, Order.OrderStatus.ACCEPTED);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore
    public void changeOrderStatus_userIdIsNull() throws Exception {
        mockMvc.perform(put("/order/1/status")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonMapper.toJson(new OrderStatusDto(null, Order.OrderStatus.ACCEPTED))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.[0].field").value("userId"))
                .andExpect(jsonPath("$.[0].code").value("userId.required"))
                .andExpect(jsonPath("$.[0].message").value(Matchers.notNullValue()));
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore
    public void changeOrderStatus_userIdIsInvalid() throws Exception {
        mockMvc.perform(put("/order/1/status")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonMapper.toJson(new OrderStatusDto(-1, Order.OrderStatus.ACCEPTED))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.[0].field").value("userId"))
                .andExpect(jsonPath("$.[0].code").value("userId.negative"))
                .andExpect(jsonPath("$.[0].message").value(Matchers.notNullValue()));
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore
    public void changeOrderStatus_orderStatusIsNull() throws Exception {
        mockMvc.perform(put("/order/1/status")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonMapper.toJson(new OrderStatusDto(1, null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.[0].field").value("orderStatus"))
                .andExpect(jsonPath("$.[0].code").value("orderStatus.nullOrInvalid"))
                .andExpect(jsonPath("$.[0].message").value(Matchers.notNullValue()));
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore
    public void changeOrderStatus_throwsIllegalArgumentException() throws Exception {
        when(orderService.changeOrderStatus(1L, 1, Order.OrderStatus.ACCEPTED))
                .thenThrow(new IllegalArgumentException("Exception message"));

        mockMvc.perform(put("/order/1/status")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonMapper.toJson(new OrderStatusDto(1, Order.OrderStatus.ACCEPTED))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Exception message"));
        verify(orderService).changeOrderStatus(1L, 1, Order.OrderStatus.ACCEPTED);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore
    public void changeOrderStatus_throwsIllegalStateException() throws Exception {
        when(orderService.changeOrderStatus(1L, 1, Order.OrderStatus.ACCEPTED))
                .thenThrow(new IllegalStateException("Exception message"));

        mockMvc.perform(put("/order/1/status")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonMapper.toJson(new OrderStatusDto(1, Order.OrderStatus.ACCEPTED))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Exception message"));
        verify(orderService).changeOrderStatus(1L, 1, Order.OrderStatus.ACCEPTED);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore
    public void updateOrder() throws Exception {
//        OrderDto orderDto = new OrderDto(1, NOW.plusHours(1), "start", "end", new OrderPriceDto(10.0, Collections.emptyList()), null);
//        when(orderService.updateOrder(1L, 1, orderDto)).thenReturn(new Order());
//        mockMvc.perform(put("/order/1")
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(JsonMapper.toJson(orderDto)))
//                .andExpect(status().isOk());
//        verify(orderService).updateOrder(1L, 1, orderDto);
    }

    @Test
    @Ignore
    public void updateOrder_InvalidStartTime() throws Exception {
//        OrderDto orderDto = new OrderDto(null, NOW.minusHours(1), "start", "end", null, null);
//        mockMvc.perform(put("/order/1")
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(JsonMapper.toJson(orderDto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.length()").value(3))
//                .andExpect(jsonPath("$.[2].field").value("startTime"))
//                .andExpect(jsonPath("$.[2].code").value("startTime.futureTimeRequired"))
//                .andExpect(jsonPath("$.[2].message").value(Matchers.notNullValue()));
//        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Ignore
    public void updateOrder_illegalArgumentException() throws Exception {
//        OrderDto orderDto = new OrderDto(1, NOW.plusHours(1), "start", "end", new OrderPriceDto(10.0, Collections.emptyList()), null);
//        when(orderService.updateOrder(1L, 1, orderDto)).thenThrow(new IllegalArgumentException("ExceptionMessage"));
//        mockMvc.perform(put("/order/1")
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(JsonMapper.toJson(orderDto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("ExceptionMessage"));
//        verify(orderService).updateOrder(1L, 1, orderDto);
    }

    @Test
    @Ignore
    public void addOrder_allIsOk() throws Exception {
//        OrderPriceDto orderPrice = new OrderPriceDto(100.0, Collections.emptyList());
//        OrderDto orderDto = new OrderDto(1, NOW.plusHours(1), "start", "end", orderPrice, null);
//        when(orderService.addOrder(orderDto)).thenReturn(new Order());
//        mockMvc.perform(post("/order")
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(JsonMapper.toJson(orderDto)))
//                .andExpect(status().isOk());
//        verify(orderService).addOrder(orderDto);
    }

    @Test
    @Ignore
    public void addOrder_orderPriceExpected() throws Exception {
//        OrderDto orderDto = new OrderDto(1, NOW.plusHours(1), "start", "end", null, null);
//        mockMvc.perform(post("/order")
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(JsonMapper.toJson(orderDto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.[0].field").value("orderPrice"))
//                .andExpect(jsonPath("$.[0].code").value("orderPrice.required"))
//                .andExpect(jsonPath("$.[0].message").value(Matchers.notNullValue()));
//        verifyNoMoreInteractions(orderService);
    }

//    @Test
    @Ignore
    public void addOrder_orderPriceDistanceExpected() throws Exception {
//        OrderPriceDto orderPrice = new OrderPriceDto(-100.0, Collections.emptyList());
//        OrderDto orderDto = new OrderDto(1, NOW.plusHours(1), "start", "end",
//                orderPrice, null);
//        mockMvc.perform(post("/order")
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(JsonMapper.toJson(orderDto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.[0].field").value("orderPrice.distance"))
//                .andExpect(jsonPath("$.[0].code").value("distance.invalidValue"))
//                .andExpect(jsonPath("$.[0].message").value(Matchers.notNullValue()));
//        verifyNoMoreInteractions(orderService);
        System.out.println(JsonMapper.toJson(new Order()));
    }

}