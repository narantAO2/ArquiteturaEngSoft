package br.com.mvc.controller;

import br.com.mvc.exception.RegraNegocioException;
import br.com.mvc.integracao.IntegracaoHubException;
import br.com.mvc.model.Pedido;
import br.com.mvc.model.StatusProduto;
import br.com.mvc.service.PedidoService;
import br.com.mvc.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ProdutoService produtoService;

    public PedidoController(PedidoService pedidoService, ProdutoService produtoService) {
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pedidos", pedidoService.listar());
        return "pedidos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pedido", new Pedido());
        return "pedidos/form";
    }

    @PostMapping
    public String criar(@ModelAttribute Pedido pedido, Model model) {
        try {
            Pedido salvo = pedidoService.criar(pedido);
            return "redirect:/pedidos/" + salvo.getId();
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("pedido", pedido);
            return "pedidos/form";
        }
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("pedido", pedidoService.buscar(id));
        model.addAttribute("produtos", produtoService.listar().stream()
                .filter(p -> p.getStatus() == StatusProduto.DISPONIVEL)
                .toList());
        return "pedidos/detalhe";
    }

    @PostMapping("/{id}/itens")
    public String adicionarItem(@PathVariable Long id,
                                @RequestParam(required = false) Long produtoId,
                                @RequestParam(required = false) Integer quantidade,
                                RedirectAttributes redirect) {
        try {
            pedidoService.adicionarItem(id, produtoId, quantidade);
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/pedidos/" + id;
    }

    @PostMapping("/{id}/itens/{itemId}/remover")
    public String removerItem(@PathVariable Long id, @PathVariable Long itemId, RedirectAttributes redirect) {
        try {
            pedidoService.removerItem(id, itemId);
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/pedidos/" + id;
    }

    @PostMapping("/{id}/fechar")
    public String fechar(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        try {
            Pedido pedido = pedidoService.fechar(id, PerfilAtual.obter(session));
            redirect.addFlashAttribute("sucesso", "Pedido fechado e faturado. Fatura: " + pedido.getFaturaId());
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        } catch (IntegracaoHubException e) {
            redirect.addFlashAttribute("erro", "Não foi possível faturar o pedido: " + e.getMessage()
                    + " O pedido continua ABERTO e o estoque não foi baixado.");
        }
        return "redirect:/pedidos/" + id;
    }
}
