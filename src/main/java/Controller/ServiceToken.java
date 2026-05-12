package Controller;

import dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ServiceToken {

    @Autowired
    private TokenService tokenService;

    public String gerarToken(UserDTO user) {

        if (user.getId() == null || user.getId() == 0 ||
            user.getNome() == null || user.getNome().isEmpty() ||
            user.getEmail() == null || user.getEmail().isEmpty() ||
            user.getSenha() == null || user.getSenha().isEmpty()) {
            
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos para gerar token");
        }


        return tokenService.generateToken(user.getId(), user.getEmail(), user.getRole());
        "Um ou mais capos faltantes");
        
        
    }
    return
}
