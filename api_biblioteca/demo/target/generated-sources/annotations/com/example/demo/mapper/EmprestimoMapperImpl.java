package com.example.demo.mapper;

import com.example.demo.Entities.Cliente;
import com.example.demo.Entities.Emprestimo;
import com.example.demo.Entities.Livro;
import com.example.demo.dto.EmprestimoDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-02T19:10:43-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class EmprestimoMapperImpl implements EmprestimoMapper {

    @Override
    public EmprestimoDTO toDTO(Emprestimo emprestimo) {
        if ( emprestimo == null ) {
            return null;
        }

        EmprestimoDTO emprestimoDTO = new EmprestimoDTO();

        emprestimoDTO.setClienteId( emprestimoClienteId( emprestimo ) );
        emprestimoDTO.setLivroId( emprestimoLivroId( emprestimo ) );
        emprestimoDTO.setDataDevolucao( emprestimo.getDataDevolucao() );
        emprestimoDTO.setDataEmprestimo( emprestimo.getDataEmprestimo() );
        emprestimoDTO.setId( emprestimo.getId() );
        emprestimoDTO.setStatus( emprestimo.getStatus() );

        return emprestimoDTO;
    }

    @Override
    public Emprestimo toEntity(EmprestimoDTO emprestimoDTO) {
        if ( emprestimoDTO == null ) {
            return null;
        }

        Emprestimo emprestimo = new Emprestimo();

        emprestimo.setDataDevolucao( emprestimoDTO.getDataDevolucao() );
        emprestimo.setId( emprestimoDTO.getId() );
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

    private Long emprestimoClienteId(Emprestimo emprestimo) {
        if ( emprestimo == null ) {
            return null;
        }
        Cliente cliente = emprestimo.getCliente();
        if ( cliente == null ) {
            return null;
        }
        Long id = cliente.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long emprestimoLivroId(Emprestimo emprestimo) {
        if ( emprestimo == null ) {
            return null;
        }
        Livro livro = emprestimo.getLivro();
        if ( livro == null ) {
            return null;
        }
        Long id = livro.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
