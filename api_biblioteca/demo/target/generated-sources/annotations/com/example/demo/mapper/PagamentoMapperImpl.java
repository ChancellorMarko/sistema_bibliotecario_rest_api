package com.example.demo.mapper;

import com.example.demo.Entities.Cliente;
import com.example.demo.Entities.Pagamento;
import com.example.demo.dto.PagamentoDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-08T18:27:16-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class PagamentoMapperImpl implements PagamentoMapper {

    @Override
    public PagamentoDTO toDTO(Pagamento pagamento) {
        if ( pagamento == null ) {
            return null;
        }

        PagamentoDTO pagamentoDTO = new PagamentoDTO();

        pagamentoDTO.setClienteId( pagamentoClienteId( pagamento ) );
        pagamentoDTO.setDataPagamento( pagamento.getDataPagamento() );
        pagamentoDTO.setFormaPagamento( pagamento.getFormaPagamento() );
        pagamentoDTO.setId( pagamento.getId() );
        pagamentoDTO.setStatus( pagamento.getStatus() );
        pagamentoDTO.setValor( pagamento.getValor() );

        return pagamentoDTO;
    }

    @Override
    public Pagamento toEntity(PagamentoDTO pagamentoDTO) {
        if ( pagamentoDTO == null ) {
            return null;
        }

        Pagamento pagamento = new Pagamento();

        pagamento.setCliente( pagamentoDTOToCliente( pagamentoDTO ) );
        pagamento.setDataPagamento( pagamentoDTO.getDataPagamento() );
        pagamento.setFormaPagamento( pagamentoDTO.getFormaPagamento() );
        pagamento.setId( pagamentoDTO.getId() );
        pagamento.setStatus( pagamentoDTO.getStatus() );
        pagamento.setValor( pagamentoDTO.getValor() );

        return pagamento;
    }

    @Override
    public List<PagamentoDTO> toDTOList(List<Pagamento> pagamentos) {
        if ( pagamentos == null ) {
            return null;
        }

        List<PagamentoDTO> list = new ArrayList<PagamentoDTO>( pagamentos.size() );
        for ( Pagamento pagamento : pagamentos ) {
            list.add( toDTO( pagamento ) );
        }

        return list;
    }

    private Long pagamentoClienteId(Pagamento pagamento) {
        if ( pagamento == null ) {
            return null;
        }
        Cliente cliente = pagamento.getCliente();
        if ( cliente == null ) {
            return null;
        }
        Long id = cliente.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected Cliente pagamentoDTOToCliente(PagamentoDTO pagamentoDTO) {
        if ( pagamentoDTO == null ) {
            return null;
        }

        Cliente cliente = new Cliente();

        cliente.setId( pagamentoDTO.getClienteId() );

        return cliente;
    }
}
