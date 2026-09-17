package com.universidad.demo.services;

import com.universidad.demo.models.Materia;
import com.universidad.demo.models.MateriaAprobada;
import com.universidad.demo.models.Usuario;
import com.universidad.demo.repositories.MateriaAprobadaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MateriaService {

    private final Map<String, Materia> todasLasMaterias;
    private final MateriaAprobadaRepository materiaAprobadaRepository;

    public MateriaService(Map<String, Materia> todasLasMaterias, MateriaAprobadaRepository materiaAprobadaRepository) {
        this.todasLasMaterias = todasLasMaterias;
        this.materiaAprobadaRepository = materiaAprobadaRepository;
    }
    
    // Método para obtener todas las materias
    public Map<String, Materia> obtenerTodasLasMaterias() {
        return todasLasMaterias;
    }

    // Método para obtener las materias disponibles para cursar
    public List<Materia> obtenerMateriasDisponibles(Usuario usuario) {
        List<String> materiasAprobadas = obtenerMateriasAprobadas(usuario.getUsername());

        // Filtrar las materias disponibles
        return todasLasMaterias.values().stream()
                .filter(materia -> !materiasAprobadas.contains(materia.getCodigo())) // Excluir materias aprobadas
                .filter(materia -> puedeCursarAhora(materia, materiasAprobadas))
                .collect(Collectors.toList());
    }

    // Si una materia se puede cursar ya mismo, dadas las materias aprobadas.
    // Centraliza el caso especial de LABI (requiere 14 materias de Licenciatura,
    // no correlativas puntuales) para que tanto "materias disponibles" como la
    // estimación de semestres usen exactamente la misma regla.
    private boolean puedeCursarAhora(Materia materia, List<String> materiasAprobadas) {
        if ("LABI".equals(materia.getCodigo())) {
            long materiasLicenciaturaAprobadas = materiasAprobadas.stream()
                .filter(todasLasMaterias::containsKey)
                .count();
            return materiasLicenciaturaAprobadas >= 14;
        }
        return materia.puedeCursar(todasLasMaterias, materiasAprobadas);
    }

    // Estima cuántos semestres faltan para terminar un plan, cursando como máximo
    // "ritmoPorSemestre" materias por semestre.
    public int estimarSemestresRestantes(Collection<String> codigosDelPlan, List<String> materiasAprobadas, int ritmoPorSemestre) {
        return planificarCamino(codigosDelPlan, materiasAprobadas, ritmoPorSemestre).size();
    }

    // Arma el camino semestre a semestre para terminar un plan (el conjunto de
    // códigos que lo componen), cursando como máximo "ritmoPorSemestre" materias
    // por semestre y respetando correlativas. En cada semestre elige, entre las
    // disponibles, las que más otras materias pendientes desbloquean directamente
    // — así prioriza destrabar el resto del plan en vez de tomar materias al azar.
    public List<List<Materia>> planificarCamino(Collection<String> codigosDelPlan, List<String> materiasAprobadas, int ritmoPorSemestre) {
        List<String> aprobadasSimuladas = new ArrayList<>(materiasAprobadas);
        List<String> pendientes = codigosDelPlan.stream()
            .filter(codigo -> !aprobadasSimuladas.contains(codigo))
            .collect(Collectors.toList());

        List<List<Materia>> camino = new ArrayList<>();
        // Límite de seguridad por si el plan tuviera correlativas circulares o datos inconsistentes.
        while (!pendientes.isEmpty() && camino.size() < 200) {
            List<String> pendientesActuales = pendientes;
            List<Materia> disponibles = pendientes.stream()
                .map(todasLasMaterias::get)
                .filter(materia -> materia != null && puedeCursarAhora(materia, aprobadasSimuladas))
                .sorted(Comparator.comparingInt(
                    (Materia materia) -> contarDependientesDirectos(materia.getCodigo(), pendientesActuales)
                ).reversed())
                .collect(Collectors.toList());

            if (disponibles.isEmpty()) {
                break; // no debería pasar con un plan consistente
            }

            List<Materia> tomar = disponibles.stream()
                .limit(ritmoPorSemestre)
                .collect(Collectors.toList());
            List<String> codigosTomados = tomar.stream().map(Materia::getCodigo).collect(Collectors.toList());

            aprobadasSimuladas.addAll(codigosTomados);
            pendientes.removeAll(codigosTomados);
            camino.add(tomar);
        }
        return camino;
    }

    private int contarDependientesDirectos(String codigo, List<String> pendientes) {
        int cantidad = 0;
        for (String otroCodigo : pendientes) {
            Materia otra = todasLasMaterias.get(otroCodigo);
            if (otra != null && otra.getCorrelativasCodigos() != null && otra.getCorrelativasCodigos().contains(codigo)) {
                cantidad++;
            }
        }
        return cantidad;
    }
    // Método para obtener las materias aprobadas de un usuario
    public List<String> obtenerMateriasAprobadas(String username) {
        return materiaAprobadaRepository.findByUsername(username)
                .stream()
                .map(MateriaAprobada::getMateriaCodigo)
                .collect(Collectors.toList());
    }
    @Transactional
    public void eliminarMateriaAprobada(String username, String materiaCodigo) {
        // Buscar la materia aprobada por usuario y código
        List<MateriaAprobada> materias = materiaAprobadaRepository.findByUsername(username);
        materias.stream()
                .filter(materia -> materia.getMateriaCodigo().equals(materiaCodigo))
                .findFirst()
                .ifPresent(materiaAprobadaRepository::delete);
    }

    // Método para actualizar las materias aprobadas de un usuario
    @Transactional
    public void actualizarMateriasAprobadas(String username, List<String> materiasSeleccionadas) {
        // Obtener las materias aprobadas actuales del usuario
        List<String> materiasAprobadasActuales = materiaAprobadaRepository.findByUsername(username)
                .stream()
                .map(MateriaAprobada::getMateriaCodigo)
                .collect(Collectors.toList());

        // Combinar las materias actuales con las nuevas seleccionadas
        if (materiasSeleccionadas != null) {
            materiasAprobadasActuales.addAll(materiasSeleccionadas);
        }

        // Eliminar duplicados
        List<String> materiasUnicas = materiasAprobadasActuales.stream()
                .distinct()
                .collect(Collectors.toList());

        // Eliminar todas las materias aprobadas actuales del usuario
        materiaAprobadaRepository.deleteByUsername(username);

        // Guardar la lista combinada de materias aprobadas
        List<MateriaAprobada> nuevasMaterias = materiasUnicas.stream()
                .map(codigo -> new MateriaAprobada(username, codigo))
                .collect(Collectors.toList());
        if (!nuevasMaterias.isEmpty()) {
            materiaAprobadaRepository.saveAll(nuevasMaterias);
        }
    }
}
