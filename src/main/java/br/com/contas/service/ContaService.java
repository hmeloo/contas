package br.com.contas.service;

import br.com.contas.model.dto.ContaDTO;
import br.com.contas.model.entity.Conta;
import br.com.contas.model.mapper.ContaMapper;
import br.com.contas.exception.ResourceNotFoundException;
import br.com.contas.repository.ContaRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ContaMapper contaMapper;

    public ContaDTO cadastrarConta(ContaDTO contaDTO) {
        Conta conta = contaMapper.toEntity(contaDTO);
        Conta novaConta = contaRepository.save(conta);
        return contaMapper.toDTO(novaConta);
    }

    public ContaDTO atualizarConta(Long id, ContaDTO contaDTO) {
        Conta contaExistente = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com ID " + id));

        contaExistente.setDataVencimento(contaDTO.getDataVencimento());
        contaExistente.setDataPagamento(contaDTO.getDataPagamento());
        contaExistente.setValor(contaDTO.getValor());
        contaExistente.setDescricao(contaDTO.getDescricao());
        contaExistente.setSituacao(contaDTO.getSituacao());

        Conta contaAtualizada = contaRepository.save(contaExistente);
        return contaMapper.toDTO(contaAtualizada);
    }

    public void alterarSituacao(Long id, String situacao) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com ID " + id));
        conta.setSituacao(situacao);
        contaRepository.save(conta);
    }

    public Page<ContaDTO> obterContas(LocalDate startDate, LocalDate endDate, String descricao, Pageable pageable) {
        Page<Conta> contas = contaRepository.findByDataVencimentoBetweenAndDescricaoContaining(startDate, endDate, descricao, pageable);
        return contas.map(contaMapper::toDTO);
    }

    public ContaDTO obterContaPorId(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com ID " + id));
        return contaMapper.toDTO(conta);
    }

    public BigDecimal obterTotalPagoPorPeriodo(LocalDate startDate, LocalDate endDate) {
        return contaRepository.findByDataVencimentoBetween(startDate, endDate)
                .stream()
                .map(Conta::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void saveAll(List<Conta> contas) {
        contaRepository.saveAll(contas);
    }

    public String processarUploadCSV(MultipartFile file) {
        if (file.isEmpty()) {
            return "Por favor, faça o upload de um arquivo CSV!";
        }

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            List<Conta> contas = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                Conta conta = new Conta();
                conta.setDataVencimento(LocalDate.parse(csvRecord.get("data_vencimento")));
                conta.setDataPagamento(csvRecord.get("data_pagamento").isEmpty() ? null : LocalDate.parse(csvRecord.get("data_pagamento")));
                conta.setValor(new BigDecimal(csvRecord.get("valor")));
                conta.setDescricao(csvRecord.get("descricao"));
                conta.setSituacao(csvRecord.get("situacao"));
                contas.add(conta);
            }

            saveAll(contas);
            return "Arquivo CSV enviado com sucesso!";
        } catch (IOException e) {
            return "Erro ao processar o arquivo CSV";
        }
    }
}