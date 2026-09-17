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
}
