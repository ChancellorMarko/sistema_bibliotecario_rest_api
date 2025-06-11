package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entities.Cliente;
import com.example.demo.Entities.Emprestimo;
import com.example.demo.Entities.Multa;
import com.example.demo.dto.MultaDTO;
import com.example.demo.mapper.MultaMapper;
import com.example.demo.repository.IClienteRepository;
import com.example.demo.repository.IEmprestimoRepository;
import com.example.demo.repository.IMultaRepository;

@Service
public class MultaService {

    private static final Logger logger = LoggerFactory.getLogger(MultaService.class);
    private static final BigDecimal VALOR_MULTA_POR_DIA = new BigDecimal("2.50"); // R$ 2,50 por dia

    @Autowired
    private IMultaRepository multaRepository;

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private IEmprestimoRepository emprestimoRepository;

    @Autowired
    private MultaMapper multaMapper;

    // Buscar por ID
    public Optional<MultaDTO> buscarPorId(Long id) {
        logger.info("Buscando multa com ID: {}", id);
        
        try {
            if (id == null) {
                logger.warn("ID da multa é nulo");
                return Optional.empty();
            }
            
            Optional<Multa> multaOpt = multaRepository.findById(id);
            
            if (multaOpt.isPresent()) {
                Multa multa = multaOpt.get();
                logger.info("Multa encontrada: ID={}, clienteId={}, valor={}, paga={}", 
                    multa.getId(), 
                    multa.getCliente() != null ? multa.getCliente().getId() : "null", 
                    multa.getValor(),
                    multa.getDataPagamento() != null ? "SIM" : "NÃO");
                
                MultaDTO dto = multaMapper.toDTO(multa);
                logger.info("MultaDTO convertido: {}", dto);
                return Optional.of(dto);
            } else {
                logger.warn("Multa não encontrada com ID: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar multa com ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar multa: " + e.getMessage(), e);
        }
    }

    // Salvar multa
    public MultaDTO salvar(MultaDTO multaDTO) {
        logger.info("Salvando multa: {}", multaDTO);
        try {
            // Buscar cliente
            Cliente cliente = clienteRepository.findById(multaDTO.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + multaDTO.getClienteId()));

            // Buscar empréstimo
            Emprestimo emprestimo = emprestimoRepository.findById(multaDTO.getEmprestimoId())
                    .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado com ID: " + multaDTO.getEmprestimoId()));

            // Criar multa
            Multa multa = new Multa();
            multa.setCliente(cliente);
            multa.setEmprestimo(emprestimo);
            multa.setValor(multaDTO.getValor());
            multa.setDataMulta(multaDTO.getDataMulta() != null ? multaDTO.getDataMulta() : LocalDateTime.now());
            multa.setDataPagamento(multaDTO.getDataPagamento());

            // Salvar e retornar DTO
            Multa multaSalva = multaRepository.save(multa);
            logger.info("Multa salva com sucesso: {}", multaSalva);
            return multaMapper.toDTO(multaSalva);
        } catch (Exception e) {
            logger.error("Erro ao salvar multa: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao salvar multa: " + e.getMessage(), e);
        }
    }

    // Criar multa automaticamente por atraso
    public MultaDTO criarMultaPorAtraso(Long emprestimoId) {
        logger.info("Criando multa por atraso para empréstimo ID: {}", emprestimoId);
        
        try {
            Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                    .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado com ID: " + emprestimoId));

            // Verificar se já existe multa para este empréstimo
            List<Multa> multasExistentes = multaRepository.findByEmprestimoId(emprestimoId);
            if (!multasExistentes.isEmpty()) {
                logger.warn("Já existe multa para o empréstimo ID: {}", emprestimoId);
                return multaMapper.toDTO(multasExistentes.get(0));
            }

            // Calcular dias de atraso
            LocalDateTime agora = LocalDateTime.now();
            LocalDateTime dataDevolucao = emprestimo.getDataDevolucao();
            
            if (dataDevolucao.isAfter(agora)) {
                throw new RuntimeException("Empréstimo não está em atraso");
            }

            long diasAtraso = ChronoUnit.DAYS.between(dataDevolucao, agora);
            BigDecimal valorMulta = VALOR_MULTA_POR_DIA.multiply(new BigDecimal(diasAtraso));

            // Criar multa
            Multa multa = new Multa();
            multa.setCliente(emprestimo.getCliente());
            multa.setEmprestimo(emprestimo);
            multa.setValor(valorMulta);
            multa.setDataMulta(agora);

            Multa multaSalva = multaRepository.save(multa);
            logger.info("Multa por atraso criada: valor={}, dias={}", valorMulta, diasAtraso);
            
            return multaMapper.toDTO(multaSalva);
        } catch (Exception e) {
            logger.error("Erro ao criar multa por atraso: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao criar multa por atraso: " + e.getMessage(), e);
        }
    }

