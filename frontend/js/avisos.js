const aviso = document.getElementById('aviso')

export function mostrarAviso(mensagem, tipo) {
  aviso.textContent = mensagem
  aviso.className = `aviso aviso-${tipo}`
  aviso.hidden = false
}

export function esconderAviso() {
  aviso.hidden = true
}
