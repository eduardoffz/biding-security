 package com.bidding.system.frontend.controller;

import com.bidding.system.frontend.dto.Dtos.*;
import com.bidding.system.frontend.service.ApiService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

// Exercício 3 - Autenticação (login e registro)
@Controller
public class AuthController {

    private final ApiService apiService;

    public AuthController(ApiService apiService) {
        this.apiService = apiService;
    }

    // GET /login
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // POST /login
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String senha,
                        HttpSession session,
                        Model model) {
        try {
            LoginResponseDTO response = apiService.logar(new UserRequestDTO(email, senha));
            
            
            
            // Exercício 3 teste 2   guardaro token JWT em HttpSession
            
            session.setAttribute("token", response.token());
            session.setAttribute("role", response.role());
            session.setAttribute("email", response.email());
            return "redirect:/editais";
        } catch (HttpClientErrorException e) {
            model.addAttribute("erro", "Email ou senha inválidos.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao conectar com o servidor.");
            return "login";
        }
    }

    // GET /register
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // POST /register
    @PostMapping("/register")
    public String register(@RequestParam String nome,
                           @RequestParam String email,
                           @RequestParam String senha,
                           @RequestParam String role,
                           Model model) {
        try {
            apiService.registrarUsuario(new UserDTO(nome, email, senha, role));
            return "redirect:/login";
        } catch (HttpClientErrorException e) {
            model.addAttribute("erro", "Erro no cadastro: " + e.getResponseBodyAsString());
            return "register";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao conectar com o servidor.");
            return "register";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
