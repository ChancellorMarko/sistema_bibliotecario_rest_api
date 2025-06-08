package com.example.demo.service;


import com.example.demo.Entities.Manutencao;
import com.example.demo.Entities.Livro;
import com.example.demo.repository.IManutencaoRepository;
import com.example.demo.repository.ILivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class ManutencaoService {


@Autowired
private IManutencaoRepository manutencaoRepository;


@Autowired
private ILivroRepository livroRepository;


public Manutencao salvar(Manutencao manutencao) {
    Livro livro = 
    livroRepository.findById(manutencao.getLivro().getId())
    .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
    manutencao.setLivro(livro);
    return manutencaoRepository.save(manutencao);
}


public List<Manutencao> listarEmAndamento() {
    return manutencaoRepository.findByStatus("Em andamento");
}


public List<Manutencao> listarPorLivro(Long livroId) {
    return manutencaoRepository.findByLivroId(livroId);
}


public Manutencao concluir(Long id) {
    Manutencao manutencao = manutencaoRepository.findById(id)
    .orElseThrow(() -> new RuntimeException("Manutenção não encontrada"));
    manutencao.setStatus("Concluída");
    manutencao.setDataFim(java.time.LocalDateTime.now());
    return manutencaoRepository.save(manutencao);
}
}
