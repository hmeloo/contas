package br.com.contas.controller;

import br.com.contas.model.dto.ContaDTO;
import br.com.contas.service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @PostMapping
    public ResponseEntity<ContaDTO> cadastrarConta(@RequestBody ContaDTO contaDTO) {
        ContaDTO novaContaDTO = contaService.cadastrarConta(contaDTO);
        return new ResponseEntity<>(novaContaDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaDTO> atualizarConta(@PathVariable Long id, @RequestBody ContaDTO contaDTO) {
        ContaDTO contaAtualizadaDTO = contaService.atualizarConta(id, contaDTO);
        return ResponseEntity.ok(contaAtualizadaDTO);
    }

    @PatchMapping("/{id}/situacao")
    public ResponseEntity<Void> alterarSituacao(@PathVariable Long id, @RequestParam String situacao) {
        contaService.alterarSituacao(id, situacao);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ContaDTO>> obterContas(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam String descricao,
            Pageable pageable) {
        Page<ContaDTO> contasDTO = contaService.obterContas(startDate, endDate, descricao, pageable);
        return ResponseEntity.ok(contasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaDTO> obterContaPorId(@PathVariable Long id) {
        ContaDTO contaDTO = contaService.obterContaPorId(id);
        return ResponseEntity.ok(contaDTO);
    }

    @GetMapping("/total-pago")
    public ResponseEntity<BigDecimal> obterTotalPagoPorPeriodo(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        BigDecimal totalPago = contaService.obterTotalPagoPorPeriodo(startDate, endDate);
        return ResponseEntity.ok(totalPago);
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        String responseMessage = contaService.processarUploadCSV(file);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
