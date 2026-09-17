package com.universidad.demo.services;

import com.universidad.demo.models.Materia;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MateriaServiceTest {

    // Plan sintético: A sin correlativas; B y C requieren A; D requiere B y C.
    private Map<String, Materia> planDePrueba() {
        return Map.of(
            "A", new Materia("A", "Materia A", "Semestral", 1, 1, List.of()),
            "B", new Materia("B", "Materia B", "Semestral", 1, 1, List.of("A")),
            "C", new Materia("C", "Materia C", "Semestral", 1, 1, List.of("A")),
            "D", new Materia("D", "Materia D", "Semestral", 1, 1, List.of("B", "C"))
        );
    }

    @Test
    void unaMateriaPorSemestreRespetaElOrdenDeCorrelativas() {
        MateriaService service = new MateriaService(planDePrueba(), null);

        // A (sem 1) -> B (sem 2) -> C (sem 3) -> D (sem 4): 4 semestres.
        int semestres = service.estimarSemestresRestantes(planDePrueba().keySet(), List.of(), 1);

        assertEquals(4, semestres);
    }

    @Test
    void masRitmoAprovechaMateriasEnParalelo() {
        MateriaService service = new MateriaService(planDePrueba(), null);

        // A sola (sem 1, B y C todavía no habilitadas) -> B y C juntas (sem 2) -> D (sem 3).
        int semestres = service.estimarSemestresRestantes(planDePrueba().keySet(), List.of(), 2);

        assertEquals(3, semestres);
    }

    @Test
    void noCuentaLoQueYaEstaAprobado() {
        MateriaService service = new MateriaService(planDePrueba(), null);

        int semestres = service.estimarSemestresRestantes(planDePrueba().keySet(), List.of("A", "B", "C"), 1);

        assertEquals(1, semestres);
    }

    @Test
    void planCompletoNoNecesitaSemestres() {
        MateriaService service = new MateriaService(planDePrueba(), null);

        int semestres = service.estimarSemestresRestantes(planDePrueba().keySet(), List.of("A", "B", "C", "D"), 3);

        assertEquals(0, semestres);
    }

    @Test
    void elCaminoDetalladoMuestraQueMateriaVaEnCadaSemestre() {
        MateriaService service = new MateriaService(planDePrueba(), null);

        List<List<Materia>> camino = service.planificarCamino(planDePrueba().keySet(), List.of(), 2);

        assertEquals(3, camino.size());
        assertEquals(List.of("A"), codigos(camino.get(0)));
        assertEquals(List.of("B", "C"), codigos(camino.get(1)));
        assertEquals(List.of("D"), codigos(camino.get(2)));
    }

    private List<String> codigos(List<Materia> materias) {
        return materias.stream().map(Materia::getCodigo).sorted().collect(java.util.stream.Collectors.toList());
    }
}
