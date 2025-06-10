package com.example.demo.mapper;

import com.example.demo.Entities.Livro;
import com.example.demo.Entities.Manutencao;
import com.example.demo.dto.ManutencaoDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-10T14:27:16-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class ManutencaoMapperImpl implements ManutencaoMapper {

    @Override
    public ManutencaoDTO toDTO(Manutencao manutencao) {
        if ( manutencao == null ) {
            return null;
        }

        ManutencaoDTO manutencaoDTO = new ManutencaoDTO();

        manutencaoDTO.setLivroId( manutencaoLivroId( manutencao ) );
        manutencaoDTO.setDataFim( manutencao.getDataFim() );
        manutencaoDTO.setDataInicio( manutencao.getDataInicio() );
        manutencaoDTO.setDescricao( manutencao.getDescricao() );
        manutencaoDTO.setId( manutencao.getId() );
        manutencaoDTO.setStatus( manutencao.getStatus() );

        return manutencaoDTO;
    }

    @Override
    public Manutencao toEntity(ManutencaoDTO manutencaoDTO) {
        if ( manutencaoDTO == null ) {
            return null;
        }

        Manutencao manutencao = new Manutencao();

        manutencao.setLivro( manutencaoDTOToLivro( manutencaoDTO ) );
        manutencao.setDataFim( manutencaoDTO.getDataFim() );
        manutencao.setDataInicio( manutencaoDTO.getDataInicio() );
        manutencao.setDescricao( manutencaoDTO.getDescricao() );
        manutencao.setId( manutencaoDTO.getId() );
        manutencao.setStatus( manutencaoDTO.getStatus() );

        return manutencao;
    }

    @Override
    public List<ManutencaoDTO> toDTOList(List<Manutencao> lista) {
        if ( lista == null ) {
            return null;
        }

        List<ManutencaoDTO> list = new ArrayList<ManutencaoDTO>( lista.size() );
        for ( Manutencao manutencao : lista ) {
            list.add( toDTO( manutencao ) );
        }

        return list;
    }

    private Long manutencaoLivroId(Manutencao manutencao) {
        if ( manutencao == null ) {
            return null;
        }
        Livro livro = manutencao.getLivro();
        if ( livro == null ) {
            return null;
        }
        Long id = livro.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected Livro manutencaoDTOToLivro(ManutencaoDTO manutencaoDTO) {
        if ( manutencaoDTO == null ) {
            return null;
        }

        Livro livro = new Livro();

        livro.setId( manutencaoDTO.getLivroId() );

        return livro;
    }
}
