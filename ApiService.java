package com.bidding.system.frontend.controller;

import com.bidding.system.frontend.dto.Dtos.*;
import com.bidding.system.frontend.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

// Exercício 6 - Detalhes do edital e envio de lance
@Controller
public class EditalDetalhesController {

    private final ApiService apiService;

    public EditalDetalhesController(ApiService apiService) {
        this.apiService = apiService;
    }

    // GET /editais/{id}
    @GetMapping("/editais/{id}")
    public String detalhesEdital(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {
            Map edital = apiService.buscarEdital(id, token);
            model.addAttribute("edital", edital);
            model.addAttribute("role", session.getAttribute("role"));
        } catch (HttpClientErrorException.NotFound e) {
            model.addAttribute("erro", "Edital não encontrado.");
            return "erro";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar edital.");
            return "erro";
        }

        return "edital-detalhes";
    }

    // POST /editais/{id}/lance
    @PostMapping("/editais/{id}/lance")
    public String enviarLance(@PathVariable Long id,
                               @RequestParam Double valor,
                               HttpSession session,
                               Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {
            apiService.registrarLance(id, new LanceDTO(valor), token);
            model.addAttribute("sucesso", "Lance enviado com sucesso!");
        } catch (HttpClientErrorException.BadRequest e) {
            model.addAttribute("erro", "Dados inválidos: " + e.getResponseBodyAsString());
        } catch (HttpClientErrorException.Conflict e) {
            // Exercício 6 - lance já existente
            model.addAttribute("erro", "Você já enviou um lance para este edital.");
        } catch (HttpClientErrorException e) {
            String body = e.getResponseBodyAsString();
            // Exercício 6 - edital fechado ou data inválida
            if (e.getStatusCode().value() == 422 || body.contains("fechado") || body.contains("ENCERRADO")) {
                model.addAttribute("erro", "Este edital está encerrado e não aceita mais lances.");
            } else {
                model.addAttribute("erro", "Erro ao enviar lance: " + body);
            }
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao conectar com o servidor.");
        }

        // Recarrega o edital para exibir mensagem na mesma tela
        try {
            Map edital = apiService.buscarEdital(id, token);
            model.addAttribute("edital", edital);
            model.addAttribute("role", session.getAttribute("role"));
        } catch (Exception ignored) {}

        return "edital-detalhes";
    }
}
