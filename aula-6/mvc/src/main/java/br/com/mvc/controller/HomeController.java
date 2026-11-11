package br.com.mvc.controller;

import br.com.mvc.model.Perfil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home() {
        return "index";
    }

    /** Troca o perfil do "usuário logado" (simulação de login). */
    @PostMapping("/perfil")
    public String trocarPerfil(@RequestParam Perfil perfil,
                               @RequestParam(defaultValue = "/home") String voltarPara,
                               HttpSession session) {
        session.setAttribute(PerfilAtual.ATRIBUTO_SESSAO, perfil);
        String destino = voltarPara.startsWith("/") ? voltarPara : "/home";
        return "redirect:" + destino;
    }
}
