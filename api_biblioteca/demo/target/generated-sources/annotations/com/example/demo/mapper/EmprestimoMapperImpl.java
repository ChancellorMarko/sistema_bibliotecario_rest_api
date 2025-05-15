package com.example.demo.mapper;

import com.example.demo.Entities.Emprestimo;
import com.example.demo.dto.EmprestimoDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-14T23:30:35-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.z20250331-1358, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class EmprestimoMapperImpl implements EmprestimoMapper {

    @Override
    public EmprestimoDTO toDTO(Emprestimo emprestimo) {
        if ( emprestimo == null ) {
            return null;
        }

        EmprestimoDTO emprestimoDTO = new EmprestimoDTO();

        emprestimoDTO.setClienteId( emprestimo.getClienteId() );
        emprestimoDTO.setDataDevolucao( emprestimo.getDataDevolucao() );
        emprestimoDTO.setDataEmprestimo( emprestimo.getDataEmprestimo() );
        emprestimoDTO.setId( emprestimo.getId() );
        emprestimoDTO.setLivroId( emprestimo.getLivroId() );
        emprestimoDTO.setStatus( emprestimo.getStatus() );

        return emprestimoDTO;
    }

    @Override
    public Emprestimo toEntity(EmprestimoDTO emprestimoDTO) {
        if ( emprestimoDTO == null ) {
            return null;
        }

        Emprestimo emprestimo = new Emprestimo();

        emprestimo.setClienteId( emprestimoDTO.getClienteId() );
        emprestimo.setDataDevolucao( emprestimoDTO.getDataDevolucao() );
        emprestimo.setDataEmprestimo( emprestimoDTO.getDataEmprestimo() );
        emprestimo.setId( emprestimoDTO.getId() );
        emprestimo.setLivroId( emprestimoDTO.getLivroId() );
        emprestimo.setStatus( emprestimoDTO.getStatus() );

        return emprestimo;
    }

    @Override
    public List<EmprestimoDTO> toDTOList(List<Emprestimo> emprestimos) {
        if ( emprestimos == null ) {
            return null;
        }

        List<EmprestimoDTO> list = new ArrayList<EmprestimoDTO>( emprestimos.size() );
        for ( Emprestimo emprestimo : emprestimos ) {
            list.add( toDTO( emprestimo ) );
        }

        return list;
    }
}
