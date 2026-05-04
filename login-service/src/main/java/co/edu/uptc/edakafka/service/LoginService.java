package co.edu.uptc.edakafka.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uptc.edakafka.model.Login;
import co.edu.uptc.edakafka.repository.LoginRepository;

@Service
public class LoginService {

    @Autowired
    private LoginRepository loginRepository;

    // Método para crear un nuevo Login
    public Login createLogin(Login login) {
        // 1. Guardamos el Login en la base de datos local
        Login savedLogin = loginRepository.save(login);
        
        return savedLogin;
    }

    // Método para obtener todos los Logins
    public List<Login> getAllLogins() {
        return loginRepository.findAll();
    }

    // Método para buscar un Login asociado a un Customer específico
    public Optional<Login> getLoginByCustomerId(String customerId) {
        return loginRepository.findByCustomerId(customerId);
    }
}
