package br.com.hub.config;

import br.com.hub.adapter.out.boleto.BoletoFilaAdapter;
import br.com.hub.adapter.out.email.EmailSmtpAdapter;
import br.com.hub.adapter.out.notafiscal.NotaFiscalSoapAdapter;
import br.com.hub.adapter.out.persistence.FaturaRepositoryEmMemoriaAdapter;
import br.com.hub.application.port.out.EmissaoBoletoPort;
import br.com.hub.application.port.out.EmissaoNotaFiscalPort;
import br.com.hub.application.port.out.FaturaRepositoryPort;
import br.com.hub.application.port.out.NotificacaoClientePort;
import br.com.hub.application.service.FaturamentoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Monta o hexágono: liga cada porta de saída ao seu adaptador.
 * O núcleo (domain + application) não tem nenhuma anotação do Spring.
 */
@Configuration
public class HubConfiguration {

    @Bean
    public FaturaRepositoryPort faturaRepositoryPort() {
        return new FaturaRepositoryEmMemoriaAdapter();
    }

    @Bean
    public EmissaoBoletoPort emissaoBoletoPort() {
        return new BoletoFilaAdapter();
    }

    @Bean
    public EmissaoNotaFiscalPort emissaoNotaFiscalPort() {
        return new NotaFiscalSoapAdapter();
    }

    @Bean
    public NotificacaoClientePort notificacaoClientePort() {
        return new EmailSmtpAdapter();
    }

    @Bean
    public FaturamentoService faturamentoService(FaturaRepositoryPort repository,
                                                 EmissaoBoletoPort boleto,
                                                 EmissaoNotaFiscalPort notaFiscal,
                                                 NotificacaoClientePort notificacao) {
        return new FaturamentoService(repository, boleto, notaFiscal, notificacao);
    }
}
