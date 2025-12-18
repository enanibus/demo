package com.example.demo.adapter.inbound.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_URL = "/brand/{brandId}/product/{productId}/prices";
    private static final Integer BRAND_ID = 1;
    private static final Integer PRODUCT_ID = 35455;

    /**
     * Provides test data for the parameterized test cases.
     * Each test case contains:
     * - Test description
     * - Application date (yyyy-MM-ddTHH:mm:ss)
     * - Expected priceList
     * - Expected price
     */
    static Stream<Arguments> priceTestCases() {
        return Stream.of(
            // Test 1: 10:00 on day 14 - Applies rate 1 (35.50 EUR)
            Arguments.of(
                "Test 1: Request at 10:00 on day 14 - Should return priceList 1",
                "2020-06-14T10:00:00",
                1,
                "35.50 EUR"
            ),
            // Test 2: 16:00 on day 14 - Applies rate 2 (25.45 EUR) due to higher priority
            Arguments.of(
                "Test 2: Request at 16:00 on day 14 - Should return priceList 2 (higher priority)",
                "2020-06-14T16:00:00",
                2,
                "25.45 EUR"
            ),
            // Test 3: 21:00 on day 14 - Applies rate 1 (35.50 EUR) since rate 2 ends at 18:30
            Arguments.of(
                "Test 3: Request at 21:00 on day 14 - Should return priceList 1",
                "2020-06-14T21:00:00",
                1,
                "35.50 EUR"
            ),
            // Test 4: 10:00 on day 15 - Applies rate 3 (30.50 EUR) due to higher priority
            Arguments.of(
                "Test 4: Request at 10:00 on day 15 - Should return priceList 3 (higher priority)",
                "2020-06-15T10:00:00",
                3,
                "30.50 EUR"
            ),
            // Test 5: 21:00 on day 16 - Applies rate 4 (38.95 EUR) due to higher priority
            Arguments.of(
                "Test 5: Request at 21:00 on day 16 - Should return priceList 4 (higher priority)",
                "2020-06-16T21:00:00",
                4,
                "38.95 EUR"
            )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("priceTestCases")
    @DisplayName("Get priority price for product 35455 and brand 1 (ZARA)")
    void getPriorityPrice_ShouldReturnCorrectPrice(
            String testDescription,
            String applicationDate,
            Integer expectedPriceList,
            String expectedPrice) throws Exception {

        mockMvc.perform(get(BASE_URL, BRAND_ID, PRODUCT_ID)
                        .param("applicationDate", applicationDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(PRODUCT_ID))
                .andExpect(jsonPath("$.brandId").value(BRAND_ID))
                .andExpect(jsonPath("$.priceList").value(expectedPriceList))
                .andExpect(jsonPath("$.finalPrice").value(expectedPrice))
                .andExpect(jsonPath("$.startDate").exists())
                .andExpect(jsonPath("$.endDate").exists());
    }
}

