package com.universidad.demo.models;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MateriaTest {

    @Test
    void sinCorrelativasSiemprePuedeCursarse() {
        Materia materia = new Materia("TIC", "Taller Inicial Común", "Semestral", 3, 48, List.of());

        assertTrue(materia.puedeCursar(Map.of(), List.of()));
    }

    @Test
    void puedeCursarSiTodasLasCorrelativasEstanAprobadas() {
        Materia materia = new Materia("PROG1", "Programación I", "Semestral", 8, 128, List.of("IPROG", "TIC"));

        assertTrue(materia.puedeCursar(Map.of(), List.of("IPROG", "TIC", "IMAT")));
    }

    @Test
    void noPuedeCursarSiFaltaAlgunaCorrelativa() {
        Materia materia = new Materia("PROG1", "Programación I", "Semestral", 8, 128, List.of("IPROG", "TIC"));

        assertFalse(materia.puedeCursar(Map.of(), List.of("IPROG")));
    }

    @Test
    void noPuedeCursarSiNoHayNingunaMateriaAprobada() {
        Materia materia = new Materia("PROG1", "Programación I", "Semestral", 8, 128, List.of("IPROG", "TIC"));

        assertFalse(materia.puedeCursar(Map.of(), List.of()));
    }

    @Test
    void ignoraEspaciosAlComparaCodigos() {
        Materia materia = new Materia("PROG1", "Programación I", "Semestral", 8, 128, List.of(" IPROG ", " TIC"));

        assertTrue(materia.puedeCursar(Map.of(), List.of("IPROG", "TIC ")));
    }

    @Test
    void puedeCursarPorLaRutaDeTecnicaturaAunqueNoCumplaLaGeneral() {
        // MATD en Licenciatura pide LYTN + CALC + ALG, pero en Tecnicatura alcanza con LYTN.
        Materia materia = new Materia("MATD", "Matemática Discreta", "Semestral", 6, 96,
                List.of("LYTN", "CALC", "ALG"))
                .conCorrelativasTecnicatura(List.of("LYTN"));

        assertTrue(materia.puedeCursar(Map.of(), List.of("LYTN")));
    }

    @Test
    void noPuedeCursarSiNoCumpleNingunaDeLasDosRutas() {
        Materia materia = new Materia("MATD", "Matemática Discreta", "Semestral", 6, 96,
                List.of("LYTN", "CALC", "ALG"))
                .conCorrelativasTecnicatura(List.of("LYTN"));

        assertFalse(materia.puedeCursar(Map.of(), List.of("IMAT")));
    }

    @Test
    void puedeCursarPorLaRutaGeneralAunqueNoCumplaLaDeTecnicatura() {
        Materia materia = new Materia("MATD", "Matemática Discreta", "Semestral", 6, 96,
                List.of("LYTN", "CALC", "ALG"))
                .conCorrelativasTecnicatura(List.of("LYTN", "IMAT"));

        assertTrue(materia.puedeCursar(Map.of(), List.of("LYTN", "CALC", "ALG")));
    }

    @Test
    void ingl2PuedeCursarseEnTecnicaturaSinTled() {
        // Mismo caso que MATD: en Licenciatura INGL2 pide INGL1 + TLED, pero TLED
        // no es parte de la Tecnicatura, así que ahí alcanza con INGL1.
        Materia materia = new Materia("INGL2", "Inglés Lectocomprensión II", "Semestral", 3, 48,
                List.of("INGL1", "TLED"))
                .conCorrelativasTecnicatura(List.of("INGL1"));

        assertTrue(materia.puedeCursar(Map.of(), List.of("INGL1")));
    }
}
