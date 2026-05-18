package com.alquiler.coches.repository;

import com.alquiler.coches.entity.Coche;
import com.alquiler.coches.entity.EstadoCoche;
import com.alquiler.coches.entity.TipoCombustible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CocheRepository extends JpaRepository<Coche, Long> {

    List<Coche> findByEstado(EstadoCoche estado);

    List<Coche> findByTipoCombustible(TipoCombustible tipo);

    List<Coche> findByMarcaContainingIgnoreCase(String marca);

    List<Coche> findByEstadoAndTipoCombustible(EstadoCoche estado, TipoCombustible tipo);
}
