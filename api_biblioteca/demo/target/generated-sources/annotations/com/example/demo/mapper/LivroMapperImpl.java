package com.example.demo.mapper;

import com.example.demo.Entities.Livro;
import com.example.demo.dto.LivroDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-01T15:45:00-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class LivroMapperImpl implements LivroMapper {

    @Override
    public LivroDTO toDTO(Livro livro) {
        if ( livro == null ) {
            return null;
        }

        LivroDTO livroDTO = new LivroDTO();

        livroDTO.setAnoPublicacao( livro.getAnoPublicacao() );
        livroDTO.setAutor( livro.getAutor() );
        livroDTO.setCategoria( livro.getCategoria() );
        livroDTO.setEditora( livro.getEditora() );
        livroDTO.setId( livro.getId() );
        livroDTO.setIsbn( livro.getIsbn() );
        livroDTO.setQuantidade( livro.getQuantidade() );
        livroDTO.setTitulo( livro.getTitulo() );

        return livroDTO;
    }

    @Override
    public Livro toEntity(LivroDTO livroDTO) {
        if ( livroDTO == null ) {
            return null;
        }

        Livro livro = new Livro();

        livro.setAnoPublicacao( livroDTO.getAnoPublicacao() );
        livro.setAutor( livroDTO.getAutor() );
        livro.setCategoria( livroDTO.getCategoria() );
        livro.setEditora( livroDTO.getEditora() );
        livro.setId( livroDTO.getId() );
        livro.setIsbn( livroDTO.getIsbn() );
        livro.setQuantidade( livroDTO.getQuantidade() );
        livro.setTitulo( livroDTO.getTitulo() );

        return livro;
    }

    @Override
    public List<LivroDTO> toDTOList(List<Livro> livros) {
        if ( livros == null ) {
            return null;
        }

        List<LivroDTO> list = new ArrayList<LivroDTO>( livros.size() );
        for ( Livro livro : livros ) {
            list.add( toDTO( livro ) );
        }

        return list;
    }
}
