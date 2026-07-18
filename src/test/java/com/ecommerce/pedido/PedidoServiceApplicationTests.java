package com.ecommerce.pedido;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Feature("Aplicação")
class PedidoServiceApplicationTests {

	@Test
	@Severity(SeverityLevel.TRIVIAL)
	void contextLoads() {
	}
}
