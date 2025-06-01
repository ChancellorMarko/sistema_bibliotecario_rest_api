package com.example.demo.mapper;


import org.mapstruct.Mapper;
import com.example.demo.Entities.Livro;
import com.example.demo.dto.LivroDTO;
import java.util.List;


@Mapper(componentModel = "spring")
public interface LivroMapper {
    LivroDTO toDTO(Livro livro);
    Livro toEntity(LivroDTO livroDTO);
    List<LivroDTO> toDTOList(List<Livro> livros);
}
