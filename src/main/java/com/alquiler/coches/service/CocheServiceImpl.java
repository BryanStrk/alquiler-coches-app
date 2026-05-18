package com.alquiler.coches.service;

import com.alquiler.coches.dto.CocheRequestDTO;
import com.alquiler.coches.dto.CocheResponseDTO;
import com.alquiler.coches.entity.Coche;
import com.alquiler.coches.entity.EstadoCoche;
import com.alquiler.coches.entity.TipoCombustible;
import com.alquiler.coches.exception.CocheNotFoundException;
import com.alquiler.coches.repository.CocheRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CocheServiceImpl implements CocheService {

    private final CocheRepository cocheRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CocheResponseDTO> findAll(@Nullable TipoCombustible tipo,
                                          @Nullable EstadoCoche estado,
                                          @Nullable String marca) {
        List<Coche> coches;
        if (estado != null && tipo != null) {
            coches = cocheRepository.findByEstadoAndTipoCombustible(estado, tipo);
        } else if (estado != null) {
            coches = cocheRepository.findByEstado(estado);
        } else if (tipo != null) {
            coches = cocheRepository.findByTipoCombustible(tipo);
        } else if (marca != null && !marca.isBlank()) {
            coches = cocheRepository.findByMarcaContainingIgnoreCase(marca);
        } else {
            coches = cocheRepository.findAll();
        }
        if (marca != null && !marca.isBlank()) {
            String needle = marca.toLowerCase();
            coches = coches.stream()
                    .filter(c -> c.getMarca().toLowerCase().contains(needle))
                    .toList();
        }
        return coches.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CocheResponseDTO findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CocheResponseDTO> findDisponibles() {
        return cocheRepository.findByEstado(EstadoCoche.DISPONIBLE).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CocheResponseDTO create(CocheRequestDTO request) {
        Coche coche = new Coche();
        applyRequest(coche, request);
        return toResponse(cocheRepository.save(coche));
    }

    @Override
    @Transactional
    public CocheResponseDTO update(Long id, CocheRequestDTO request) {
        Coche coche = getOrThrow(id);
        applyRequest(coche, request);
        return toResponse(cocheRepository.save(coche));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!cocheRepository.existsById(id)) {
            throw new CocheNotFoundException(id);
        }
        cocheRepository.deleteById(id);
    }

    private Coche getOrThrow(Long id) {
        return cocheRepository.findById(id)
                .orElseThrow(() -> new CocheNotFoundException(id));
    }

    private void applyRequest(Coche coche, CocheRequestDTO r) {
        coche.setMarca(r.marca());
        coche.setModelo(r.modelo());
        coche.setMatricula(r.matricula());
        coche.setAnio(r.anio());
        coche.setPrecioPorDia(r.precioPorDia());
        coche.setTipoCombustible(r.tipoCombustible());
        coche.setEstado(r.estado());
        coche.setKilometros(r.kilometros());
        coche.setDescripcion(r.descripcion());
        coche.setImageUrls(r.imageUrls() != null
                ? new ArrayList<>(r.imageUrls())
                : new ArrayList<>());
    }

    private CocheResponseDTO toResponse(Coche c) {
        return new CocheResponseDTO(
                c.getId(),
                c.getMarca(),
                c.getModelo(),
                c.getMatricula(),
                c.getAnio(),
                c.getPrecioPorDia(),
                c.getTipoCombustible(),
                c.getEstado(),
                c.getKilometros(),
                c.getDescripcion(),
                c.getImageUrls() != null ? c.getImageUrls() : List.of(),
                c.getCreatedAt(),
                c.getUpdatedAt());
    }
}
