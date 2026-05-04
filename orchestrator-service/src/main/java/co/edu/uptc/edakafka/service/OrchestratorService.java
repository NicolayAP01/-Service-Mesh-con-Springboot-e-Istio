package co.edu.uptc.edakafka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import co.edu.uptc.edakafka.model.Customer;
import co.edu.uptc.edakafka.model.Login;

@Service
public class OrchestratorService {
    private final Logger log = LoggerFactory.getLogger(OrchestratorService.class);

    public void startCustomerRegistrationSaga(Customer customer) {
        log.info("SAGA INICIADO: Solicitando la creacion del cliente: {}", customer.getDocument());
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            // Llama directo a customer-service
            ResponseEntity<String> responseCustomer = restTemplate.postForEntity(
                "http://customer-service:8081/customer/add", customer, String.class);
            
            if (responseCustomer.getStatusCode().is2xxSuccessful()) {
                log.info("SAGA PASO 2: Cliente creado. Precediendo con Login.");
                
                Login login = new Login();
                login.setUsername(customer.getEmail());
                login.setPassword(customer.getDocument());
                login.setCustomerId(customer.getDocument());
                
                log.info("SAGA PASO 3: Solicitando creacion de cuenta/login...");
                try {
                    // Llama directo a login-service
                    ResponseEntity<String> responseLogin = restTemplate.postForEntity(
                        "http://login-service:8082/api/logins", login, String.class);
                    
                    if(responseLogin.getStatusCode().is2xxSuccessful()){
                         log.info("SAGA FINALIZADA CON EXITO: El login fue creado. Registro completado.");
                    }
                } catch (Exception e) {
                    // Accion Compensatoria
                    log.error("SAGA FALLIDA: No se pudo crear el login. Iniciando reversion (Compensacion)!");
                    restTemplate.delete("http://customer-service:8081/customer/delete/" + customer.getDocument());
                    log.info("SAGA COMPENSACION: Cliente cancelado.");
                }
            }
        } catch (Exception e) {
            log.error("SAGA FALLIDA: Error al intentar crear el cliente en el customer-service: " + e.getMessage());
        }
    }
}

