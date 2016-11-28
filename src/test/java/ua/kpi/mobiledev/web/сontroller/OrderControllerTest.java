package ua.kpi.mobiledev.web.сontroller;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
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
import ua.kpi.mobiledev.domain.*;
import ua.kpi.mobiledev.service.OrderService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration({"classpath:RESTContext.xml", "classpath:testContext.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private static final LocalDateTime NOW = LocalDateTime.now();

    private static User mockUser;

    private static TaxiDriver taxiDriver;

    private static Order mockNewOrder;

    private static Order mockOrderWithoutDriverAndAddReqs;

    @BeforeClass
    public static void beforeClass() throws Exception {
        mockUser = new User(1, "oleh", "ol@gmail.com", User.UserType.CUSTOMER, Collections.emptyList());
        taxiDriver = new TaxiDriver(2, "taxist", "taxist@gmail.com", Collections.emptyList(), mock(Car.class));
        AdditionalRequirement additionalRequirement = mock(AdditionalRequirement.class);
        when(additionalRequirement.getId()).thenReturn(1);
        AdditionalRequirement additionalRequirement1 = mock(AdditionalRequirement.class);
        when(additionalRequirement1.getId()).thenReturn(2);
        Map<AdditionalRequirement, Integer> additionalRequirementIntegerMap = new LinkedHashMap<>();
        additionalRequirementIntegerMap.put(additionalRequirement, 1);
        additionalRequirementIntegerMap.put(additionalRequirement1, 3);
        mockNewOrder = new Order(1L, mockUser, taxiDriver, NOW,
                "Start", "End", 100.0, Order.OrderStatus.ACCEPTED, additionalRequirementIntegerMap);

        mockOrderWithoutDriverAndAddReqs = new Order(1L, mockUser, null, NOW,
                "Start", "End", 100.0, Order.OrderStatus.NEW, Collections.emptyMap());
    }

    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
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
    public void readAllOrders_validStatusLowerCased() throws Exception {
        Order additionalOrder = new Order(1L, mockUser, taxiDriver, NOW,
                "Start1", "End1", 115.0, Order.OrderStatus.ACCEPTED, Collections.emptyMap());
        when(orderService.getOrderList(Order.OrderStatus.ACCEPTED)).thenReturn(Arrays.asList(mockNewOrder, additionalOrder));
        mockMvc.perform(get("/order?orderStatus=accepted")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].customerId").value(1))
                .andExpect(jsonPath("$.[0].startTime").value(NOW.toString()))
                .andExpect(jsonPath("$.[0].startPoint").value("Start"))
                .andExpect(jsonPath("$.[0].endPoint").value("End"))
                .andExpect(jsonPath("$.[0].price").value(100.0))

                .andExpect(jsonPath("$.[1].customerId").value(1))
                .andExpect(jsonPath("$.[1].startTime").value(NOW.toString()))
                .andExpect(jsonPath("$.[1].startPoint").value("Start1"))
                .andExpect(jsonPath("$.[1].endPoint").value("End1"))
                .andExpect(jsonPath("$.[1].price").value(115.0));
        verify(orderService, times(1)).getOrderList(Order.OrderStatus.ACCEPTED);
        verifyNoMoreInteractions(orderService);
    }

    @Test
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
    public void readAllOrders_invalidStatus() throws Exception {
        mockMvc.perform(get("/order?orderStatus=ertretertervfddfgd")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.message").value(Matchers.notNullValue()));
        verifyNoMoreInteractions(orderService);
    }
}