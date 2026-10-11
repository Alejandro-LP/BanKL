package co.edu.konradlorenz.bankl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:bankl;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class BanklApplicationTests {

    @Autowired private MockMvc mvc;

	@Test
	void contextLoads() {
	}

    @Test
    void accountAndAdminApisRejectUnauthenticatedRequests() throws Exception {
        mvc.perform(get("/clientes/cuentas").param("usuario", "otro-usuario"))
            .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/clientes"))
            .andExpect(status().isUnauthorized());
    }

}
