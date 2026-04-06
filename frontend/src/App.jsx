import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import Login from './pages/auth/Login'
import RegistroEstudiante from './pages/auth/RegistroEstudiante'
import RegistroUniversidad from './pages/auth/RegistroUniversidad'
import EstudianteInicio from './pages/solicitudes/EstudianteInicio'
import CrearSolicitud from './pages/solicitudes/CrearSolicitud'
import ExplorarGrados from './pages/solicitudes/ExplorarGrados'
import VerSolicitud from './pages/solicitudes/VerSolicitud'
import VerResultados from './pages/solicitudes/VerResultados'
import UniversidadInicio from './pages/universidad/UniversidadInicio'
import PublicarOferta from './pages/universidad/PublicarOferta'
import MisOfertas from './pages/universidad/MisOfertas'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/estudiantes/login" element={<Login rol="ESTUDIANTE" />} />
        <Route path="/universidades/login" element={<Login rol="UNIVERSIDAD" />} />
        <Route path="/registro/estudiante" element={<RegistroEstudiante />} />
        <Route path="/registro/universidad" element={<RegistroUniversidad />} />
        <Route path="/estudiante/inicio" element={<EstudianteInicio />} />
        <Route path="/estudiante/solicitud" element={<CrearSolicitud />} />
        <Route path="/estudiante/grados" element={<ExplorarGrados />} />
        <Route path="/estudiante/ver-solicitud" element={<VerSolicitud />} />
        <Route path="/estudiante/resultados" element={<VerResultados />} />
        <Route path="/universidad/inicio" element={<UniversidadInicio />} />
        <Route path="/universidad/publicar-oferta" element={<PublicarOferta />} />
        <Route path="/universidad/mis-ofertas" element={<MisOfertas />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
