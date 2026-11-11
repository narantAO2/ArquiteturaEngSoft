package br.com.mvc.controller;

import br.com.mvc.model.Perfil;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Simula o usuário logado guardando o perfil escolhido na sessão.
 * Disponibiliza "perfilAtual" e "perfis" para todas as telas.
 */
@ControllerAdvice
public class PerfilAtual {

    public static final String ATRIBUTO_SESSAO = "perfil";

    public static Perfil obter(HttpSession session) {
        Object perfil = session.getAttribute(ATRIBUTO_SESSAO);
        return perfil instanceof Perfil p ? p : Perfil.VENDEDOR;
    }

    @ModelAttribute("perfilAtual")
    public Perfil perfilAtual(HttpSession session) {
        return obter(session);
    }

    @ModelAttribute("perfis")
    public Perfil[] perfis() {
        return Perfil.values();
    }
}
