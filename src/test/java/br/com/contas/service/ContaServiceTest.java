package br.com.contas.service;

import br.com.contas.model.dto.ContaDTO;
import br.com.contas.model.entity.Conta;
import br.com.contas.model.mapper.ContaMapper;
import br.com.contas.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContaServiceTest {

    @InjectMocks
    private ContaService contaService;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ContaMapper contaMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCadastrarConta() {
        ContaDTO contaDTO = new ContaDTO();
        contaDTO.setDataVencimento(LocalDate.now());
        contaDTO.setValor(new BigDecimal("100.00"));
        contaDTO.setSituacao("PENDENTE");

        Conta conta = new Conta();
        when(contaMapper.toEntity(any(ContaDTO.class))).thenReturn(conta);
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);
        when(contaMapper.toDTO(any(Conta.class))).thenReturn(contaDTO);

        ContaDTO result = contaService.cadastrarConta(contaDTO);

        assertNotNull(result);
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    void testAtualizarConta() {
        Long id = 1L;
        ContaDTO contaDTO = new ContaDTO();
        contaDTO.setDataVencimento(LocalDate.now());
        contaDTO.setValor(new BigDecimal("100.00"));
        contaDTO.setSituacao("PENDENTE");

        Conta conta = new Conta();
        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);
        when(contaMapper.toDTO(any(Conta.class))).thenReturn(contaDTO);

        ContaDTO result = contaService.atualizarConta(id, contaDTO);

        assertNotNull(result);
        verify(contaRepository, times(1)).findById(id);
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    void testAlterarSituacao() {
        Long id = 1L;
        String novaSituacao = "PAGA";

        Conta conta = new Conta();
        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));

        contaService.alterarSituacao(id, novaSituacao);

        assertEquals(novaSituacao, conta.getSituacao());
        verify(contaRepository, times(1)).findById(id);
        verify(contaRepository, times(1)).save(conta);
    }

    @Test
    void testObterContaPorId() {
        Long id = 1L;
        Conta conta = new Conta();
        ContaDTO contaDTO = new ContaDTO();
        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
        when(contaMapper.toDTO(any(Conta.class))).thenReturn(contaDTO);

        ContaDTO result = contaService.obterContaPorId(id);

        assertNotNull(result);
        verify(contaRepository, times(1)).findById(id);
    }

    @Test
    void testObterTotalPagoPorPeriodo() {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();

        Conta conta1 = new Conta();
        conta1.setValor(new BigDecimal("100.00"));
        Conta conta2 = new Conta();
        conta2.setValor(new BigDecimal("200.00"));
        List<Conta> contas = Arrays.asList(conta1, conta2);

        when(contaRepository.findByDataVencimentoBetween(startDate, endDate)).thenReturn(contas);

        BigDecimal result = contaService.obterTotalPagoPorPeriodo(startDate, endDate);

        assertEquals(new BigDecimal("300.00"), result);
        verify(contaRepository, times(1)).findByDataVencimentoBetween(startDate, endDate);
    }

    @Test
    void testObterContas() {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();
        String descricao = "Descricao";

        Pageable pageable = PageRequest.of(0, 10);
        Conta conta = new Conta();
        List<Conta> contas = List.of(conta);
        Page<Conta> page = new PageImpl<>(contas, pageable, contas.size());

        ContaDTO contaDTO = new ContaDTO();
        when(contaRepository.findByDataVencimentoBetweenAndDescricaoContaining(startDate, endDate, descricao, pageable)).thenReturn(page);
        when(contaMapper.toDTO(any(Conta.class))).thenReturn(contaDTO);

        Page<ContaDTO> result = contaService.obterContas(startDate, endDate, descricao, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(contaRepository, times(1)).findByDataVencimentoBetweenAndDescricaoContaining(startDate, endDate, descricao, pageable);
    }
}
