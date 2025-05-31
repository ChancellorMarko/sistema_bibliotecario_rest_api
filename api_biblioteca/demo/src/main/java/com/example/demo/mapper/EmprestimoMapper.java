package com.example.demo.mapper;

import java.util.List;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.Entities.Emprestimo;
import com.example.demo.dto.EmprestimoDTO;

@Mapper(componentModel = "spring", uses = {ClienteMapper.class, LivroMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EmprestimoMapper {
    // Mapeando explicitamente os nomes das variáveis
    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "livroId", source = "livro.id")
    EmprestimoDTO toDTO(Emprestimo emprestimo);

    // Ignorando o mapeamento para não gerar erros ao tentar converter IDs Long para Classe Livro ou cliente
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "livro", ignore = true)
    @Mapping(target = "dataEmprestimo", ignore = true)
    Emprestimo toEntity(EmprestimoDTO emprestimoDTO);

    List<EmprestimoDTO> toDTOList(List<Emprestimo> emprestimos);
}