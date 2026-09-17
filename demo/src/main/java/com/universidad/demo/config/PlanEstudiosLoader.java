package com.universidad.demo.config;

import com.universidad.demo.models.Materia;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.*;

@Configuration
public class PlanEstudiosLoader {

    @Bean
    public Map<String, Materia> todasLasMaterias() {
        Map<String, Materia> materias = new LinkedHashMap<>(); // Mantiene orden de inserción
        
        // Talleres Iniciales
        materias.put("TIC", new Materia(
            "TIC", 
            "Taller Inicial Común: Taller de Lectura y Escritura",
            "Semestral", 3, 48, Collections.emptyList()));
        
        materias.put("TIO", new Materia(
            "TIO", 
            "Taller Inicial Orientado: Ciencias Exactas",
            "Semestral", 3, 48, Collections.emptyList()));
            
        materias.put("TIO-MAT", new Materia(
            "TIO-MAT", 
            "Taller Inicial Obligatorio del Área de Matemática",
            "Semestral", 3, 48, Collections.emptyList()));
        
        // Primer Año
        materias.put("IPROG", new Materia(
            "IPROG", 
            "Introducción a la Programación",
            "Semestral", 6, 96, List.of("TIO", "TIO-MAT")));
            
        materias.put("IMAT", new Materia(
            "IMAT", 
            "Introducción a la Matemática",
            "Semestral", 8, 128, List.of("TIO", "TIO-MAT")));
            
        materias.put("TLED", new Materia(
            "TLED", 
            "Taller de Lectura y Escritura en las Disciplinas",
            "Semestral", 2, 32, List.of("TIC")));
        
        // Segundo Año
        materias.put("PROG1", new Materia(
            "PROG1", 
            "Programación I",
            "Semestral", 8, 128, List.of("IPROG", "TIC")));
            
        materias.put("LYTN", new Materia(
            "LYTN", 
            "Lógica y Teoría de Números",
            "Semestral", 6, 96, List.of("IMAT")));
            
        materias.put("ORG1", new Materia(
            "ORG1", 
            "Organización del Computador I",
            "Semestral", 6, 96, List.of("IPROG", "TIC")));
            
        materias.put("PROG2", new Materia(
            "PROG2", 
            "Programación II",
            "Semestral", 6, 96, List.of("PROG1", "IMAT")));
            
        materias.put("ALG", new Materia(
            "ALG", 
            "Álgebra Lineal",
            "Semestral", 8, 128, List.of("IMAT", "TIC")));
            
        materias.put("SOR1", new Materia(
            "SOR1", 
            "Sistemas Operativos y Redes I",
            "Semestral", 6, 96, List.of("ORG1", "PROG1")));
        
        // Tercer Año
        materias.put("PROG3", new Materia(
            "PROG3", 
            "Programación III",
            "Semestral", 8, 128, List.of("PROG2")));
            
        materias.put("CALC", new Materia(
            "CALC", 
            "Cálculo para Computación",
            "Semestral", 8, 128, List.of("IMAT", "ALG")));
            
        materias.put("PSC", new Materia(
            "PSC", 
            "Problemas Socioeconómicos Contemporáneos",
            "Semestral", 4, 64, List.of("TIC")));
            
        materias.put("BD1", new Materia(
            "BD1", 
            "Bases de Datos I",
            "Semestral", 8, 128, List.of("LYTN", "PROG2", "ORG1")));
            
        materias.put("MATD", new Materia(
            "MATD",
            "Matemática Discreta",
            "Semestral", 6, 96, List.of("LYTN", "CALC", "ALG"))
            // En Tecnicatura no existen ALG ni CALC: alcanza con LYTN.
            .conCorrelativasTecnicatura(List.of("LYTN")));
            
        materias.put("EVS", new Materia(
            "EVS", 
            "Especificaciones y Verificación de Software",
            "Semestral", 6, 96, List.of("LYTN", "PROG3")));
            
        materias.put("TCOM", new Materia(
            "TCOM", 
            "Teoría de la Computación",
            "Semestral", 6, 96, List.of("PROG3", "MATD", "ORG1")));
            
        materias.put("ING1", new Materia(
            "ING1", 
            "Ingeniería de Software I",
            "Semestral", 6, 96, List.of("PROG3")));
            
        materias.put("PYE", new Materia(
            "PYE", 
            "Probabilidad y Estadística",
            "Semestral", 6, 96, List.of("CALC", "MATD")));
        
        // Cuarto Año
        materias.put("PPS1", new Materia(
            "PPS1",
            "Proyecto Profesional I", // En Tecnicatura: Laboratorio de Construcción de Software
            "Semestral", 8, 128, List.of("PSC", "TLED", "BD1", "ING1", "EVS"))
            // TLED no es parte de la Tecnicatura, así que ahí no puede pedirse como correlativa.
            .conCorrelativasTecnicatura(List.of("PSC", "BD1", "ING1", "EVS")));
            
        materias.put("ING2", new Materia(
            "ING2", 
            "Ingeniería de Software II",
            "Semestral", 6, 96, List.of("ING1", "EVS")));
            
        materias.put("ORG2", new Materia(
            "ORG2", 
            "Organización del Computador II",
            "Semestral", 6, 96, List.of("ORG1")));
            
        materias.put("PPS2", new Materia(
            "PPS2", 
            "Proyecto Profesional II",
            "Semestral", 8, 128, List.of("PPS1")));
            
        materias.put("BD2", new Materia(
            "BD2", 
            "Bases de Datos II",
            "Semestral", 8, 128, List.of("BD1", "PROG3")));
            
        materias.put("SOR2", new Materia(
            "SOR2", 
            "Sistemas Operativos y Redes II",
            "Semestral", 6, 96, List.of("SOR1")));
            
        materias.put("PPS", new Materia(
            "PPS", 
            "Práctica Profesional Supervisada",
            "Semestral", 8, 128, List.of("PPS2", "BD2")));
            
        materias.put("MOD", new Materia(
            "MOD", 
            "Modelado y Optimización",
            "Semestral", 6, 96, List.of("PYE")));
            
        materias.put("ISOC", new Materia(
            "ISOC", 
            "Informática y Sociedad",
            "Semestral", 4, 64, List.of("ING1")));
            
        materias.put("TTES", new Materia(
            "TTES", 
            "Taller de Tesina de Licenciatura",
            "Anual", 4, 128, List.of("PPS2", "BD2")));
            
        materias.put("GPRO", new Materia(
            "GPRO", 
            "Gestión de Proyectos",
            "Semestral", 6, 96, List.of("ING2", "PPS1")));
            
        materias.put("LABI", new Materia(
            "LABI", 
            "Laboratorio Interdisciplinario",
            "Semestral", 4, 64, List.of())); // 14 materias aprobadas
        
        // Otros requisitos
        materias.put("TUTIL", new Materia(
            "TUTIL", 
            "Taller de Utilitarios",
            "Semestral", 2, 32, List.of()));
            
        materias.put("INGL1", new Materia(
            "INGL1", 
            "Inglés Lectocomprensión I",
            "Semestral", 3, 48, List.of("TIC")));
            
        materias.put("INGL2", new Materia(
            "INGL2",
            "Inglés Lectocomprensión II",
            "Semestral", 3, 48, List.of("INGL1", "TLED"))
            // TLED no es parte de la Tecnicatura: ahí alcanza con INGL1.
            .conCorrelativasTecnicatura(List.of("INGL1")));
            
        materias.put("INGL3", new Materia(
            "INGL3", 
            "Inglés Lectocomprensión III",
            "Semestral", 3, 48, List.of("INGL2")));

        return materias;
    }
}