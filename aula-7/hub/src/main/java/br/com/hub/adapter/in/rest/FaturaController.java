package br.com.hub.adapter.in.rest;

import br.com.hub.application.port.in.ConsultarFaturaUseCase;
import br.com.hub.application.port.in.FaturarPedidoUseCase;
import br.com.hub.domain.exception.RegraFaturamentoException;
import br.com.hub.domain.model.Cliente;
import br.com.hub.domain.model.ItemPedido;
import br.com.hub.domain.model.Pedido;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/** Adaptador de entrada REST: traduz HTTP/JSON para as portas de entrada. */
@RestController
@RequestMapping("/faturas")
public class FaturaController {

    private final FaturarPedidoUseCase faturarPedido;
    private final ConsultarFaturaUseCase consultarFatura;

    public FaturaController(FaturarPedidoUseCase faturarPedido, ConsultarFaturaUseCase consultarFatura) {
        this.faturarPedido = faturarPedido;
        this.consultarFatura = consultarFatura;
    }

    @PostMapping
    public ResponseEntity<FaturamentoResponse> faturar(@RequestBody FaturamentoRequest request) {
        String faturaId = faturarPedido.faturar(paraDominio(request));
        return ResponseEntity.created(URI.create("/faturas/" + faturaId))
                .body(new FaturamentoResponse(faturaId));
    }

    @GetMapping("/{id}")
    public FaturaResponse buscar(@PathVariable String id) {
        return FaturaResponse.de(consultarFatura.buscar(id));
    }

    @GetMapping
    public List<FaturaResponse> listar() {
        return consultarFatura.listar().stream().map(FaturaResponse::de).toList();
    }

    private Pedido paraDominio(FaturamentoRequest request) {
        if (request == null || request.cliente() == null) {
            throw new RegraFaturamentoException("Pedido e cliente são obrigatórios.");
        }
        Cliente cliente = new Cliente(request.cliente().nome(), request.cliente().email(),
                request.cliente().documento());
        List<ItemPedido> itens = request.itens() == null ? List.of() : request.itens().stream()
                .map(i -> new ItemPedido(i.descricao(), i.quantidade() == null ? 0 : i.quantidade(),
                        i.valorUnitario()))
                .toList();
        return new Pedido(request.pedidoId(), cliente, itens);
    }
}