    // Marcar multa como paga
    public MultaDTO marcarComoPaga(Long id) {
        logger.info("Marcando multa como paga: {}", id);
        try {
            Multa multa = multaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Multa não encontrada com ID: " + id));

            multa.setDataPagamento(LocalDateTime.now());

            Multa multaAtualizada = multaRepository.save(multa);
            logger.info("Multa marcada como paga: {}", multaAtualizada);
            return multaMapper.toDTO(multaAtualizada);
        } catch (Exception e) {
            logger.error("Erro ao marcar multa como paga: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao marcar multa como paga: " + e.getMessage(), e);
        }
    }

    // Atualizar multa
    public MultaDTO atualizar(Long id, MultaDTO multaDTO) {
        logger.info("Atualizando multa com ID {}: {}", id, multaDTO);
        try {
            Multa multaExistente = multaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Multa não encontrada com ID: " + id));

            multaExistente.setValor(multaDTO.getValor());
            multaExistente.setDataMulta(multaDTO.getDataMulta());
            multaExistente.setDataPagamento(multaDTO.getDataPagamento());

            Multa multaAtualizada = multaRepository.save(multaExistente);
            logger.info("Multa atualizada com sucesso: {}", multaAtualizada);
            return multaMapper.toDTO(multaAtualizada);
        } catch (Exception e) {
            logger.error("Erro ao atualizar multa com ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro ao atualizar multa: " + e.getMessage(), e);
        }
    }

    // Deletar multa
    public void deletar(Long id) {
        logger.info("Deletando multa com ID: {}", id);
        try {
            Multa multa = multaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Multa não encontrada com ID: " + id));
            
            multaRepository.delete(multa);
            logger.info("Multa deletada com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao deletar multa com ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro ao deletar multa: " + e.getMessage(), e);
        }
    }

    // Listar todas as multas
    public List<MultaDTO> listarTodos() {
        logger.info("Listando todas as multas");
        try {
            List<Multa> multas = multaRepository.findAll();
            logger.info("Total de multas encontradas: {}", multas.size());
            return multaMapper.toDTOList(multas);
        } catch (Exception e) {
            logger.error("Erro ao listar multas: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao listar multas: " + e.getMessage(), e);
        }
    }

    // Listar multas por cliente
    public List<MultaDTO> listarPorCliente(Long clienteId) {
        logger.info("Listando multas para o cliente com ID: {}", clienteId);
        try {
            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + clienteId));

            List<Multa> multas = multaRepository.findByClienteId(clienteId);
            logger.info("Total de multas encontradas para o cliente {}: {}", clienteId, multas.size());
            return multaMapper.toDTOList(multas);
        } catch (Exception e) {
            logger.error("Erro ao listar multas do cliente {}: {}", clienteId, e.getMessage(), e);
            throw new RuntimeException("Erro ao listar multas do cliente: " + e.getMessage(), e);
        }
    }

    // Listar multas pendentes (não pagas)
    public List<MultaDTO> listarMultasPendentes() {
        logger.info("Listando multas pendentes");
        try {
            List<Multa> multas = multaRepository.findByDataPagamentoIsNull();
            logger.info("Total de multas pendentes encontradas: {}", multas.size());
            return multaMapper.toDTOList(multas);
        } catch (Exception e) {
            logger.error("Erro ao listar multas pendentes: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao listar multas pendentes: " + e.getMessage(), e);
        }
    }

    // Listar multas pagas
    public List<MultaDTO> listarMultasPagas() {
        logger.info("Listando multas pagas");
        try {
            List<Multa> multas = multaRepository.findByDataPagamentoIsNotNull();
            logger.info("Total de multas pagas encontradas: {}", multas.size());
            return multaMapper.toDTOList(multas);
        } catch (Exception e) {
            logger.error("Erro ao listar multas pagas: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao listar multas pagas: " + e.getMessage(), e);
        }
    }

    // Listar multas pendentes por cliente
    public List<MultaDTO> listarMultasPendentesPorCliente(Long clienteId) {
        logger.info("Listando multas pendentes para o cliente com ID: {}", clienteId);
        try {
            List<Multa> multas = multaRepository.findByClienteIdAndDataPagamentoIsNull(clienteId);
            logger.info("Total de multas pendentes encontradas para o cliente {}: {}", clienteId, multas.size());
            return multaMapper.toDTOList(multas);
        } catch (Exception e) {
            logger.error("Erro ao listar multas pendentes do cliente {}: {}", clienteId, e.getMessage(), e);
            throw new RuntimeException("Erro ao listar multas pendentes do cliente: " + e.getMessage(), e);
        }
    }

    // Verificar se cliente tem multas pendentes
    public boolean clienteTemMultasPendentes(Long clienteId) {
        logger.info("Verificando se cliente {} tem multas pendentes", clienteId);
        try {
            boolean temMultas = multaRepository.clienteTemMultasPendentes(clienteId);
            logger.info("Cliente {} {} multas pendentes", clienteId, temMultas ? "TEM" : "NÃO TEM");
            return temMultas;
        } catch (Exception e) {
            logger.error("Erro ao verificar multas pendentes do cliente {}: {}", clienteId, e.getMessage(), e);
            throw new RuntimeException("Erro ao verificar multas pendentes: " + e.getMessage(), e);
        }
    }
}
