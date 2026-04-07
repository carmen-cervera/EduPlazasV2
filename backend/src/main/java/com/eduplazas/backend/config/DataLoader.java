package com.eduplazas.backend.config;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.repository.*;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    ApplicationRunner loadData(
            ConvocatoriaRepository convocatoriaRepo,
            UniversidadRepository universidadRepo,
            OfertaRepository ofertaRepo,
            SolicitanteRepository solicitanteRepo,
            UsuarioRepository usuarioRepo) {

        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // Convocatoria
            Convocatoria conv = new Convocatoria();
            conv.setNombre("EvAU Junio 2026");
            conv.setFechaInicio(LocalDate.of(2025, 6, 1));
            conv.setFechaFin(LocalDate.of(2025, 6, 30));
            conv.setEstado("ABIERTA");
            convocatoriaRepo.save(conv);

            // Universidades
            Universidad ucm = new Universidad();
            ucm.setNombre("Universidad Complutense de Madrid");
            ucm.setProvincia("Madrid");
            universidadRepo.save(ucm);

            Universidad upm = new Universidad();
            upm.setNombre("Universidad Politécnica de Madrid");
            upm.setProvincia("Madrid");
            universidadRepo.save(upm);

            Universidad uam = new Universidad();
            uam.setNombre("Universidad Autónoma de Madrid");
            uam.setProvincia("Madrid");
            universidadRepo.save(uam);

            Universidad uc3m = new Universidad();
            uc3m.setNombre("Universidad Carlos III de Madrid");
            uc3m.setProvincia("Madrid");
            universidadRepo.save(uc3m);

            Universidad urjc = new Universidad();
            urjc.setNombre("Universidad Rey Juan Carlos");
            urjc.setProvincia("Madrid");
            universidadRepo.save(urjc);

            Universidad uah = new Universidad();
            uah.setNombre("Universidad de Alcalá");
            uah.setProvincia("Madrid");
            universidadRepo.save(uah);

            Universidad uned = new Universidad();
            uned.setNombre("Universidad Nacional de Educación a Distancia");
            uned.setProvincia("Madrid");
            universidadRepo.save(uned);

            // Ofertas con criterios
            Oferta oferta1 = new Oferta();
            oferta1.setGrado("Ingeniería Informática");
            oferta1.setPlazas(120);
            oferta1.setUniversidad(upm);
            oferta1.setConvocatoria(conv);

            CriterioAdmision c1 = new CriterioAdmision();
            c1.setAsignatura("Matemáticas II");
            c1.setPeso(0.2);
            c1.setOferta(oferta1);

            CriterioAdmision c2 = new CriterioAdmision();
            c2.setAsignatura("Física");
            c2.setPeso(0.1);
            c2.setOferta(oferta1);

            oferta1.setCriterios(List.of(c1, c2));
            ofertaRepo.save(oferta1);


            Oferta oferta2 = new Oferta();
            oferta2.setGrado("Medicina");
            oferta2.setPlazas(80);
            oferta2.setUniversidad(ucm);
            oferta2.setConvocatoria(conv);

            CriterioAdmision c3 = new CriterioAdmision();
            c3.setAsignatura("Biología");
            c3.setPeso(0.2);
            c3.setOferta(oferta2);

            CriterioAdmision c4 = new CriterioAdmision();
            c4.setAsignatura("Química");
            c4.setPeso(0.2);
            c4.setOferta(oferta2);

            oferta2.setCriterios(List.of(c3, c4));
            ofertaRepo.save(oferta2);


            Oferta oferta3 = new Oferta();
            oferta3.setGrado("Derecho");
            oferta3.setPlazas(650);
            oferta3.setUniversidad(ucm);
            oferta3.setConvocatoria(conv);

            CriterioAdmision c5 = new CriterioAdmision();
            c5.setAsignatura("Historia del Arte");
            c5.setPeso(0.2);
            c5.setOferta(oferta3);

            CriterioAdmision c6 = new CriterioAdmision();
            c6.setAsignatura("Latín");
            c6.setPeso(0.2);
            c6.setOferta(oferta3);

            oferta3.setCriterios(List.of(c5, c6));
            ofertaRepo.save(oferta3);


            Oferta oferta4 = new Oferta();
            oferta4.setGrado("Psicología");
            oferta4.setPlazas(350);
            oferta4.setUniversidad(uam);
            oferta4.setConvocatoria(conv);

            CriterioAdmision c7 = new CriterioAdmision();
            c7.setAsignatura("Biología");
            c7.setPeso(0.2);
            c7.setOferta(oferta4);

            CriterioAdmision c8 = new CriterioAdmision();
            c8.setAsignatura("Física");
            c8.setPeso(0.2);
            c8.setOferta(oferta4);

            oferta4.setCriterios(List.of(c7, c8));
            ofertaRepo.save(oferta4);


            Oferta oferta5 = new Oferta();
            oferta5.setGrado("Bellas Artes");
            oferta5.setPlazas(70);
            oferta5.setUniversidad(urjc);
            oferta5.setConvocatoria(conv);

            CriterioAdmision c9 = new CriterioAdmision();
            c9.setAsignatura("Dibujo Técnico II");
            c9.setPeso(0.2);
            c9.setOferta(oferta5);

            CriterioAdmision c10 = new CriterioAdmision();
            c10.setAsignatura("Historia del Arte");
            c10.setPeso(0.2);
            c10.setOferta(oferta5);

            oferta5.setCriterios(List.of(c9, c10));
            ofertaRepo.save(oferta5);

            // Usuarios estudiante de ejemplo
            Usuario uAna = new Usuario();
            uAna.setNombre("Ana");
            uAna.setApellidos("García López");
            uAna.setEmail("ana@eduplazas.es");
            uAna.setPassword(encoder.encode("1234"));
            uAna.setDni("11111111A");
            uAna.setIdEvau("MAD-2025-001");
            uAna.setRol("ESTUDIANTE");
            usuarioRepo.save(uAna);

            Usuario uCarlos = new Usuario();
            uCarlos.setNombre("Carlos");
            uCarlos.setApellidos("Martínez Ruiz");
            uCarlos.setEmail("carlos@eduplazas.es");
            uCarlos.setPassword(encoder.encode("1234"));
            uCarlos.setDni("22222222B");
            uCarlos.setIdEvau("MAD-2025-002");
            uCarlos.setRol("ESTUDIANTE");
            usuarioRepo.save(uCarlos);

            // Solicitantes
            Solicitante s1 = new Solicitante();
            s1.setNombre("Ana");
            s1.setApellidos("García López");
            s1.setEmail("ana@eduplazas.es");
            s1.setNotaBase(0.0);
            s1.setUsuario(uAna);

            solicitanteRepo.save(s1);

            Solicitante s2 = new Solicitante();
            s2.setNombre("Carlos");
            s2.setApellidos("Martínez Ruiz");
            s2.setEmail("carlos@eduplazas.es");
            s2.setNotaBase(0.0);
            s2.setUsuario(uCarlos);

            solicitanteRepo.save(s2);

            System.out.println("✅ Datos de ejemplo cargados correctamente");
        };
    }
}