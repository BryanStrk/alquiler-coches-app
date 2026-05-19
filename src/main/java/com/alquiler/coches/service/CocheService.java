package com.alquiler.coches.service;

import com.alquiler.coches.dto.CocheRequestDTO;
import com.alquiler.coches.dto.CocheResponseDTO;
import com.alquiler.coches.entity.EstadoCoche;
import com.alquiler.coches.entity.TipoCombustible;
import org.jspecify.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Operaciones de negocio sobre la flota de coches.
 */
public interface CocheService {

    List<CocheResponseDTO> findAll(@Nullable TipoCombustible tipo,
                                   @Nullable EstadoCoche estado,
                                   @Nullable String marca);

    CocheResponseDTO findById(Long id);

    List<CocheResponseDTO> findDisponibles();

    CocheResponseDTO create(CocheRequestDTO request, @Nullable List<MultipartFile> imagenes);

    CocheResponseDTO addImages(Long id, List<MultipartFile> imagenes);

    CocheResponseDTO update(Long id, CocheRequestDTO request);

    void delete(Long id);
}
