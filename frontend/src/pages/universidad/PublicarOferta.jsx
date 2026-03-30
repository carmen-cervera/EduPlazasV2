import React from 'react';
import styles from './PublicarOferta.module.css';
import logo from '../../assets/LogoPequeño_FondoBlanco_SinGorro.png'

const PublicarOferta = () => {
  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <img src="/src/assets/LogoPequeño_FondoBlanco_SinGorro.png" alt="Logo" className={styles.logoImg} />
        <h1 className={styles.tituloHeader}>Publicar oferta</h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div className={styles.userBox}>
            <div className={styles.avatar}>🏛️</div>
            <p className={styles.email}>Universidad Politécnica de Madrid</p>
          </div>
          <nav>
            <button className={styles.primaryButton}>Publicar Oferta</button>
            <button className={styles.button}>Grados Publicados</button>
          </nav>
          <button className={styles.button}>Log Out</button>
        </aside>

        <main className={styles.main}>
          <div className={styles.card}>
            <h2 className={styles.sectionTitle}>PUBLICAR OFERTA</h2>
            <p className={styles.convocatoria}>Convocatoria: EvAU Junio 2026</p>

            <form className={styles.formulario}>
              <label className={styles.label}>Nombre del grado</label>
              <input type="text" className={styles.input} placeholder="Ej: Grado en Ingeniería Aeroespacial" />

              <label className={styles.label}>Número de plazas disponibles</label>
              <input type="number" className={styles.input} placeholder="0" />

              <label className={styles.label}>Criterios de admisión aplicables</label>
              <div className={styles.checkboxContainer}>
                <label className={styles.checkboxLabel}><input type="checkbox" /> Nota de corte mínima</label>
                <label className={styles.checkboxLabel}><input type="checkbox" /> Prueba de aptitud</label>
                <label className={styles.checkboxLabel}><input type="checkbox" /> Expediente académico</label>
                <label className={styles.checkboxLabel}><input type="checkbox" /> Entrevista personal</label>
                <label className={styles.checkboxLabel}><input type="checkbox" /> Lista de espera</label>
                <label className={styles.checkboxLabel}><input type="checkbox" /> Otros</label>
              </div>

              <div className={styles.footerButtons}>
                <button type="submit" className={styles.btnEnviarFormulario} style={{maxWidth: '300px'}}>
                  PUBLICAR GRADO
                </button>
              </div>
            </form>
          </div>
        </main>
      </div>
    </div>
  );
};

export default PublicarOferta;