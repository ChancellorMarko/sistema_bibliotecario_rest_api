package com.example.demo.mapper;

import com.example.demo.Entities.Pagamento;
import com.example.demo.dto.PagamentoDTO;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PagamentoMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    PagamentoDTO toDTO(Pagamento pagamento);

    @Mapping(source = "clienteId", target = "cliente.id")
    Pagamento toEntity(PagamentoDTO pagamentoDTO);

    List<PagamentoDTO> toDTOList(List<Pagamento> pagamentos);
}
