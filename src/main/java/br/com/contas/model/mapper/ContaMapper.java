package br.com.contas.model.mapper;

import br.com.contas.model.dto.ContaDTO;
import br.com.contas.model.entity.Conta;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContaMapper {
    ContaMapper INSTANCE = Mappers.getMapper(ContaMapper.class);

    ContaDTO toDTO(Conta conta);
    Conta toEntity(ContaDTO contaDTO);
    List<ContaDTO> toDTOList(List<Conta> contas);
    List<Conta> toEntityList(List<ContaDTO> contaDTOs);
}