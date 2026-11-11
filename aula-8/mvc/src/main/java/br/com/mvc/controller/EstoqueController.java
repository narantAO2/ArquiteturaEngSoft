package br.com.mvc.controller;

import br.com.mvc.exception.RegraNegocioException;
import br.com.mvc.model.MovimentacaoEstoque;
import br.com.mvc.model.TipoMovimentacao;
import br.com.mvc.service.EstoqueService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping
    public String tela(Model model) {
        model.addAttribute("produtos", estoqueService.produtosMovimentaveis());
        model.addAttribute("movimentacoes", estoqueService.historico());
        model.addAttribute("tipos", TipoMovimentacao.values());
        return "estoque/estoque";
    }

    @PostMapping
    public String movimentar(@RequestParam(required = false) Long produtoId,
                             @RequestParam(required = false) TipoMovimentacao tipo,
                             @RequestParam(required = false) Integer quantidade,
                             @RequestParam(required = false) String observacao,
                             HttpSession session,
                             RedirectAttributes redirect) {
        try {
            MovimentacaoEstoque mov = estoqueService.movimentar(
                    produtoId, tipo, quantidade, PerfilAtual.obter(session), observacao);
            redirect.addFlashAttribute("sucesso", mov.getTipo().getDescricao() + " de " + mov.getQuantidade()
                    + " unidade(s) de \"" + mov.getProduto().getNome() + "\" registrada. Saldo: "
                    + mov.getSaldoApos() + " (" + mov.getProduto().getStatus().getDescricao() + ").");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/estoque";
    }
}
