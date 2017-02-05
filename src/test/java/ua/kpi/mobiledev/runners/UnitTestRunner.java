package ua.kpi.mobiledev.runners;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ua.kpi.mobiledev.testCategories.UnitTest;

@RunWith(Categories.class)
@Categories.IncludeCategory(UnitTest.class)
@Suite.SuiteClasses({})
public class UnitTestRunner {
}
