package com.alquiler.coches.service;

import com.alquiler.coches.dto.CocheRequestDTO;
import com.alquiler.coches.dto.CocheResponseDTO;
import com.alquiler.coches.dto.UploadResponse;
import com.alquiler.coches.entity.Coche;
import com.alquiler.coches.entity.EstadoCoche;
import com.alquiler.coches.entity.TipoCombustible;
import com.alquiler.coches.exception.CocheNotFoundException;
import com.alquiler.coches.repository.CocheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CocheServiceImpl implements CocheService {

    private final CocheRepository cocheRepository;
    private final CloudinaryService cloudinaryService;

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
    public CocheResponseDTO create(CocheRequestDTO request,
                                   @Nullable List<MultipartFile> imagenes) {
        // El DTO ya viene validado por @Valid en el controller.
        List<String> uploadedPublicIds = new ArrayList<>();
        try {
            Coche coche = new Coche();
            applyRequest(coche, request);

            List<String> secureUrls = uploadAll(imagenes, uploadedPublicIds);
            // Acumula sobre las imageUrls que pudieran venir en el JSON.
            coche.getImageUrls().addAll(secureUrls);

            // saveAndFlush para que un fallo de persistencia salte aquí dentro
            // y podamos revertir las imágenes ya subidas a Cloudinary.
            return toResponse(cocheRepository.saveAndFlush(coche));
        } catch (RuntimeException ex) {
            rollbackUploads(uploadedPublicIds);
            throw ex;
        }
    }

    @Override
    @Transactional
    public CocheResponseDTO addImages(Long id, List<MultipartFile> imagenes) {
        // Resolvemos el coche ANTES de subir nada: si no existe -> 404 y cero huérfanos.
        Coche coche = getOrThrow(id);

        List<String> uploadedPublicIds = new ArrayList<>();
        try {
            List<String> secureUrls = uploadAll(imagenes, uploadedPublicIds);
            coche.getImageUrls().addAll(secureUrls); // acumular, no reemplazar
            return toResponse(cocheRepository.saveAndFlush(coche));
        } catch (RuntimeException ex) {
            // Solo revierte las recién subidas; las preexistentes del coche se respetan.
            rollbackUploads(uploadedPublicIds);
            throw ex;
        }
    }

    /**
     * Sube todas las imágenes a Cloudinary acumulando sus public_id en
     * {@code uploadedPublicIds} (para un eventual rollback) y devuelve sus secure_url.
     */
    private List<String> uploadAll(@Nullable List<MultipartFile> imagenes,
                                    List<String> uploadedPublicIds) {
        List<String> secureUrls = new ArrayList<>();
        if (imagenes == null || imagenes.isEmpty()) {
            return secureUrls;
        }
        for (MultipartFile file : imagenes) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            UploadResponse uploaded = cloudinaryService.uploadImage(file);
            uploadedPublicIds.add(uploaded.publicId());
            secureUrls.add(uploaded.secureUrl());
        }
        return secureUrls;
    }

    /**
     * Borra de Cloudinary cada imagen ya subida. Un fallo individual de borrado
     * se registra como WARN y no impide intentar limpiar las demás.
     */
    private void rollbackUploads(List<String> publicIds) {
        for (String publicId : publicIds) {
            try {
                cloudinaryService.deleteImage(publicId);
                log.debug("Rollback: imagen {} eliminada de Cloudinary", publicId);
            } catch (RuntimeException ex) {
                log.warn("Rollback: no se pudo eliminar la imagen {} de Cloudinary: {}",
                        publicId, ex.getMessage());
            }
        }
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
