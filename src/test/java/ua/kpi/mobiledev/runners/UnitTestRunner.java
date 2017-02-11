package ua.kpi.mobiledev.runners;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ua.kpi.mobiledev.domain.orderStatusManagement.OrderStatusTransitionManagerTest;
import ua.kpi.mobiledev.domain.orderStatusManagement.OrderStatusTransitionTest;
import ua.kpi.mobiledev.service.OrderServiceTest;
import ua.kpi.mobiledev.service.UserServiceTest;
import ua.kpi.mobiledev.testCategories.UnitTest;

@RunWith(Categories.class)
@Categories.IncludeCategory(UnitTest.class)
@Suite.SuiteClasses({
        OrderStatusTransitionManagerTest.class,
        OrderStatusTransitionTest.class,
        OrderServiceTest.class,
        UserServiceTest.class
})
public class UnitTestRunner {
}
