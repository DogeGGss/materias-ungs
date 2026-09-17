package com.universidad.demo.models;

import java.util.List;
import java.util.Map;

public class Materia {
    private String codigo;
    private String nombre;
    private String regimen;
    private int horasSemanales;
    private int horasTotales;
    private boolean aprobada;
    private List<String> correlativasCodigos;
    // Correlativas alternativas para cuando la materia se cursa desde la Tecnicatura.
    // Solo se completa en las materias compartidas ("Ambas") donde la correlativa
    // real es distinta según la carrera (ej: MATD pide ALG y CALC en Licenciatura,
    // pero esas materias no existen en la Tecnicatura). Si es null, se usa la misma
    // lista que correlativasCodigos para las dos carreras.
    private List<String> correlativasTecnicatura;

    // Constructor completo
    public Materia(String codigo, String nombre, String regimen,
                  int horasSemanales, int horasTotales,
                  List<String> correlativasCodigos) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.regimen = regimen;
        this.horasSemanales = horasSemanales;
        this.horasTotales = horasTotales;
        this.correlativasCodigos = correlativasCodigos;
        this.aprobada = false; // Por defecto no aprobada
    }

    // Declara una ruta de correlativas alternativa para cursar esta materia desde la Tecnicatura.
    public Materia conCorrelativasTecnicatura(List<String> correlativasTecnicatura) {
        this.correlativasTecnicatura = correlativasTecnicatura;
        return this;
    }

    // Getters
    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRegimen() {
        return regimen;
    }

    public int getHorasSemanales() {
        return horasSemanales;
    }

    public int getHorasTotales() {
        return horasTotales;
    }

    public boolean isAprobada() {
        return aprobada;
    }

    public List<String> getCorrelativasCodigos() {
        return correlativasCodigos;
    }

    // Método para verificar si se puede cursar.
    // Si la materia tiene una ruta alternativa para Tecnicatura, alcanza con cumplir
    // cualquiera de las dos (la app no distingue en qué carrera está el usuario).
    public boolean puedeCursar(Map<String, Materia> todasLasMaterias, List<String> materiasAprobadas) {
        if (cumpleCorrelativas(correlativasCodigos, materiasAprobadas)) {
            return true;
        }
        return correlativasTecnicatura != null
            && cumpleCorrelativas(correlativasTecnicatura, materiasAprobadas);
    }

    private static boolean cumpleCorrelativas(List<String> correlativas, List<String> materiasAprobadas) {
        // Si no hay correlativas, se puede cursar
        if (correlativas == null || correlativas.isEmpty()) {
            return true;
        }
        // Verificar que todas las correlativas estén aprobadas (comparación case-sensitive y sin espacios)
        return correlativas.stream()
            .map(String::trim) // Eliminar espacios
            .allMatch(codigo -> materiasAprobadas.stream()
                .map(String::trim)
                .anyMatch(aprobada -> aprobada.equals(codigo)));
    }

    // Método para marcar como aprobada
    public void marcarComoAprobada() {
        this.aprobada = true;
    }
}